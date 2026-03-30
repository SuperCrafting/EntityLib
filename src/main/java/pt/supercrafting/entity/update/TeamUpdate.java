package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

record TeamUpdate(@NotNull String id, @NotNull Component name, @NotNull NamedTextColor color,
                  @Nullable Component prefix, @Nullable Component suffix,
                  @NotNull WrapperPlayServerTeams.NameTagVisibility tagVisibility,
                  @NotNull WrapperPlayServerTeams.CollisionRule collisionRule,
                  @Nullable WrapperPlayServerTeams.OptionData optionData,
                  @NotNull Collection<String> members) implements VirtualEntityUpdate {

    public TeamUpdate(@NotNull String id, @NotNull Component name, @NotNull NamedTextColor color, @Nullable Component prefix, @Nullable Component suffix, @NotNull WrapperPlayServerTeams.NameTagVisibility tagVisibility, @NotNull WrapperPlayServerTeams.CollisionRule collisionRule, @Nullable WrapperPlayServerTeams.OptionData optionData, @NotNull Collection<String> members) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.color = Objects.requireNonNull(color, "color cannot be null");
        this.prefix = Objects.requireNonNullElse(prefix, Component.empty());
        this.suffix = Objects.requireNonNullElse(suffix, Component.empty());
        this.tagVisibility = Objects.requireNonNull(tagVisibility, "tagVisibility cannot be null");
        this.collisionRule = Objects.requireNonNull(collisionRule, "collisionRule cannot be null");
        this.optionData = Objects.requireNonNull(optionData, "optionData cannot be null");
        if (Objects.requireNonNull(members, "members cannot be null").isEmpty())
            throw new IllegalArgumentException("members cannot be empty");
        this.members = Collections.unmodifiableCollection(members);
    }

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {
        return List.of(
                new WrapperPlayServerTeams(
                        id,
                        WrapperPlayServerTeams.TeamMode.CREATE,
                        toScoreboardTeamInfo()
                ),
                new WrapperPlayServerTeams(
                        id,
                        WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                        toScoreboardTeamInfo(),
                        members
                )
        );
    }

    public WrapperPlayServerTeams.ScoreBoardTeamInfo toScoreboardTeamInfo() {
        return new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                name,
                prefix,
                suffix,
                tagVisibility,
                collisionRule,
                color,
                optionData
        );
    }

}
