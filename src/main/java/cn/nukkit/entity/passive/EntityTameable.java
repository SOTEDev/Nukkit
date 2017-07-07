package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityOwnable;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

/**
 * Author: BeYkeRYkt
 * Nukkit Project
 */
public abstract class EntityTameable extends EntityAnimal implements EntityOwnable {

    public static final int DATA_TAMED_FLAG = 16;
    public static final int DATA_OWNER_NAME = 17;

    public EntityTameable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (getDataProperty(DATA_TAMED_FLAG) == null) {
            setDataProperty(new ByteEntityData(DATA_TAMED_FLAG, (byte) 0));
        }

        if (getDataProperty(DATA_OWNER_NAME) == null) {
            setDataProperty(new StringEntityData(DATA_OWNER_NAME, ""));
        }

        String ownerName = "";

        if (namedTag != null) {
            if (namedTag.contains("Owner")) {
                ownerName = namedTag.getString("Owner");
            }

            if (ownerName.length() > 0) {
                this.setOwnerName(ownerName);
                this.setTamed(true);
            }

            this.setSitting(namedTag.getBoolean("Sitting"));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.getOwnerName() == null) {
            namedTag.putString("Owner", "");
        } else {
            namedTag.putString("Owner", getOwnerName());
        }

        namedTag.putBoolean("Sitting", isSitting());
    }

    @Override
    public String getOwnerName() {
        return getDataPropertyString(DATA_OWNER_NAME);
    }

    @Override
    public void setOwnerName(String playerName) {
        setDataProperty(new StringEntityData(DATA_OWNER_NAME, playerName));
        setDataProperty(new LongEntityData(DATA_OWNER_EID, getOwner().getId()));
    }

    @Override
    public Player getOwner() {
        return getServer().getPlayer(getOwnerName());
    }

    public boolean isOwner(Player player){
        return player.equals(this.getOwner());
    }

    @Override
    public String getName() {
        return getNameTag();
    }

    public boolean isTamed() {
        return (getDataPropertyByte(DATA_TAMED_FLAG) & 4) != 0;
    }

    public void setTamed(boolean flag) {
        int var = getDataPropertyByte(DATA_TAMED_FLAG); // ?

        if (flag) {
            setDataProperty(new ByteEntityData(DATA_TAMED_FLAG, (byte) (var | 4)));
            if(this instanceof EntityWolf){
                ((EntityWolf)this).setCollarColor(DyeColor.RED);
            }
        } else {
            setDataProperty(new ByteEntityData(DATA_TAMED_FLAG, (byte) (var & -5)));
        }
    }

    public boolean isSitting() {
        return getDataFlag(DATA_FLAGS, DATA_FLAG_SITTING);
    }

    public void setSitting(boolean flag) {
        this.setDataFlag(DATA_FLAGS,  DATA_FLAG_SITTING, flag);
    }
}
