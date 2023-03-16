package com.runescape.cache.anim;

import com.runescape.cache.FileArchive;
import com.runescape.cache.anim.skeleton.AnimKeyFrameSet;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class SequenceDefinition {

    public static SequenceDefinition[] sequenceDefinitions;
    public int frameCount;
    public boolean replay = false;
    public int[] frames;
    public int[] secondary_frames;
    public int[] durations;
    public int loopFrameCount;
    public int[] mask;
    public boolean forwardRenderPadding;
    public int priority;
    public int rightHandOverride;
    public int leftHandOverride;
    public int loopCount;
    public int moveStyle;
    public int idleStyle;
    public int replayStyle;
    private int skeletalRangeBegin = -1;
    private int skeletalRangeEnd = -1;
    private int skeletalId = -1;
    private boolean[] mergedbonegroups;
    public int chatFrameIds[];
    public int frameSounds[];
    public int id;
    public Map<Integer, Integer> skeletalSounds;

    private SequenceDefinition() {
        loopFrameCount = -1;
        forwardRenderPadding = false;
        priority = 5;
        rightHandOverride = -1; //Removes shield
        leftHandOverride = -1; //Removes weapon
        loopCount = 99;
        moveStyle = -1; //Stops character from moving
        idleStyle = -1;
        replayStyle = 1;
    }

    public static void init(FileArchive archive) {
        Buffer stream = new Buffer(archive.readFile("seq.dat"));
        int length = stream.readUShort();
        if (sequenceDefinitions == null)
            sequenceDefinitions = new SequenceDefinition[length];
        for (int j = 0; j < length; j++) {
            if (sequenceDefinitions[j] == null) {
                sequenceDefinitions[j] = new SequenceDefinition();
            }
            sequenceDefinitions[j].id = j;
            sequenceDefinitions[j].decode(stream);

        }

        System.out.println("Loaded: " + length + " animations");
    }

    private void decode(Buffer buffer) {
        int opcode;
        int last_op = -1;
        while ((opcode = buffer.readUnsignedByte()) != 0) {

            if (opcode == 1) {
                frameCount = buffer.readUShort();
                frames = new int[frameCount];
                durations = new int[frameCount];

                for (int index = 0; index < frameCount; index++) {
                    durations[index] = buffer.readUShort();
                }

                for (int index = 0; index < frameCount; index++) {
                    frames[index] = buffer.readUShort();
                }

                for (int index = 0; index < frameCount; index++) {
                    frames[index] += buffer.readUShort() << 16;
                }
            } else if (opcode == 2)
                loopFrameCount = buffer.readUShort();
            else if (opcode == 3) {
                int count = buffer.readUnsignedByte();
                mask = new int[count + 1];
                for (int l = 0; l < count; l++)
                    mask[l] = buffer.readUnsignedByte();
                mask[count] = 9999999;
            } else if (opcode == 4)
                forwardRenderPadding = true;
            else if (opcode == 5)
                priority = buffer.readUnsignedByte();
            else if (opcode == 6)
                rightHandOverride = buffer.readUShort();
            else if (opcode == 7)
                leftHandOverride = buffer.readUShort();
            else if (opcode == 8) {
                loopCount = buffer.readUnsignedByte();
                replay = true;
            } else if (opcode == 9)
                moveStyle = buffer.readUnsignedByte();
            else if (opcode == 10)
                idleStyle = buffer.readUnsignedByte();
            else if (opcode == 11)
                replayStyle = buffer.readUnsignedByte();
            else if (opcode == 12) {
                int count = buffer.readUnsignedByte();
                chatFrameIds = new int[count];
                for (int i2 = 0; i2 < count; i2++) {
                    chatFrameIds[i2] = buffer.readUShort();
                }

                for (int i2 = 0; i2 < count; i2++) {
                    chatFrameIds[i2] += buffer.readUShort() << 16;
                }
            } else if (opcode == 13) {
                int count = buffer.readUnsignedByte();
                frameSounds = new int[count];
                for (int index = 0; index < count; ++index) {
                    frameSounds[index] = buffer.read24Int();
                }
            } else if (opcode == 14) {
                skeletalId = buffer.readInt();
            } else if (opcode == 15) {
                int count = buffer.readUShort();
                this.skeletalSounds = new HashMap();

                for(int i = 0; i < count; ++i) {
                    int var6 = buffer.readUShort();
                    int var7 = buffer.read24Int();
                    this.skeletalSounds.put(var6, var7);
                }
            } else if (opcode == 16) {
                skeletalRangeBegin = buffer.readUShort();
                skeletalRangeEnd = buffer.readUShort();
            } else if (opcode == 17) {
                this.mergedbonegroups = new boolean[256];

                Arrays.fill(this.mergedbonegroups, false);
                int count = buffer.readUnsignedByte();

                for(int i = 0; i < count; ++i) {
                    this.mergedbonegroups[buffer.readUnsignedByte()] = true;
                }
            } else {
                System.out.println("Error unrecognised seq config code: " + opcode + " last opcode " + last_op);
            }
            last_op = opcode;
        }
        if (frameCount == 0) {
            frameCount = 1;
            frames = new int[1];
            frames[0] = -1;
            secondary_frames = new int[1];
            secondary_frames[0] = -1;
            durations = new int[1];
            durations[0] = -1;
        }
        if (moveStyle == -1)
            if (mask != null)
                moveStyle = 2;
            else
                moveStyle = 0;
        if (idleStyle == -1) {
            if (mask != null) {
                idleStyle = 2;
                return;
            }
            idleStyle = 0;
        }
    }

    public Model animateInterfaceModel(Model model, int primaryIndex) {
        if(usingKeyframes()) {
            return this.animateEither(model, primaryIndex);
        }
        int regularFrame = frames[primaryIndex];
        Frames regularFrameset = Frames.getFrames(regularFrame >> 16);
        int regularFrameindex = regularFrame & 0xffff;
        if (regularFrameset == null) {
            return model.bakeSharedModel(true);
        }
        Frames frameSet = null;
        int fameID = 0;
        int ifFrameindex = 0;
        if (chatFrameIds != null && primaryIndex < chatFrameIds.length) {
            fameID = chatFrameIds[primaryIndex];
            frameSet = Frames.getFrames(fameID >> 16);
            ifFrameindex = fameID & 0xffff;
        }

        Model animatedModel;
        if (frameSet == null || fameID == 0xffff) {
            animatedModel = model.bakeSharedModel(!regularFrameset.hasTransparency(regularFrameindex));
            animatedModel.animate2(regularFrameset, regularFrameindex);
        } else {
            animatedModel = model.bakeSharedModel(!regularFrameset.hasTransparency(regularFrameindex) & !frameSet.hasTransparency(ifFrameindex));
            animatedModel.animate2(regularFrameset, regularFrameindex);
            animatedModel.animate2(frameSet, ifFrameindex);
        }
        return animatedModel;
    }

    public Model animateEither(Model model, int index) {
        Model animatedModel;

        if (this.usingKeyframes()) {
            AnimKeyFrameSet frameSet = getKeyframeset();
            if (null == frameSet) {
                return model.bakeSharedAnimationModel(true);
            } else {
                animatedModel = model.bakeSharedAnimationModel(!frameSet.modifiesAlpha());
                animatedModel.animateSkeletalKeyframe(frameSet, index);
                return animatedModel;
            }
        } else {
            int frame = this.frames[index];
            Frames frames = Frames.getFrames(frame >> 16);
            int frameIndex = frame & 0xffff;
            if (null == frames) {
                return model.bakeSharedAnimationModel(true);
            } else {
                animatedModel = model.bakeSharedAnimationModel(!frames.hasTransparency(frameIndex));
                animatedModel.animate2(frames, frameIndex);
                return animatedModel;
            }
        }
    }
    public Model animateMultiple(Model second_model, int primaryIndex, SequenceDefinition secondarySeq, int secondaryIndex) {
        Model model = second_model.bakeSharedAnimationModel(false);
        AnimKeyFrameSet keyFrameSet;
        Frames frameSet = null;
        boolean skeletal = false;
        Skeleton base = null;
        if(usingKeyframes()) {
            keyFrameSet = getKeyframeset();
            if (keyFrameSet == null)
                return model;

            if (secondarySeq.usingKeyframes() && mergedbonegroups == null) {
                model.animateSkeletalKeyframe(keyFrameSet, primaryIndex);
                return model;
            }

            base = keyFrameSet.base;
            model.animateSkeletalKeyframeTweening(keyFrameSet.base, keyFrameSet, primaryIndex, mergedbonegroups, false, !secondarySeq.usingKeyframes());
        } else {
            int frame = frames[primaryIndex];
            frameSet = Frames.getFrames(frame >> 16);
            primaryIndex = frame & 0xffff;
            if (frameSet == null) {
                return secondarySeq.animateEither(model, secondaryIndex);
            }

            if (!secondarySeq.usingKeyframes() && (mask == null || secondaryIndex == -1)) {
                model.animate2(frameSet, primaryIndex);
                return model;
            }

            if (mask == null || secondaryIndex == -1) {
                model.animate2(frameSet, primaryIndex);
                return model;
            }

            skeletal = secondarySeq.usingKeyframes();
            if (!skeletal) {
                model.animate(frameSet, primaryIndex, mask, false);
            }
        }

        if(secondarySeq.usingKeyframes()) {
            keyFrameSet = secondarySeq.getKeyframeset();
            if (keyFrameSet == null) {
                return model;
            }

            if (base == null) {
                base = keyFrameSet.base;
            }

            model.animateSkeletalKeyframeTweening(base, keyFrameSet, secondaryIndex, mergedbonegroups, true, true);
        } else {
            int frame = secondarySeq.frames[secondaryIndex];
            Frames frames = Frames.getFrames(frame >> 16);
            int frameID = frame & 0xffff;
            if (frames == null) {
                return animateEither(model, primaryIndex);
            }

            model.animate(frames, frameID, mask, true);
        }

        if (skeletal && frameSet != null) {
            model.animate(frameSet, primaryIndex, mask, false);
        }

        model.resetBounds();
        model.invalidate();
        return model;
    }

    public Model animateSpotanim(Model model, int index) {
        Model spotanimModel;
        if (this.usingKeyframes()) {
            AnimKeyFrameSet frameSet = getKeyframeset();
            if (frameSet == null) {
                return model.bakeSharedModel(true);
            } else {
                spotanimModel = model.bakeSharedModel(!frameSet.modifiesAlpha());
                spotanimModel.animateSkeletalKeyframe(frameSet, index);
                return spotanimModel;
            }
        } else {
            int frame = this.frames[index];
            Frames frames = Frames.getFrames(frame >> 16);
            int frameIndex = frame & 0xffff;
            if (frames == null) {
                return model.bakeSharedModel(true);
            } else {
                spotanimModel = model.bakeSharedModel(!frames.hasTransparency(frameIndex));
                spotanimModel.animate2(frames, frameIndex);
                return spotanimModel;
            }
        }
    }

    public boolean usingKeyframes() {
        return this.skeletalId >= 0;
    }

    public int getKeyframeDuration() {
        return this.skeletalRangeEnd - this.skeletalRangeBegin;
    }

    public AnimKeyFrameSet getKeyframeset() {
        return this.usingKeyframes() ? AnimKeyFrameSet.getFrames(this.skeletalId) : null;
    }

    public int getFrameSoundeffect(int frameIndex) {
        if (frameSounds != null && frameIndex < frameSounds.length && frameSounds[frameIndex] != 0) {
            return frameSounds[frameIndex];
        } else {
            return -1;
        }
    }


}