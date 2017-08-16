package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntitySmallFireball extends EntityProjectile {
    public static final int NETWORK_ID = 94;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySmallFireball(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntitySmallFireball(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public void onHit(){
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
        kill();
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = EntitySmallFireball.NETWORK_ID;
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
