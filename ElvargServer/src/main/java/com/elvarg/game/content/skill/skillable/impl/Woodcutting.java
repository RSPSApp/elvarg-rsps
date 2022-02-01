package com.elvarg.game.content.skill.skillable.impl;

import com.elvarg.game.content.PetHandler;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.object.MapObjects;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Skill;
import com.elvarg.game.model.container.impl.Equipment;
import com.elvarg.game.task.Task;
import com.elvarg.game.task.TaskManager;
import com.elvarg.game.task.impl.TimedObjectReplacementTask;
import com.elvarg.util.ItemIdentifiers;
import com.elvarg.util.Misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Represents the Woodcutting skill.
 *
 * @author Professor Oak
 */
public class Woodcutting extends DefaultSkillable {

    /**
     * The {@link GameObject} to cut down.
     */
    private final GameObject treeObject;
    /**
     * The {@code treeObject} as an enumerated type
     * which contains information about it, such as
     * required level.
     */
    private final Tree tree;
    /**
     * The axe we're using to cut down the tree.
     */
    private Optional<Axe> axe = Optional.empty();

    /**
     * Constructs a new {@link Woodcutting}.
     *
     * @param treeObject The tree to cut down.
     * @param tree       The tree's data
     */
    public Woodcutting(GameObject treeObject, Tree tree) {
        this.treeObject = treeObject;
        this.tree = tree;
    }

    @Override
    public void start(Player player) {
        player.getPacketSender().sendMessage("You swing your axe at the tree..");
        super.start(player);
    }

    @Override
    public void startAnimationLoop(Player player) {
        Task animLoop = new Task(4, player, true) {
            @Override
            protected void execute() {
                player.performAnimation(axe.get().getAnimation());
            }
        };
        TaskManager.submit(animLoop);
        getTasks().add(animLoop);
    }

    @Override
    public void onCycle(Player player, int cycle) {

        PetHandler.onSkill(player, Skill.WOODCUTTING);

        switch (axe.get().getId()) {
            case ItemIdentifiers.BRONZE_AXE:
                case ItemIdentifiers.IRON_AXE:
                if (cycle%7 == 0) {
                    //Add logs..
                    player.getInventory().add(tree.getLogId(), 1);
                    player.getPacketSender().sendMessage("You get some logs.");
                    //Add exp..
                    player.getSkillManager().addExperience(Skill.WOODCUTTING, tree.getXpReward());
                }
                break;
            case ItemIdentifiers.STEEL_AXE:
                case ItemIdentifiers.BLACK_AXE:
                if (cycle%6 == 0) {
                    //Add logs..
                    player.getInventory().add(tree.getLogId(), 1);
                    player.getPacketSender().sendMessage("You get some logs.");
                    //Add exp..
                    player.getSkillManager().addExperience(Skill.WOODCUTTING, tree.getXpReward());
                }
                break;
            case ItemIdentifiers.MITHRIL_AXE:
                if (cycle%5 == 0) {
                    //Add logs..
                    player.getInventory().add(tree.getLogId(), 1);
                    player.getPacketSender().sendMessage("You get some logs.");
                    //Add exp..
                    player.getSkillManager().addExperience(Skill.WOODCUTTING, tree.getXpReward());
                }
                break;
            case ItemIdentifiers.ADAMANT_AXE:
                if (cycle%4 == 0) {
                    //Add logs..
                    player.getInventory().add(tree.getLogId(), 1);
                    player.getPacketSender().sendMessage("You get some logs.");
                    //Add exp..
                    player.getSkillManager().addExperience(Skill.WOODCUTTING, tree.getXpReward());
                }
                break;
            case ItemIdentifiers.RUNE_AXE:
                if (cycle%3 == 0) {
                    //Add logs..
                    player.getInventory().add(tree.getLogId(), 1);
                    player.getPacketSender().sendMessage("You get some logs.");
                    //Add exp..
                    player.getSkillManager().addExperience(Skill.WOODCUTTING, tree.getXpReward());
                }
                break;
            case ItemIdentifiers.DRAGON_AXE:
            case ItemIdentifiers.INFERNAL_AXE:
                if (cycle%2 == 0) {
                    //Add logs..
                    player.getInventory().add(tree.getLogId(), 1);
                    player.getPacketSender().sendMessage("You get some logs.");
                    //Add exp..
                    player.getSkillManager().addExperience(Skill.WOODCUTTING, tree.getXpReward());
                }
                break;
        }

    }

    @Override
    public void finishedCycle(Player player) {


        //Regular trees should always despawn.
        //Multi trees are random.
        if (!tree.isMulti() || Misc.getRandom(15) >= 2) {
            //Stop skilling..
            cancel(player);

            //Despawn object and respawn it after a short period of time..
            TaskManager.submit(new TimedObjectReplacementTask(treeObject, new GameObject(1343, treeObject.getLocation(), 10, 0, player.getPrivateArea()), tree.getRespawnTimer()));
        }
    }

