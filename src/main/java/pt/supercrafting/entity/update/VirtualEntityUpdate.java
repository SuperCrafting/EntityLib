package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
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

public sealed interface VirtualEntityUpdate permits AnimationUpdate, AttachUpdate, CollectUpdate, EquipmentUpdate, HeadRotationUpdate, MetadataUpdate, MoveUpdate, TeamUpdate, TeleportUpdate, VelocityUpdate {

    @NotNull
    Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity);

    @NotNull
    static VirtualEntityUpdate animation(@NotNull WrapperPlayServerEntityAnimation.EntityAnimationType type){
        return new AnimationUpdate(type);
    }

    @NotNull
    static VirtualEntityUpdate attach(int passengerId, boolean leash) {
        return new AttachUpdate(passengerId, leash);
    }

    @NotNull
    static VirtualEntityUpdate attach(int passengerId) {
        return new AttachUpdate(passengerId, false);
    }

    @NotNull
    static VirtualEntityUpdate collect(int pickedId, int count) {
        return new CollectUpdate(pickedId, count);
    }

    @NotNull
    static VirtualEntityUpdate collide(int pickedId) {
        return new CollectUpdate(pickedId, 1);
    }

    @NotNull
    static VirtualEntityUpdate equipment(@NotNull Map<@NotNull EquipmentSlot, @Nullable ItemStack> equipment) {
        return new EquipmentUpdate(equipment);
    }

    @NotNull
    static VirtualEntityUpdate equipment(@NotNull EquipmentSlot slot, @Nullable ItemStack stack) {
        return new EquipmentUpdate(Collections.singletonMap(
                Objects.requireNonNull(slot, "slot cannot be null"),
                stack
        ));
    }

    @NotNull
    static VirtualEntityUpdate headRotation(float yaw) {
        return new HeadRotationUpdate(yaw);
    }

    @NotNull
    static VirtualEntityUpdate metadata(@NotNull Collection<@NotNull EntityData<?>> data) {
        Objects.requireNonNull(data, "data cannot be null");
        return new MetadataUpdate(new ArrayList<>(data));
    }

    @NotNull
    static VirtualEntityUpdate metadata() {
        return MetadataUpdate.BUKKIT_ON_THE_FLY;
    }

    @NotNull
    static VirtualEntityUpdate move(@NotNull Vector vector, boolean onGround) {
        return new MoveUpdate(vector, 0, 0, onGround);
    }

    static VirtualEntityUpdate move(@NotNull Vector vector) {
        return new MoveUpdate(vector, 0, 0, false);
    }

    @NotNull
    static VirtualEntityUpdate move(@NotNull Vector vector, float yaw, float pitch, boolean onGround) {
        return new MoveUpdate(vector, yaw, pitch, onGround);
    }

    @NotNull
    static VirtualEntityUpdate move(@NotNull Vector vector, float yaw, float pitch){
        return new MoveUpdate(vector, yaw, pitch, false);
    }

    @NotNull
    static VirtualEntityUpdate team(@NotNull String id, @NotNull Component name, @NotNull NamedTextColor color, @Nullable Component prefix, @Nullable Component suffix, @NotNull WrapperPlayServerTeams.NameTagVisibility tagVisibility, @Nullable WrapperPlayServerTeams.CollisionRule collisionRule, @Nullable WrapperPlayServerTeams.OptionData optionData, @NotNull Collection<String> members) {
        return new TeamUpdate(id, name, color, prefix, suffix, tagVisibility, collisionRule, optionData, members);
    }

    @NotNull
    static VirtualEntityUpdate team(@NotNull String id, @NotNull Component name, @NotNull NamedTextColor color, @Nullable Component suffix, @Nullable Component prefix, @NotNull Collection<String> members) {
        return new TeamUpdate(id, name, color, prefix, suffix, WrapperPlayServerTeams.NameTagVisibility.ALWAYS, WrapperPlayServerTeams.CollisionRule.NEVER, WrapperPlayServerTeams.OptionData.NONE, members);
    }

    @NotNull
    static VirtualEntityUpdate teleport(@NotNull Location location, boolean onGround) {
        return new TeleportUpdate(location, onGround);
    }

    @NotNull
    static VirtualEntityUpdate teleport(@NotNull Location location) {
        return new TeleportUpdate(location, false);
    }

    @NotNull
    static VirtualEntityUpdate velocity(@NotNull Vector vector) {
        return new VelocityUpdate(vector);
    }

}
