# EntityLib

EntityLib is a Spigot/Paper library for creating and managing **virtual entities** on a Minecraft server. These entities exist only at the protocol level — they are visible to players through direct packet sending, without ever being added to the world.

## Key Features

- **Virtual Entities**: Create any entity type (mobs, players, custom) visible to players without server-side spawning.
- **Player NPCs**: Spawn player-like entities with custom skins, tab-list management, and nametag hiding.
- **Equipment System**: Equip and update entity gear slots dynamically.
- **Interactions**: Register callbacks for player right-clicks and attacks on virtual entities.
- **Tick Actions**: Run logic every tick per entity (movement, animation, metadata updates).
- **Visibility Control**: Show or hide entities to specific players based on custom rules.

---

## Examples

### Basic Entity

A simple virtual zombie at a location:

```java
VirtualEntity entity = entityLib.createEntity(location, EntityType.ZOMBIE);
```

### Player NPC

A player-like NPC with a custom skin:

```java
VirtualHumanEntity human = entityLib.createHuman(location);
human.skin(new Skin("base64_textures", "signature"));
```

### Wrapping a Bukkit Entity

Use a real Bukkit entity (metadata synced automatically):

```java
VirtualBukkitEntity<Zombie> zombie = entityLib.createBukkit(location, Zombie.class);

zombie.modify(z -> z.setCustomName(Component.text("My Zombie")));
```

---

## Examples

### Interactions

Register a callback when a player interacts with an entity:

```java
entity.interactions().registerInteraction((player, virtualEntity, action) -> {
    player.sendMessage("You interacted with the entity!");
    // action can be: INTERACT, INTERACT_AT, ATTACK
});
```

### Equipment

Equip items on a virtual entity:

```java
VirtualEntityEquipment equipment = entity.equipment();

// Equip a helmet
equipment.set(EquipmentSlot.HELMET, new ItemStack(Material.DIAMOND_HELMET));

// Apply the change to all viewers
entity.update(equipment.set(EquipmentSlot.MAIN_HAND, sword));

// Clear all equipment
equipment.clear();
entity.update(equipment.toUpdate());
```

Available slots: `HELMET`, `CHESTPLATE`, `LEGGINGS`, `BOOTS`, `MAIN_HAND`, `OFF_HAND` (1.9+).

### Tick Actions

Run logic every tick (e.g. move an entity upward):

```java
entity.tickingActions().registerAction(currentTick -> {
    Location loc = entity.location().add(0, 0.05, 0);
    entity.location(loc);
    entity.update(VirtualEntityUpdate.teleport(loc));
});
```

---

## Examples

### Updates

Apply visual changes to entities through update objects:

```java
// Teleport
entity.update(VirtualEntityUpdate.teleport(newLocation));

// Relative movement
entity.update(VirtualEntityUpdate.move(new Vector(0, 0.1, 0)));

// Play an animation
entity.update(VirtualEntityUpdate.animation(AnimationType.SWING_MAIN_HAND));

// Update head rotation
entity.update(VirtualEntityUpdate.headRotation(90, 0));
```

### Visibility

Control which players see each entity:

```java
// Add a rule — only show within 50 blocks
entity.visibility().addRule((ent, player) -> {
    return player.getLocation().distanceSquared(ent.location()) < 50 * 50;
});

// Listen for show/hide events
entity.visibility().addListener(new VirtualEntityVisibilityListener() {
    @Override
    public void onShow(VirtualEntity entity, Player player) {
        player.sendMessage("You discovered an NPC!");
    }

    @Override
    public void onHide(VirtualEntity entity, Player player) {
        player.sendMessage("An NPC disappeared.");
    }
});
```

Rules use **AND logic**: if any single rule returns `false`, the player won't see the entity.

---

## Compatibility

| Minecraft | Support |
|---|---|
| 1.9.4+ | Supported |
| 1.16+ | Multi-slot equipment packets |
| Spigot / Paper | Supported |


