package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.*;

public interface VirtualEntityUpdate {

    @NotNull
    Collection<PacketWrapper<?>> packets(final @NotNull VirtualEntity entity);

    @NotNull
    static VirtualEntityUpdate animation(final WrapperPlayServerEntityAnimation.@NotNull EntityAnimationType type) {
        return new AnimationUpdate(type);
    }

    @NotNull
    static VirtualEntityUpdate attach(final int passengerId, final boolean leash) {
        return new AttachUpdate(passengerId, leash);
    }

    @NotNull
    static VirtualEntityUpdate attach(final int passengerId) {
        return new AttachUpdate(passengerId, false);
    }

    @NotNull
    static VirtualEntityUpdate collect(final int pickedId, final int count) {
        return new CollectUpdate(pickedId, count);
    }

    @NotNull
    static VirtualEntityUpdate collect(final int pickedId) {
        return new CollectUpdate(pickedId, 1);
    }

    @NotNull
    static VirtualEntityUpdate equipment(final Map<@NotNull EquipmentSlot, @Nullable ItemStack> equipment) {
        return new EquipmentUpdate(equipment);
    }

    @NotNull
    static VirtualEntityUpdate equipment(final @NotNull EquipmentSlot slot, @Nullable ItemStack stack) {
        return new EquipmentUpdate(Collections.singletonMap(
                Objects.requireNonNull(slot, "slot cannot be null"),
                stack
        ));
    }

    @NotNull
    static VirtualEntityUpdate headRotation(final float yaw) {
        return new HeadRotationUpdate(yaw);
    }

    @NotNull
    static VirtualEntityUpdate metadata(final Collection<@NotNull EntityData<?>> data) {
        Objects.requireNonNull(data, "data cannot be null");
        return new MetadataUpdate(new ArrayList<>(data));
    }

    @NotNull
    static VirtualEntityUpdate metadata() {
        return MetadataUpdate.BUKKIT_ON_THE_FLY;
    }

    @NotNull
    static VirtualEntityUpdate move(final @NotNull Vector vector, boolean onGround) {
        return new MoveUpdate(vector, 0, 0, onGround);
    }

    static VirtualEntityUpdate move(final @NotNull Vector vector) {
        return new MoveUpdate(vector, 0, 0, false);
    }

    @NotNull
    static VirtualEntityUpdate move(final @NotNull Vector vector, float yaw, float pitch, boolean onGround) {
        return new MoveUpdate(vector, yaw, pitch, onGround);
    }

    @NotNull
    static VirtualEntityUpdate move(final @NotNull Vector vector, float yaw, float pitch) {
        return new MoveUpdate(vector, yaw, pitch, false);
    }

    @NotNull
    static VirtualEntityUpdate team(final @NotNull String id,
                                    final @NotNull Component name,
                                    final @NotNull NamedTextColor color,
                                    final @Nullable Component prefix,
                                    final @Nullable Component suffix,
                                    final WrapperPlayServerTeams.@NotNull NameTagVisibility tagVisibility,
                                    final WrapperPlayServerTeams.@NotNull CollisionRule collisionRule,
                                    final WrapperPlayServerTeams.@NotNull OptionData optionData,
                                    final Collection<@NotNull String> members) {
        return new TeamUpdate(id, name, color, prefix, suffix, tagVisibility, collisionRule, optionData, members);
    }

    @NotNull
    static VirtualEntityUpdate team(final @NotNull String id,
                                    final @NotNull Component name,
                                    final @NotNull NamedTextColor color,
                                    final @Nullable Component suffix,
                                    final @Nullable Component prefix,
                                    final Collection<@NotNull String> members) {
        return new TeamUpdate(id, name, color, prefix, suffix,
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.NEVER,
                WrapperPlayServerTeams.OptionData.NONE,
                members);
    }

    @NotNull
    static VirtualEntityUpdate teleport(final @NotNull Location location, boolean onGround) {
        return new TeleportUpdate(location, onGround);
    }

    @NotNull
    static VirtualEntityUpdate teleport(final @NotNull Location location) {
        return new TeleportUpdate(location, false);
    }

    @NotNull
    static VirtualEntityUpdate velocity(final @NotNull Vector vector) {
        return new VelocityUpdate(vector);
    }

    static @NotNull VirtualEntityUpdate playerInfo(final WrapperPlayServerPlayerInfo.@NotNull Action action) {
        return new PlayerInfoUpdate(action);
    }

}
