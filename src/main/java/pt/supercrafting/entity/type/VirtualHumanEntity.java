package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public sealed interface VirtualHumanEntity extends VirtualEntity permits VirtualHumanEntityImpl {

    static VirtualHumanEntity create(int id, @NotNull Location location) {
        return new VirtualHumanEntityImpl(id, location);
    }

    @NotNull UserProfile profile();

    void profile(@NotNull UserProfile profile);

    WrapperPlayServerPlayerInfo.PlayerData toPlayerData();

    boolean bodyAlign();

    void bodyAlign(boolean align);

    void skin(@NotNull Skin skin);

    record Skin(@NotNull String value, @Nullable String signature) {

        public Skin(@NotNull String value) {
            this(value, null);
        }

        public Skin(@NotNull String value, @Nullable String signature) {
            this.value = Objects.requireNonNull(value, "Skin value cannot be null");
            this.signature = signature;
        }

    }

}
