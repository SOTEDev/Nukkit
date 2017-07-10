package cn.nukkit.entity.ai;

import java.util.Random;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;

public class RandomPositionGenerator{

    private static Vector3 staticVector = new Vector3(0.0D, 0.0D, 0.0D);

    public static Vector3 findRandomTarget(EntityCreature entitycreatureIn, int xz, int y){
        return findRandomTargetBlock(entitycreatureIn, xz, y, (Vector3)null);
    }

    public static Vector3 findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vector3 targetVec3){
        staticVector = targetVec3.subtract(entitycreatureIn.x, entitycreatureIn.y, entitycreatureIn.z);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    public static Vector3 findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz, int y, Vector3 targetVec3){
        staticVector = (new Vector3(entitycreatureIn.x, entitycreatureIn.y, entitycreatureIn.z)).subtract(targetVec3);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    private static Vector3 findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, Vector3 targetVec3){
        Random random = entitycreatureIn.getRNG();
        boolean flag = false;
        int i = 0;
        int j = 0;
        int k = 0;
        float f = -99999.0F;
        boolean flag1;

        if (entitycreatureIn.hasHome()){
            double d0 = entitycreatureIn.getHomePosition().distanceSq((double)MathHelper.floor_double(entitycreatureIn.x), (double)MathHelper.floor_double(entitycreatureIn.y), (double)MathHelper.floor_double(entitycreatureIn.z)) + 4.0D;
            double d1 = (double)(entitycreatureIn.getMaximumHomeDistance() + (float)xz);
            flag1 = d0 < d1 * d1;
        }else{
            flag1 = false;
        }

        for (int j1 = 0; j1 < 10; ++j1){
            int l = random.nextInt(2 * xz + 1) - xz;
            int k1 = random.nextInt(2 * y + 1) - y;
            int i1 = random.nextInt(2 * xz + 1) - xz;

            if (targetVec3 == null || (double)l * targetVec3.x + (double)i1 * targetVec3.z >= 0.0D){
                if (entitycreatureIn.hasHome() && xz > 1){
                    BlockVector3 blockpos = entitycreatureIn.getHomePosition();

                    if (entitycreatureIn.x > (double)blockpos.getX()){
                        l -= random.nextInt(xz / 2);
                    }else{
                        l += random.nextInt(xz / 2);
                    }

                    if (entitycreatureIn.z > (double)blockpos.getZ()){
                        i1 -= random.nextInt(xz / 2);
                    }else{
                        i1 += random.nextInt(xz / 2);
                    }
                }

                l = l + MathHelper.floor_double(entitycreatureIn.x);
                k1 = k1 + MathHelper.floor_double(entitycreatureIn.y);
                i1 = i1 + MathHelper.floor_double(entitycreatureIn.z);
                BlockVector3 blockpos1 = new BlockVector3(l, k1, i1);

                if (!flag1 || entitycreatureIn.isWithinHomeDistanceFromPosition(blockpos1)){
                    float f1 = entitycreatureIn.getBlockPathWeight(blockpos1);

                    if (f1 > f){
                        f = f1;
                        i = l;
                        j = k1;
                        k = i1;
                        flag = true;
                    }
                }
            }
        }

        if (flag){
            return new Vector3((double)i, (double)j, (double)k);
        }else{
            return null;
        }
    }
}