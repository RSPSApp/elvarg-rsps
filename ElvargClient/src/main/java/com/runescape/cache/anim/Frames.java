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
        int version = stream.readUShort();

        int baseSize = stream.readInt();
        byte[] baseData = new byte[baseSize];
        stream.readBytes(baseSize, 0, baseData);
        Buffer baseBuffer = new Buffer(baseData);
        Skeleton skeleton = new Skeleton(baseBuffer, baseSize);
        int frameCount = stream.readUShort();
        animations = new Animation[frameCount];

        Animation.load(stream, fileid, frameCount, skeleton, animations);

        if(frameset[fileid] == null) {
            frameset[fileid] = this;
        }

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
        return this.animations[frame_index].hasAlpha;
    }

}
