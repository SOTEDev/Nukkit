package cn.nukkit.block;

import cn.nukkit.level.Level;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockWaterStill extends BlockWater {

    public BlockWaterStill() {
        super(0);
    }

    public BlockWaterStill(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STILL_WATER;
    }

    @Override
    public String getName() {
        return "Still Water";
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return new BlockWaterStill(meta);
    }

<<<<<<< HEAD
    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_SCHEDULED) {
            return super.onUpdate(type);
        }
        return 0;
    }
=======
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
}
