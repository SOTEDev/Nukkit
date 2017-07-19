package cn.nukkit.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AddHangingEntityPacket;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.AddItemPacket;
import cn.nukkit.network.protocol.AddPaintingPacket;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.AdventureSettingsPacket;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.AvailableCommandsPacket;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.BlockPickRequestPacket;
import cn.nukkit.network.protocol.BossEventPacket;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.network.protocol.ChunkRadiusUpdatedPacket;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import cn.nukkit.network.protocol.CommandStepPacket;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.ContainerSetContentPacket;
import cn.nukkit.network.protocol.ContainerSetDataPacket;
import cn.nukkit.network.protocol.ContainerSetSlotPacket;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.CraftingEventPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.DropItemPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.EntityFallPacket;
import cn.nukkit.network.protocol.ExplodePacket;
import cn.nukkit.network.protocol.FullChunkDataPacket;
import cn.nukkit.network.protocol.GameRulesChangedPacket;
import cn.nukkit.network.protocol.HurtArmorPacket;
import cn.nukkit.network.protocol.InteractPacket;
import cn.nukkit.network.protocol.InventoryActionPacket;
import cn.nukkit.network.protocol.ItemFrameDropItemPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.MapInfoRequestPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.MoveEntityPacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.PlayerActionPacket;
import cn.nukkit.network.protocol.PlayerInputPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RemoveBlockPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.ReplaceItemInSlotPacket;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.network.protocol.ResourcePackChunkDataPacket;
import cn.nukkit.network.protocol.ResourcePackChunkRequestPacket;
import cn.nukkit.network.protocol.ResourcePackClientResponsePacket;
import cn.nukkit.network.protocol.ResourcePackDataInfoPacket;
import cn.nukkit.network.protocol.ResourcePackStackPacket;
import cn.nukkit.network.protocol.ResourcePacksInfoPacket;
import cn.nukkit.network.protocol.RespawnPacket;
import cn.nukkit.network.protocol.RiderJumpPacket;
import cn.nukkit.network.protocol.SetCommandsEnabledPacket;
import cn.nukkit.network.protocol.SetDifficultyPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import cn.nukkit.network.protocol.SetHealthPacket;
import cn.nukkit.network.protocol.SetPlayerGameTypePacket;
import cn.nukkit.network.protocol.SetSpawnPositionPacket;
import cn.nukkit.network.protocol.SetTimePacket;
import cn.nukkit.network.protocol.SetTitlePacket;
import cn.nukkit.network.protocol.ShowCreditsPacket;
import cn.nukkit.network.protocol.SpawnExperienceOrbPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.protocol.UpdateTradePacket;
import cn.nukkit.network.protocol.UseItemPacket;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Zlib;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Network {

    public static final byte CHANNEL_NONE = 0;
    public static final byte CHANNEL_PRIORITY = 1; //Priority channel, only to be used when it matters
    public static final byte CHANNEL_WORLD_CHUNKS = 2; //Chunk sending
    public static final byte CHANNEL_MOVEMENT = 3; //Movement sending
    public static final byte CHANNEL_BLOCKS = 4; //Block updates or explosions
    public static final byte CHANNEL_WORLD_EVENTS = 5; //Entity, level or blockentity entity events
    public static final byte CHANNEL_ENTITY_SPAWNING = 6; //Entity spawn/despawn channel
    public static final byte CHANNEL_TEXT = 7; //Chat and other text stuff
    public static final byte CHANNEL_END = 31;

    private Class<? extends DataPacket>[] packetPool = new Class[256];

    private final Server server;

    private final Set<SourceInterface> interfaces = new HashSet<>();

    private final Set<AdvancedSourceInterface> advancedInterfaces = new HashSet<>();

    private double upload = 0;
    private double download = 0;

    private String name;

    public Network(Server server) {
        this.registerPackets();
        this.server = server;
    }

    public void addStatistics(double upload, double download) {
        this.upload += upload;
        this.download += download;
    }

    public double getUpload() {
        return upload;
    }

    public double getDownload() {
        return download;
    }

    public void resetStatistics() {
        this.upload = 0;
        this.download = 0;
    }

    public Set<SourceInterface> getInterfaces() {
        return interfaces;
    }

    public void processInterfaces() {
        for (SourceInterface interfaz : this.interfaces) {
            try {
                interfaz.process();
            } catch (Exception e) {
                if (Nukkit.DEBUG > 1) {
                    this.server.getLogger().logException(e);
                }

                interfaz.emergencyShutdown();
                this.unregisterInterface(interfaz);
                this.server.getLogger().critical(this.server.getLanguage().translateString("nukkit.server.networkError", new String[]{interfaz.getClass().getName(), e.getMessage()}));
            }
        }
    }

    public void registerInterface(SourceInterface interfaz) {
        this.interfaces.add(interfaz);
        if (interfaz instanceof AdvancedSourceInterface) {
            this.advancedInterfaces.add((AdvancedSourceInterface) interfaz);
            ((AdvancedSourceInterface) interfaz).setNetwork(this);
        }
        interfaz.setName(this.name);
    }

    public void unregisterInterface(SourceInterface interfaz) {
        this.interfaces.remove(interfaz);
        this.advancedInterfaces.remove(interfaz);
    }

    public void setName(String name) {
        this.name = name;
        this.updateName();
    }

    public String getName() {
        return name;
    }

    public void updateName() {
        for (SourceInterface interfaz : this.interfaces) {
            interfaz.setName(this.name);
        }
    }

    public void registerPacket(byte id, Class<? extends DataPacket> clazz) {
        this.packetPool[id & 0xff] = clazz;
    }

    public Server getServer() {
        return server;
    }

    public void processBatch(BatchPacket packet, Player player) {
        byte[] data;
        try {
            data = Zlib.inflate(packet.payload, 64 * 1024 * 1024);
        } catch (Exception e) {
            return;
        }

        int len = data.length;
        BinaryStream stream = new BinaryStream(data);
        try {
            List<DataPacket> packets = new ArrayList<>();
            while (stream.offset < len) {
                byte[] buf = stream.getByteArray();

                DataPacket pk;

                if ((pk = this.getPacket(buf[0])) != null) {
                    pk.setBuffer(buf, 1);

                    pk.decode();

                    packets.add(pk);
                }
            }

            processPackets(player, packets);

        } catch (Exception e) {
            if (Nukkit.DEBUG > 0) {
                this.server.getLogger().debug("BatchPacket 0x" + Binary.bytesToHexString(packet.payload));
                this.server.getLogger().logException(e);
            }
        }
    }

    /**
     * Process packets obtained from batch packets
     * Required to perform additional analyses and filter unnecessary packets
     *
     * @param packets
     */
    public void processPackets(Player player, List<DataPacket> packets) {
        if (packets.isEmpty()) return;
        List<Byte> filter = new ArrayList<>();
        for (DataPacket packet : packets) {
            switch (packet.pid()) {
                case ProtocolInfo.USE_ITEM_PACKET:
                    // Prevent double fire of PlayerInteractEvent
                    if (!filter.contains(ProtocolInfo.USE_ITEM_PACKET)) {
                        player.handleDataPacket(packet);
                        filter.add(ProtocolInfo.USE_ITEM_PACKET);
                    }
                    break;
                default:
                    player.handleDataPacket(packet);
            }
        }
    }


    public DataPacket getPacket(byte id) {
        Class<? extends DataPacket> clazz = this.packetPool[id & 0xff];
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
        return null;
    }

    public void sendPacket(String address, int port, byte[] payload) {
        for (AdvancedSourceInterface interfaz : this.advancedInterfaces) {
            interfaz.sendRawPacket(address, port, payload);
        }
    }

    public void blockAddress(String address) {
        this.blockAddress(address, 300);
    }

    public void blockAddress(String address, int timeout) {
        for (AdvancedSourceInterface interfaz : this.advancedInterfaces) {
            interfaz.blockAddress(address, timeout);
        }
    }

    private void registerPackets() {
        this.packetPool = new Class[256];

        this.registerPacket(ProtocolInfo.ADD_ENTITY_PACKET, AddEntityPacket.class);
        this.registerPacket(ProtocolInfo.ADD_HANGING_ENTITY_PACKET, AddHangingEntityPacket.class);
        this.registerPacket(ProtocolInfo.ADD_ITEM_ENTITY_PACKET, AddItemEntityPacket.class);
        this.registerPacket(ProtocolInfo.ADD_ITEM_PACKET, AddItemPacket.class);
        this.registerPacket(ProtocolInfo.ADD_PAINTING_PACKET, AddPaintingPacket.class);
        this.registerPacket(ProtocolInfo.ADD_PLAYER_PACKET, AddPlayerPacket.class);
        this.registerPacket(ProtocolInfo.ADVENTURE_SETTINGS_PACKET, AdventureSettingsPacket.class);
        this.registerPacket(ProtocolInfo.ANIMATE_PACKET, AnimatePacket.class);
        this.registerPacket(ProtocolInfo.AVAILABLE_COMMANDS_PACKET, AvailableCommandsPacket.class);
        this.registerPacket(ProtocolInfo.BATCH_PACKET, BatchPacket.class);
        this.registerPacket(ProtocolInfo.BLOCK_ENTITY_DATA_PACKET, BlockEntityDataPacket.class);
        this.registerPacket(ProtocolInfo.BLOCK_EVENT_PACKET, BlockEventPacket.class);
        this.registerPacket(ProtocolInfo.BLOCK_PICK_REQUEST_PACKET, BlockPickRequestPacket.class);
        this.registerPacket(ProtocolInfo.BOSS_EVENT_PACKET, BossEventPacket.class);
        this.registerPacket(ProtocolInfo.CHANGE_DIMENSION_PACKET, ChangeDimensionPacket.class);
        this.registerPacket(ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET, ChunkRadiusUpdatedPacket.class);
        this.registerPacket(ProtocolInfo.CLIENTBOUND_MAP_ITEM_DATA_PACKET, ClientboundMapItemDataPacket.class);
        this.registerPacket(ProtocolInfo.COMMAND_STEP_PACKET, CommandStepPacket.class);
        this.registerPacket(ProtocolInfo.CONTAINER_CLOSE_PACKET, ContainerClosePacket.class);
        this.registerPacket(ProtocolInfo.CONTAINER_OPEN_PACKET, ContainerOpenPacket.class);
        this.registerPacket(ProtocolInfo.CONTAINER_SET_CONTENT_PACKET, ContainerSetContentPacket.class);
        this.registerPacket(ProtocolInfo.CONTAINER_SET_DATA_PACKET, ContainerSetDataPacket.class);
        this.registerPacket(ProtocolInfo.CONTAINER_SET_SLOT_PACKET, ContainerSetSlotPacket.class);
        this.registerPacket(ProtocolInfo.CRAFTING_DATA_PACKET, CraftingDataPacket.class);
        this.registerPacket(ProtocolInfo.CRAFTING_EVENT_PACKET, CraftingEventPacket.class);
        this.registerPacket(ProtocolInfo.DISCONNECT_PACKET, DisconnectPacket.class);
        this.registerPacket(ProtocolInfo.DROP_ITEM_PACKET, DropItemPacket.class);
        this.registerPacket(ProtocolInfo.ENTITY_EVENT_PACKET, EntityEventPacket.class);
        this.registerPacket(ProtocolInfo.ENTITY_FALL_PACKET, EntityFallPacket.class);
        this.registerPacket(ProtocolInfo.EXPLODE_PACKET, ExplodePacket.class);
        this.registerPacket(ProtocolInfo.FULL_CHUNK_DATA_PACKET, FullChunkDataPacket.class);
        this.registerPacket(ProtocolInfo.GAME_RULES_CHANGED_PACKET, GameRulesChangedPacket.class);
        this.registerPacket(ProtocolInfo.HURT_ARMOR_PACKET, HurtArmorPacket.class);
        this.registerPacket(ProtocolInfo.INTERACT_PACKET, InteractPacket.class);
        this.registerPacket(ProtocolInfo.INVENTORY_ACTION_PACKET, InventoryActionPacket.class);
        this.registerPacket(ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET, ItemFrameDropItemPacket.class);
        this.registerPacket(ProtocolInfo.LEVEL_EVENT_PACKET, LevelEventPacket.class);
        this.registerPacket(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET, LevelSoundEventPacket.class);
        this.registerPacket(ProtocolInfo.LOGIN_PACKET, LoginPacket.class);
        this.registerPacket(ProtocolInfo.MAP_INFO_REQUEST_PACKET, MapInfoRequestPacket.class);
        this.registerPacket(ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET, MobArmorEquipmentPacket.class);
        this.registerPacket(ProtocolInfo.MOB_EQUIPMENT_PACKET, MobEquipmentPacket.class);
        this.registerPacket(ProtocolInfo.MOVE_ENTITY_PACKET, MoveEntityPacket.class);
        this.registerPacket(ProtocolInfo.MOVE_PLAYER_PACKET, MovePlayerPacket.class);
        this.registerPacket(ProtocolInfo.PLAYER_ACTION_PACKET, PlayerActionPacket.class);
        this.registerPacket(ProtocolInfo.PLAYER_INPUT_PACKET, PlayerInputPacket.class);
        this.registerPacket(ProtocolInfo.PLAYER_LIST_PACKET, PlayerListPacket.class);
        this.registerPacket(ProtocolInfo.PLAY_SOUND_PACKET, PlaySoundPacket.class);
        this.registerPacket(ProtocolInfo.PLAY_STATUS_PACKET, PlayStatusPacket.class);
        this.registerPacket(ProtocolInfo.REMOVE_BLOCK_PACKET, RemoveBlockPacket.class);
        this.registerPacket(ProtocolInfo.REMOVE_ENTITY_PACKET, RemoveEntityPacket.class);
        this.registerPacket(ProtocolInfo.REPLACE_ITEM_IN_SLOT_PACKET, ReplaceItemInSlotPacket.class);
        this.registerPacket(ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET, RequestChunkRadiusPacket.class);
        this.registerPacket(ProtocolInfo.RESOURCE_PACKS_INFO_PACKET, ResourcePacksInfoPacket.class);
        this.registerPacket(ProtocolInfo.RESOURCE_PACK_STACK_PACKET, ResourcePackStackPacket.class);
        this.registerPacket(ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET, ResourcePackClientResponsePacket.class);
        this.registerPacket(ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET, ResourcePackDataInfoPacket.class);
        this.registerPacket(ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET, ResourcePackChunkDataPacket.class);
        this.registerPacket(ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET, ResourcePackChunkRequestPacket.class);
        this.registerPacket(ProtocolInfo.RESPAWN_PACKET, RespawnPacket.class);
        this.registerPacket(ProtocolInfo.RIDER_JUMP_PACKET, RiderJumpPacket.class);
        this.registerPacket(ProtocolInfo.SET_COMMANDS_ENABLED_PACKET, SetCommandsEnabledPacket.class);
        this.registerPacket(ProtocolInfo.SET_DIFFICULTY_PACKET, SetDifficultyPacket.class);
        this.registerPacket(ProtocolInfo.SET_ENTITY_DATA_PACKET, SetEntityDataPacket.class);
        this.registerPacket(ProtocolInfo.SET_ENTITY_LINK_PACKET, SetEntityLinkPacket.class);
        this.registerPacket(ProtocolInfo.SET_ENTITY_MOTION_PACKET, SetEntityMotionPacket.class);
        this.registerPacket(ProtocolInfo.SET_HEALTH_PACKET, SetHealthPacket.class);
        this.registerPacket(ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET, SetPlayerGameTypePacket.class);
        this.registerPacket(ProtocolInfo.SET_SPAWN_POSITION_PACKET, SetSpawnPositionPacket.class);
        this.registerPacket(ProtocolInfo.SET_TITLE_PACKET, SetTitlePacket.class);
        this.registerPacket(ProtocolInfo.SET_TIME_PACKET, SetTimePacket.class);
        this.registerPacket(ProtocolInfo.SHOW_CREDITS_PACKET, ShowCreditsPacket.class);
        this.registerPacket(ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET, SpawnExperienceOrbPacket.class);
        this.registerPacket(ProtocolInfo.START_GAME_PACKET, StartGamePacket.class);
        this.registerPacket(ProtocolInfo.TAKE_ITEM_ENTITY_PACKET, TakeItemEntityPacket.class);
        this.registerPacket(ProtocolInfo.TEXT_PACKET, TextPacket.class);
        this.registerPacket(ProtocolInfo.UPDATE_BLOCK_PACKET, UpdateBlockPacket.class);
        this.registerPacket(ProtocolInfo.USE_ITEM_PACKET, UseItemPacket.class);
        this.registerPacket(ProtocolInfo.UPDATE_TRADE_PACKET, UpdateTradePacket.class);
    }
}
