package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityFireball extends EntityProjectile {

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
        return 1.0f;
    }

    @Override
    public float getHeight() {
        return 1.0f;
    }

    @Override
    protected float getGravity() {
        return 0.0f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    public EntityFireball(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityFireball(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source instanceof EntityDamageByEntityEvent){
            Entity entity = ((EntityDamageByEntityEvent)source).getDamager();
            this.motionX = -Math.sin(entity.yaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI);
            this.motionY = -Math.sin(entity.pitch / 180 * Math.PI);
            this.motionZ = Math.cos(entity.yaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI);
        }else if(source instanceof EntityDamageByChildEntityEvent){
            Entity entity = ((EntityDamageByChildEntityEvent)source).getDamager();
            this.motionX = -Math.sin(entity.yaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI);
            this.motionY = -Math.sin(entity.pitch / 180 * Math.PI);
            this.motionZ = Math.cos(entity.yaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI);
        }
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    @Override
    public void spawnTo(Player player) {
    }
}
