package cn.nukkit.item;

public class ItemHorseArmorLeather extends Item {
    public ItemHorseArmorLeather() {
        this(0, 1);
    }

    public ItemHorseArmorLeather(Integer meta) {
        this(meta, 1);
    }

    public ItemHorseArmorLeather(Integer meta, int count) {
        super(LEATHER_HORSE_ARMOR, meta, count, "Leather Horse Armor");
    }
<<<<<<< HEAD
=======

    @Override
    public int getMaxStackSize() {
        return 1;
    }
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
}
