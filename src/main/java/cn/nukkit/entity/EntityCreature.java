package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityCreature extends EntityLiving {

    private BlockVector3 homePosition = new BlockVector3();
    private float maximumHomeDistance = 999999999;//-1.0F;
    //private EntityAIBase aiBase = new EntityAIMoveTowardsRestriction(this, 1.0D);
    private boolean isMovementAITaskSet;

    public EntityCreature(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public float getBlockPathWeight(BlockVector3 pos){
        return 0.0F;
    }

    public boolean hasPath(){
        return !this.navigator.noPath();
    }

    public boolean isWithinHomeDistanceCurrentPosition(){
        return this.isWithinHomeDistanceFromPosition(new BlockVector3((int)this.x, (int)this.y, (int)this.z));
    }

    public boolean isWithinHomeDistanceFromPosition(BlockVector3 pos){
        return this.maximumHomeDistance == -1.0F ? true : this.homePosition.distanceSq(pos) < (double)(this.maximumHomeDistance * this.maximumHomeDistance);
    }

    public void setHomePosAndDistance(BlockVector3 pos, int distance){
        this.homePosition = pos;
        this.maximumHomeDistance = (float)distance;
    }

    public BlockVector3 getHomePosition(){
        return this.homePosition;
    }

    public float getMaximumHomeDistance(){
        return this.maximumHomeDistance;
    }

    public void detachHome(){
        this.maximumHomeDistance = -1.0F;
    }

    public boolean hasHome(){
        return this.maximumHomeDistance != -1.0F;
    }
}
