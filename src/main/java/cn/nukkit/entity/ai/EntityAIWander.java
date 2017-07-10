package cn.nukkit.entity.ai;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.math.Vector3;

public class EntityAIWander extends EntityAIBase{

    private EntityCreature entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private double speed;
    private int executionChance;
    private boolean mustUpdate;

    public EntityAIWander(EntityCreature creatureIn, double speedIn){
        this(creatureIn, speedIn, 120);
    }

    public EntityAIWander(EntityCreature creatureIn, double speedIn, int chance){
        this.entity = creatureIn;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setMutexBits(1);
    }

    public boolean shouldExecute(){
        if (!this.mustUpdate){
            //if (this.entity.getAge() >= 100){
            //    return false;
            //}

            if (this.entity.getRNG().nextInt(this.executionChance) != 0){
                return false;
            }
        }

        Vector3 vec3 = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);

        if (vec3 == null){
            return false;
        }else{
            System.out.println("found target position");
            this.xPosition = vec3.x;
            this.yPosition = vec3.y;
            this.zPosition = vec3.z;
            this.mustUpdate = false;
            return true;
        }
    }

    public boolean continueExecuting(){
        return !this.entity.getNavigator().noPath();
    }

    public void startExecuting(){
    	System.out.println("start");
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    public void makeUpdate(){
        this.mustUpdate = true;
    }

    public void setExecutionChance(int newchance){
        this.executionChance = newchance;
    }
}