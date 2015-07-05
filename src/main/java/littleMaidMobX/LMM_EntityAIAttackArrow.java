package littleMaidMobX;

import java.lang.reflect.Field;
import java.util.List;

import mmmlibx.lib.MMM_Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class LMM_EntityAIAttackArrow extends EntityAIBase implements LMM_IEntityAI {

	protected boolean fEnable;
	
	protected LMM_EntityLittleMaid fMaid;
	protected EntityPlayer fAvatar;
	protected LMM_InventoryLittleMaid fInventory;
	protected LMM_SwingStatus swingState;
	protected World worldObj;
	protected EntityLivingBase fTarget;
	protected int fForget;

	
	public LMM_EntityAIAttackArrow(LMM_EntityLittleMaid pEntityLittleMaid) {
		fMaid = pEntityLittleMaid;
		fAvatar = pEntityLittleMaid.maidAvatar;
		fInventory = pEntityLittleMaid.maidInventory;
		swingState = pEntityLittleMaid.getSwingStatusDominant();
		worldObj = pEntityLittleMaid.worldObj;
		fEnable = false;
		setMutexBits(3);
	}
	
	public LMM_IEntityLittleMaidAvatar getAvatarIF()
	{
		return (LMM_IEntityLittleMaidAvatar)fAvatar;
	}
	
	@Override
	public boolean shouldExecute() {
		EntityLivingBase entityliving = fMaid.getAttackTarget();
		if(fMaid.isMaidWaitEx()) return false;
		
		if (!fEnable || entityliving == null || entityliving.isDead) {
			fMaid.setAttackTarget(null);
			//fMaid.setTarget(null);
			fMaid.getNavigator().clearPathEntity();
			fTarget = null;
			resetTask();
			return false;
		} else {
			fTarget = entityliving;
			return true;
		}
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
//		fMaid.playLittleMaidSound(fMaid.isBloodsuck() ? LMM_EnumSound.findTarget_B : LMM_EnumSound.findTarget_N, false);
		swingState = fMaid.getSwingStatusDominant();
	}

	@Override
	public boolean continueExecuting() {
		return shouldExecute() || (fTarget != null && !fMaid.getNavigator().noPath());
	}

	@Override
	public void resetTask() {
		fTarget = null;
		fAvatar.stopUsingItem();
		fAvatar.clearItemInUse();
		fForget=0;
	}

	@Override
	public void updateTask() {

		double backupPosX = fMaid.posX;
		double backupPosZ = fMaid.posZ;
		
		// プレイヤーに乗っていると射線にプレイヤーが入り、撃てなくなるため僅かに目標エンティティに近づける
		// 関数を抜ける前に元に戻す必要があるので途中で return しないこと
		if(fMaid.ridingEntity instanceof EntityPlayer)
		{
			double dtx = fTarget.posX - fMaid.posX;
			double dtz = fTarget.posZ - fMaid.posZ;
			double distTarget = MathHelper.sqrt_double(dtx*dtx + dtz*dtz);
			fMaid.posX += dtx / distTarget * 1.0;	// 1m 目標に近づける
			fMaid.posZ += dtz / distTarget * 1.0;	// 1m 目標に近づける
		}
		
		double lrange = 225D;
		double ldist = fMaid.getDistanceSqToEntity(fTarget);
		boolean lsee = fMaid.getEntitySenses().canSee(fTarget);
		
		// 視界の外に出たら一定時間で飽きる
		if (lsee) {
			fForget = 0;
		} else {
			fForget++;
		}
		
		// 攻撃対象を見る
		fMaid.getLookHelper().setLookPositionWithEntity(fTarget, 30F, 30F);
		
		if(fForget>=20){
			resetTask();
		}
		if (ldist < lrange) {
			if(fTarget==null){
				resetTask();
				return;
			}

			// 有効射程内
			double atx = fTarget.posX - fMaid.posX;
			double aty = fTarget.posY - fMaid.posY;
			double atz = fTarget.posZ - fMaid.posZ;
			if (fTarget.isEntityAlive()) {
				ItemStack litemstack = fMaid.getCurrentEquippedItem();
				// 敵とのベクトル
				double atl = atx * atx + aty * aty + atz * atz;
				double il = -1D;
				double milsq = 10D;
				Entity masterEntity = fMaid.getMaidMasterEntity();
				if (masterEntity != null && !fMaid.isPlaying()) {
					// 主とのベクトル
					double amx = masterEntity.posX - fMaid.posX;
					double amy = masterEntity.posY - fMaid.posY;//-2D
					double amz = masterEntity.posZ - fMaid.posZ;
					
					// この値が０～１ならターゲットとの間に主がいる
					il = (amx * atx + amy * aty + amz * atz) / atl;
					
					// 射線ベクトルと主との垂直ベクトル
					double mix = (fMaid.posX + il * atx) - masterEntity.posX;
					double miy = (fMaid.posY + il * aty) - masterEntity.posY;// + 2D;
					double miz = (fMaid.posZ + il * atz) - masterEntity.posZ;
					// 射線から主との距離
					milsq = mix * mix + miy * miy + miz * miz;
//					mod_LMM_littleMaidMob.Debug("il:%f, milsq:%f", il, milsq);
				}
				
				if (litemstack != null && !(litemstack.getItem() instanceof ItemFood) && !fMaid.weaponReload) {
//					int lastentityid = worldObj.loadedEntityList.size();
					int itemcount = litemstack.stackSize;
					fMaid.mstatAimeBow = true;
					getAvatarIF().getValueVectorFire(atx, aty, atz, atl);
					// ダイヤ、金ヘルムなら味方への誤射を気持ち軽減
					boolean lcanattack = true;
					boolean ldotarget = false;
					double tpr = Math.sqrt(atl);
					Entity lentity = MMM_Helper.getRayTraceEntity(fMaid.maidAvatar, tpr + 1.0F, 1.0F, 1.0F);
					Item helmid = !fMaid.isMaskedMaid() ? null : fInventory.armorInventory[3].getItem();
					if (helmid == Items.diamond_helmet || helmid == Items.golden_helmet) {
						// 射線軸の確認
						if (lentity != null && fMaid.getIFF(lentity)) {
							lcanattack = false;
//							mod_LMM_littleMaidMob.Debug("ID:%d-friendly fire to ID:%d.", fMaid.entityId, lentity.entityId);
						}
					}
					if (lentity == fTarget) {
						ldotarget = true;
					}
					lcanattack &= (milsq > 3D || il < 0D);
					lcanattack &= ldotarget;
					// 横移動
					if (!lcanattack) {
						// 射撃位置を確保する
						double tpx = fMaid.posX;
						double tpy = fMaid.posY;
						double tpz = fMaid.posZ;
//						double tpr = Math.sqrt(atl) * 0.5D;
						tpr = tpr * 0.5D;
						if (fMaid.isBloodsuck()) {
							// 左回り
							tpx += (atz / tpr);
							tpz -= (atx / tpr);
						} else {
							// 右回り
							tpx -= (atz / tpr);
							tpz += (atx / tpr);
						}
						fMaid.getNavigator().tryMoveToXYZ(tpx, tpy, tpz, 1.0F);
					}
					else if (lsee & ldist < 100) {
						fMaid.getNavigator().clearPathEntity();
//						mod_LMM_littleMaidMob.Debug("Shooting Range.");
					}
					
					lcanattack &= lsee;
//            		mod_littleMaidMob.Debug(String.format("id:%d at:%d", entityId, attackTime));
					if (((fMaid.weaponFullAuto && !lcanattack) || (lcanattack && fMaid.getSwingStatusDominant().canAttack())) && getAvatarIF().getIsItemTrigger()) {
						// シュート
						// フルオート武器は射撃停止
						LMM_LittleMaidMobNX.Debug("id:%d shoot.", fMaid.getEntityId());
						fAvatar.stopUsingItem();
						fMaid.setSwing(30, LMM_EnumSound.shoot, !fMaid.isPlaying());
					} else {
						// チャージ
						if (litemstack.getMaxItemUseDuration() > 500) {
//                			mod_littleMaidMob.Debug(String.format("non reload.%b", isMaskedMaid));
							// リロード無しの通常兵装
							if (!getAvatarIF().isUsingItemLittleMaid()) {
								// 構え
								if (!fMaid.weaponFullAuto || lcanattack) {
									// フルオート兵装の場合は射線確認
									int at = ((helmid == Items.iron_helmet) || (helmid == Items.diamond_helmet)) ? 26 : 16;
									if (swingState.attackTime < at) {
										fMaid.setSwing(at, LMM_EnumSound.sighting, !fMaid.isPlaying());
										litemstack = litemstack.useItemRightClick(worldObj, fAvatar);
										LMM_LittleMaidMobNX.Debug("id:%d redygun.", fMaid.getEntityId());
									}
								} else {
									if(fMaid.maidMode!=LMM_EntityMode_Playing.mmode_Playing)
										LMM_LittleMaidMobNX.Debug(String.format("ID:%d-friendly fire FullAuto.", fMaid.getEntityId()));
								}
							}
						} 
						else if (litemstack.getMaxItemUseDuration() == 0) {
							// 通常投擲兵装
							if (swingState.canAttack() && !fAvatar.isUsingItem()) {
								if (lcanattack) {
									litemstack = litemstack.useItemRightClick(worldObj, fAvatar);
									// 意図的にショートスパンで音が鳴るようにしてある
									fMaid.mstatAimeBow = false;
									fMaid.setSwing(10, (litemstack.stackSize == itemcount) ? LMM_EnumSound.shoot_burst : LMM_EnumSound.Null, !fMaid.isPlaying());
									LMM_LittleMaidMobNX.Debug(String.format("id:%d throw weapon.(%d:%f:%f)", fMaid.getEntityId(), swingState.attackTime, fMaid.rotationYaw, fMaid.rotationYawHead));
								} else {
									if(fMaid.maidMode!=LMM_EntityMode_Playing.mmode_Playing)
										LMM_LittleMaidMobNX.Debug(String.format("ID:%d-friendly fire throw weapon.", fMaid.getEntityId()));
								}
							}
						} else {
							// リロード有りの特殊兵装
							if (!getAvatarIF().isUsingItemLittleMaid()) {
								litemstack = litemstack.useItemRightClick(worldObj, fAvatar);
								LMM_LittleMaidMobNX.Debug(String.format("%d reload.", fMaid.getEntityId()));
							}
							// リロード終了まで強制的に構える
							swingState.attackTime = 5;
						}
					}
//            		maidAvatarEntity.setValueRotation();
					getAvatarIF().setValueVector();
					// アイテムが亡くなった
					if (litemstack.stackSize <= 0) {
						fMaid.destroyCurrentEquippedItem();
						fMaid.getNextEquipItem();
					} else {
						fInventory.setInventoryCurrentSlotContents(litemstack);
					}
					
					// 発生したEntityをチェックしてmaidAvatarEntityが居ないかを確認
					// TODO issue #9 merge from LittleMaidMobAX(https://github.com/asiekierka/littleMaidMobX/commit/92b2850b1bc4a70b69629cfc84c92748174c8bc6)
					/*
					List<Entity> newentitys = worldObj.loadedEntityList.subList(lastentityid, worldObj.loadedEntityList.size());
					boolean shootingflag = false;
					if (newentitys != null && newentitys.size() > 0) {
						LMM_LittleMaidMobNX.Debug(String.format("new FO entity %d", newentitys.size()));
						for (Entity te : newentitys) {
							if (te.isDead) {
								shootingflag = true;
								continue;
							}
							try {
								// 飛翔体の主を置き換える
								Field fd[] = te.getClass().getDeclaredFields();
//                				mod_littleMaidMob.Debug(String.format("%s, %d", e.getClass().getName(), fd.length));
								for (Field ff : fd) {
									// 変数を検索しAvatarと同じ物を自分と置き換える
									ff.setAccessible(true);
									Object eo = ff.get(te);
									if (eo.equals(fAvatar)) {
										ff.set(te, this);
										LMM_LittleMaidMobNX.Debug("Replace FO Owner.");
									}
								}
							}
							catch (Exception exception) {
							}
						}
					}
					// 既に命中していた場合の処理
					if (shootingflag) {
						for (Object obj : worldObj.loadedEntityList) {
							if (obj instanceof EntityCreature && !(obj instanceof LMM_EntityLittleMaid)) {
								EntityCreature ecr = (EntityCreature)obj;
								//1.8修正検討
								if (ecr.getAttackTarget() == fAvatar) {
									ecr.setAttackTarget(fMaid);
									ecr.setRevengeTarget(fMaid);
									ecr.getNavigator().getPathToEntityLiving(fMaid);
								}
							}
						}
					}
					*/
				}
			}
		} else {
			// 有効射程外
			if (fMaid.getNavigator().noPath()) {
				fMaid.getNavigator().tryMoveToEntityLiving(fTarget, 1.0);
				fMaid.setAttackTarget(null);
			}
			if (fMaid.weaponFullAuto && getAvatarIF().getIsItemTrigger()) {
				FMLCommonHandler.instance().getFMLLogger().debug("DEBUG INFO=NO TARGET");
				fAvatar.stopUsingItem();
			} else {
				FMLCommonHandler.instance().getFMLLogger().debug("DEBUG INFO=NO TARGET(C)");
				fAvatar.clearItemInUse();
			}
			resetTask();
		}
		

		// プレイヤーが射線に入らないように、変更したメイドさんの位置を元に戻す
		fMaid.posX = backupPosX;
		fMaid.posZ = backupPosZ;
	}

	@Override
	public void setEnable(boolean pFlag) {
		fEnable = pFlag;
	}

	@Override
	public boolean getEnable() {
		return fEnable;
	}

}
