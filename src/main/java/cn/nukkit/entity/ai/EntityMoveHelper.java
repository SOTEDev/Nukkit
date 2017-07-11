package cn.nukkit.entity.ai;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.math.MathHelper;

public class EntityMoveHelper{

    protected EntityLiving entity;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected double speed;
    protected boolean update;

    public EntityMoveHelper(EntityLiving entitylivingIn){
        this.entity = entitylivingIn;
        this.posX = entitylivingIn.x;
        this.posY = entitylivingIn.y;
        this.posZ = entitylivingIn.z;
    }

    public boolean isUpdating(){
        return this.update;
    }

    public double getSpeed(){
        return this.speed;
    }

    public void setMoveTo(double x, double y, double z, double speedIn){
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.speed = speedIn;
        this.update = true;
    }

    public void onUpdateMoveHelper(){
        this.entity.setMoveForward(0.0F);

        if (this.update){
            this.update = false;
            int i = MathHelper.floor_double(this.entity.getBoundingBox().minY + 0.5D);
            double d0 = this.posX - this.entity.x;
            double d1 = this.posZ - this.entity.z;
            double d2 = this.posY - (double)i;
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;

            if (d3 >= 2.500000277905201E-7D){
                float f = (float)(MathHelper.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
                this.entity.yaw = this.limitAngle((float)this.entity.yaw, f, 30.0F);
                this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getAttributeMap().getAttribute(Attribute.MOVEMENT_SPEED).getValue()));

                if (d2 > 0.0D && d0 * d0 + d1 * d1 < 1.0D){
                    this.entity.getJumpHelper().setJumping();
                }
            }
        }
    }

    protected float limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_){
        float f = MathHelper.wrapAngleTo180_float(p_75639_2_ - p_75639_1_);

        if (f > p_75639_3_){
            f = p_75639_3_;
        }

        if (f < -p_75639_3_){
            f = -p_75639_3_;
        }

        float f1 = p_75639_1_ + f;

        if (f1 < 0.0F){
            f1 += 360.0F;
        }else if (f1 > 360.0F){
            f1 -= 360.0F;
        }

        return f1;
    }

    public double getX(){
        return this.posX;
    }

    public double getY(){
        return this.posY;
    }

    public double getZ(){
        return this.posZ;
    }
}