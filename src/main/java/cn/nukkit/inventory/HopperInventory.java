package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityHopper;

/**
<<<<<<< HEAD
 * author: MagicDroidX
 * Nukkit Project
=======
 * Created by CreeperFace on 8.5.2017.
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
 */
public class HopperInventory extends ContainerInventory {

    public HopperInventory(BlockEntityHopper hopper) {
        super(hopper, InventoryType.HOPPER);
    }

    @Override
    public BlockEntityHopper getHolder() {
<<<<<<< HEAD
        return (BlockEntityHopper) this.holder;
=======
        return (BlockEntityHopper) super.getHolder();
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    }
}
