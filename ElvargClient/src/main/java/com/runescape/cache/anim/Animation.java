package com.runescape.cache.anim;

import com.runescape.io.Buffer;

public final class Animation {

    public int frameset_id;
    public int delay;
    public Skeleton base;
    public int transformationCount;
    public int transformationIndices[];
    public int transformX[];
    public int transformY[];
    public int transformZ[];
    boolean hasAlpha = false;

    public static void load(Buffer stream, int groupId, int framegroupCount, Skeleton base, Animation[] out) {
        try {

            int indices[] = new int[500];
            int ai1[] = new int[500];
            int ai2[] = new int[500];
            int ai3[] = new int[500];
            for(int l1 = 0; l1 < framegroupCount; l1++) {
                int frame_id = stream.readUShort();
                int frame_size = stream.readUShort();
                int before_frame = stream.currentPosition;
                int frame_end = frame_size + before_frame;
                if(frame_id == 65535) {
                    System.out.println("Empty frame at anim" + groupId + ", read frameidx" + frame_id +", iterated frameidx" + l1 + ", framecount:" + framegroupCount);
                    continue;
                }
                if(frame_id != l1) {
                    System.out.println("Frame id mismatch " + groupId + ", read frameidx" + frame_id +", iterated frameidx" + l1 + ", framecount:" + framegroupCount);
                    continue;
                }

                //might need to put frame_id here and forget about using the l1
                Animation animation = out[l1] = new Animation();
                animation.frameset_id = groupId;
                animation.base = base;
                int transform_count = stream.readUnsignedByte();
                int attr_pos = stream.currentPosition;
                int data_pos = attr_pos + transform_count;
                int used = 0;
                int last = -1;

                for(int index = 0; index < transform_count; index++) {
                    stream.currentPosition = attr_pos;
                    int attr = stream.readUnsignedByte();
                    attr_pos = stream.currentPosition;
                    if(attr > 0) {
                        if(base.transformationType[index] != 0) {
                            for(int l3 = index - 1; l3 > last; l3--) {
                                if(base.transformationType[l3] != 0)
                                    continue;
                                indices[used] = l3;
                                ai1[used] = 0;
                                ai2[used] = 0;
                                ai3[used] = 0;
                                used++;
                                break;
                            }
                        }
                        indices[used] = index;
                        short value = 0;
                        stream.currentPosition = data_pos;
                        int startpos = stream.currentPosition;
                        if(base.transformationType[index] == 3)
                            value = (short)128;
                        if((attr & 1) != 0)
                            ai1[used] = stream.readSmartByteorshort();
                        else
                            ai1[used] = value;
                        if((attr & 2) != 0)
                            ai2[used] = stream.readSmartByteorshort();
                        else
                            ai2[used] = value;
                        if((attr & 4) != 0)
                            ai3[used] = stream.readSmartByteorshort();
                        else
                            ai3[used] = value;
                        last = index;
                        used++;
                        if (animation.base.transformationType[index] == 5) {//alpha
                            animation.hasAlpha = true;
                        }
                        data_pos = stream.currentPosition;

                    }
                }
                if(data_pos != frame_end) {
                    System.err.println("Frame decoding read size mismatch pos " + data_pos +", expected: " + frame_end +", " +  groupId + ", read frameidx" + frame_id +", iterated frameidx" + l1 + ", framecount:" + framegroupCount);
                }
                animation.transformationCount = used;
                animation.transformationIndices = new int[used];
                animation.transformX = new int[used];
                animation.transformY = new int[used];
                animation.transformZ = new int[used];
                for(int k3 = 0; k3 < used; k3++) {
                    animation.transformationIndices[k3] = indices[k3];
                    animation.transformX[k3] = ai1[k3];
                    animation.transformY[k3] = ai2[k3];
                    animation.transformZ[k3] = ai3[k3];
                }
            }
        } catch(Exception exception) {
            exception.printStackTrace();
            System.err.println("Error unpacking anim frames: " + groupId);
        }
    }

    public static void clear() {
        Frames.frameset = null;
    }

    public static boolean noAnimationInProgress(int i) {
        return i == -1;
    }

}
