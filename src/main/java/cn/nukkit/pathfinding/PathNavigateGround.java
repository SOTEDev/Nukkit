package cn.nukkit.pathfinding;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSlab;
import cn.nukkit.block.BlockStairs;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.level.Level;
import cn.nukkit.level.pathfinder.WalkNodeProcessor;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;

public class PathNavigateGround extends PathNavigate{

    protected WalkNodeProcessor nodeProcessor;
    private boolean shouldAvoidSun;

    public PathNavigateGround(EntityLiving entitylivingIn, Level worldIn){
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder(){
        this.nodeProcessor = new WalkNodeProcessor();
        this.nodeProcessor.setEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }

    protected boolean canNavigate(){
        return this.theEntity.onGround || this.getCanSwim() && this.isInLiquid();// || this.theEntity.isRiding() && this.theEntity instanceof EntityZombie && this.theEntity.ridingEntity instanceof EntityChicken;
    }

    protected Vector3 getEntityPosition(){
        return new Vector3(this.theEntity.x, (double)this.getPathablePosY(), this.theEntity.z);
    }

    private int getPathablePosY(){
        if (this.theEntity.isInsideOfWater() && this.getCanSwim()){
            int i = (int)this.theEntity.getBoundingBox().minY - 1;
            Block block = this.worldObj.getBlock(new Vector3(MathHelper.floor_double(this.theEntity.x), i, MathHelper.floor_double(this.theEntity.z)));
            int j = 0;

            while (block.getId() == Block.WATER || block.getId() == Block.STILL_WATER){
                ++i;
                block = this.worldObj.getBlock(new Vector3(MathHelper.floor_double(this.theEntity.x), i, MathHelper.floor_double(this.theEntity.z)));
                ++j;

                if (j > 16){
                    return (int)this.theEntity.getBoundingBox().minY;
                }
            }

            return i;
        }else{
            return (int)(this.theEntity.getBoundingBox().minY + 0.5D);
        }
    }

    protected void removeSunnyPath(){
        super.removeSunnyPath();

        /*if (this.shouldAvoidSun){
            if (this.worldObj.canSeeSky(new BlockPos(MathHelper.floor_double(this.theEntity.posX), (int)(this.theEntity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(this.theEntity.posZ)))){
                return;
            }

            for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i){
                PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);

                if (this.worldObj.canSeeSky(new BlockPos(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord))){
                    this.currentPath.setCurrentPathLength(i - 1);
                    return;
                }
            }
        }*/
    }

    protected boolean isDirectPathBetweenPoints(Vector3 posVec31, Vector3 posVec32, int sizeX, int sizeY, int sizeZ){
        int i = MathHelper.floor_double(posVec31.x);
        int j = MathHelper.floor_double(posVec31.z);
        double d0 = posVec32.x - posVec31.x;
        double d1 = posVec32.z - posVec31.z;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D){
            return false;
        }else{
            double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;

            if (!this.isSafeToStandAt(i, (int)posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)){
                return false;
            }else{
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double)(i * 1) - posVec31.x;
                double d7 = (double)(j * 1) - posVec31.z;

                if (d0 >= 0.0D){
                    ++d6;
                }

                if (d1 >= 0.0D){
                    ++d7;
                }

                d6 = d6 / d0;
                d7 = d7 / d1;
                int k = d0 < 0.0D ? -1 : 1;
                int l = d1 < 0.0D ? -1 : 1;
                int i1 = MathHelper.floor_double(posVec32.x);
                int j1 = MathHelper.floor_double(posVec32.z);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while (k1 * k > 0 || l1 * l > 0){
                    if (d6 < d7){
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    }else{
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }

                    if (!this.isSafeToStandAt(i, (int)posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)){
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3 vec31, double p_179683_8_, double p_179683_10_){
        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;

        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)){
            return false;
        }else{
            for (int k = i; k < i + sizeX; ++k){
                for (int l = j; l < j + sizeZ; ++l){
                    double d0 = (double)k + 0.5D - vec31.x;
                    double d1 = (double)l + 0.5D - vec31.z;

                    if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0D){
                        Block block = this.worldObj.getBlock(new Vector3(k, y - 1, l));

                        if (block.getId() == Block.AIR){
                            return false;
                        }

                        if (block.getId() == Block.WATER || block.getId() == Block.STILL_WATER && !this.theEntity.isInsideOfWater()){
                            return false;
                        }

                        if (block.getId() == Block.LAVA || block.getId() == Block.STILL_LAVA){
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private boolean isPositionClear(int p_179692_1_, int p_179692_2_, int p_179692_3_, int p_179692_4_, int p_179692_5_, int p_179692_6_, Vector3 p_179692_7_, double p_179692_8_, double p_179692_10_){
        for (BlockVector3 blockpos : getAllInBox(new BlockVector3(p_179692_1_, p_179692_2_, p_179692_3_), new BlockVector3(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1))){
            double d0 = (double)blockpos.getX() + 0.5D - p_179692_7_.x;
            double d1 = (double)blockpos.getZ() + 0.5D - p_179692_7_.z;

            if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0.0D){
                Block block = this.worldObj.getBlock(new Vector3(blockpos.x, blockpos.y, blockpos.z));

                if (!block.isTransparent()){
                    return false;
                }
                if (block instanceof BlockSlab){
                    return false;
                }
                if (block instanceof BlockStairs){
                    return false;
                }
            }
        }

        return true;
    }

    public static Iterable<BlockVector3> getAllInBox(BlockVector3 from, BlockVector3 to){
        final BlockVector3 blockpos = new BlockVector3(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockVector3 blockpos1 = new BlockVector3(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockVector3>(){
            public Iterator<BlockVector3> iterator(){
                return new AbstractIterator<BlockVector3>(){
                    private BlockVector3 lastReturned = null;
                    protected BlockVector3 computeNext(){
                        if (this.lastReturned == null){
                            this.lastReturned = blockpos;
                            return this.lastReturned;
                        }else if (this.lastReturned.equals(blockpos1)){
                            return (BlockVector3)this.endOfData();
                        }else{
                            int i = this.lastReturned.getX();
                            int j = this.lastReturned.getY();
                            int k = this.lastReturned.getZ();

                            if (i < blockpos1.getX()){
                                ++i;
                            }else if (j < blockpos1.getY()){
                                i = blockpos.getX();
                                ++j;
                            }else if (k < blockpos1.getZ()){
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.lastReturned = new BlockVector3(i, j, k);
                            return this.lastReturned;
                        }
                    }
                };
            }
        };
    }

    public void setAvoidsWater(boolean avoidsWater){
        this.nodeProcessor.setAvoidsWater(avoidsWater);
    }

    public boolean getAvoidsWater(){
        return this.nodeProcessor.getAvoidsWater();
    }

    public void setBreakDoors(boolean canBreakDoors){
        this.nodeProcessor.setBreakDoors(canBreakDoors);
    }

    public void setEnterDoors(boolean par1){
        this.nodeProcessor.setEnterDoors(par1);
    }

    public boolean getEnterDoors(){
        return this.nodeProcessor.getEnterDoors();
    }

    public void setCanSwim(boolean canSwim){
        this.nodeProcessor.setCanSwim(canSwim);
    }

    public boolean getCanSwim(){
        return this.nodeProcessor.getCanSwim();
    }

    public void setAvoidSun(boolean par1){
        this.shouldAvoidSun = par1;
    }
}