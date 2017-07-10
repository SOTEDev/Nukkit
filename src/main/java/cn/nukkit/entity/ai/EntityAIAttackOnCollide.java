package cn.nukkit.entity.ai;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.pathfinding.PathEntity;

public class EntityAIAttackOnCollide extends EntityAIBase{

    Level worldObj;
    protected EntityCreature attacker;
    int attackTick;
    double speedTowardsTarget;
    boolean longMemory;
    PathEntity entityPathEntity;
    Class <? extends Entity > classTarget;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;

    public EntityAIAttackOnCollide(EntityCreature creature, Class <? extends Entity > targetClass, double speedIn, boolean useLongMemory){
        this(creature, speedIn, useLongMemory);
        this.classTarget = targetClass;
    }

    public EntityAIAttackOnCollide(EntityCreature creature, double speedIn, boolean useLongMemory){
        this.attacker = creature;
        this.worldObj = creature.level;
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.setMutexBits(3);
    }

    public boolean shouldExecute(){
        EntityLiving entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null){
            return false;
        }else if (!entitylivingbase.isAlive()){
            return false;
        }else if (this.classTarget != null && !this.classTarget.isAssignableFrom(entitylivingbase.getClass())){
            return false;
        }else{
            this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
            return this.entityPathEntity != null;
        }
    }

    public boolean continueExecuting(){
        EntityLiving entitylivingbase = this.attacker.getAttackTarget();
        return entitylivingbase == null ? false : (!entitylivingbase.isAlive() ? false : (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistanceFromPosition(new BlockVector3((int)entitylivingbase.x, (int)entitylivingbase.y,(int) entitylivingbase.z))));
    }

    public void startExecuting(){
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
        this.delayCounter = 0;
    }

    public void resetTask(){
        this.attacker.getNavigator().clearPathEntity();
    }

    public void updateTask(){
        EntityLiving entitylivingbase = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
        double d0 = this.attacker.distanceSq(entitylivingbase.x, entitylivingbase.getBoundingBox().minY, entitylivingbase.z);
        double d1 = this.func_179512_a(entitylivingbase);
        --this.delayCounter;

        if ((this.longMemory || (this.delayCounter <= 0 && this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.distanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F))){
            this.targetX = entitylivingbase.x;
            this.targetY = entitylivingbase.getBoundingBox().minY;
            this.targetZ = entitylivingbase.z;
            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);

            if (d0 > 1024.0D){
                this.delayCounter += 10;
            }else if (d0 > 256.0D){
                this.delayCounter += 5;
            }

            if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget)){
                this.delayCounter += 15;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);

        if (d0 <= d1 && this.attackTick <= 0){
            this.attackTick = 20;

            this.attacker.attackEntityAsMob(entitylivingbase);
        }
    }

    protected double func_179512_a(EntityLiving attackTarget){
        return (double)(this.attacker.getWidth() * 2.0F * this.attacker.getWidth() * 2.0F + attackTarget.getWidth());
    }
}