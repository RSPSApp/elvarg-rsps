package com.runescape.cache.anim;

import com.runescape.cache.anim.skeleton.SkeletalAnimBase;
import com.runescape.io.Buffer;

public final class Skeleton {
    private SkeletalAnimBase skeletalBase;

    public int count;
    public int[] transformationType;
    public int[][] skinList;

    public Skeleton(Buffer data, int bufferSize) {
        int before_read = data.currentPosition;
        count = data.readUnsignedByte();
        transformationType = new int[count];
        skinList = new int[count][];
        for(int index = 0; index < count; index++) {
            transformationType[index] = data.readUnsignedByte();
        }

        for(int index = 0; index < count; index++) {
            skinList[index] = new int[data.readUnsignedByte()];
        }

        for(int j = 0; j < count; j++) {
            for (int l = 0; l < skinList[j].length; l++) {
                skinList[j][l] = data.readUnsignedByte();
            }
        }

        int readTotalSize = data.currentPosition - before_read;

        if (readTotalSize != bufferSize) {
            try {
                int size = data.readUShort();
                if (size > 0) {
                    this.skeletalBase = new SkeletalAnimBase(data, size);
                }
            } catch (Throwable t) {
                System.err.println("Tried to load base because there was extra base data but skeletal failed to load.");
                t.printStackTrace();
            }
        }
        int read2_size = data.currentPosition - before_read;

        if(read2_size != bufferSize) {
            throw new RuntimeException("base data size mismatch: " + read2_size + ", expected " + bufferSize);
        }

    }
    public int transformsCount() {
        return this.count;
    }

    public SkeletalAnimBase getSkeletalBase() {
        return this.skeletalBase;
    }

}
