package pt.supercrafting.entity.interaction;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.EntityLib;

public final class VirtualEntityPacketListener extends PacketListenerAbstract {

    private final EntityLib entityLib;

    public VirtualEntityPacketListener(final EntityLib entityLib) {
        this.entityLib = entityLib;
    }

    @Override
    public void onPacketReceive(final @NotNull PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            final var packet = new WrapperPlayClientInteractEntity(event);
            this.entityLib.entityById(packet.getEntityId()).ifPresent(entity -> {
                for (final var interaction : entity.interactions()) {
                    interaction.onInteract(event.getUser(), packet.getAction(), entity);
                }
            });
        }
    }
}
