package cn.nukkit.level.pathfinder;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.block.BlockFence;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.block.BlockRail;
import cn.nukkit.block.BlockWall;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.level.Level;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;
import cn.nukkit.pathfinding.PathPoint;

public class WalkNodeProcessor extends NodeProcessor
{
    private boolean canEnterDoors;
    private boolean canBreakDoors;
    private boolean avoidsWater;
    private boolean canSwim;
    private boolean shouldAvoidWater;

    public void initProcessor(Level iblockaccessIn, Entity entityIn){
        super.initProcessor(iblockaccessIn, entityIn);
        this.shouldAvoidWater = this.avoidsWater;
    }

    public void postProcess(){
        super.postProcess();
        this.avoidsWater = this.shouldAvoidWater;
    }

    public PathPoint getPathPointTo(Entity entityIn){
        int i;

        if (this.canSwim && entityIn.isInsideOfWater()){
            i = (int)entityIn.getBoundingBox().minY;
            Vector3 blockpos$mutableblockpos = new Vector3(MathHelper.floor_double(entityIn.x), i, MathHelper.floor_double(entityIn.z));

            for (Block block = this.blockaccess.getBlock(blockpos$mutableblockpos); block.getId() == Block.WATER || block.getId() == Block.STILL_WATER; block = this.blockaccess.getBlock(blockpos$mutableblockpos)){
                ++i;
                blockpos$mutableblockpos.setComponents(MathHelper.floor_double(entityIn.x), i, MathHelper.floor_double(entityIn.z));
            }

            this.avoidsWater = false;
        }else{
            i = MathHelper.floor_double(entityIn.getBoundingBox().minY + 0.5D);
        }

        return this.openPoint(MathHelper.floor_double(entityIn.getBoundingBox().minX), i, MathHelper.floor_double(entityIn.getBoundingBox().minZ));
    }

    public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target){
        return this.openPoint(MathHelper.floor_double(x - (double)(entityIn.getWidth() / 2.0F)), MathHelper.floor_double(y), MathHelper.floor_double(target - (double)(entityIn.getWidth() / 2.0F)));
    }

    public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance){
        int i = 0;
        int j = 0;

        if (this.getVerticalOffset(entityIn, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord) == 1){
            j = 1;
        }

        PathPoint pathpoint = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord+1, currentPoint.zCoord + 1, j);
        PathPoint pathpoint1 = this.getSafePoint(entityIn, currentPoint.xCoord - 1, currentPoint.yCoord+1, currentPoint.zCoord, j);
        PathPoint pathpoint2 = this.getSafePoint(entityIn, currentPoint.xCoord + 1, currentPoint.yCoord+1, currentPoint.zCoord, j);
        PathPoint pathpoint3 = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord+1, currentPoint.zCoord - 1, j);

        if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance){
            pathOptions[i++] = pathpoint;
        }

        if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.distanceTo(targetPoint) < maxDistance){
            pathOptions[i++] = pathpoint1;
        }

        if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.distanceTo(targetPoint) < maxDistance){
            pathOptions[i++] = pathpoint2;
        }

        if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.distanceTo(targetPoint) < maxDistance){
            pathOptions[i++] = pathpoint3;
        }

        return i;
    }

    private PathPoint getSafePoint(Entity entityIn, int x, int y, int z, int p_176171_5_){
        PathPoint pathpoint = null;
        int i = this.getVerticalOffset(entityIn, x, y, z);

        if (i == 2){
            return this.openPoint(x, y, z);
        }else{
            if (i == 1){
                pathpoint = this.openPoint(x, y, z);
            }

            if (pathpoint == null && p_176171_5_ > 0 && i != -3 && i != -4 && this.getVerticalOffset(entityIn, x, y + p_176171_5_, z) == 1){
                pathpoint = this.openPoint(x, y + p_176171_5_, z);
                y += p_176171_5_;
            }

            if (pathpoint != null){
                int j = 0;
                int k;

                for (k = 0; y > 0; pathpoint = this.openPoint(x, y, z)){
                    k = this.getVerticalOffset(entityIn, x, y - 1, z);

                    if (this.avoidsWater && k == -1){
                        return null;
                    }

                    if (k != 1){
                        break;
                    }

                    if (j++ >= ((EntityLiving)entityIn).getMaxFallHeight()){
                        return null;
                    }

                    --y;

                    if (y <= 0){
                        return null;
                    }
                }

                if (k == -2){
                    return null;
                }
            }

            return pathpoint;
        }
    }

    private int getVerticalOffset(Entity entityIn, int x, int y, int z){
        return func_176170_a(this.blockaccess, entityIn, x, y, z, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.avoidsWater, this.canBreakDoors, this.canEnterDoors);
    }

    public static int func_176170_a(Level blockaccessIn, Entity entityIn, int x, int y, int z, int sizeX, int sizeY, int sizeZ, boolean avoidWater, boolean breakDoors, boolean enterDoors){
        boolean flag = false;
        Vector3 blockpos = new Vector3(entityIn.x, entityIn.y, entityIn.z);
        Vector3 blockpos$mutableblockpos = new Vector3();

        for (int i = x; i < x + sizeX; ++i){
            for (int j = y; j < y + sizeY; ++j){
                for (int k = z; k < z + sizeZ; ++k){
                    blockpos$mutableblockpos.setComponents(i, j, k);
                    Block block = blockaccessIn.getBlock(blockpos$mutableblockpos);

                    if (block.getId() != Block.AIR){
                        if (block.getId() != Block.TRAPDOOR && block.getId() != Block.IRON_TRAPDOOR){
                            if (block.getId() != Block.WATER && block.getId() != Block.STILL_WATER){
                                if (!enterDoors && block instanceof BlockDoor/* && block.getMaterial() == Material.wood*/){
                                    return 0;
                                }
                            }else{
                                if (avoidWater){
                                    return -1;
                                }

                                flag = true;
                            }
                        }else{
                            flag = true;
                        }

                        if (entityIn.level.getBlock(blockpos$mutableblockpos) instanceof BlockRail){
                            if (!(entityIn.level.getBlock(blockpos) instanceof BlockRail) && !(entityIn.level.getBlock(blockpos.down()) instanceof BlockRail)){
                                return -3;
                            }
                        }else if (!block.isTransparent() && (!breakDoors || !(block instanceof BlockDoor)/* || block.getMaterial() != Material.wood*/)){
                            if (block instanceof BlockFence || block instanceof BlockFenceGate || block instanceof BlockWall){
                                return -3;
                            }

                            if (block.getId() != Block.TRAPDOOR && block.getId() != Block.IRON_TRAPDOOR){
                                return -4;
                            }

                            if (block.getId() == Block.LAVA || block.getId() == Block.STILL_LAVA){
                                return 0;
                            }

                            if (!entityIn.isInsideOfLava()){
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return flag ? 2 : 1;
    }

    public void setEnterDoors(boolean canEnterDoorsIn){
        this.canEnterDoors = canEnterDoorsIn;
    }

    public void setBreakDoors(boolean canBreakDoorsIn){
        this.canBreakDoors = canBreakDoorsIn;
    }

    public void setAvoidsWater(boolean avoidsWaterIn){
        this.avoidsWater = avoidsWaterIn;
    }

    public void setCanSwim(boolean canSwimIn){
        this.canSwim = canSwimIn;
    }

    public boolean getEnterDoors(){
        return this.canEnterDoors;
    }

    public boolean getCanSwim(){
        return this.canSwim;
    }

    public boolean getAvoidsWater(){
        return this.avoidsWater;
    }
}