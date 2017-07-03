package cn.nukkit.block;

<<<<<<< HEAD
import cn.nukkit.item.Item;
=======
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
import cn.nukkit.item.ItemTool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockIcePacked extends BlockIce {

    public BlockIcePacked() {
        this(0);
    }

    public BlockIcePacked(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return PACKED_ICE;
    }

    @Override
    public String getName() {
        return "Packed Ice";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int onUpdate(int type) {
        return 0; //not being melted
    }

    @Override
<<<<<<< HEAD
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), 0, 1}
        };
=======
    public boolean canHarvestWithHand() {
        return false;
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    }
}
