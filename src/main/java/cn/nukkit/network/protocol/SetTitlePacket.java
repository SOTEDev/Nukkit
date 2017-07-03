package cn.nukkit.network.protocol;

<<<<<<< HEAD
public class SetTitlePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_TITLE_PACKET;

=======
/**
 * @author Tee7even
 */
public class SetTitlePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_TITLE_PACKET;

    public static final int TYPE_CLEAR = 0;
    public static final int TYPE_RESET = 1;
    public static final int TYPE_TITLE = 2;
    public static final int TYPE_SUBTITLE = 3;
    public static final int TYPE_ACTION_BAR = 4;
    public static final int TYPE_ANIMATION_TIMES = 5;

    public int type;
    public String text = "";
    public int fadeInTime = 0;
    public int stayTime = 0;
    public int fadeOutTime = 0;

>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    @Override
    public byte pid() {
        return NETWORK_ID;
    }

<<<<<<< HEAD
    public static final byte TYPE_CLEAR = 0;
    public static final byte TYPE_RESET = 1;
    public static final byte TYPE_TITLE = 2;
    public static final byte TYPE_SUB_TITLE = 3;
    public static final byte TYPE_ACTION_BAR = 4;
    public static final byte TYPE_TIMES = 5;

    public int type;
    public String title = "";
    public int fadeInDuration  = 0;
    public int duration = 0;
    public int fadeOutDuration = 0;

    @Override
    public void decode() {
        this.type = this.getVarInt();
        this.title = this.getString();
        this.fadeInDuration = this.getVarInt();
        this.duration = this.getVarInt();
        this.fadeOutDuration = this.getVarInt();
=======
    @Override
    public void decode() {
        this.type = this.getVarInt();
        this.text = this.getString();
        this.fadeInTime = this.getVarInt();
        this.stayTime = this.getVarInt();
        this.fadeOutTime = this.getVarInt();
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
    }

    @Override
    public void encode() {
        this.reset();
<<<<<<< HEAD
        this.putVarInt(this.type);
        this.putString(this.title);
        this.putVarInt(this.fadeInDuration);
        this.putVarInt(this.duration);
        this.putVarInt(this.fadeOutDuration);
    }

=======
        this.putVarInt(type);
        this.putString(text);
        this.putVarInt(fadeInTime);
        this.putVarInt(stayTime);
        this.putVarInt(fadeOutTime);
    }
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
}
