package cn.nukkit.block;

import cn.nukkit.Player;
<<<<<<< HEAD
import cn.nukkit.Server;
=======
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
<<<<<<< HEAD
=======
import cn.nukkit.math.BlockFace;
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: Angelic47 Nukkit Project
 */
public class BlockBeacon extends BlockTransparent {

    public BlockBeacon() {
        this(0);
    }

    public BlockBeacon(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BEACON;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Beacon";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        // TODO handle GUI
        //Server.getInstance().getLogger().info("BlockBeacon.onActivate called");
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        boolean blockSuccess = super.place(item, block, target, face, fx, fy, fz, player);

        if (blockSuccess) {
            CompoundTag nbt = new CompoundTag("")
                    .putString("id", BlockEntity.BEACON)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z);
            new BlockEntityBeacon(this.level.getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
        }

        return blockSuccess;
    }

<<<<<<< HEAD

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        boolean blockSuccess = super.place(item, block, target, face, fx, fy, fz, player);

        if(blockSuccess) {
            CompoundTag nbt = new CompoundTag("")
                    .putString("id", BlockEntity.BEACON)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z);
            new BlockEntityBeacon(this.level.getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
        }

        return blockSuccess;
=======
    @Override
    public boolean canBePushed() {
        return false;
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    }
}
