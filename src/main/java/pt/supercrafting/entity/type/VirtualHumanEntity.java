package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.protocol.player.UserProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public sealed interface VirtualHumanEntity extends VirtualEntity permits VirtualHumanEntityImpl {

    @NotNull UserProfile profile();
    void profile(@NotNull UserProfile profile);

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
