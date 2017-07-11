package cn.nukkit.entity;

import cn.nukkit.math.MathHelper;

public class EntityBodyHelper{

    private EntityLiving theLiving;
    private int rotationTickCounter;
    private float prevRenderYawHead;

    public EntityBodyHelper(EntityLiving p_i1611_1_){
        this.theLiving = p_i1611_1_;
    }

    public void updateRenderAngles(){
        double d0 = this.theLiving.x - this.theLiving.lastX;
        double d1 = this.theLiving.z - this.theLiving.lastZ;

        if (d0 * d0 + d1 * d1 > 2.500000277905201E-7D){
            this.theLiving.renderYawOffset = (float)this.theLiving.yaw;
            this.theLiving.headYaw = this.computeAngleWithBound(this.theLiving.renderYawOffset, (float)this.theLiving.headYaw, 75.0F);
            this.prevRenderYawHead = (float)this.theLiving.headYaw;
            this.rotationTickCounter = 0;
        }else{
            float f = 75.0F;

            if (Math.abs(this.theLiving.headYaw - this.prevRenderYawHead) > 15.0F){
                this.rotationTickCounter = 0;
                this.prevRenderYawHead = (float)this.theLiving.headYaw;
            }else{
                ++this.rotationTickCounter;
                int i = 10;

                if (this.rotationTickCounter > 10){
                    f = Math.max(1.0F - (float)(this.rotationTickCounter - 10) / 10.0F, 0.0F) * 75.0F;
                }
            }

            this.theLiving.renderYawOffset = this.computeAngleWithBound((float)this.theLiving.headYaw, this.theLiving.renderYawOffset, f);
        }
    }

    private float computeAngleWithBound(float p_75665_1_, float p_75665_2_, float p_75665_3_){
        float f = MathHelper.wrapAngleTo180_float(p_75665_1_ - p_75665_2_);

        if (f < -p_75665_3_){
            f = -p_75665_3_;
        }

        if (f >= p_75665_3_){
            f = p_75665_3_;
        }

        return p_75665_1_ - f;
    }
}