package net.blacklab.lmmnx;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class LMMNX_EntityAIWatchClosest2 extends LMMNX_EntityAIWatchClosest {

	public LMMNX_EntityAIWatchClosest2(EntityLiving entitylivingIn,
			Class<? extends Entity> watchTargetClass, float maxDistance, float f) {
		super(entitylivingIn, watchTargetClass, maxDistance);
		setMutexBits(3);
	}

}
