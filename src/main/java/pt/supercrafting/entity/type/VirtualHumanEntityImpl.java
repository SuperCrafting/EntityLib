package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.equipment.VirtualEntityEquipment;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

import java.util.*;

import static net.kyori.adventure.text.Component.empty;

final class VirtualHumanEntityImpl extends VirtualEntityImpl implements VirtualHumanEntity {

    private boolean bodyAlign = true;
    private UserProfile profile;
    private Component tabName;

    public VirtualHumanEntityImpl(int id, @NotNull Location location) {
        super(id, EntityTypes.PLAYER, location);
        profile(new UserProfile(
                location.getWorld().getUID(),
                "VH_" + id,
                new ArrayList<>()
        ));
    }

    @Override
    public @NotNull VirtualEntityPacketFactory packetFactory() {
        return new PacketFactory();
    }

    @Override
    public void onSpawn(final Player player) {
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(this.getClass()), () -> {
            this.update(VirtualEntityUpdate.playerInfo(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER));
            this.update(VirtualEntityUpdate.animation(WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM));
        }, 20L);
    }

    @Override
    public @NotNull UserProfile profile() {
        return profile;
    }

    @Override
    public void profile(@NotNull UserProfile profile) {
        this.profile = Objects.requireNonNull(profile, "UserProfile cannot be null");
        this.tabName = Component.text(profile.getName(), NamedTextColor.DARK_GRAY);
    }

    @Override
    public boolean bodyAlign() {
        return bodyAlign;
    }

    @Override
    public void bodyAlign(boolean align) {
        this.bodyAlign = align;
    }

    @Override
    public void skin(@NotNull Skin skin) {
        Objects.requireNonNull(skin, "Skin cannot be null");

        UserProfile skinnedProfile = new UserProfile(
                profile.getUUID(),
                profile.getName(),
                new ArrayList<>(
                        Collections.singleton(
                                new TextureProperty("textures", skin.value(), skin.signature())
                        )
                )
        );
        this.profile(skinnedProfile);
    }

    @Override
    public WrapperPlayServerPlayerInfo.PlayerData toPlayerData() {
        return new WrapperPlayServerPlayerInfo.PlayerData(
                tabName,
                profile,
                GameMode.CREATIVE,
                1
        );
    }

    private final class PacketFactory implements VirtualEntityPacketFactory {

        private static final EntityData<?> SKIN_FLAG = new EntityData<>(10, EntityDataTypes.BYTE, (byte) 127);

        @Override
        public Collection<PacketWrapper<?>> spawn() {
            final var human = VirtualHumanEntityImpl.this;
            Location location = human.location();

            List<PacketWrapper<?>> packets = new ArrayList<>(4);

            packets.add(new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, toPlayerData()));
            packets.add(new WrapperPlayServerSpawnPlayer(
                    id(),
                    profile.getUUID(),
                    SpigotConversionUtil.fromBukkitLocation(location),
                    new ArrayList<>(
                            List.of(SKIN_FLAG)
                    )
            ));

            VirtualEntityEquipment equipment = human.equipment();
            if (!equipment.isEmpty()) {
                packets.addAll(equipment.toUpdate().packets(human));
            }

            packets.addAll(VirtualEntityUpdate.team(
                    "npc",
                    empty(),
                    NamedTextColor.GOLD,
                    empty(),
                    empty(),
                    WrapperPlayServerTeams.NameTagVisibility.NEVER,
                    WrapperPlayServerTeams.CollisionRule.NEVER,
                    WrapperPlayServerTeams.OptionData.NONE,
                    Collections.singleton(profile.getName())
            ).packets(human));

            final var data = new ArrayList<EntityData<?>>();
            data.add(new EntityData<>(10, EntityDataTypes.BYTE, (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40)));
            final var metadataPacket = new WrapperPlayServerEntityMetadata(human.id(), data);
            packets.add(metadataPacket);

            packets.add(new WrapperPlayServerEntityHeadLook(human.id(), location.getYaw()));
            return packets;
        }

        @Override
        public Collection<PacketWrapper<?>> destroy() {
            return List.of(
                    new WrapperPlayServerDestroyEntities(id()),
                    new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER, toPlayerData())
            );
        }

        @Override
        public Collection<EntityData<?>> dataWatcher() {
            return Collections.singletonList(SKIN_FLAG);
        }

    }

}
