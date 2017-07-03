package cn.nukkit.network.protocol;

// A wild TransferPacket appeared!
<<<<<<< HEAD

public class TransferPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TRANSFER_PACKET;

    public String address; // Server address
    public short port = 19132; // Server port

=======
public class TransferPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.TRANSFER_PACKET;
    
    public String address; // Server address
    public int port = 19132; // Server port
    
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    @Override
    public void decode() {
        this.address = this.getString();
        this.port = (short) this.getLShort();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(address);
        this.putLShort(port);
    }

    @Override
    public byte pid() {
        return ProtocolInfo.TRANSFER_PACKET;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
