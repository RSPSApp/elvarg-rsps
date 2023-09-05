package com.elvarg.game.definition.loader.impl;

import com.elvarg.game.World;
import com.elvarg.game.definition.NpcSpawnDefinition;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.model.Location;
import com.elvarg.util.Misc;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author Ynneh | 13/03/2023 - 14:43
 * <https://github.com/drhenny>
 */
public class NpcSpawnDefinitionLoader {

    public static int count = 0;

    public static void load() {
        long startTime = System.currentTimeMillis();

        try {
            NpcSpawnDefinition[] s = new Gson().fromJson(new FileReader("data/definitions/npc_spawns_osrs.json"), NpcSpawnDefinition[].class);

            for (NpcSpawnDefinition sp : s) {
                if (sp == null)
                    continue;
                Location loc = sp.position[0];
                NPC npc = NPC.create(sp.id, loc);
                npc.setFace(sp.dir());
                npc.setDescription(sp.description);
                World.getAddNPCQueue().add(npc);
                count++;
            }
        } catch (JsonParseException e) {
            throw new RuntimeException("Failed to load npc spawn (" + e + ")");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.err.println("Loaded " + Misc.format(count) + " NPC Spawns in "+elapsedTime+" ms.");
    }
}
