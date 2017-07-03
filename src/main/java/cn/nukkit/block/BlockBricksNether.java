package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockBricksNether extends BlockSolid {

    public BlockBricksNether() {
        this(0);
    }

    public BlockBricksNether(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Nether Brick";
    }

    @Override
    public int getId() {
        return NETHER_BRICKS;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
<<<<<<< HEAD
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.NETHER_BRICKS, 0, 1}
            };
        } else {
            return new int[0][0];
=======
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(Item.NETHER_BRICKS, 0, 1)
            };
        } else {
            return new Item[0];
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
}
