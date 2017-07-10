package cn.nukkit.entity.ai;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.math.MathHelper;

public class EntityLookHelper{

    private EntityLiving entity;
    private float deltaLookYaw;
    private float deltaLookPitch;
    private boolean isLooking;
    private double posX;
    private double posY;
    private double posZ;

    public EntityLookHelper(EntityLiving entitylivingIn){
        this.entity = entitylivingIn;
    }

    public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch){
        this.posX = entityIn.x;

        if (entityIn instanceof EntityLiving){
            this.posY = entityIn.y + (double)entityIn.getEyeHeight();
        }else{
            this.posY = (entityIn.getBoundingBox().minY + entityIn.getBoundingBox().maxY) / 2.0D;
        }

        this.posZ = entityIn.z;
        this.deltaLookYaw = deltaYaw;
        this.deltaLookPitch = deltaPitch;
        this.isLooking = true;
    }

    public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch){
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.deltaLookYaw = deltaYaw;
        this.deltaLookPitch = deltaPitch;
        this.isLooking = true;
    }

    public void onUpdateLook(){
        this.entity.pitch = 0.0F;

        if (this.isLooking){
            this.isLooking = false;
            double d0 = this.posX - this.entity.x;
            double d1 = this.posY - (this.entity.y + (double)this.entity.getEyeHeight());
            double d2 = this.posZ - this.entity.z;
            double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);
            float f = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            float f1 = (float)(-(MathHelper.atan2(d1, d3) * 180.0D / Math.PI));
            this.entity.pitch = this.updateRotation((float)this.entity.pitch, f1, this.deltaLookPitch);
            this.entity.headYaw = this.updateRotation((float)this.entity.headYaw, f, this.deltaLookYaw);
        }else{
            this.entity.headYaw = this.updateRotation((float)this.entity.headYaw, this.entity.renderYawOffset, 10.0F);
        }

        float f2 = MathHelper.wrapAngleTo180_float((float)this.entity.headYaw - this.entity.renderYawOffset);

        if (!this.entity.getNavigator().noPath()){
            if (f2 < -75.0F){
                this.entity.headYaw = this.entity.renderYawOffset - 75.0F;
            }

            if (f2 > 75.0F){
                this.entity.headYaw = this.entity.renderYawOffset + 75.0F;
            }
        }
    }

    private float updateRotation(float p_75652_1_, float p_75652_2_, float p_75652_3_){
        float f = MathHelper.wrapAngleTo180_float(p_75652_2_ - p_75652_1_);

        if (f > p_75652_3_){
            f = p_75652_3_;
        }

        if (f < -p_75652_3_){
            f = -p_75652_3_;
        }

        return p_75652_1_ + f;
    }

    public boolean getIsLooking(){
        return this.isLooking;
    }

    public double getLookPosX(){
        return this.posX;
    }

    public double getLookPosY(){
        return this.posY;
    }

    public double getLookPosZ(){
        return this.posZ;
    }
}