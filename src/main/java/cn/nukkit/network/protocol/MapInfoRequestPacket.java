package cn.nukkit.network.protocol;

/**
 * Created by CreeperFace on 5.3.2017.
 */
public class MapInfoRequestPacket extends DataPacket {
    public long mapId;

    @Override
    public byte pid() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        mapId = this.getVarLong();
    }

    @Override
    public void encode() {

    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
