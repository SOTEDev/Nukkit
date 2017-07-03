package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
<<<<<<< HEAD
=======
import cn.nukkit.math.BlockFace;
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockEnderChest extends BlockTransparent {

    private Set<Player> viewers = new HashSet<>();

    public BlockEnderChest() {
        this(0);
    }

    public BlockEnderChest(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getId() {
        return ENDER_CHEST;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public String getName() {
        return "Chest";
    }

    @Override
    public double getHardness() {
        return 22.5;
    }

    @Override
    public double getResistance() {
        return 3000;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x + 0.0625,
                this.y,
                this.z + 0.0625,
                this.x + 0.9375,
                this.y + 0.9475,
                this.z + 0.9375
        );
    }

    @Override
<<<<<<< HEAD
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        int[] faces = {4, 2, 5, 3};
        this.meta = faces[player != null ? player.getDirection() : 0];
=======
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {2, 5, 3, 4};
        this.meta = faces[player != null ? player.getDirection().getHorizontalIndex() : 0];
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e

        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag("")
                .putString("id", BlockEntity.ENDER_CHEST)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        new BlockEntityEnderChest(this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
<<<<<<< HEAD
            Block top = this.getSide(1);
=======
            Block top = this.up();
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
            if (!top.isTransparent()) {
                return true;
            }

            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityEnderChest chest;
            if (t instanceof BlockEntityEnderChest) {
                chest = (BlockEntityEnderChest) t;
            } else {
                CompoundTag nbt = new CompoundTag("")
                        .putString("id", BlockEntity.ENDER_CHEST)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                chest = new BlockEntityEnderChest(this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
            }

            if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") instanceof StringTag) {
                if (!chest.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            player.setViewingEnderChest(this);
            player.addWindow(player.getEnderChestInventory());
        }

        return true;
    }

    @Override
<<<<<<< HEAD
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.OBSIDIAN, 0, 8}
            };
        } else {
            return new int[0][0];
=======
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(Item.OBSIDIAN, 0, 8)
            };
        } else {
            return new Item[0];
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.OBSIDIAN_BLOCK_COLOR;
    }

    public Set<Player> getViewers() {
        return viewers;
    }
<<<<<<< HEAD
=======

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
}
