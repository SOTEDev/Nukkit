package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.EntityEventPacket;

public class EntityFishingHook extends EntityProjectile {
    public static final int NETWORK_ID = 77;

    public int attractTimer = 100;
    public int coughtTimer = 0;
    public boolean damageRod = false;

    public boolean isCatched = false;
    public Entity catchEntity;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.1f;
    }

    @Override
    protected float getDrag() {
        return 0.05f;
    }

    public EntityFishingHook(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityFishingHook(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
        this.setDataProperty(new LongEntityData(Entity.DATA_OWNER_EID, this.shootingEntity.getId()), true);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);


        if(!isCatched){
            if(this.isCollidedVertically && this.isInsideOfWater()){
                this.motionX = 0;
                this.motionY += 0.01;
                this.motionZ = 0;
                this.motionChanged = true;
                hasUpdate = true;
            }else if(this.isCollided && this.keepMovement){
                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;
                this.motionChanged = true;
                this.keepMovement = false;
                hasUpdate = true;
            }
            if(this.attractTimer == 0 && (int)(Math.random() * 100) <= 30){
                this.coughtTimer = ((int)(Math.random() * 5) + 5) * 20;
                this.attractTimer = ((int)(Math.random() * 70) + 30) * 20;
                this.attractFish();
                if(this.shootingEntity instanceof Player) ((Player)this.shootingEntity).sendTip("A fish bites!");
            }else if(this.attractTimer > 0){
                this.attractTimer--;
            }
            if(this.coughtTimer > 0){
                this.coughtTimer--;
                this.fishBites();
            }
        }else{
            if(this.catchEntity instanceof Entity){
                this.setPosition(this.catchEntity.add(0, this.catchEntity.getEyeHeight(), 0));
                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;
                this.motionChanged = true;
                this.keepMovement = false;
            }
        }

        this.timing.stopTiming();

        return hasUpdate;
    }

    public void fishBites(){
        if(this.shootingEntity instanceof Player && this.isInsideOfWater()){
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.shootingEntity.getId();
            pk.event = EntityEventPacket.FISH_HOOK_HOOK;
            Server.broadcastPacket(this.shootingEntity.getViewers().values(), pk);
        }
    }

    public void attractFish(){
        if(this.shootingEntity instanceof Player && this.isInsideOfWater()){
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.shootingEntity.getId();
            pk.event = EntityEventPacket.FISH_HOOK_BUBBLE;
            Server.broadcastPacket(this.shootingEntity.getViewers().values(), pk);
        }
    }

    public boolean reelLine(){
        if(!this.isInsideOfWater()) return false;
        this.damageRod = false;

        if(this.shootingEntity instanceof Player && this.coughtTimer > 0){
            int[] fishes = new int[]{Item.RAW_FISH, Item.RAW_SALMON, Item.PUFFERFISH};
            int fish = (int)(Math.random() * fishes.length);
            Item item = Item.get(fishes[fish]);
            //TODO FishEvent
            //if(!ev.isCancelled()){
            Vector3 motion = new Vector3((this.shootingEntity.x - this.x), (this.shootingEntity.y - this.y), (this.shootingEntity.z - this.z));
            this.level.dropItem(this, item, motion);
            ((Player)this.shootingEntity).addExperience((int)(Math.random() * 5) + 1);
            this.damageRod = true;
            //}
        }

        if(this.shootingEntity instanceof Player){
            ((Player)this.shootingEntity).unlinkHookFromPlayer();
        }

        if(!this.closed){
            this.kill();
            this.close();//TODO Return motion
        }

        return this.damageRod;
    }

    public void onCatch(Entity entity){
        this.isCatched = true;
        this.catchEntity = entity;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.motionChanged = true;
        this.keepMovement = false;
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = EntityFishingHook.NETWORK_ID;
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }
}
