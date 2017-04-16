package cn.nukkit.entity.passive;

import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.DyeColor;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public class EntityWolf extends EntityTameable{

    public static final int NETWORK_ID = 14;

    public Random rand = new Random();

    public EntityWolf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getLength() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.8f; // No have information
        }
        return 0.8f;
    }

    @Override
    public float getEyeHeight() {
        if (isBaby()) {
            return 0.8f * getHeight(); // No have information
        }
        return 0.8f * getHeight();
    }


    /**
     * @param player
     * @return
     */
    public boolean interact(Player player){
        Item item = player.getInventory().getItemInHand();
        if (this.isTamed()){
            if (item != null){
                if (item instanceof ItemEdible){
                    ItemEdible itemfood = (ItemEdible)item;
                    if (itemfood.isWolfsFavoriteMeat() && this.getHealth() < 20.0F){
                        if (!player.isCreative()){
                            item.count--;
                        }
                        //this.heal(item.getHealAmount());//TODO 
                        player.getInventory().setItemInHand(item);
                        player.getInventory().sendHeldItem(player);
                        return true;
                    }
                }else if (item.getId() == Item.DYE){
                    DyeColor dyeColor = DyeColor.getByDyeData(item.getDamage());
                    if (dyeColor.getDyeData() == this.getCollarColor().getDyeData()){
                        this.setCollarColor(dyeColor);
                        if (!player.isCreative()){
                            item.count--;
                        }
                        player.getInventory().setItemInHand(item);
                        player.getInventory().sendHeldItem(player);
                        return true;
                    }
                }
            }
            if (this.isOwner(player)){
                this.setSitting(!this.isSitting());
                //this.isJumping = false;
                this.setAttackTarget((Entity)null);
            }
        }else if (item != null && item.getId() == Item.BONE && !this.isAngry()){
            if (!player.isCreative()){
                item.count--;
            }
            player.getInventory().setItemInHand(item);
            player.getInventory().sendHeldItem(player);
            if (this.rand.nextInt(3) == 0){
                this.setTamed(true);
                this.setAttackTarget((Entity)null);
                this.setSitting(true);
                this.setHealth(20.0F);
                this.setOwnerName(player.getName());
                EntityEventPacket pk = new EntityEventPacket();
                pk.eid = this.getId();
                pk.event = EntityEventPacket.TAME_SUCCESS;
                Server.broadcastPacket(this.getViewers().values(), pk);
            }else{
                EntityEventPacket pk = new EntityEventPacket();
                pk.eid = this.getId();
                pk.event = EntityEventPacket.TAME_FAIL;
                Server.broadcastPacket(this.getViewers().values(), pk);
            }
            return true;
        }

        return super.interact(player);
    }

    public boolean isBreedingItem(Item item){
        return item == null ? false : (!(item instanceof ItemEdible) ? false : ((ItemEdible)item).isWolfsFavoriteMeat());
    }

    public boolean isAngry(){
        return this.getDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_ANGRY);
    }

    public void setAngry(boolean angry){
        this.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_ANGRY, angry);
    }

    public DyeColor getCollarColor(){
        return DyeColor.getByDyeData(this.getDataProperty(Entity.DATA_COLOUR).getType());
    }

    public void setCollarColor(DyeColor collarcolor){
        this.setDataProperty(new ByteEntityData(Entity.DATA_COLOUR, collarcolor.getDyeData()), true);
    }

    public void setAttackTarget(Entity entity){
        if(entity == null){
            this.setAngry(false);
        }else if(!isTamed()){
            this.setAngry(true);
        }
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{};
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        setMaxHealth(8);
    }
}