    @Override
    public int cyclesRequired(Player player) {
        int cycles = tree.getCycles() + Misc.getRandom(6);
        cycles -= (int) player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) * 0.1;
        cycles -= cycles * axe.get().getSpeed();
        if (cycles < 3) {
            cycles = 3;
        }
        return cycles;
    }

    @Override
    public boolean hasRequirements(Player player) {
        //Attempt to find an axe..
        axe = Optional.empty();
        for (Axe a : Axe.values()) {
            if (player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == a.getId()
                    || player.getInventory().contains(a.getId())) {

                //If we have already found an axe,
                //don't select others that are worse or can't be used
                if (axe.isPresent()) {
                    if (player.getSkillManager().getMaxLevel(Skill.WOODCUTTING) < a.getRequiredLevel()) {
                        continue;
                    }
                    if (a.getRequiredLevel() < axe.get().getRequiredLevel()) {
                        continue;
                    }
                }

                axe = Optional.of(a);
            }
        }

        //Check if we found one..
        if (!axe.isPresent()) {
            player.getPacketSender().sendMessage("You don't have an axe which you can use.");
            return false;
        }

        //Check if we have the required level to cut down this {@code tree} using the {@link Axe} we found..
        if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < axe.get().getRequiredLevel()) {
            player.getPacketSender().sendMessage("You don't have an axe which you have the required Woodcutting level to use.");
            return false;
        }

        //Check if we have the required level to cut down this {@code tree}..
        if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < tree.getRequiredLevel()) {
            player.getPacketSender().sendMessage("You need a Woodcutting level of at least " + tree.getRequiredLevel() + " to cut this tree.");
            return false;
        }

        //Finally, check if the tree object remains there.
        //Another player may have cut it down already.
        if (!MapObjects.exists(treeObject)) {
            return false;
        }

        return super.hasRequirements(player);
    }

    @Override
    public boolean loopRequirements() {
        return true;
    }

    @Override
    public boolean allowFullInventory() {
        return false;
    }

    public GameObject getTreeObject() {
        return treeObject;
    }

    /**
     * Holds data related to the axes
     * that can be used for this skill.
     */
    public static enum Axe {
        BRONZE_AXE(1351, 1, 0.02, new Animation(879)),
        IRON_AXE(1349, 1, 0.05, new Animation(877)),
        STEEL_AXE(1353, 6, 0.10, new Animation(875)),
        BLACK_AXE(1361, 6, 0.13, new Animation(873)),
        MITHRIL_AXE(1355, 21, 0.16, new Animation(871)),
        ADAMANT_AXE(1357, 31, 0.19, new Animation(869)),
        RUNE_AXE(1359, 41, 0.21, new Animation(867)),
        DRAGON_AXE(6739, 61, 0.25, new Animation(2846)),
        INFERNAL(13241, 61, 0.3, new Animation(2117));

        private final int id;
        private final int requiredLevel;
        private final double speed;
        private final Animation animation;

        private Axe(int id, int level, double speed, Animation animation) {
            this.id = id;
            this.requiredLevel = level;
            this.speed = speed;
            this.animation = animation;
        }

        public int getId() {
            return id;
        }

        public int getRequiredLevel() {
            return requiredLevel;
        }

        public double getSpeed() {
            return speed;
        }

        public Animation getAnimation() {
            return animation;
        }
    }

    /**
     * Holds data related to the trees
     * which can be used to train this skill.
     */
    public static enum Tree {
        NORMAL(1, 25, 1511, new int[]{1276, 1277, 1278, 1279, 1280, 1282, 1283, 1284, 1285, 1286, 1289, 1290, 1291, 1315, 1316, 1318, 1319, 1330, 1331, 1332, 1365, 1383, 1384, 3033, 3034, 3035, 3036, 3881, 3882, 3883, 5902, 5903, 5904}, 11, 8, false),
        ACHEY(1, 25, 2862, new int[]{2023}, 13, 9, false),
        OAK(15, 38, 1521, new int[]{1281, 3037}, 14, 11, true),
        WILLOW(30, 68, 1519, new int[]{1308, 5551, 5552, 5553}, 15, 14, true),
        TEAK(35, 85, 6333, new int[]{9036}, 16, 16, true),
        DRAMEN(36, 88, 771, new int[]{1292}, 16, 17, true),
        MAPLE(45, 100, 1517, new int[]{1759, 4674}, 17, 18, true),
        MAHOGANY(50, 125, 6332, new int[]{9034}, 17, 20, true),
        YEW(60, 175, 1515, new int[]{1309, 1753}, 18, 28, true),
        MAGIC(75, 250, 1513, new int[]{1761}, 20, 40, true),
        REDWOOD(90, 380, 19669, new int[]{}, 22, 43, true);

        private static final Map<Integer, Tree> trees = new HashMap<Integer, Tree>();

        static {
            for (Tree t : Tree.values()) {
                for (int obj : t.objects) {
                    trees.put(obj, t);
                }
                trees.put(t.getLogId(), t);
            }
        }

        private final int[] objects;
        private final int requiredLevel;
        private final int xpReward;
        private final int logId;
        private final int cycles;
        private final int respawnTimer;
        private final boolean multi;

        Tree(int req, int xp, int log, int[] obj, int cycles, int respawnTimer, boolean multi) {
            this.requiredLevel = req;
            this.xpReward = xp;
            this.logId = log;
            this.objects = obj;
            this.cycles = cycles;
            this.respawnTimer = respawnTimer;
            this.multi = multi;
        }

        public static Optional<Tree> forObjectId(int objectId) {
            return Optional.ofNullable(trees.get(objectId));
        }

        public boolean isMulti() {
            return multi;
        }

        public int getCycles() {
            return cycles;
        }

        public int getRespawnTimer() {
            return respawnTimer;
        }

        public int getLogId() {
            return logId;
        }

        public int getXpReward() {
            return xpReward;
        }

        public int getRequiredLevel() {
            return requiredLevel;
        }
    }
}
