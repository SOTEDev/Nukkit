package cn.nukkit.item;

public class ItemHorseArmorGold extends Item {
    public ItemHorseArmorGold() {
        this(0, 0);
    }

    public ItemHorseArmorGold(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorGold(Integer meta, int count) {
        super(GOLD_HORSE_ARMOR, meta, count, "Gold Horse Armor");
    }
<<<<<<< HEAD
=======

    @Override
    public int getMaxStackSize() {
        return 1;
    }
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
}
