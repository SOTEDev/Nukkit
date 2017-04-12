package cn.nukkit.entity.projectile;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.RainSplashParticle;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.WeightedRandom;
import cn.nukkit.utils.WeightedRandomFishable;

public class EntityFishingHook extends EntityProjectile {
    public static final int NETWORK_ID = 77;

    public static final List<WeightedRandomFishable> JUNK = Arrays.<WeightedRandomFishable>asList(new WeightedRandomFishable[] {(new WeightedRandomFishable(Item.get(Item.LEATHER_BOOTS), 10)).setMaxDamagePercent(0.9F), new WeightedRandomFishable(Item.get(Item.LEATHER), 10), new WeightedRandomFishable(Item.get(Item.BONE), 10), new WeightedRandomFishable(Item.get(Item.GLASS_BOTTLE), 10), new WeightedRandomFishable(Item.get(Item.STRING), 5), (new WeightedRandomFishable(Item.get(Item.FISHING_ROD), 2)).setMaxDamagePercent(0.9F), new WeightedRandomFishable(Item.get(Item.BOWL), 10), new WeightedRandomFishable(Item.get(Item.STICK), 5), new WeightedRandomFishable(Item.get(Item.DYE), 1), new WeightedRandomFishable(Item.get(Item.TRIPWIRE_HOOK), 10), new WeightedRandomFishable(Item.get(Item.ROTTEN_FLESH), 10)});
    public static final List<WeightedRandomFishable> TREASURE = Arrays.<WeightedRandomFishable>asList(new WeightedRandomFishable[] {new WeightedRandomFishable(Item.get(Item.WATER_LILY), 1), new WeightedRandomFishable(Item.get(Item.NAME_TAG), 1), new WeightedRandomFishable(Item.get(Item.SADDLE), 1), (new WeightedRandomFishable(Item.get(Item.BOW), 1)).setMaxDamagePercent(0.25F).setEnchantable(), (new WeightedRandomFishable(Item.get(Item.FISHING_ROD), 1)).setMaxDamagePercent(0.25F).setEnchantable(), (new WeightedRandomFishable(Item.get(Item.BOOK), 1)).setEnchantable()});
    public static final List<WeightedRandomFishable> FISH = Arrays.<WeightedRandomFishable>asList(new WeightedRandomFishable[] {new WeightedRandomFishable(Item.get(Item.RAW_FISH), 60), new WeightedRandomFishable(Item.get(Item.RAW_SALMON), 25), new WeightedRandomFishable(Item.get(Item.CLOWNFISH), 2), new WeightedRandomFishable(Item.get(Item.PUFFERFISH), 13)});

    public int ticksCaughtDelay = 0;
    public int ticksCatchableDelay = 0;
    public int ticksCatchable = 0;
    public boolean damageRod = false;

    public boolean isFishSpawned = false;
    public boolean isCatched = false;
    public Entity caughtEntity;
    private float fishApproachAngle;
    private Random rand = new Random();

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
        this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
        if(this.shootingEntity instanceof Player){
            Enchantment lure = ((Player)this.shootingEntity).getInventory().getItemInHand().getEnchantment(Enchantment.ID_LURE);
            if(lure != null) this.ticksCaughtDelay -= lure.getLevel() * 20 * 5;
        }
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
            if(this.ticksCaughtDelay == 0 && (int)(Math.random() * 100) <= 30 && !this.isFishSpawned){
                this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
                this.isFishSpawned = true;
            }else if(this.ticksCaughtDelay > 0){
                this.ticksCaughtDelay--;
            }
            if(this.isFishSpawned){
                if(this.ticksCatchableDelay == 0){
                    this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
                    this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
                    if(this.shootingEntity instanceof Player){
                        Enchantment lure = ((Player)this.shootingEntity).getInventory().getItemInHand().getEnchantment(Enchantment.ID_LURE);
                        if(lure != null) this.ticksCaughtDelay -= lure.getLevel() * 20 * 5;
                    }
                    this.fishBites();
                    this.isFishSpawned = false;
                    this.motionX = 0;
                    this.motionY = -0.5;
                    this.motionZ = 0;
                    this.motionChanged = true;
                    hasUpdate = true;
                }else if(this.ticksCatchableDelay > 0){
                    this.ticksCatchableDelay--;
                    this.attractFish();
                }
            }
            if(this.ticksCatchable > 0){
                this.ticksCatchable--;
            }
        }else{
            if(this.caughtEntity instanceof Entity){
                this.setPosition(this.caughtEntity.add(0, this.caughtEntity.getEyeHeight(), 0));
                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;
                this.motionChanged = true;
                this.keepMovement = false;
                hasUpdate = true;
            }
        }

        this.timing.stopTiming();

        return hasUpdate;
    }

    public void fishBites(){
        if(this.shootingEntity instanceof Player && this.isInsideOfWater()){
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = EntityEventPacket.FISH_HOOK_HOOK;
            Server.broadcastPacket(this.level.getPlayers().values(), pk);
        }
    }

    public void attractFish(){
        if(this.shootingEntity instanceof Player && this.isInsideOfWater()){
            this.fishApproachAngle = (float)((double)this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
            float f7 = this.fishApproachAngle * 0.017453292F;
            float f10 = MathHelper.sin(f7);
            float f11 = MathHelper.cos(f7);
            double d13 = this.x + (double)(f10 * (float)this.ticksCatchableDelay * 0.1F);
            double d15 = (double)((float)MathHelper.floor_double(this.getBoundingBox().minY) + 1.0F);
            double d16 = this.z + (double)(f11 * (float)this.ticksCatchableDelay * 0.1F);
            RainSplashParticle particle = new RainSplashParticle(new Vector3(d13, d15, d16));
            this.level.addParticle(particle);
        }
    }

    public boolean reelLine(){
        if(!this.isInsideOfWater()) return false;
        this.damageRod = false;

        if(this.shootingEntity instanceof Player && this.ticksCatchable > 0){
            Item item = getFishingResult(this.rand, (Player)this.shootingEntity);
            //TODO FishEvent
            //if(!ev.isCancelled()){

            double d1 = this.shootingEntity.x - this.x;
            double d3 = (this.shootingEntity.y + 0.2) - this.y;
            double d5 = this.shootingEntity.z - this.z;
            double d7 = (double)MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
            double d9 = 0.1D;
            Vector3 motion = new Vector3();
            motion.x = d1 * d9;
            motion.y = d3 * d9 + (double)MathHelper.sqrt_double(d7) * 0.08D;
            motion.z = d5 * d9;
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
            this.close();
        }

        return this.damageRod;
    }

    public Item getFishingResult(Random rand, Player player){
        float f = rand.nextFloat();
        int i = 0;//LuckOfSea
        int j = 0;//Lure
        float f1 = 0.1F - (float)i * 0.025F - (float)j * 0.01F;
        float f2 = 0.05F + (float)i * 0.01F - (float)j * 0.01F;
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f2 = MathHelper.clamp_float(f2, 0.0F, 1.0F);

        if (f < f1){
            return ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, JUNK)).getItemStack(this.rand);
        }else{
            f = f - f1;
            if (f < f2){
                return ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, TREASURE)).getItemStack(this.rand);
            }else{
                return ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, FISH)).getItemStack(this.rand);
            }
        }
    }

    public double distance2(Vector3 pos) {
        return Math.sqrt(this.distanceSquared2(pos));
    }

    public double distanceSquared2(Vector3 pos) {
        return Math.pow(this.x - pos.x, 2) + Math.pow(this.z - pos.z, 2);
    }

    public void onCatch(Entity entity){
        this.isCatched = true;
        this.caughtEntity = entity;
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
