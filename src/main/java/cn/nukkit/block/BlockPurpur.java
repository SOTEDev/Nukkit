package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
<<<<<<< HEAD
import cn.nukkit.item.ItemTool;
=======
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e

public class BlockPurpur extends BlockSolid {

    public static final int PURPUR_NORMAL = 0;
    public static final int PURPUR_PILLAR = 2;

    public BlockPurpur() {
        this(0);
    }

    public BlockPurpur(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Purpur Block",
                "",
                "Purpur Pillar",
                ""
        };

        return names[this.meta & 0x03];
    }

    @Override
    public int getId() {
        return PURPUR_BLOCK;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
<<<<<<< HEAD
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
=======
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
        if (this.meta != PURPUR_NORMAL) {
            short[] faces = new short[]{
                    0,
                    0,
                    0b1000,
                    0b1000,
                    0b0100,
                    0b0100
            };

<<<<<<< HEAD
            this.meta = ((this.meta & 0x03) | faces[face]);
=======
            this.meta = ((this.meta & 0x03) | faces[face.getIndex()]);
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
<<<<<<< HEAD
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.PURPUR_BLOCK, this.meta & 0x03, 1}
            };
        } else {
            return new int[0][0];
        }
    }
=======
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockPurpur(), this.meta & 0x03, 1);
    }
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
}
