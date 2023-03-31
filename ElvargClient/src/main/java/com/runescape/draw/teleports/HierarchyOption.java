package com.runescape.draw.teleports;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * @author relex lawl
 */
public interface HierarchyOption {

    Dimension getDimension();

    String getName();

    int getShortcutKey();

    String getDescription();

    int[] getIndex();

    ArrayList<HierarchyOption> getOptions();
}
