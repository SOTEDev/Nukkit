package cn.nukkit.block;

<<<<<<< HEAD
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

/**
 * author: Angelic47
 * Nukkit Project
=======
import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemHopper;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * @author CreeperFace
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
 */
public class BlockHopper extends BlockTransparent {

    public BlockHopper() {
        this(0);
    }

    public BlockHopper(int meta) {
        super(meta);
    }

    @Override
<<<<<<< HEAD
    public boolean canBeActivated() {
        return true;
    }

    @Override
=======
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    public int getId() {
        return HOPPER_BLOCK;
    }

    @Override
    public String getName() {
<<<<<<< HEAD
        return "Dropper";
=======
        return "Hopper Block";
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    }

    @Override
    public double getHardness() {
<<<<<<< HEAD
        return 3.5;
=======
        return 3;
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    }

    @Override
    public double getResistance() {
<<<<<<< HEAD
        return 12.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        int[] faces = {0, 0, 3, 2, 5, 4};

        this.meta = faces[face];

        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag("")
=======
        return 24;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        BlockFace facing = face.getOpposite();

        if (facing == BlockFace.UP) {
            facing = BlockFace.DOWN;
        }

        this.meta = facing.getIndex();

        boolean powered = this.level.isBlockPowered(this);

        if (powered == this.isEnabled()) {
            this.setEnabled(!powered);
        }

        this.level.setBlock(this, this);

        CompoundTag nbt = new CompoundTag()
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.HOPPER)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

<<<<<<< HEAD
        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntity blockEntity = new BlockEntityHopper(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);

=======
        new BlockEntityHopper(this.level.getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4), nbt);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityHopper) {
            return player.addWindow(((BlockEntityHopper) blockEntity).getInventory()) != -1;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean hasComparatorInputOverride() {
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
        return true;
    }

    @Override
<<<<<<< HEAD
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new BlockAir(), true, true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityHopper hopper;
            if (t instanceof BlockEntityHopper) {
                hopper = (BlockEntityHopper) t;
            } else {
                CompoundTag nbt = new CompoundTag("")
                        .putList(new ListTag<>("Items"))
                        .putString("id", BlockEntity.HOPPER)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                hopper = new BlockEntityHopper(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            player.addWindow(hopper.getInventory());
        }

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), 0, 1}
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
=======
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityHopper) {
            return ContainerInventory.calculateRedstone(((BlockEntityHopper) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(this.meta & 7);
    }

    public boolean isEnabled() {
        return (this.meta & 0x08) != 8;
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            this.meta ^= 0x08;
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            boolean powered = this.level.isBlockPowered(this);

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
                this.level.setBlock(this, this, true, false);
            }

            return type;
        }

        return 0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{toItem()};
        }

        return new Item[0];
    }

    @Override
    public Item toItem() {
        return new ItemHopper();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    }
}
