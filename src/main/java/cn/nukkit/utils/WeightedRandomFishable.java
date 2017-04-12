package cn.nukkit.utils;

import java.util.Random;

import cn.nukkit.item.Item;

public class WeightedRandomFishable extends WeightedRandom.Item{

    private final Item returnItem;
    private float maxDamagePercent;
    private boolean enchantable;

    public WeightedRandomFishable(Item returnItemIn, int itemWeightIn){
        super(itemWeightIn);
        this.returnItem = returnItemIn;
    }

    public Item getItemStack(Random random){
        Item itemstack = this.returnItem.clone();

        if (this.maxDamagePercent > 0.0F){
            int i = (int)(this.maxDamagePercent * (float)this.returnItem.getMaxDurability());
            int j = itemstack.getMaxDurability() - random.nextInt(random.nextInt(i) + 1);

            if (j > i){
                j = i;
            }

            if (j < 1){
                j = 1;
            }

            itemstack.setDamage(j);
        }

        /*if (this.enchantable){
            addRandomEnchantment(random, itemstack, 30);//TODO Enchantment
        }*/

        return itemstack;
    }

    /*public static Item addRandomEnchantment(Random p_77504_0_, Item p_77504_1_, int p_77504_2_){
        List<Enchantment> list = buildEnchantmentList(p_77504_0_, p_77504_1_, p_77504_2_);
        boolean flag = p_77504_1_.getId() == Item.BOOK;

        if (flag){
            p_77504_1_ = Item.get(Item.ENCHANTED_BOOK);
        }

        if (list != null){
            for (Enchantment enchantmentdata : list){
            	p_77504_1_.addEnchantment(enchantmentdata);
            }
        }
        return p_77504_1_;
    }

    public static List<Enchantment> buildEnchantmentList(Random randomIn, Item item, int p_77513_2_){
        int i = item.getItemEnchantability();

        if (i <= 0)

        {

            return null;

        }else{
            i = i / 2;
            i = 1 + randomIn.nextInt((i >> 1) + 1) + randomIn.nextInt((i >> 1) + 1);
            int j = i + p_77513_2_;
            float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
            int k = (int)((float)j * (1.0F + f) + 0.5F);

            if (k < 1){
                k = 1;
            }

            List<Enchantment> list = null;
            Map<Integer, Enchantment> map = mapEnchantmentData(k, itemStackIn);

            if (map != null && !map.isEmpty()){
                Enchantment enchantmentdata = (Enchantment)WeightedRandom.getRandomItem(randomIn, map.values());

                if (enchantmentdata != null){

                    list = Lists.<Enchantment>newArrayList();
                    list.add(enchantmentdata);

                    for (int l = k; randomIn.nextInt(50) <= l; l >>= 1){
                        Iterator<Integer> iterator = map.keySet().iterator();

                        while (iterator.hasNext()){
                            Integer integer = (Integer)iterator.next();
                            boolean flag = true;

                            for (Enchantment enchantmentdata1 : list){
                                if (!enchantmentdata1.enchantmentobj.canApplyTogether(Enchantment.get(integer.intValue()))){
                                    flag = false;
                                    break;
                                }
                            }
                            if (!flag){
                                iterator.remove();
                            }
                        }
                        if (!map.isEmpty()){
                            Enchantment enchantmentdata2 = (Enchantment)WeightedRandom.getRandomItem(randomIn, map.values());
                            list.add(enchantmentdata2);
                        }
                    }
                }
            }

            return list;
        }
    }*/

    public WeightedRandomFishable setMaxDamagePercent(float maxDamagePercentIn){
        this.maxDamagePercent = maxDamagePercentIn;
        return this;
    }

    public WeightedRandomFishable setEnchantable(){
        this.enchantable = true;
        return this;
    }
}