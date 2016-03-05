package net.blacklab.lmmnx.entity.littlemaid.mode;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityModeBase;
import net.blacklab.lmmnx.entity.ai.LMMNX_EntityAIWatchClosest2;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;

public class EntityMode_DeathWait extends LMM_EntityModeBase {
	
	public static final int mmode_DeathWait = 0x00d0; 

	public EntityMode_DeathWait(LMM_EntityLittleMaid pEntity) {
		super(pEntity);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public int priority() {
		// TODO 自動生成されたメソッド・スタブ
		return -1;
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		// TODO 自動生成されたメソッド・スタブ
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = new EntityAITasks(null);
		ltasks[1] = new EntityAITasks(null);

		ltasks[0].addTask(1, new LMMNX_EntityAIWatchClosest2(owner, EntityLivingBase.class, 10F, 0.02F));
		ltasks[0].addTask(2, new LMMNX_EntityAIWatchClosest2(owner, LMM_EntityLittleMaid.class, 10F, 0.02F));
		ltasks[0].addTask(2, new LMMNX_EntityAIWatchClosest2(owner, EntityPlayer.class, 10F, 0.02F));
		ltasks[0].addTask(2, new EntityAILookIdle(owner));

		owner.addMaidMode(ltasks, "DeathWait", mmode_DeathWait);
	}

}
