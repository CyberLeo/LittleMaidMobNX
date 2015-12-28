package littleMaidMobX;

import java.util.List;

import net.blacklab.lib.minecraft.vector.VectorUtil;
import net.blacklab.lmmnx.api.item.LMMNX_API_Item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;

public class LMM_EntityAICollectItem extends EntityAIBase {
	
	protected LMM_EntityLittleMaid theMaid;
	protected float moveSpeed;
	protected EntityItem targetItem;
	protected boolean lastAvoidWater;
	
	
	public LMM_EntityAICollectItem(LMM_EntityLittleMaid pEntityLittleMaid, float pmoveSpeed) {
		theMaid = pEntityLittleMaid;
		moveSpeed = pmoveSpeed;
		setMutexBits(3);
	}


	@Override
	public boolean shouldExecute() {
		if(theMaid.isMaidWaitEx()) return false;
		if (theMaid.maidInventory.getFirstEmptyStack() > -1) {
			List llist = theMaid.worldObj.getEntitiesWithinAABB(EntityItem.class, theMaid.getEntityBoundingBox().expand(8F, 2D, 8F));
			if (!llist.isEmpty()) {
				int li = theMaid.getRNG().nextInt(llist.size());
				EntityItem ei = (EntityItem)llist.get(li);
				EntityPlayer ep = theMaid.mstatMasterEntity != null ? theMaid.mstatMasterEntity : theMaid.worldObj.getClosestPlayerToEntity(theMaid, 16F);
				
				NBTTagCompound p = new NBTTagCompound();
				ei.writeEntityToNBT(p);
				if (!ei.isDead && ei.onGround && p.getShort("PickupDelay") <= 0 && !ei.isBurning()
						&& canEntityItemBeSeen(ei) && (ep == null ||
						ep.getDistanceSq(
								ei.posX + MathHelper.sin(ep.rotationYaw * 0.01745329252F) * 2.0D,
								ei.posY,
								ei.posZ - MathHelper.cos(ep.rotationYaw * 0.01745329252F) * 2.0D) > 7.5D))
				{
					ItemStack lstack = ei.getEntityItem();
					if (!LMMNX_API_Item.isSugar(lstack.getItem())) {
						if ((theMaid.maidActiveModeClass == null)) {
							return false;
						}
						if ((!theMaid.maidActiveModeClass.checkItemStack(lstack))) {
							return false;
						}
					}
					theMaid.playLittleMaidSound(LMM_EnumSound.findTarget_I, false);
					targetItem = ei;
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		/*
		lastAvoidWater = theMaid.getNavigator().getAvoidsWater();
		theMaid.getNavigator().func_179690_a(true);
		*/
	}

	@Override
	public boolean continueExecuting() {
		return !targetItem.isDead && (theMaid.maidInventory.getFirstEmptyStack() > -1) && theMaid.getDistanceSqToEntity(targetItem) < 100D;
	}

	@Override
	public void resetTask() {
		targetItem = null;
		theMaid.getNavigator().clearPathEntity();
//		theMaid.getNavigator().func_179690_a(lastAvoidWater);
	}

	@Override
	public void updateTask() {
		theMaid.getLookHelper().setLookPositionWithEntity(targetItem, 30F, theMaid.getVerticalFaceSpeed());
		
		PathNavigate lnavigater = theMaid.getNavigator();
		if (lnavigater.noPath()) {
			if (targetItem.isInWater()) {
				//lnavigater.func_179690_a(false);
			}
			PathEntity lpath = lnavigater.getPathToXYZ(targetItem.posX, targetItem.posY, targetItem.posZ);
			lnavigater.setPath(lpath, moveSpeed);
		}
	}

	public boolean canEntityItemBeSeen(Entity entity) {
		// アイテムの可視判定
//		return theMaid.worldObj.rayTraceBlocks(new Vec3(theMaid.posX, theMaid.posY + (double)theMaid.getEyeHeight(), theMaid.posZ), new Vec3(entity.posX, entity.posY + ((entity.getEntityBoundingBox().minY - entity.getEntityBoundingBox().minY) / 2), entity.posZ)) == null;
		return VectorUtil.canMoveThrough(theMaid.maidActiveModeClass.owner, 0D, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), false, true, false);
	}

}
