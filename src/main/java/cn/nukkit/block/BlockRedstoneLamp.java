package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

/**
 * @author Nukkit Project Team
 */
public class BlockRedstoneLamp extends BlockSolid {

    public BlockRedstoneLamp(int meta) {
        super(meta);
    }

    public BlockRedstoneLamp() {
        this(0);
    }

    @Override
    public String getName() {
        return "Redstone Lamp";
    }

    @Override
    public int getId() {
        return REDSTONE_LAMP;
    }

    @Override
    public double getHardness() {
        return 0.3D;
    }

    @Override
    public double getResistance() {
        return 1.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.level.isBlockPowered(this)) {
            this.level.setBlock(this, new BlockRedstoneLampLit(), false, true);
        } else {
            this.level.setBlock(this, this, false, true);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
<<<<<<< HEAD
        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) && this.getNeighborPowerLevel() > 0) {
            int level = this.getPowerLevel();
            Redstone.deactive(this, level);
            this.getLevel().setBlock(this, new BlockRedstoneLampLit());
=======
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            if (this.level.isBlockPowered(this)) {
                this.level.setBlock(this, new BlockRedstoneLampLit(), false, false);
                return 1;
            }
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemBlock(new BlockRedstoneLamp())
        };
    }

}
