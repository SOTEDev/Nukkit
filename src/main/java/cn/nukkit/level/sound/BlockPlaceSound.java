package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
<<<<<<< HEAD
import cn.nukkit.network.protocol.LevelEventPacket;
=======
>>>>>>> 5da02c06ab18955d570103283c2f44d58ec01a6e
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class BlockPlaceSound extends LevelSoundEventSound {
    public BlockPlaceSound(Vector3 pos, int blockId) {
        super(pos, LevelSoundEventPacket.SOUND_PLACE, blockId, 1);
    }
}