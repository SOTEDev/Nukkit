package cn.nukkit.entity.ai;

import cn.nukkit.entity.EntityLiving;
import cn.nukkit.pathfinding.PathNavigateGround;

public class EntityAISwimming extends EntityAIBase{

    private EntityLiving theEntity;

    public EntityAISwimming(EntityLiving entitylivingIn){
        this.theEntity = entitylivingIn;
        this.setMutexBits(4);
        ((PathNavigateGround)entitylivingIn.getNavigator()).setCanSwim(true);
    }

    public boolean shouldExecute(){
        return this.theEntity.isInsideOfWater() || this.theEntity.isInsideOfLava();
    }

    public void updateTask(){
        if (this.theEntity.getRNG().nextFloat() < 0.8F){
            this.theEntity.getJumpHelper().setJumping();
        }
    }
}