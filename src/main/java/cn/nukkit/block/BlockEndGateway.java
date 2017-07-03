<<<<<<< HEAD
package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityPortalEnterEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockEndGateway extends BlockSolid {

    public BlockEndGateway() {
        this(0);
    }

    public BlockEndGateway(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "End Gateway";
    }

    @Override
    public int getId() {
        return END_GATEWAY;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }


    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

}
=======
package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 *
 * @author PikyCZ
 */
public class BlockEndGateway extends BlockSolid {

    public BlockEndGateway() {
        this(0);
    }

    public BlockEndGateway(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "End Gateway";
    }

    @Override
    public int getId() {
        return END_GATEWAY;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

}
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
