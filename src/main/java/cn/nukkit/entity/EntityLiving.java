package cn.nukkit.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.ai.EntityAITasks;
import cn.nukkit.entity.ai.EntityJumpHelper;
import cn.nukkit.entity.ai.EntityLookHelper;
import cn.nukkit.entity.ai.EntityMoveHelper;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.pathfinding.PathNavigate;
import cn.nukkit.pathfinding.PathNavigateGround;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockIterator;
import co.aikar.timings.Timings;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityLiving extends Entity implements EntityDamageable {
    public EntityLiving(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.attributeMap = new AttributeMap();
        this.navigator = this.getNewNavigator(this.level);
    }

    public AttributeMap getAttributeMap(){
        return this.attributeMap;
    }

    @Override
    protected float getGravity() {
        return 0.08f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    protected int attackTime = 0;

    protected boolean invisible = false;

    protected float movementSpeed = 0.1f;

    private EntityLookHelper lookHelper = new EntityLookHelper(this);
    protected EntityMoveHelper moveHelper = new EntityMoveHelper(this);
    protected EntityJumpHelper jumpHelper = new EntityJumpHelper(this);
    //private EntityBodyHelper bodyHelper;

    protected PathNavigate navigator;
    public final EntityAITasks tasks = new EntityAITasks();
    public final EntityAITasks targetTasks = new EntityAITasks();
    private EntityLiving attackTarget;

    public float jumpMovementFactor = 0.02F;
    protected Player attackingPlayer;
    protected int recentlyHit;

    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;

    protected boolean isJumping;
    public float moveStrafing;
    public float moveForward;
    protected float randomYawVelocity;
    protected int newPosRotationIncrements;

    private EntityLiving entityLivingToAttack;
    private int revengeTimer;
    private EntityLiving lastAttacker;
    private int lastAttackerTime;
    private float landMovementFactor;
    private int jumpTicks;
    private float absorptionAmount;

    private float prevOnGroundSpeedFactor;
    private float onGroundSpeedFactor;

    private int ticksExisted;

    private float swingProgress;

    private AttributeMap attributeMap;

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("HealF")) {
            this.namedTag.putFloat("Health", this.namedTag.getShort("HealF"));
            this.namedTag.remove("HealF");
        }

        if (!this.namedTag.contains("Health") || !(this.namedTag.get("Health") instanceof FloatTag)) {
            this.namedTag.putFloat("Health", this.getMaxHealth());
        }

        this.setHealth(this.namedTag.getFloat("Health"));
    }

    @Override
    public void setHealth(float health) {
        boolean wasAlive = this.isAlive();
        super.setHealth(health);
        if (this.isAlive() && !wasAlive) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.eid = EntityEventPacket.RESPAWN;
            Server.broadcastPacket(this.hasSpawned.values(), pk);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putFloat("Health", this.getHealth());
    }

    public boolean hasLineOfSight(Entity entity) {
        //todo
        return true;
    }

    @Override
    public void heal(EntityRegainHealthEvent source) {
        super.heal(source);
        if (source.isCancelled()) {
            return;
        }

        this.attackTime = 0;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.attackTime > 0 || this.noDamageTicks > 0) {
            EntityDamageEvent lastCause = this.getLastDamageCause();
            if (lastCause != null && lastCause.getDamage() >= source.getDamage()) {
                return false;
            }
        }

        if (super.attack(source)) {
            if (source instanceof EntityDamageByEntityEvent) {
                Entity e = ((EntityDamageByEntityEvent) source).getDamager();
                if (source instanceof EntityDamageByChildEntityEvent) {
                    e = ((EntityDamageByChildEntityEvent) source).getChild();
                }

                if (e.isOnFire() && !(e instanceof Player)) {
                    this.setOnFire(2 * this.server.getDifficulty());
                }

                double deltaX = this.x - e.x;
                double deltaZ = this.z - e.z;
                this.knockBack(e, source.getDamage(), deltaX, deltaZ, ((EntityDamageByEntityEvent) source).getKnockBack());
            }

            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = this.getHealth() <= 0 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;
            Server.broadcastPacket(this.hasSpawned.values(), pk);

            this.attackTime = 10;

            return true;
        } else {
            return false;
        }
    }

    public void knockBack(Entity attacker, double damage, double x, double z) {
        this.knockBack(attacker, damage, x, z, 0.4);
    }

    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }

        f = 1 / f;

        Vector3 motion = new Vector3(this.motionX, this.motionY, this.motionZ);

        motion.x /= 2d;
        motion.y /= 2d;
        motion.z /= 2d;
        motion.x += x * f * base;
        motion.y += base;
        motion.z += z * f * base;

        if (motion.y > base) {
            motion.y = base;
        }

        this.setMotion(motion);
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }
        super.kill();
        EntityDeathEvent ev = new EntityDeathEvent(this, this.getDrops());
        this.server.getPluginManager().callEvent(ev);

        if (this.level.getGameRules().getBoolean("doEntityDrops")) {
            for (cn.nukkit.item.Item item : ev.getDrops()) {
                this.getLevel().dropItem(this, item);
            }
        }
    }

    @Override
    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        Timings.livingEntityBaseTickTimer.startTiming();
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_BREATHING, !this.isInsideOfWater());

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            if(!(this instanceof EntityHuman)){
                if (this.recentlyHit > 0){
                    --this.recentlyHit;
                }else{
                    this.attackingPlayer = null;
                }

                if (this.lastAttacker != null && !this.lastAttacker.isAlive()){
                    this.lastAttacker = null;
                }

                if (this.entityLivingToAttack != null){
                    if (!this.entityLivingToAttack.isAlive()){
                        this.setRevengeTarget((EntityLiving)null);
                    }else if (this.ticksExisted - this.revengeTimer > 100){
                        this.setRevengeTarget((EntityLiving)null);
                    }
                }
                this.onLivingUpdate();
                double d0 = this.x - this.lastX;
                double d1 = this.z - this.lastZ;
                float f = (float)(d0 * d0 + d1 * d1);
                float f1 = this.renderYawOffset;
                float f2 = 0.0F;
                this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
                float f3 = 0.0F;

                if (f > 0.0025000002F){
                    f3 = 1.0F;
                    f2 = (float)Math.sqrt((double)f) * 3.0F;
                    f1 = (float)MathHelper.atan2(d1, d0) * 180.0F / (float)Math.PI - 90.0F;
                }

                if (this.swingProgress > 0.0F){
                    f1 = (float)this.yaw;
                }

                if (!this.onGround){
                    f3 = 0.0F;
                }

                this.onGroundSpeedFactor += (f3 - this.onGroundSpeedFactor) * 0.3F;
                f2 = this.func_110146_f(f1, f2);

                while (this.yaw - this.lastYaw < -180.0F){
                    this.lastYaw -= 360.0F;
                }

                while (this.yaw - this.lastYaw >= 180.0F){
                    this.lastYaw += 360.0F;
                }

                while (this.renderYawOffset - this.lastRenderYawOffset < -180.0F){
                    this.lastRenderYawOffset -= 360.0F;
                }

                while (this.renderYawOffset - this.lastRenderYawOffset >= 180.0F){
                    this.lastRenderYawOffset += 360.0F;
                }

                while (this.pitch - this.lastPitch < -180.0F){
                    this.lastPitch -= 360.0F;
                }

                while (this.pitch - this.lastPitch >= 180.0F){
                    this.lastPitch += 360.0F;
                }

                while (this.headYaw - this.lastHeadYaw < -180.0F){
                    this.lastHeadYaw -= 360.0F;
                }

                while (this.headYaw - this.lastHeadYaw >= 180.0F){
                    this.lastHeadYaw += 360.0F;
                }
            }

            if (this.isInsideOfSolid()) {
                hasUpdate = true;
                this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 1));
            }

            if (!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()) {
                if (this instanceof EntityWaterAnimal) {
                    this.setDataProperty(new ShortEntityData(DATA_AIR, 400));
                } else {
                    hasUpdate = true;
                    int airTicks = this.getDataPropertyShort(DATA_AIR) - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        this.attack(new EntityDamageEvent(this, DamageCause.DROWNING, 2));
                    }

                    this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
                }
            } else {
                if (this instanceof EntityWaterAnimal) {
                    hasUpdate = true;
                    int airTicks = this.getDataPropertyInt(DATA_AIR) - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 2));
                    }

                    this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
                } else {
                    this.setDataProperty(new ShortEntityData(DATA_AIR, 400));
                }
            }
        }

        if (this.attackTime > 0) {
            this.attackTime -= tickDiff;
        }
        Timings.livingEntityBaseTickTimer.stopTiming();

        return hasUpdate;
    }

    protected float func_110146_f(float p_110146_1_, float p_110146_2_){
        float f = MathHelper.wrapAngleTo180_float(p_110146_1_ - this.renderYawOffset);
        this.renderYawOffset += f * 0.3F;
        float f1 = MathHelper.wrapAngleTo180_float((float)this.yaw - this.renderYawOffset);
        boolean flag = f1 < -90.0F || f1 >= 90.0F;

        if (f1 < -75.0F){
            f1 = -75.0F;
        }

        if (f1 >= 75.0F){
            f1 = 75.0F;
        }

        this.renderYawOffset = (float)this.yaw - f1;

        if (f1 * f1 > 2500.0F){
            this.renderYawOffset += f1 * 0.2F;
        }

        if (flag){
            p_110146_2_ *= -1.0F;
        }

        return p_110146_2_;
    }

    public Item[] getDrops() {
        return new Item[0];
    }

    public Block[] getLineOfSight(int maxDistance) {
        return this.getLineOfSight(maxDistance, 0);
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength) {
        return this.getLineOfSight(maxDistance, maxLength, new Integer[]{});
    }

    @Deprecated
    public Block[] getLineOfSight(int maxDistance, int maxLength, Map<Integer, Object> transparent) {
        return this.getLineOfSight(maxDistance, maxLength, transparent.keySet().stream().toArray(Integer[]::new));
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength, Integer[] transparent) {
        if (maxDistance > 120) {
            maxDistance = 120;
        }

        if (transparent != null && transparent.length == 0) {
            transparent = null;
        }

        List<Block> blocks = new ArrayList<>();

        BlockIterator itr = new BlockIterator(this.level, this.getPosition(), this.getDirectionVector(), this.getEyeHeight(), maxDistance);

        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);

            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }

            int id = block.getId();

            if (transparent == null) {
                if (id != 0) {
                    break;
                }
            } else {
                if (Arrays.binarySearch(transparent, id) < 0) {
                    break;
                }
            }
        }

        return blocks.stream().toArray(Block[]::new);
    }

    public Block getTargetBlock(int maxDistance) {
        return getTargetBlock(maxDistance, new Integer[]{});
    }

    @Deprecated
    public Block getTargetBlock(int maxDistance, Map<Integer, Object> transparent) {
        return getTargetBlock(maxDistance, transparent.keySet().stream().toArray(Integer[]::new));
    }

    public Block getTargetBlock(int maxDistance, Integer[] transparent) {
        try {
            Block[] blocks = this.getLineOfSight(maxDistance, 1, transparent);
            Block block = blocks[0];
            if (block != null) {
                if (transparent != null && transparent.length != 0) {
                    if (Arrays.binarySearch(transparent, block.getId()) < 0) {
                        return block;
                    }
                } else {
                    return block;
                }
            }
        } catch (Exception ignored) {

        }

        return null;
    }

    public void setMovementSpeed(float speed) {
        this.movementSpeed = speed;
    }

    public float getMovementSpeed() {
        return this.movementSpeed;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return true;
    }

    public Random getRNG(){
        return this.rand;
    }

    protected PathNavigate getNewNavigator(Level worldIn){
        return new PathNavigateGround(this, worldIn);
    }

    public EntityLookHelper getLookHelper(){
        return this.lookHelper;
    }

    public EntityMoveHelper getMoveHelper(){
        return this.moveHelper;
    }

    public EntityJumpHelper getJumpHelper(){
        return this.jumpHelper;
    }

    public PathNavigate getNavigator(){
        return this.navigator;
    }

    public EntityLiving getAttackTarget(){
        return this.attackTarget;
    }

    public void setAttackTarget(EntityLiving entitylivingbaseIn){
        this.attackTarget = entitylivingbaseIn;
    }

    //public boolean canAttackClass(Class <? extends EntityLiving > cls){
    //    return cls != EntityGhast.class;
    //}

    public void setMoveForward(float p_70657_1_){
        this.moveForward = p_70657_1_;
    }

    public void setAIMoveSpeed(float speedIn){
        this.landMovementFactor = speedIn;
        this.setMoveForward(speedIn);
    }

    protected final void updateEntityActionState(){
        //++this.entityAge;
        this.targetTasks.onUpdateTasks();
        this.tasks.onUpdateTasks();
        this.navigator.onUpdateNavigation();
        this.updateAITasks();
        this.moveHelper.onUpdateMoveHelper();
        this.lookHelper.onUpdateLook();
        this.jumpHelper.doJump();
    }

    protected void updateAITasks(){
    }

    public int getVerticalFaceSpeed(){
        return 40;
    }

    public void faceEntity(Entity entityIn, float p_70625_2_, float p_70625_3_){
        double d0 = entityIn.x - this.x;
        double d2 = entityIn.z - this.z;
        double d1;

        if (entityIn instanceof EntityLiving){
            EntityLiving entitylivingbase = (EntityLiving)entityIn;
            d1 = entitylivingbase.y + (double)entitylivingbase.getEyeHeight() - (this.y + (double)this.getEyeHeight());
        }else{
            d1 = (entityIn.getBoundingBox().minY + entityIn.getBoundingBox().maxY) / 2.0D - (this.y + (double)this.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float)(-(MathHelper.atan2(d1, d3) * 180.0D / Math.PI));
        this.pitch = this.updateRotation((float)this.pitch, f1, p_70625_3_);
        this.yaw = this.updateRotation((float)this.yaw, f, p_70625_2_);
    }

    private float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_){
        float f = MathHelper.wrapAngleTo180_float(p_70663_2_ - p_70663_1_);

        if (f > p_70663_3_){
            f = p_70663_3_;
        }

        if (f < -p_70663_3_){
            f = -p_70663_3_;
        }

        return p_70663_1_ + f;
    }

    public int getMaxFallHeight(){
        if (this.getAttackTarget() == null){
            return 3;
        }else{
            int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
            i = i - (3 - 1/*this.level.getDifficulty().getDifficultyId()*/) * 4;

            if (i < 0){
                i = 0;
            }

            return i + 3;
        }
    }

    public void onLivingUpdate(){
        if (this.jumpTicks > 0){
            --this.jumpTicks;
        }

        /*if (this.newPosRotationIncrements > 0){
            double d0 = this.x + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double d1 = this.y + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double d2 = this.z + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }*/

        if (Math.abs(this.motionX) < 0.005D){
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.005D){
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.005D){
            this.motionZ = 0.0D;
        }

        this.updateEntityActionState();

        if (this.isJumping){
            if (this.isInsideOfWater()){
                this.updateAITick();
            }else if (this.isInsideOfLava()){
                this.handleJumpLava();
            }else if (this.onGround && this.jumpTicks == 0){
                this.jump();
                this.jumpTicks = 10;
            }
        }else{
            this.jumpTicks = 0;
        }

        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
    }

    public EntityLiving getAITarget(){
        return this.entityLivingToAttack;
    }

    public int getRevengeTimer(){
        return this.revengeTimer;
    }

    public void setRevengeTarget(EntityLiving livingBase){
        this.entityLivingToAttack = livingBase;
        this.revengeTimer = this.ticksExisted;
    }

    public EntityLiving getLastAttacker(){
        return this.lastAttacker;
    }

    public int getLastAttackerTime(){
        return this.lastAttackerTime;
    }

    public void setLastAttacker(Entity entityIn){
        if (entityIn instanceof EntityLiving){
            this.lastAttacker = (EntityLiving)entityIn;
        }else{
            this.lastAttacker = null;
        }

        this.lastAttackerTime = this.ticksExisted;
    }

    public boolean isOnLadder(){
        int i = MathHelper.floor_double(this.x);
        int j = MathHelper.floor_double(this.getBoundingBox().minY);
        int k = MathHelper.floor_double(this.z);
        return false;
        //Block block = this.worldObj.getBlockState(new BlockPos(i, j, k)).getBlock();
        //return (block == Blocks.ladder || block == Blocks.vine) && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).isSpectator());
    }

    protected float getJumpUpwardsMotion(){
        return 0.42F;
    }

    protected void jump(){
        this.motionY = (double)this.getJumpUpwardsMotion();

        //if (this.isPotionActive(Potion.jump)){
        //    this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        //}

        if (this.isSprinting()){
            float f = (float)this.yaw * 0.017453292F;
            this.motionX -= (double)(MathHelper.sin(f) * 0.2F);
            this.motionZ += (double)(MathHelper.cos(f) * 0.2F);
        }

        //this.isAirBorne = true;
    }

    protected void updateAITick(){
        this.motionY += 0.03999999910593033D;
    }

    protected void handleJumpLava(){
        this.motionY += 0.03999999910593033D;
    }

    public void moveEntityWithHeading(float strafe, float forward){
        if (!this.isInsideOfWater()){
            if (!this.isInsideOfLava()){
                float f4 = 0.91F;

                if (this.onGround){
                    //f4 = this.level.getBlock(new Vector3(MathHelper.floor_double(this.x), MathHelper.floor_double(this.getBoundingBox().minY) - 1, MathHelper.floor_double(this.z))).slipperiness * 0.91F;
                }

                float f = 0.16277136F / (f4 * f4 * f4);
                float f5;

                if (this.onGround){
                    f5 = this.getAIMoveSpeed() * f;
                }else{
                    f5 = this.jumpMovementFactor;
                }

                this.moveFlying(strafe, forward, f5);
                f4 = 0.91F;

                if (this.onGround){
                    //4 = this.level.getBlock(new Vector3(MathHelper.floor_double(this.x), MathHelper.floor_double(this.getBoundingBox().minY) - 1, MathHelper.floor_double(this.z))).slipperiness * 0.91F;
                }

                if (this.isOnLadder()){
                    float f6 = 0.15F;
                    this.motionX = MathHelper.clamp_double(this.motionX, (double)(-f6), (double)f6);
                    this.motionZ = MathHelper.clamp_double(this.motionZ, (double)(-f6), (double)f6);
                    this.fallDistance = 0.0F;

                    if (this.motionY < -0.15D){
                        this.motionY = -0.15D;
                    }

                    boolean flag = this.isSneaking() && this instanceof Player;

                    if (flag && this.motionY < 0.0D){
                        this.motionY = 0.0D;
                    }
                }

                this.move(this.motionX, this.motionY, this.motionZ);

                if (this.isCollidedHorizontally && this.isOnLadder()){
                    this.motionY = 0.2D;
                }
                    //if (this.posY > 0.0D){
                    //    this.motionY = -0.1D;
                    //}else{
                    //    this.motionY = 0.0D;
                    //}
                this.motionY -= 0.08D;

                this.motionY *= 0.9800000190734863D;
                this.motionX *= (double)f4;
                this.motionZ *= (double)f4;
            }else{
                double d1 = this.y;
                this.moveFlying(strafe, forward, 0.02F);
                this.move(this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
                this.motionY -= 0.02D;

                //if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d1, this.motionZ)){
                //    this.motionY = 0.30000001192092896D;
                //}
            }
        }else{
            double d0 = this.y;
            float f1 = 0.8F;
            float f2 = 0.02F;
            float f3 = 0F;//(float)EnchantmentHelper.getDepthStriderModifier(this);

            if (f3 > 3.0F){
                f3 = 3.0F;
            }

            if (!this.onGround){
                f3 *= 0.5F;
            }

            if (f3 > 0.0F){
                f1 += (0.54600006F - f1) * f3 / 3.0F;
                f2 += (this.getAIMoveSpeed() * 1.0F - f2) * f3 / 3.0F;
            }

            this.moveFlying(strafe, forward, f2);
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)f1;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= (double)f1;
            this.motionY -= 0.02D;

            //if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ)){
            //    this.motionY = 0.30000001192092896D;
            //}
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d2 = this.x - this.lastX;
        double d3 = this.z - this.lastZ;
        float f7 = MathHelper.sqrt_double(d2 * d2 + d3 * d3) * 4.0F;

        if (f7 > 1.0F){
            f7 = 1.0F;
        }

        this.limbSwingAmount += (f7 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    public void moveFlying(float strafe, float forward, float friction){
        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F){
            f = MathHelper.sqrt_float(f);

            if (f < 1.0F)
            {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = MathHelper.sin((float)this.yaw * (float)Math.PI / 180.0F);
            float f2 = MathHelper.cos((float)this.yaw * (float)Math.PI / 180.0F);
            this.motionX += (double)(strafe * f2 - forward * f1);
            this.motionZ += (double)(forward * f2 + strafe * f1);
        }
    }

    public float getAIMoveSpeed(){
        return this.landMovementFactor;
    }

    public boolean attackEntityAsMob(Entity entityIn){
        this.setLastAttacker(entityIn);

        EntityEventPacket ePacket = new EntityEventPacket();
        ePacket.eid = this.getId();
        ePacket.event = EntityEventPacket.ATTACK_ANIMATION;
        Server.broadcastPacket(this.getViewers().values(), ePacket);

        Attribute attribute = this.getAttributeMap().getAttribute(Attribute.ATTACK_DAMAGE);
        float damage = attribute == null ? 1.0F : attribute.getValue();
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(this, entityIn, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
        entityIn.attack(event);
        return true;
    }

    public void setJumping(boolean p_70637_1_){
        this.isJumping = p_70637_1_;
    }

    //public Team getTeam(){
    //    return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
    //}

    public boolean isOnSameTeam(EntityLiving otherEntity){
        //return this.isOnTeam(otherEntity.getTeam());
        return false;
    }

    //public boolean isOnTeam(Team p_142012_1_){
    //    return this.getTeam() != null ? this.getTeam().isSameTeam(p_142012_1_) : false;
    //}
}
