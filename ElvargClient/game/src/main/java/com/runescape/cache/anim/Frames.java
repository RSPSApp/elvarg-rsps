package com.runescape.cache.anim;

import com.runescape.Client;
import com.runescape.io.Buffer;

public class Frames {
    public static Frames[] frameset;
    int fileid;
    public Animation[] animations;

    Frames(int fileid, byte[] fileData) {
        this.fileid = fileid;
        Buffer stream = new Buffer(fileData);
        int baseCount = stream.readUShort();
        int[] transformationType = new int[baseCount];
        int[][] skinList = new int[baseCount][];
        for (int index = 0; index < baseCount; index++) transformationType[index] = stream.readUShort();
        for (int index = 0; index < baseCount; index++) skinList[index] = new int[stream.readUShort()];
        for (int index = 0; index < baseCount; index++) {
            for (int skin = 0; skin < skinList[index].length; skin++) skinList[index][skin] = stream.readUShort();
        }
        int frameCount = stream.readUShort();
        animations = new Animation[frameCount * 3];
        Animation.loadLegacy(stream, fileid, frameCount, new Skeleton(baseCount, transformationType, skinList), animations);

        for (Animation animation : animations) {
            if (animation != null) {
                frameset[fileid] = this;
                return;
            }
        }
        throw new IllegalArgumentException("No animation frames decoded for group " + fileid);

    }

    public static void loadAnimFrames(int id, byte[] fileData) {
        frameset[id] = new Frames(id, fileData);
    }

    public static void init() {
        frameset = new Frames[4000];
    }

    public static Frames getFrames(int frameID) {
        try {
            if(frameset[frameID] == null) {
                Client.instance.resourceProvider.provide(1, frameID);
                return null;
            }
            return frameset[frameID];
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean hasTransparency(int frame_index) {
        return frame_index >= 0 && frame_index < animations.length && animations[frame_index] != null
                && animations[frame_index].hasAlpha;
    }

}
