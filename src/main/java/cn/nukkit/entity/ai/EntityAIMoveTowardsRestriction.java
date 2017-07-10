package cn.nukkit.entity.ai;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

public class EntityAIMoveTowardsRestriction extends EntityAIBase{

    private EntityCreature theEntity;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private double movementSpeed;

    public EntityAIMoveTowardsRestriction(EntityCreature creatureIn, double speedIn){
        this.theEntity = creatureIn;
        this.movementSpeed = speedIn;
        this.setMutexBits(1);
    }

    public boolean shouldExecute(){
        if (this.theEntity.isWithinHomeDistanceCurrentPosition()){
            return false;
        }else{
            BlockVector3 blockpos = this.theEntity.getHomePosition();
            Vector3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, new Vector3((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()));

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
        return !this.theEntity.getNavigator().noPath();
    }

    public void startExecuting(){
        this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
    }
}