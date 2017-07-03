package cn.nukkit.block;

/**
 * Created by Pub4Game on 21.02.2016.
 */
public class BlockSlime extends BlockSolid {

    public BlockSlime() {
        this(0);
    }

    public BlockSlime(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public String getName() {
        return "Slime Block";
    }

    @Override
    public int getId() {
        return SLIME_BLOCK;
    }

    @Override
    public double getResistance() {
        return 0;
<<<<<<< HEAD
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.SLIME_BLOCK, 0, 1}
        };
=======
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    }
}
