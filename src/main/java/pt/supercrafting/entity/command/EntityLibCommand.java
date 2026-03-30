package pt.supercrafting.entity.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pt.supercrafting.entity.EntityLib;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.type.VirtualHumanEntity;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class EntityLibCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of(
            "spawn", "spawnhuman", "list", "remove", "removeall",
            "show", "hide", "tp", "animate", "equip", "skin"
    );
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final EntityLib entityLib;
    private final Plugin plugin;

    public EntityLibCommand(final EntityLib entityLib, final Plugin plugin) {
        this.entityLib = entityLib;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "spawn" -> handleSpawn(player, args);
            case "spawnhuman" -> handleSpawnHuman(player);
            case "list" -> handleList(player);
            case "remove" -> handleRemove(player, args);
            case "removeall" -> handleRemoveAll(player);
            case "show" -> handleShow(player, args);
            case "hide" -> handleHide(player, args);
            case "tp" -> handleTp(player, args);
            case "animate" -> handleAnimate(player, args);
            case "equip" -> handleEquip(player, args);
            case "skin" -> handleSkin(player, args);
            default -> sendHelp(player);
        }

        return true;
    }

    private void handleSpawn(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /entitylib spawn <type>");
            return;
        }

        var typeName = args[1].toLowerCase();
        var entityType = EntityTypes.getByName(typeName);

        var entity = entityLib.createEntity(player.getLocation(), entityType);
        entity.visibility().addViewer(player);
        player.sendMessage(ChatColor.GREEN + "Spawned " + typeName + " (id=" + entity.id() + ")");
    }

    private void handleSpawnHuman(Player player) {
        var human = entityLib.createHuman(player.getLocation());
        human.visibility().addViewer(player);
        player.sendMessage(ChatColor.GREEN + "Spawned human NPC (id=" + human.id() + ")");
    }

    private void handleList(Player player) {
        var entities = entityLib.entities();
        if (entities.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No entities spawned.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "Entities (" + entities.size() + "):");
        for (VirtualEntity entity : entities) {
            var loc = entity.location();
            var viewers = entity.visibility().viewers().size();
            player.sendMessage(ChatColor.GRAY + "  #" + entity.id()
                    + " " + entity.type().getName().getKey()
                    + " @ " + loc.getWorld().getName()
                    + " " + (int) loc.getX() + "," + (int) loc.getY() + "," + (int) loc.getZ()
                    + " [" + viewers + " viewers]");
        }
    }

    private void handleRemove(Player player, String[] args) {
        var entity = resolveEntity(player, args);
        if (entity == null) return;

        entity.remove();
        player.sendMessage(ChatColor.GREEN + "Removed entity #" + entity.id());
    }

    private void handleRemoveAll(Player player) {
        int count = entityLib.entities().size();
        entityLib.entities().forEach(VirtualEntity::remove);
        player.sendMessage(ChatColor.GREEN + "Removed " + count + " entities.");
    }

    private void handleShow(Player player, String[] args) {
        var entity = resolveEntity(player, args);
        if (entity == null) return;

        if (entity.visibility().addViewer(player)) {
            player.sendMessage(ChatColor.GREEN + "You can now see entity #" + entity.id());
        } else {
            player.sendMessage(ChatColor.YELLOW + "You are already viewing entity #" + entity.id());
        }
    }

    private void handleHide(Player player, String[] args) {
        var entity = resolveEntity(player, args);
        if (entity == null) return;

        if (entity.visibility().removeViewer(player)) {
            player.sendMessage(ChatColor.GREEN + "Hidden entity #" + entity.id());
        } else {
            player.sendMessage(ChatColor.YELLOW + "You were not viewing entity #" + entity.id());
        }
    }

    private void handleTp(Player player, String[] args) {
        var entity = resolveEntity(player, args);
        if (entity == null) return;

        entity.location(player.getLocation());
        entity.update(VirtualEntityUpdate.teleport(player.getLocation()));
        player.sendMessage(ChatColor.GREEN + "Teleported entity #" + entity.id() + " to your location.");
    }

    private void handleAnimate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /entitylib animate <id> <animation>");
            return;
        }

        var entity = resolveEntity(player, args);
        if (entity == null) return;

        WrapperPlayServerEntityAnimation.EntityAnimationType animType;
        try {
            animType = WrapperPlayServerEntityAnimation.EntityAnimationType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Unknown animation: " + args[2] + ". Available: " +
                    String.join(
                            ", ",
                            Arrays.stream(WrapperPlayServerEntityAnimation.EntityAnimationType.values())
                                    .map(Enum::name)
                                    .toList()));
            return;
        }

        entity.update(VirtualEntityUpdate.animation(animType));
        player.sendMessage(ChatColor.GREEN + "Played animation " + animType.name() + " on entity #" + entity.id());
    }

    private void handleEquip(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /entitylib equip <id> <slot>");
            return;
        }

        var entity = resolveEntity(player, args);
        if (entity == null) return;

        EquipmentSlot slot;
        try {
            slot = EquipmentSlot.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Unknown slot: " + args[2] + ". Available: " + String.join(
                    ", ",
                    Arrays.stream(EquipmentSlot.values())
                            .map(Enum::name)
                            .toList()));
            return;
        }

        ItemStack held = player.getItemInHand();
        var update = entity.equipment().set(slot, held == null || held.getType() == Material.AIR ? null : held);
        entity.update(update);
        player.sendMessage(ChatColor.GREEN + "Equipped " + (held != null ? held.getType().name() : "nothing")
                + " to slot " + slot.name() + " on entity #" + entity.id());
    }

    private void handleSkin(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /entitylib skin <id> <username>");
            return;
        }

        var entity = resolveEntity(player, args);
        if (entity == null) return;

        if (!(entity instanceof VirtualHumanEntity human)) {
            player.sendMessage(ChatColor.RED + "Entity #" + entity.id() + " is not a human NPC.");
            return;
        }

        String username = args[2];
        player.sendMessage(ChatColor.YELLOW + "Fetching skin for " + username + "...");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String uuid = fetchUuid(username);
                if (uuid == null) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                            player.sendMessage(ChatColor.RED + "Player '" + username + "' not found."));
                    return;
                }

                String[] skinData = fetchSkin(uuid);
                if (skinData == null) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                            player.sendMessage(ChatColor.RED + "Could not retrieve skin data for " + username + "."));
                    return;
                }

                Bukkit.getScheduler().runTask(plugin, () -> {
                    human.skin(new VirtualHumanEntity.Skin(skinData[0], skinData[1]));

                    // Re-spawn for current viewers so the new skin is applied
                    var viewers = List.copyOf(human.visibility().viewers());
                    for (Player viewer : viewers) {
                        human.visibility().removeViewer(viewer);
                        human.visibility().addViewer(viewer);
                    }

                    player.sendMessage(ChatColor.GREEN + "Skin of entity #" + human.id() + " set to " + username + ".");
                });
            } catch (IOException | InterruptedException e) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(ChatColor.RED + "Failed to fetch skin: " + e.getMessage()));
            }
        });
    }

    /**
     * Returns the trimmed UUID string for a username, or null if not found.
     */
    private String fetchUuid(String username) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) return null;

        return OBJECT_MAPPER.readTree(response.body()).get("id").asText();
    }

    /**
     * Returns [value, signature] for the skin textures property, or null on failure.
     */
    private String[] fetchSkin(String uuid) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) return null;

        var properties = OBJECT_MAPPER.readTree(response.body()).get("properties");
        for (var prop : properties) {
            if ("textures".equals(prop.get("name").asText())) {
                String value = prop.get("value").asText();
                String signature = prop.has("signature") ? prop.get("signature").asText() : null;
                return new String[]{value, signature};
            }
        }
        return null;
    }

    private VirtualEntity resolveEntity(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /entitylib " + args[0] + " <id>");
            return null;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid id: " + args[1]);
            return null;
        }

        for (VirtualEntity entity : entityLib.entities()) {
            if (entity.id() == id) return entity;
        }

        player.sendMessage(ChatColor.RED + "No entity with id " + id);
        return null;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "--- EntityLib Commands ---");
        player.sendMessage(ChatColor.YELLOW + "/entitylib spawn <type>" + ChatColor.GRAY + " - Spawn an entity");
        player.sendMessage(ChatColor.YELLOW + "/entitylib spawnhuman" + ChatColor.GRAY + " - Spawn a human NPC");
        player.sendMessage(ChatColor.YELLOW + "/entitylib list" + ChatColor.GRAY + " - List all entities");
        player.sendMessage(ChatColor.YELLOW + "/entitylib remove <id>" + ChatColor.GRAY + " - Remove an entity");
        player.sendMessage(ChatColor.YELLOW + "/entitylib removeall" + ChatColor.GRAY + " - Remove all entities");
        player.sendMessage(ChatColor.YELLOW + "/entitylib show <id>" + ChatColor.GRAY + " - Add yourself as viewer");
        player.sendMessage(ChatColor.YELLOW + "/entitylib hide <id>" + ChatColor.GRAY + " - Remove yourself as viewer");
        player.sendMessage(ChatColor.YELLOW + "/entitylib tp <id>" + ChatColor.GRAY + " - Teleport entity to you");
        player.sendMessage(ChatColor.YELLOW + "/entitylib animate <id> <animation>" + ChatColor.GRAY + " - Play an animation");
        player.sendMessage(ChatColor.YELLOW + "/entitylib equip <id> <slot>" + ChatColor.GRAY + " - Equip held item to slot");
        player.sendMessage(ChatColor.YELLOW + "/entitylib skin <id> <username>" + ChatColor.GRAY + " - Set human NPC skin");
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) return List.of();

        if (args.length == 1) {
            return filterPrefix(SUBCOMMANDS, args[0]);
        }

        if (args.length == 2) {
            return switch (args[0].toLowerCase()) {
                case "spawn" -> filterPrefix(
                        EntityTypes.values()
                                .stream()
                                .map(entityType -> entityType.getName().getKey())
                                .toList(), args[1]);
                case "remove", "show", "hide", "tp", "animate", "equip", "skin" -> entityIdSuggestions(args[1]);
                default -> List.of();
            };
        }

        if (args.length == 3) {
            return switch (args[0].toLowerCase()) {
                case "animate" ->
                        filterPrefix(Arrays.stream(WrapperPlayServerEntityAnimation.EntityAnimationType.values())
                                .map(Enum::name)
                                .toList(), args[2]);
                case "equip" -> filterPrefix(Arrays.stream(EquipmentSlot.values())
                        .map(Enum::name)
                        .toList(), args[2]);
                default -> List.of();
            };
        }

        return List.of();
    }

    private List<String> entityIdSuggestions(String prefix) {
        return entityLib.entities().stream()
                .map(e -> String.valueOf(e.id()))
                .filter(id -> id.startsWith(prefix))
                .collect(Collectors.toList());
    }

    private List<String> filterPrefix(List<String> options, String prefix) {
        if (prefix.isEmpty()) return new ArrayList<>(options);
        return options.stream()
                .filter(s -> s.startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
