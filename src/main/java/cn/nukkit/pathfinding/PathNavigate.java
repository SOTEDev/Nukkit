package cn.nukkit.pathfinding;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;

public abstract class PathNavigate{

    protected EntityLiving theEntity;
    protected Level worldObj;
    protected PathEntity currentPath;
    protected double speed;
    private float pathSearchRange;
    private int totalTicks;
    private int ticksAtLastPos;
    private Vector3 lastPosCheck = new Vector3(0.0D, 0.0D, 0.0D);
    private float heightRequirement = 1.0F;
    private final PathFinder pathFinder;

    public PathNavigate(EntityLiving entitylivingIn, Level worldIn){
        this.theEntity = entitylivingIn;
        this.worldObj = worldIn;
        this.pathSearchRange = 25;//entitylivingIn.getAttribute("followRange");
        this.pathFinder = this.getPathFinder();
    }

    protected abstract PathFinder getPathFinder();

    public void setSpeed(double speedIn){
        this.speed = speedIn;
    }

    public float getPathSearchRange(){
        return this.pathSearchRange;
    }

    public final PathEntity getPathToXYZ(double x, double y, double z){
        return this.getPathToPos(new BlockVector3(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z)));
    }

    public PathEntity getPathToPos(BlockVector3 pos){
        if (!this.canNavigate()){
            return null;
        }else{
            float f = this.getPathSearchRange();
            BlockVector3 blockpos = new BlockVector3((int)this.theEntity.x, (int)this.theEntity.y, (int)this.theEntity.z);
            int i = (int)(f + 8.0F);
            PathEntity pathentity = this.pathFinder.createEntityPathTo(this.worldObj, this.theEntity, pos, f);
            return pathentity;
        }
    }

    public boolean tryMoveToXYZ(double x, double y, double z, double speedIn){
        PathEntity pathentity = this.getPathToXYZ((double)MathHelper.floor_double(x), (double)((int)y), (double)MathHelper.floor_double(z));
        return this.setPath(pathentity, speedIn);
    }

    public void setHeightRequirement(float jumpHeight){
        this.heightRequirement = jumpHeight;
    }

    public PathEntity getPathToEntityLiving(Entity entityIn){
        if (!this.canNavigate()){
            return null;
        }else{
            float f = this.getPathSearchRange();
            BlockVector3 blockpos = (new BlockVector3((int)this.theEntity.x, (int)this.theEntity.y, (int)this.theEntity.z)).getSide(BlockFace.UP);
            int i = (int)(f + 16.0F);
            PathEntity pathentity = this.pathFinder.createEntityPathTo(this.worldObj, this.theEntity, entityIn, f);
            return pathentity;
        }
    }

    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn){
        PathEntity pathentity = this.getPathToEntityLiving(entityIn);
        return pathentity != null ? this.setPath(pathentity, speedIn) : false;
    }

    public boolean setPath(PathEntity pathentityIn, double speedIn){
        if (pathentityIn == null){
            this.currentPath = null;
            return false;
        }else{
            if (!pathentityIn.isSamePath(this.currentPath)){
                this.currentPath = pathentityIn;
            }

            this.removeSunnyPath();

            if (this.currentPath.getCurrentPathLength() == 0){
                return false;
            }else{
                this.speed = speedIn;
                Vector3 vec3 = this.getEntityPosition();
                this.ticksAtLastPos = this.totalTicks;
                this.lastPosCheck = vec3;
                return true;
            }
        }
    }

    public PathEntity getPath(){
        return this.currentPath;
    }

    public void onUpdateNavigation(){
        ++this.totalTicks;

        if (!this.noPath()){
            if (this.canNavigate()){
                this.pathFollow();
            }else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()){
                Vector3 vec3 = this.getEntityPosition();
                Vector3 vec31 = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());

                if (vec3.y > vec31.y && !this.theEntity.onGround && MathHelper.floor_double(vec3.x) == MathHelper.floor_double(vec31.x) && MathHelper.floor_double(vec3.z) == MathHelper.floor_double(vec31.z)){
                    this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
                }
            }

            if (!this.noPath()){
                Vector3 vec32 = this.currentPath.getPosition(this.theEntity);

                if (vec32 != null){
                    AxisAlignedBB axisalignedbb1 = (new AxisAlignedBB(vec32.x, vec32.y, vec32.z, vec32.x, vec32.y, vec32.z)).expand(0.5D, 0.5D, 0.5D);
                    AxisAlignedBB[] list = this.worldObj.getCollisionCubes(this.theEntity, axisalignedbb1.addCoord(0.0D, -1.0D, 0.0D));
                    double d0 = -1.0D;
                    axisalignedbb1 = axisalignedbb1.offset(0.0D, 1.0D, 0.0D);

                    for (AxisAlignedBB axisalignedbb : list){
                        d0 = axisalignedbb.calculateYOffset(axisalignedbb1, d0);
                    }

                    this.theEntity.getMoveHelper().setMoveTo(vec32.x, vec32.y + d0, vec32.z, this.speed);
                }
            }
        }
    }

    protected void pathFollow(){
        Vector3 vec3 = this.getEntityPosition();
        int i = this.currentPath.getCurrentPathLength();

        for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j){
            if (this.currentPath.getPathPointFromIndex(j).yCoord != (int)vec3.y){
                i = j;
                break;
            }
        }

        float f = this.theEntity.getWidth() * this.theEntity.getWidth() * this.heightRequirement;

        for (int k = this.currentPath.getCurrentPathIndex(); k < i; ++k){
            Vector3 vec31 = this.currentPath.getVectorFromIndex(this.theEntity, k);

            if (vec3.distanceSq(vec31) < (double)f){
                this.currentPath.setCurrentPathIndex(k + 1);
            }
        }

        int j1 = MathHelper.ceiling_float_int((float)(this.theEntity.getWidth() + 0.5));
        int k1 = (int)this.theEntity.getHeight() + 1;
        int l = j1;

        for (int i1 = i - 1; i1 >= this.currentPath.getCurrentPathIndex(); --i1){
            if (this.isDirectPathBetweenPoints(vec3, this.currentPath.getVectorFromIndex(this.theEntity, i1), j1, k1, l)){
                this.currentPath.setCurrentPathIndex(i1);
                break;
            }
        }

        this.checkForStuck(vec3);
    }

    protected void checkForStuck(Vector3 positionVec3){
        if (this.totalTicks - this.ticksAtLastPos > 100){
            if (positionVec3.distanceSq(this.lastPosCheck) < 2.25D){
                this.clearPathEntity();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = positionVec3;
        }
    }

    public boolean noPath(){
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public void clearPathEntity(){
        this.currentPath = null;
    }

    protected abstract Vector3 getEntityPosition();

    protected abstract boolean canNavigate();

    protected boolean isInLiquid(){
        return this.theEntity.isInsideOfWater() || this.theEntity.isInsideOfLava();
    }

    protected void removeSunnyPath(){
    }

    protected abstract boolean isDirectPathBetweenPoints(Vector3 posVec31, Vector3 posVec32, int sizeX, int sizeY, int sizeZ);
}