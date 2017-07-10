package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.ai.EntityAIAttackOnCollide;
import cn.nukkit.entity.ai.EntityAIHurtByTarget;
import cn.nukkit.entity.ai.EntityAILookIdle;
import cn.nukkit.entity.ai.EntityAIMoveTowardsRestriction;
import cn.nukkit.entity.ai.EntityAIMoveTowardsTarget;
import cn.nukkit.entity.ai.EntityAINearestAttackableTarget;
import cn.nukkit.entity.ai.EntityAISwimming;
import cn.nukkit.entity.ai.EntityAIWander;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * @author PikyCZ
 */
public class EntitySilverfish extends EntityMob {

    public static final int NETWORK_ID = 39;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySilverfish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(3, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWander(this, 0.6D));
        //this.tasks.addTask(7, new EntityAIWatchClosest(this, Player.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, Player.class, false));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.getAttributeMap().addAttribute(Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue((float)0.25));
    }

    @Override
    public String getName() {
        return "Silver fish";
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 0.3f;
    }

    @Override
    public float getEyeHeight(){
        return 0.1F;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        this.renderYawOffset = (float)this.yaw;
        return super.onUpdate(currentTick);
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(8);
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = this.getNetworkId();
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
