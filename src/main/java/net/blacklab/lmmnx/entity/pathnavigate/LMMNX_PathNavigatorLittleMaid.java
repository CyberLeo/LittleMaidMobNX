package net.blacklab.lmmnx.entity.pathnavigate;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class LMMNX_PathNavigatorLittleMaid extends PathNavigateGround {
	
	protected LMM_EntityLittleMaid theMaid;

	public LMMNX_PathNavigatorLittleMaid(EntityLiving entitylivingIn,
			World worldIn) {
		super(entitylivingIn, worldIn);
		if (theEntity instanceof LMM_EntityLittleMaid) theMaid = (LMM_EntityLittleMaid) theEntity;
	}

	@Override
	protected PathFinder func_179679_a() {
		field_179695_a = new LMMNX_MaidMoveNodeProcessor();
		field_179695_a.func_176175_a(true);
		return new PathFinder(field_179695_a);
	}

	@Override
	protected Vec3 getEntityPosition() {
		if (theMaid.isSwimmingEnabled() && theMaid.isInWater())
			return new Vec3(theEntity.posX, theEntity.posY + (double)theEntity.height * 0.5D, theEntity.posZ);
		return super.getEntityPosition();
	}

	@Override
	protected void pathFollow() {
		if (theMaid.isSwimmingEnabled() && theMaid.isInWater()) {
			Vec3 vec3 = getEntityPosition();
			float f = theEntity.width * theEntity.width;
			int i = 6;

			if (vec3.squareDistanceTo(currentPath.getVectorFromIndex(theEntity, currentPath.getCurrentPathIndex())) < (double)f) {
				currentPath.incrementPathIndex();
			}

			for (int j = Math.min(currentPath.getCurrentPathIndex() + i, currentPath.getCurrentPathLength() - 1); j > currentPath.getCurrentPathIndex(); --j) {
				Vec3 vec31 = currentPath.getVectorFromIndex(theEntity, j);

				if (vec31.squareDistanceTo(vec3) <= 36.0D && isDirectPathBetweenPoints(vec3, vec31, 0, 0, 0)) {
					currentPath.setCurrentPathIndex(j);
					break;
				}
			}

			func_179677_a(vec3);
			return;
		}
		super.pathFollow();
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32,
			int sizeX, int sizeY, int sizeZ) {
		if (theMaid.isSwimmingEnabled()) {
			MovingObjectPosition movingobjectposition = worldObj.rayTraceBlocks(posVec31, new Vec3(posVec32.xCoord, posVec32.yCoord + (double)theEntity.height * 0.5D, posVec32.zCoord), false, true, false);
			return movingobjectposition == null || movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
		}
		return super.isDirectPathBetweenPoints(posVec31, posVec32, sizeX, sizeY, sizeZ);
	}

}
