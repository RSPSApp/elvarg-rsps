package net.runelite.client.plugins.hd.scene.model_overrides;

import com.google.gson.annotations.JsonAdapter;
import lombok.NoArgsConstructor;
import net.runelite.client.plugins.hd.data.NpcID;
import net.runelite.client.plugins.hd.data.ObjectID;
import net.runelite.client.plugins.hd.data.materials.Material;
import net.runelite.client.plugins.hd.data.materials.UvType;
import net.runelite.client.plugins.hd.utils.AABB;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class ModelOverride
{
    public static ModelOverride NONE = new ModelOverride();

    private static final Set<Integer> EMPTY = new HashSet<>();

    public String description = "UNKNOWN";

    @JsonAdapter(NpcID.JsonAdapter.class)
    public Set<Integer> npcIds = EMPTY;
    @JsonAdapter(ObjectID.JsonAdapter.class)
    public Set<Integer> objectIds = EMPTY;

    public Material baseMaterial = Material.NONE;
    public Material textureMaterial = Material.NONE;
    public UvType uvType = UvType.VANILLA;
    public boolean flatNormals = false;
    public boolean removeBakedLighting = false;
    public TzHaarRecolorType tzHaarRecolorType = TzHaarRecolorType.NONE;
    public InheritTileColorType inheritTileColorType = InheritTileColorType.NONE;

    @JsonAdapter(AABB.JsonAdapter.class)
    public AABB[] hideInAreas = {};
}
