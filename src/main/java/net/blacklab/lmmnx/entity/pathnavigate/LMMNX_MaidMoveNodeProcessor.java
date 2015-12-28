package net.blacklab.lmmnx.entity.pathnavigate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class LMMNX_MaidMoveNodeProcessor extends WalkNodeProcessor {
	
	protected boolean canSwim;
	
	@Override
	public void func_176178_d(boolean canSwimIn) {
		super.func_176178_d(canSwimIn);
		canSwim = canSwimIn;
	}
	
	@Override
	public PathPoint func_176161_a(Entity entityIn) {
		if (canSwim) {
			return this.func_176159_a(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
		}
		return super.func_176161_a(entityIn);
	}

	@Override
	public PathPoint func_176160_a(Entity entityIn, double x, double y,
			double target) {
		if (canSwim) {
			return this.func_176159_a(MathHelper.floor_double(x - (double)(entityIn.width / 2.0F)), MathHelper.floor_double(y + 0.5D), MathHelper.floor_double(target - (double)(entityIn.width / 2.0F)));
		}
		return super.func_176160_a(entityIn, x, y, target);
	}
	
	@Override
	public int func_176164_a(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
		if (canSwim && entityIn.isInWater()) {
			int i = 0;

			for (EnumFacing enumfacing : EnumFacing.values()) {
				PathPoint pathpoint = this.getSafePoint(entityIn, currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());

				if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
					pathOptions[i++] = pathpoint;
				}
			}

			return i;
		}
		return super.func_176164_a(pathOptions, entityIn, currentPoint, targetPoint, maxDistance);
	}

	/**
	 * Returns a point that the entity can safely move to
	 */
	private PathPoint getSafePoint(Entity entityIn, int x, int y, int z) {
		int i = -1;//this.func_176186_b(entityIn, x, y, z);
		return i == -1 ? this.func_176159_a(x, y, z) : null;
	}

/*
	private int func_176186_b(Entity entityIn, int x, int y, int z) {

		for (int i = x; i < x + this.field_176168_c; ++i) {
			for (int j = y; j < y + this.field_176165_d; ++j) {
				for (int k = z; k < z + this.field_176166_e; ++k) {
					Block block = entityIn.worldObj.getBlockState(new BlockPos(i, j, k)).getBlock();

					if (block.getMaterial() != Material.water) {
						return 0;
					}
				}
			}
		}

		return -1;
	}
*/
}
