package cn.nukkit.item;

public class ItemHorseArmorDiamond extends Item {
    public ItemHorseArmorDiamond() {
        this(0, 0);
    }

    public ItemHorseArmorDiamond(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorDiamond(Integer meta, int count) {
        super(DIAMOND_HORSE_ARMOR, meta, count, "Diamond Horse Armor");
    }
<<<<<<< HEAD
=======

    @Override
    public int getMaxStackSize() {
        return 1;
    }
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
}
