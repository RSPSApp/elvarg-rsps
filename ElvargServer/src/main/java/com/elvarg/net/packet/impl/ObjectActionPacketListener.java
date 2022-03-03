package com.elvarg.net.packet.impl;

import com.elvarg.Server;
import com.elvarg.game.collision.RegionManager;
import com.elvarg.game.content.ArmorAnimator;
import com.elvarg.game.content.minigames.FightCaves;
import com.elvarg.game.content.skill.skillable.impl.Smithing;
import com.elvarg.game.content.skill.skillable.impl.Smithing.Bar;
import com.elvarg.game.content.skill.skillable.impl.Smithing.EquipmentMaking;
import com.elvarg.game.content.skill.skillable.impl.Thieving.StallThieving;
import com.elvarg.game.content.skill.skillable.impl.Woodcutting;
import com.elvarg.game.definition.ObjectDefinition;
import com.elvarg.game.definition.ObjectDefinition.ObjectFace;
import com.elvarg.game.definition.ObjectDefinition.ObjectType;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.object.LadderHandler;
import com.elvarg.game.entity.impl.object.LadderHandler.Ladders;
import com.elvarg.game.entity.impl.object.MapObjects;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.ForceMovement;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.MagicSpellbook;
import com.elvarg.game.model.Skill;
import com.elvarg.game.model.areas.impl.CombatRingArea;
import com.elvarg.game.model.areas.impl.PrivateArea;
import com.elvarg.game.model.areas.impl.WildernessArea;
import com.elvarg.game.model.movement.WalkToAction;
import com.elvarg.game.model.rights.PlayerRights;
import com.elvarg.game.model.teleportation.TeleportHandler;
import com.elvarg.game.model.teleportation.TeleportType;
import com.elvarg.game.task.TaskManager;
import com.elvarg.game.task.impl.ForceMovementTask;
import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketConstants;
import com.elvarg.net.packet.PacketExecutor;
import com.elvarg.util.ObjectIdentifiers;
import org.apache.commons.lang.ArrayUtils;


/**
 * This packet listener is called when a player clicked on a game object.
 *
 * @author relex lawl
 */

public class ObjectActionPacketListener extends ObjectIdentifiers implements PacketExecutor {

