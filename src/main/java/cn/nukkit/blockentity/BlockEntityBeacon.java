package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityBeacon extends BlockEntitySpawnable {

    public BlockEntityBeacon(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        if (!namedTag.contains("Lock")) {
            namedTag.putString("Lock", "");
        }

        if (!namedTag.contains("Levels")) {
            namedTag.putInt("Levels", 0);
        }

        if (!namedTag.contains("Primary")) {
            namedTag.putInt("Primary", 0);
        }

        if (!namedTag.contains("Secondary")) {
            namedTag.putInt("Secondary", 0);
        }

        scheduleUpdate();
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = getBlock().getId();
        return blockID == Block.BEACON;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.BEACON)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putString("Lock", this.namedTag.getString("Lock"))
                .putInt("Levels", this.namedTag.getInt("Levels"))
                .putInt("Primary", this.namedTag.getInt("Primary"))
                .putInt("Secondary", this.namedTag.getInt("Secondary"));
    }

    private long currentTick = 0;

    @Override
    public boolean onUpdate() {
        //Only check every 100 ticks
<<<<<<< HEAD
        if(currentTick++ % 100 != 0) {
=======
        if (currentTick++ % 100 != 0) {
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
            return true;
        }

        setPowerLevel(calculatePowerLevel());

        return true;
    }

    private static final int POWER_LEVEL_MAX = 4;

    private int calculatePowerLevel() {
        int tileX = getFloorX();
        int tileY = getFloorY();
        int tileZ = getFloorZ();

        //The power level that we're testing for
<<<<<<< HEAD
        for(int powerLevel = 1; powerLevel <= POWER_LEVEL_MAX; powerLevel++) {
=======
        for (int powerLevel = 1; powerLevel <= POWER_LEVEL_MAX; powerLevel++) {
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
            int queryY = tileY - powerLevel; //Layer below the beacon block

            for (int queryX = tileX - powerLevel; queryX <= tileX + powerLevel; queryX++) {
                for (int queryZ = tileZ - powerLevel; queryZ <= tileZ + powerLevel; queryZ++) {

                    int testBlockId = level.getBlockIdAt(queryX, queryY, queryZ);
                    if (
<<<<<<< HEAD
                        testBlockId != Block.IRON_BLOCK &&
                        testBlockId != Block.GOLD_BLOCK &&
                        testBlockId != Block.EMERALD_BLOCK &&
                        testBlockId != Block.DIAMOND_BLOCK
                    ) {
=======
                            testBlockId != Block.IRON_BLOCK &&
                                    testBlockId != Block.GOLD_BLOCK &&
                                    testBlockId != Block.EMERALD_BLOCK &&
                                    testBlockId != Block.DIAMOND_BLOCK
                            ) {
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
                        return powerLevel - 1;
                    }

                }
            }
        }

        return POWER_LEVEL_MAX;
    }

    public int getPowerLevel() {
        return namedTag.getInt("Level");
    }

    public void setPowerLevel(int level) {
        int currentLevel = getPowerLevel();
<<<<<<< HEAD
        if(level != currentLevel) {
=======
        if (level != currentLevel) {
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
            namedTag.putInt("Level", level);
            chunk.setChanged();
            this.spawnToAll();
        }
    }
}
