package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.CriticalParticle;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.level.particle.MobSpellInstantaneousParticle;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityArrow extends EntityProjectile {
    public static final int NETWORK_ID = 80;

    public static final int DATA_SOURCE_ID = 17;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getLength() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public float getGravity() {
        return 0.05f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    protected float gravity = 0.05f;
    protected float drag = 0.01f;

    public int particleType = 0;

    public int[] rgba = new int[4];

    public EntityArrow(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);
        this.setCritical(critical);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.damage = namedTag.contains("damage") ? namedTag.getDouble("damage") : 2;
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public void setCritical(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CRITICAL, value);
    }

    public boolean isCritical() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_CRITICAL);
    }

    @Override
    public int getResultDamage() {
        int base = super.getResultDamage();

        if (this.isCritical()) {
            base += this.level.rand.nextInt(base / 2 + 2);
        }

        return base;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.onGround || this.hadCollision) {
            this.setCritical(false);
        }
        if(this.particleType == 1){
            NukkitRandom random = new NukkitRandom();
            Vector3 pos = this.add(
                    this.getWidth() / 2 + ((double) NukkitMath.randomRange(random, -100, 100)) / 500,
                    this.getHeight() / 2 + ((double) NukkitMath.randomRange(random, -100, 100)) / 500,
                    this.getWidth() / 2 + ((double) NukkitMath.randomRange(random, -100, 100)) / 500);
            this.level.addParticle(new DustParticle(pos, rgba[0], rgba[1], rgba[2], rgba[3]));
        }else if(this.particleType == 2){
            NukkitRandom random = new NukkitRandom();
            Vector3 pos = this.add(
                    this.getWidth() / 2 + ((double) NukkitMath.randomRange(random, -100, 100)) / 500,
                    this.getHeight() / 2 + ((double) NukkitMath.randomRange(random, -100, 100)) / 500,
                    this.getWidth() / 2 + ((double) NukkitMath.randomRange(random, -100, 100)) / 500);
            this.level.addParticle(new MobSpellInstantaneousParticle(pos, rgba[0], rgba[1], rgba[2], rgba[3]));
        }

        if (this.age > 1200) {
            this.close();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }

    public void setParticleColor(int r, int g, int b, int a, int type){
        this.particleType = type;
        this.rgba = new int[]{r,g,b,a};
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = EntityArrow.NETWORK_ID;
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.yaw = (float) this.yaw;
        pk.pitch = (float) this.pitch;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }
}
