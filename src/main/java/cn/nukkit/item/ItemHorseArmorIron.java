package cn.nukkit.item;

public class ItemHorseArmorIron extends Item {
    public ItemHorseArmorIron() {
        this(0, 0);
    }

    public ItemHorseArmorIron(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorIron(Integer meta, int count) {
        super(IRON_HORSE_ARMOR, meta, count, "Iron Horse Armor");
    }
<<<<<<< HEAD
=======

    @Override
    public int getMaxStackSize() {
        return 1;
    }
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
}
