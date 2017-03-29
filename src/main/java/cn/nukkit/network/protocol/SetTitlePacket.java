package cn.nukkit.network.protocol;

public class SetTitlePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_TITLE_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static final byte TYPE_CLEAR = 0;
    public static final byte TYPE_RESET = 1;
    public static final byte TYPE_TITLE = 2;
    public static final byte TYPE_SUB_TITLE = 3;
    public static final byte TYPE_ACTION_BAR = 4;
    public static final byte TYPE_TIMES = 5;

    public byte type;
    public String title = "";
    public int fadeInDuration  = 0;
    public int fadeOutDuration = 0;

    @Override
    public void decode() {
        this.type = (byte) getByte();
        this.title = this.getString();
        this.fadeInDuration = this.getVarInt();
        this.fadeOutDuration = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putString(this.title);
        this.putVarInt(this.fadeInDuration);
        this.putVarInt(this.fadeOutDuration);
    }

}
