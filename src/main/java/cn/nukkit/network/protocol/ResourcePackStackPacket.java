package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;

public class ResourcePackStackPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_STACK_PACKET;

    public boolean mustAccept = false;
    public ResourcePack[] behaviourPackStack = new ResourcePack[0];
    public ResourcePack[] resourcePackStack = new ResourcePack[0];

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);

<<<<<<< HEAD
        this.putLShort(this.behaviourPackStack.length);
=======
        this.putUnsignedVarInt(this.behaviourPackStack.length);
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
        for (ResourcePack entry : this.behaviourPackStack) {
            this.putString(entry.getPackId());
            this.putString(entry.getPackVersion());
        }

<<<<<<< HEAD
        this.putLShort(this.resourcePackStack.length);
=======
        this.putUnsignedVarInt(this.resourcePackStack.length);
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
        for (ResourcePack entry : this.resourcePackStack) {
            this.putString(entry.getPackId());
            this.putString(entry.getPackVersion());
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
