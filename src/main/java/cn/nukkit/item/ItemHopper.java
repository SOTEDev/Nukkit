package cn.nukkit.item;

import cn.nukkit.block.BlockHopper;

/**
<<<<<<< HEAD
 * Created by Pub4Game on 03.07.2016.
=======
 * Created by CreeperFace on 13.5.2017.
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
 */
public class ItemHopper extends Item {

    public ItemHopper() {
<<<<<<< HEAD
        this(0, 1);
=======
        this(0);
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    }

    public ItemHopper(Integer meta) {
        this(meta, 1);
    }

    public ItemHopper(Integer meta, int count) {
<<<<<<< HEAD
        super(HOPPER, meta, count, "Hopper");
=======
        super(HOPPER, 0, count, "Hopper");
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
        this.block = new BlockHopper();
    }
}
