package cn.nukkit.network.protocol;

<<<<<<< HEAD
/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShowCreditsPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SHOW_CREDITS_PACKET;

    public long eid;
    public int credit;
=======
public class ShowCreditsPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SHOW_CREDITS_PACKET;

    public static final int STATUS_START_CREDITS = 0;
    public static final int STATUS_END_CREDITS = 1;

    public long eid;
    public int status;
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
<<<<<<< HEAD
        this.reset();
        this.putVarLong(this.eid);
        this.putVarInt(this.credit);
    }

=======
        this.putVarLong(this.eid);
        this.putVarInt(this.status);
    }
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
}
