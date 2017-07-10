package cn.nukkit.level.pathfinder;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;
import cn.nukkit.pathfinding.PathPoint;

public class SwimNodeProcessor extends NodeProcessor{

    public void initProcessor(Level iblockaccessIn, Entity entityIn){
        super.initProcessor(iblockaccessIn, entityIn);
    }

    public void postProcess(){
        super.postProcess();
    }

    public PathPoint getPathPointTo(Entity entityIn){
        return this.openPoint(MathHelper.floor_double(entityIn.getBoundingBox().minX), MathHelper.floor_double(entityIn.getBoundingBox().minY + 0.5D), MathHelper.floor_double(entityIn.getBoundingBox().minZ));
    }

    public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target){
        return this.openPoint(MathHelper.floor_double(x - (double)(entityIn.getWidth() / 2.0F)), MathHelper.floor_double(y + 0.5D), MathHelper.floor_double(target - (double)(entityIn.getWidth() / 2.0F)));
    }

    public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance){
        int i = 0;

        //for (EnumFacing enumfacing : EnumFacing.values()){
        //    PathPoint pathpoint = this.getSafePoint(entityIn, currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());

        //    if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance)
        //    {
        //        pathOptions[i++] = pathpoint;
        //    }
        //}

        return i;
    }

    private PathPoint getSafePoint(Entity entityIn, int x, int y, int z){
        int i = this.func_176186_b(entityIn, x, y, z);
        return i == -1 ? this.openPoint(x, y, z) : null;
    }

    private int func_176186_b(Entity entityIn, int x, int y, int z){
        Vector3 blockpos$mutableblockpos = new Vector3();

        for (int i = x; i < x + this.entitySizeX; ++i){
            for (int j = y; j < y + this.entitySizeY; ++j){
                for (int k = z; k < z + this.entitySizeZ; ++k){
                    Block block = this.blockaccess.getBlock(blockpos$mutableblockpos.setComponents(i, j, k));

                    if (block.getId() != Block.WATER && block.getId() != Block.STILL_WATER){
                        return 0;
                    }
                }
            }
        }

        return -1;
    }
}