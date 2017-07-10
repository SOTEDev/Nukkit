package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.EntityAIAttackOnCollide;
import cn.nukkit.entity.ai.EntityAIHurtByTarget;
import cn.nukkit.entity.ai.EntityAILookIdle;
import cn.nukkit.entity.ai.EntityAIMoveTowardsRestriction;
import cn.nukkit.entity.ai.EntityAIMoveTowardsTarget;
import cn.nukkit.entity.ai.EntityAINearestAttackableTarget;
import cn.nukkit.entity.ai.EntityAIWander;
import cn.nukkit.entity.ai.EntityAIWatchClosest;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.MathHelper;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.pathfinding.PathNavigateGround;

/**
 * @author Box.
 */
public class EntityIronGolem extends EntityMob {

    public static final int NETWORK_ID = 20;

    private int homeCheckTimer;
    private int attackTimer;
    private int holdRoseTick;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    public EntityIronGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
        //this.tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.6D, true));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        //this.tasks.addTask(5, new EntityAILookAtVillager(this));
        this.tasks.addTask(6, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, Player.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, Player.class, false));
        //this.targetTasks.addTask(1, new EntityAIDefendVillage(this));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
        //this.targetTasks.addTask(3, new EntityIronGolem.AINearestAttackableTargetNonCreeper(this, EntityLiving.class, 10, false, true, IMob.VISIBLE_MOB_SELECTOR));
        this.getAttributeMap().addAttribute(Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue((float)0.25));
        this.getAttributeMap().addAttribute(Attribute.getAttribute(Attribute.ATTACK_DAMAGE).setValue((float)7.0));
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(100);
    }

    protected void updateAITasks(){
        /*if (--this.homeCheckTimer <= 0){
            this.homeCheckTimer = 70 + this.rand.nextInt(50);
            this.villageObj = this.level.getVillageCollection().getNearestVillage(new BlockPos(this), 32);

            if (this.villageObj == null){
                this.detachHome();
            }else{
                BlockVector3 blockpos = this.villageObj.getCenter();
                this.setHomePosAndDistance(blockpos, (int)((float)this.villageObj.getVillageRadius() * 0.6F));
            }
        }*/

        super.updateAITasks();
    }

    public boolean attackEntityAsMob(Entity entityIn){
        super.attackEntityAsMob(entityIn);
        this.attackTimer = 10;
        entityIn.motionY += 0.4000000059604645D;
        //this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
        return true;
    }

    public void onLivingUpdate(){
        super.onLivingUpdate();

        if (this.attackTimer > 0){
            --this.attackTimer;
        }

        if (this.holdRoseTick > 0){
            --this.holdRoseTick;
        }

        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.500000277905201E-7D && this.rand.nextInt(5) == 0){
            int i = MathHelper.floor_double(this.x);
            int j = MathHelper.floor_double(this.y - 0.20000000298023224D);
            int k = MathHelper.floor_double(this.z);
            //IBlockState iblockstate = this.worldObj.getBlockState(new BlockPos(i, j, k));
            //Block block = iblockstate.getBlock();

            //if (block.getMaterial() != Material.air){
            //    this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D, new int[] {Block.getStateId(iblockstate)});
            //}
        }
    }

    @Override
    public Item[] getDrops() {
        //if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
        //    return new Item[]{Item.get(Item.GUNPOWDER, level.rand.nextInt(2) + 1)};
        //}
        return new Item[0];
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
