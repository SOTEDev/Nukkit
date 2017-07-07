package cn.nukkit.entity.passive;

import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.PlaySound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public class EntitySheep extends EntityAnimal {

    public static final int NETWORK_ID = 13;

    public static final int DATA_COLOR_INFO = 16;

    public Random rand = new Random();

    public EntitySheep(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        if(!nbt.contains("Color")){
            nbt.putByte("Color", getRandomSheepColor(new Random()).getWoolData());
        }
        this.setDataProperty(new ByteEntityData(DATA_COLOR_INFO, getFleeceColor().getWoolData()));
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.9f; // No have information
        }
        return 1.3f;
    }

    @Override
    public float getEyeHeight() {
        if (isBaby()) {
            return 0.95f * 0.9f; // No have information
        }
        return 0.95f * getHeight();
    }

    public boolean interact(Player player){
        Item item = player.getInventory().getItemInHand();
        if (item != null && item.getId() == Item.SHEARS && !this.getSheared()/* && !this.isChild()*/){
            this.setSheared(true);
            int i = 1 + this.rand.nextInt(3);

            for (int j = 0; j < i; ++j){
                Vector3 motion = new Vector3();
                motion.y += (double)(this.rand.nextFloat() * 0.05F);
                motion.x += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                motion.z += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                this.level.dropItem(this, Item.get(Item.WOOL, this.getFleeceColor().getWoolData()), motion);
            }

            if (player.isSurvival()) {
                item.useOn((Entity) null);
                if (item.getDamage() >= item.getMaxDurability()) {
                    player.getInventory().setItemInHand(new ItemBlock(new BlockAir()));
                } else {
                    player.getInventory().setItemInHand(item);
                }
            }

            this.level.addSound(new PlaySound(this, "mob.sheep.shear", 1.0F, 1.0F));

        }



        return super.interact(player);

    }

    public static DyeColor getRandomSheepColor(Random random){
        int i = random.nextInt(100);
        return i < 5 ? DyeColor.BLACK : (i < 10 ? DyeColor.GRAY : (i < 15 ? DyeColor.LIGHT_GRAY : (i < 18 ? DyeColor.BROWN : (random.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE))));
    }

    public DyeColor getFleeceColor(){
        return DyeColor.getByWoolData(this.getDataPropertyByte(DATA_COLOR_INFO));
    }

    public void setFleeceColor(DyeColor dyeColor){
        this.setDataProperty(new ByteEntityData(DATA_COLOR_INFO, dyeColor.getWoolData()));
    }

    public boolean getSheared(){
        return this.getDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SHEARED);
    }

    public void setSheared(boolean sheared){
        this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SHEARED, sheared);
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public Item[] getDrops() {
        EntityDamageEvent ev = this.getLastDamageCause();
        int looting = ev instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent) ev).getDamager() instanceof Player ? ((Player)((EntityDamageByEntityEvent) ev).getDamager()).getInventory().getItemInHand().getEnchantmentLevel(Enchantment.ID_LOOTING) : 0 : 0;

        int i = this.rand.nextInt(2) + 1 + this.rand.nextInt(1 + looting);

        Item mutton;
        if (this.isOnFire()){
            mutton = Item.get(Item.COOKED_MUTTON, 0, 1);
        }else{
            mutton = Item.get(Item.RAW_MUTTON, 0, 1);
        }
        if(!this.getSheared()){
            return new Item[]{mutton, Item.get(Item.WOOL, this.getFleeceColor().getWoolData(), i)};
        }
        return new Item[]{mutton};
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);
    }
}
