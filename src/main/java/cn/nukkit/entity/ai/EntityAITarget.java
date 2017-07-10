package cn.nukkit.entity.ai;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.math.MathHelper;
import cn.nukkit.pathfinding.PathEntity;
import cn.nukkit.pathfinding.PathPoint;

public abstract class EntityAITarget extends EntityAIBase{

    protected final EntityCreature taskOwner;
    protected boolean shouldCheckSight;
    private boolean nearbyOnly;
    private int targetSearchStatus;
    private int targetSearchDelay;
    private int targetUnseenTicks;

    public EntityAITarget(EntityCreature creature, boolean checkSight){
        this(creature, checkSight, false);
    }

    public EntityAITarget(EntityCreature creature, boolean checkSight, boolean onlyNearby){
        this.taskOwner = creature;
        this.shouldCheckSight = checkSight;
        this.nearbyOnly = onlyNearby;
    }

    public boolean continueExecuting(){
        EntityLiving entitylivingbase = this.taskOwner.getAttackTarget();

        if (entitylivingbase == null){
            return false;
        }else if (!entitylivingbase.isAlive()){
            return false;
        }else{
            //Team team = this.taskOwner.getTeam();
            //Team team1 = entitylivingbase.getTeam();

            //if (team != null && team1 == team){
            //    return false;
            //}else{
                double d0 = this.getTargetDistance();

                if (this.taskOwner.distanceSq(entitylivingbase) > d0 * d0){
                    return false;
                }else{
                    if (this.shouldCheckSight){
                        this.targetUnseenTicks = 0;
                    }

                    return !(entitylivingbase instanceof Player) || ((Player)entitylivingbase).getGamemode() != 1;
                }
            //}
        }
    }

    protected double getTargetDistance(){
        Attribute attribute = this.taskOwner.getAttributeMap().getAttribute(Attribute.FOLLOW_RANGE);
        return attribute == null ? 16.0D : attribute.getValue();
    }

    public void startExecuting(){
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.targetUnseenTicks = 0;
    }

    public void resetTask(){
        this.taskOwner.setAttackTarget((EntityLiving)null);
    }

    public static boolean isSuitableTarget(EntityLiving attacker, EntityLiving target, boolean includeInvincibles, boolean checkSight){
        if (target == null){
            return false;
        }else if (target == attacker){
            return false;
        }else if (!target.isAlive()){
            return false;
        }else{
            //Team team = attacker.getTeam();
            //Team team1 = target.getTeam();

            //if (team != null && team1 == team){
            //    return false;
            //}else{
                /*if (attacker instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)attacker).getOwnerId())){
                    if (target instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId().equals(((IEntityOwnable)target).getOwnerId()))
                    {
                        return false;
                    }

                    if (target == ((IEntityOwnable)attacker).getOwner())
                    {
                        return false;
                    }
                }else if (target instanceof EntityPlayer && !includeInvincibles && ((EntityPlayer)target).capabilities.disableDamage){
                    return false;
                }*/

                return !checkSight;
            //}
        }
    }

    protected boolean isSuitableTarget(EntityLiving target, boolean includeInvincibles){
        if (!isSuitableTarget(this.taskOwner, target, includeInvincibles, this.shouldCheckSight)){
            return false;
        //}else if (!this.taskOwner.isWithinHomeDistanceFromPosition(new BlockVector3((int)target.x, (int)target.y, (int)target.z))){
        //    return false;
        }else{
            if (this.nearbyOnly){
                if (--this.targetSearchDelay <= 0){
                    this.targetSearchStatus = 0;
                }

                if (this.targetSearchStatus == 0){
                    this.targetSearchStatus = this.canEasilyReach(target) ? 1 : 2;
                }

                if (this.targetSearchStatus == 2){
                    return false;
                }
            }

            return true;
        }
    }

    private boolean canEasilyReach(EntityLiving p_75295_1_){
        this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
        PathEntity pathentity = this.taskOwner.getNavigator().getPathToEntityLiving(p_75295_1_);

        if (pathentity == null){
            return false;
        }else{
            PathPoint pathpoint = pathentity.getFinalPathPoint();

            if (pathpoint == null){
                return false;
            }else{
                int i = pathpoint.xCoord - MathHelper.floor_double(p_75295_1_.x);
                int j = pathpoint.zCoord - MathHelper.floor_double(p_75295_1_.z);
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }
}