package com.elvarg.game.definition;

import com.elvarg.game.World;
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
public class NPCSpawnLoader {

    public static int count = 0;

    public static void loadJson() {

        File spawns = new File("data/definitions/npc_spawns/");
        int files = spawns.listFiles().length;
        for (File file : spawns.listFiles()) {
            if (file.getName().endsWith(".json")) {
                try {
                    JsonNPC[] s = new Gson().fromJson(new FileReader(file), JsonNPC[].class);

                    for (JsonNPC sp : s) {
                        if (sp == null)
                            continue;
                        Location loc = sp.position[0];
                        NPC npc = new NPC(sp.id, loc);
                        npc.setDirection(sp.dir());
                        npc.setDescription(sp.description);
                        World.getAddNPCQueue().add(npc);
                        count++;
                    }
                } catch (JsonParseException e) {
                    throw new RuntimeException("Failed to load npc spawn: " + file.getAbsolutePath() + " (" + e + ")");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (file.isDirectory()) {
                loadJson();
            }
        }
        System.err.println("Loaded " + Misc.format(count) + " NPC Spawns for "+Misc.format(files)+" Regions.");
    }
}