	/**
	 * Handles the first click option on an object.
	 *
	 * @param player
	 *            The player that clicked on the object.
	 * @param object
	 *            The packet containing the object's information.
	 */
    private static void firstClick(Player player, GameObject object) {
        // Skills..
        if (player.getSkillManager().startSkillable(object)) {
            return;
        }

        switch (object.getId()) {
        case MAGICAL_ANIMATOR:
            ArmorAnimator.animateArmor(player);
            break;
        case COMBAT_RING:
            CombatRingArea.handleRingFirstClick(player);
            break;
        case KBD_LADDER_DOWN:
            TeleportHandler.teleport(player, new Location(3069, 10255), TeleportType.LADDER_DOWN, false);
            break;
        case KBD_LADDER_UP:
            TeleportHandler.teleport(player, new Location(3017, 3850), TeleportType.LADDER_UP, false);
            break;
        case KBD_ENTRANCE_LEVER:
            if (!player.getCombat().getTeleBlockTimer().finished()) {
                player.getPacketSender().sendMessage("A magical spell is blocking you from teleporting.");
                return;
            }
            TeleportHandler.teleport(player, new Location(2271, 4680), TeleportType.LEVER, false);
            break;
        case KBD_EXIT_LEVER:
            TeleportHandler.teleport(player, new Location(3067, 10253), TeleportType.LEVER, false);
            break;
        case FIGHT_CAVES_ENTRANCE:
            player.moveTo(FightCaves.ENTRANCE.clone());
            FightCaves.start(player);
            break;
        case FIGHT_CAVES_EXIT:
            player.moveTo(FightCaves.EXIT);
            break;
        case PORTAL_51:
            //DialogueManager.sendStatement(player, "Construction will be avaliable in the future.");
            break;
        case ANVIL:
            EquipmentMaking.openInterface(player);
            break;
        case ALTAR:
        case CHAOS_ALTAR_2:
            player.performAnimation(new Animation(645));
            if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                    .getMaxLevel(Skill.PRAYER)) {
                player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                        player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
                player.getPacketSender().sendMessage("You recharge your Prayer points.");
            }
            break;

        case BANK_CHEST:
            player.getBank(player.getCurrentBankTab()).open();
            break;

        case DITCH_PORTAL:
            player.getPacketSender().sendMessage("You are teleported to the Wilderness ditch.");
            player.moveTo(new Location(3087, 3520));
            break;

        case WILDERNESS_DITCH:
            player.getMovementQueue().reset();
            if (player.getForceMovement() == null && player.getClickDelay().elapsed(2000)) {
                final Location crossDitch = new Location(player.getLocation().getX() < 3835 ? 3 : -3, 0);
                TaskManager.submit(new ForceMovementTask(player, 3, new ForceMovement(player.getLocation().clone(),
                        crossDitch, 0, 70, crossDitch.getX() == 3 ? 1 : 4, 6132)));
                player.getClickDelay().reset();
            }
            break;

        case MAGICAL_ALTAR:
           /* //DialogueManager.start(player, 8);
            player.setDialogueOptions(new DialogueOptions() {
                @Override
                public void handleOption(Player player, int option) {
                    switch (option) {
                    case 1: // Normal spellbook option
                        player.getPacketSender().sendInterfaceRemoval();
                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
                        break;
                    case 2: // Ancient spellbook option
                        player.getPacketSender().sendInterfaceRemoval();
                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
                        break;
                    case 3: // Lunar spellbook option
                        player.getPacketSender().sendInterfaceRemoval();
                        MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
                        break;
                    case 4: // Cancel option
                        player.getPacketSender().sendInterfaceRemoval();
                        break;
                    }
                }
            });*/
            break;

        case ORNATE_REJUVENATION_POOL:
            player.getPacketSender().sendMessage("You feel slightly renewed.");
            player.performGraphic(new Graphic(683));
            player.resetAttributes();
            break;

        }
    }

	/**
     * Handles the second click option on an object.
     *
     * @param player
     *            The player that clicked on the object.
     * @param object
     *            The packet containing the object's information.
     */
    private static void secondClick(Player player, GameObject object) {
        // Check thieving..
        if (StallThieving.init(player, object)) {
            return;
        }

        switch (object.getId()) {
        case PORTAL_51:
            //DialogueManager.sendStatement(player, "Construction will be avaliable in the future.");
            break;
        case FURNACE_18:
            for (Bar bar : Smithing.Bar.values()) {
                player.getPacketSender().sendInterfaceModel(bar.getFrame(), bar.getBar(), 150);
            }
            player.getPacketSender().sendChatboxInterface(2400);
            break;
        case BANK_CHEST:
        case BANK:
        case BANK_BOOTH:
        case BANK_BOOTH_2:
        case BANK_BOOTH_3:
        case BANK_BOOTH_4:
            player.getBank(player.getCurrentBankTab()).open();
            break;
        case MAGICAL_ALTAR:
            player.getPacketSender().sendInterfaceRemoval();
            MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL);
            break;
        }
	}

	/**
	 * Handles the third click option on an object.
	 *
	 * @param player
	 *            The player that clicked on the object.
	 * @param object
	 *            The packet containing the object's information.
	 */
	private static void thirdClick(Player player, GameObject object) {
		switch (object.getId()) {
        case PORTAL_51:
            //DialogueManager.sendStatement(player, "Construction will be avaliable in the future.");
            break;
        case MAGICAL_ALTAR:
            player.getPacketSender().sendInterfaceRemoval();
            MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT);
            break;
        }
	}

	/**
	 * Handles the fourth click option on an object.
	 *
	 * @param player
	 *            The player that clicked on the object.
	 * @param object
	 *            The packet containing the object's information.
	 */
	private static void fourthClick(Player player, GameObject object) {
	    switch (object.getId()) {
        case PORTAL_51:
            //DialogueManager.sendStatement(player, "Construction will be avaliable in the future.");
            break;
        case MAGICAL_ALTAR:
            player.getPacketSender().sendInterfaceRemoval();
            MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR);
            break;
        }
	}

    private static void objectInteract(Player player, int id, int x, int y, int clickType) {
        final Location location = new Location(x, y, player.getLocation().getZ());
        
        if (player.getRights() == PlayerRights.DEVELOPER) {
            player.getPacketSender().sendMessage("" + clickType + "-click object: " + id + ". " + location.toString());
        }
        
        final GameObject object = MapObjects.get(player, id, location);
        if (object == null) {
            // TODO: Re-add when mapdata is packed server side
            return;
        }

        // Get object definition
        final ObjectDefinition def = ObjectDefinition.forId(id);
        if (def == null) {
            Server.getLogger().info("ObjectDefinition for object " + id + " is null.");
            return;
        }

        player.setWalkToTask(new WalkToAction(player) {
            @Override
            public void execute() {

                // Face object..
                player.setPositionToFace(location);

                // Areas
                if (player.getArea() != null) {
                    if (player.getArea().handleObjectClick(player, id, clickType)) {
                        return;
                    }
                }

                // Ladders
                if (def.getName() != null && (def.getName().contains("Ladder") || ArrayUtils.contains(Ladders.LADDER_IDS, id) || def.getName().contains("Rope"))) {
                   if (Ladders.forObjectLoc(object.getLocation()).isPresent()) {

                       new LadderHandler(player, def, location, clickType-1,  Ladders.forObjectLoc(
                               object.getLocation()
                       ).get());
                   } else {
                       new LadderHandler(player, def, location, clickType-1 , (Ladders) null);
                   }

                    return;
                }

                switch (clickType) {
                    case 1 -> firstClick(player, object);
                    case 2 -> secondClick(player, object);
                    case 3 -> thirdClick(player, object);
                    case 4 -> fourthClick(player, object);
                }
            }
            
            @Override
            public boolean inDistance() {
                int type = object.getType();
                int orientation = object.getFace();
                int width;
                int height;
                if (orientation == 0 || orientation == 2) {
                    width = def.objectSizeX;
                    height = def.objectSizeY;
                } else {
                    width = def.objectSizeY;
                    height = def.objectSizeX;
                }
                int rotation = def.surroundings;
                if (orientation != ObjectFace.NORTH) {
                    // Don't need to work out bitwise rotation for North as its already 0
                    rotation = (rotation << orientation & 0xf) + (rotation >> 4 - orientation);
                }

                if (type == ObjectType.SOLID_OBJECTS || type == ObjectType.GROUND_OBJECTS || type == ObjectType.GROUND_DECORATIONS) {
                    return atObject(location, player.getLocation(), width, height, rotation, player.getPrivateArea(), type, orientation, def);
                }

                // For walls/fences, height and rotation shouldn't matter
                return atObject(location, player.getLocation(), 0, 0,
                        width, player.getPrivateArea(), type, orientation, def);
            }
        });
    }

    private static boolean atObject(Location objectLocaton, Location playerLocation, int width, int height, int rotation, PrivateArea privateArea, int type, int orientation, ObjectDefinition def) {
        int playerX = playerLocation.getX(), playerY = playerLocation.getY(), playerZ = playerLocation.getZ();
        int objectX = objectLocaton.getX(), objectY = objectLocaton.getY();
        int maxX = (objectX + width) - (width > 0 ? 1 : 0);
        int maxY = (objectY + height) - (height > 0 ? 1 : 0);

        if (playerX >= objectX && playerX <= maxX && playerY >= objectY && playerY <= maxY)  {
            // Player is already within reach of object, no need to keep walking
            return true;
        }

        int tolerance = 1;

        if (type == ObjectType.STRAGHT_WALLS_FENCES) {
            if (orientation == ObjectFace.SOUTH && playerX < objectX) {
                // Player will have to stand on the wall tile to be at it
                tolerance = 0;
            }

            if (orientation == ObjectFace.NORTH && playerX > objectX) {
                // Player will have to stand on the wall tile to be at it
                tolerance = 0;
            }

            if (orientation == ObjectFace.EAST && playerY < objectY) {
                tolerance = 0;
            }

            if (orientation == ObjectFace.WEST && playerY > objectY) {
                tolerance = 0;
            }
        }

        // Object east of player
        if (playerX == objectX - tolerance && playerY >= objectY && playerY <= maxY && (rotation & 8) == 0 &&
                (def.impenetrable || (RegionManager.getClipping(playerX, playerY, playerZ, privateArea) & 8) == 0)) {
            return true;
        }

        // Object west of player
        if (playerX == maxX + tolerance && playerY >= objectY && playerY <= maxY && (rotation & 2) == 0 &&
                (def.impenetrable || (RegionManager.getClipping(playerX, playerY, playerZ, privateArea) & 0x80) == 0)) {
            return true;
        }

        // Object north of player
        if(playerY == objectY - tolerance && playerX >= objectX && playerX <= maxX &&
                ((rotation & 4) == 0 || orientation == ObjectFace.WEST) &&
                (def.impenetrable || (RegionManager.getClipping(playerX, playerY, playerZ, privateArea) & 2) == 0)) {
            return true;
        }

        // Object south of player
        if (playerY == maxY + tolerance && playerX >= objectX && playerX <= maxX &&
                ((rotation & 1) == 0 || orientation == ObjectFace.EAST) &&
                (def.impenetrable || (RegionManager.getClipping(playerX, playerY, playerZ, privateArea) & 0x20) == 0)) {
            return true;
        }

        return false;
    }

	@Override
	public void execute(Player player, Packet packet) {

		if (player == null || player.getHitpoints() <= 0) {
			return;
		}

		// Make sure we aren't doing something else..
		if (player.busy()) {
			return;
		}
		
		int id, x, y;
		switch (packet.getOpcode()) {
		case PacketConstants.OBJECT_FIRST_CLICK_OPCODE:		    
		    x = packet.readLEShortA();
	        id = packet.readUnsignedShort();
	        y = packet.readUnsignedShortA();	        
			objectInteract(player, id, x, y, 1);
			break;
		case PacketConstants.OBJECT_SECOND_CLICK_OPCODE:		    
		    id = packet.readLEShortA();
	        y = packet.readLEShort();
	        x = packet.readUnsignedShortA();	        
	        objectInteract(player, id, x, y, 2);
			break;
		case PacketConstants.OBJECT_THIRD_CLICK_OPCODE:
		    x = packet.readLEShort();
	        y = packet.readShort();
	        id = packet.readLEShortA();
	        objectInteract(player, id, x, y, 3);
			break;
		case PacketConstants.OBJECT_FOURTH_CLICK_OPCODE:
		    x = packet.readLEShortA();
            id = packet.readUnsignedShortA();
            y = packet.readLEShortA();
            objectInteract(player, id, x, y, 4);
			break;
		case PacketConstants.OBJECT_FIFTH_CLICK_OPCODE:		    
			break;
		}
	}
}
