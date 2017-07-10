package cn.nukkit.entity.ai;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.math.Vector3;

public class EntityAIMoveTowardsTarget extends EntityAIBase{

    private EntityCreature theEntity;
    private EntityLiving targetEntity;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private double speed;
    private float maxTargetDistance;

    public EntityAIMoveTowardsTarget(EntityCreature creature, double speedIn, float targetMaxDistance){
        this.theEntity = creature;
        this.speed = speedIn;
        this.maxTargetDistance = targetMaxDistance;
        this.setMutexBits(1);
    }

    public boolean shouldExecute(){
        this.targetEntity = this.theEntity.getAttackTarget();

        if (this.targetEntity == null){
            return false;
        }else if (this.targetEntity.distanceSq(this.theEntity) > (double)(this.maxTargetDistance * this.maxTargetDistance)){
            return false;
        }else{
            Vector3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, new Vector3(this.targetEntity.x, this.targetEntity.y, this.targetEntity.z));

            if (vec3 == null){
                return false;
            }else{
                this.movePosX = vec3.x;
                this.movePosY = vec3.y;
                this.movePosZ = vec3.z;
                return true;
            }
        }
    }

    public boolean continueExecuting(){
        return !this.theEntity.getNavigator().noPath() && this.targetEntity.isAlive() && this.targetEntity.distanceSq(this.theEntity) < (double)(this.maxTargetDistance * this.maxTargetDistance);
    }

    public void resetTask(){
        this.targetEntity = null;
    }

    public void startExecuting(){
        this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    }
}