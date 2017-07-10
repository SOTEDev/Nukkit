package cn.nukkit.entity.ai;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;

public class EntityAIWatchClosest extends EntityAIBase{

    protected EntityLiving theWatcher;
    protected Entity closestEntity;
    protected float maxDistanceForPlayer;
    private int lookTime;
    private float chance;
    protected Class <? extends Entity > watchedClass;

    public EntityAIWatchClosest(EntityLiving entitylivingIn, Class <? extends Entity > watchTargetClass, float maxDistance){
        this.theWatcher = entitylivingIn;
        this.watchedClass = watchTargetClass;
        this.maxDistanceForPlayer = maxDistance;
        this.chance = 0.02F;
        this.setMutexBits(2);
    }

    public EntityAIWatchClosest(EntityLiving entitylivingIn, Class <? extends Entity > watchTargetClass, float maxDistance, float chanceIn){
        this.theWatcher = entitylivingIn;
        this.watchedClass = watchTargetClass;
        this.maxDistanceForPlayer = maxDistance;
        this.chance = chanceIn;
        this.setMutexBits(2);
    }

    public boolean shouldExecute(){
        if (this.theWatcher.getRNG().nextFloat() >= this.chance){
            return false;
        }else{
            if (this.theWatcher.getAttackTarget() != null){
                this.closestEntity = this.theWatcher.getAttackTarget();
            }

            if (this.watchedClass == Player.class){
                this.closestEntity = this.theWatcher.level.getClosestPlayerToEntity(this.theWatcher, (double)this.maxDistanceForPlayer);
            }else{
                this.closestEntity = this.theWatcher.level.findNearestEntityWithinAABB(this.watchedClass, this.theWatcher.getBoundingBox().clone().expand((double)this.maxDistanceForPlayer, 3.0D, (double)this.maxDistanceForPlayer), this.theWatcher);
            }

            return this.closestEntity != null;
        }
    }

    public boolean continueExecuting(){
        return !this.closestEntity.isAlive() ? false : (this.theWatcher.distanceSq(this.closestEntity) > (double)(this.maxDistanceForPlayer * this.maxDistanceForPlayer) ? false : this.lookTime > 0);
    }

    public void startExecuting(){
        this.lookTime = 40 + this.theWatcher.getRNG().nextInt(40);
    }

    public void resetTask(){
        this.closestEntity = null;
    }

    public void updateTask(){
        this.theWatcher.getLookHelper().setLookPosition(this.closestEntity.x, this.closestEntity.y + (double)this.closestEntity.getEyeHeight(), this.closestEntity.z, 10.0F, (float)this.theWatcher.getVerticalFaceSpeed());
        --this.lookTime;
    }
}