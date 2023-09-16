package com.runescape.entity;

import com.runescape.Client;
import com.runescape.cache.anim.SequenceDefinition;
import com.runescape.cache.anim.SpotAnimationDefinition;

public class Mob extends Renderable {
    public int directionChangeTick = 0;
    public boolean instantFacing;

    public final int[] pathX;
    public final int[] pathY;
    public final int[] hitDamages;
    public final int[] hitMarkTypes;
    public final int[] hitsLoopCycle;
    public final MoveSpeed[] pathRun;
    public int interactingEntity;
    public int walkanim_pause;
    public int degreesToTurn;
    public int runAnimIndex;
    public String spokenText;
    public int height;
    public int nextStepOrientation;
    public int idleAnimation;
    public int standTurnAnimIndex;
    public int readyanim_r;
    public int runanim_b = -1;
    public int runanim_r = -1;
    public int runanim_l = -1;
    public int crawlanim = -1;
    public int crawlanim_b = -1;
    public int crawlanim_l = -1;
    public int crawlanim_r = -1;
    public int textColour;
    public int movementAnimation;

    public int graphic;
    public int currentAnimation;
    public int graphicLoop;
    public int graphicStartLoop;
    public int graphicHeight;
    public int remainingPath;
    public int emoteAnimation;
    public int displayedEmoteFrames;
    public int emoteTimeRemaining;
    public int animationDelay;
    public int currentAnimationLoops;
    public int textEffect;
    public int loopCycleStatus;
    public int currentHealth;
    public int maxHealth;
    public int textCycle;
    public int time;

    public int secondaryanimReplaycount;
    public int secondaryanimFrameindex;
    public int secondaryanimLoopsRemaining;

    public int faceX;
    public int faceY;
    public int size;
    public boolean isWalking;
    public int anim_delay;
    public int initialX;
    public int destinationX;
    public int initialY;
    public int destinationY;
    public int startForceMovement;
    public int endForceMovement;
    public int direction;
    public int x;
    public int y;
    public int orientation;
    public int walkAnimIndex;
    public int turn180AnimIndex;
    public int turn90CWAnimIndex;
    public int turn90CCWAnimIndex;

    public int entScreenX;
    public int entScreenY;
    public int index = -1;

    public Mob() {
        pathX = new int[10];
        pathY = new int[10];
        interactingEntity = -1;
        degreesToTurn = 32;
        runAnimIndex = -1;
        height = 200;
        idleAnimation = -1;
        standTurnAnimIndex = -1;
        hitDamages = new int[4];
        hitMarkTypes = new int[4];
        hitsLoopCycle = new int[4];
        movementAnimation = -1;
        graphic = -1;
        emoteAnimation = -1;
        loopCycleStatus = -1000;
        textCycle = 100;
        size = 1;
        isWalking = false;
        pathRun = new MoveSpeed[10];

        walkAnimIndex = -1;
        turn180AnimIndex = -1;
        turn90CWAnimIndex = -1;
        turn90CCWAnimIndex = -1;
    }

    public final void setPos(int x, int y, boolean flag) {
        if (emoteAnimation != -1 && SequenceDefinition.sequenceDefinitions[emoteAnimation].idleStyle == 1)
            emoteAnimation = -1;

        if (!flag) {
            int dx = x - pathX[0];
            int dy = y - pathY[0];
            if (dx >= -8 && dx <= 8 && dy >= -8 && dy <= 8) {
                if (remainingPath < 9)
                    remainingPath++;
                for (int i1 = remainingPath; i1 > 0; i1--) {
                    pathX[i1] = pathX[i1 - 1];
                    pathY[i1] = pathY[i1 - 1];
                    pathRun[i1] = pathRun[i1 - 1];
                }

                pathX[0] = x;
                pathY[0] = y;
                pathRun[0] = MoveSpeed.WALK;
                return;
            }
        }
        remainingPath = 0;
        anim_delay = 0;
        walkanim_pause = 0;
        pathX[0] = x;
        pathY[0] = y;
        this.x = pathX[0] * 128 + size * 64;
        this.y = pathY[0] * 128 + size * 64;
    }

    public final void resetPath() {
        remainingPath = 0;
        anim_delay = 0;
    }

    public final void updateHitData(int hitType, int hitDamage, int currentTime) {
        for (int hitPtr = 0; hitPtr < 4; hitPtr++)
            if (hitsLoopCycle[hitPtr] <= currentTime) {
                hitDamages[hitPtr] = hitDamage;
                hitMarkTypes[hitPtr] = hitType;
                hitsLoopCycle[hitPtr] = currentTime + 70;
                return;
            }
    }

    public void nextPreForcedStep() {
        int remaining = startForceMovement - Client.tick;
        int tempX = initialX * 128 + size * 64;
        int tempY = initialY * 128 + size * 64;
        x += (tempX - x) / remaining;
        y += (tempY - y) / remaining;

        walkanim_pause = 0;

        if (direction == 0) {
            nextStepOrientation = 1024;
        }

        if (direction == 1) {
            nextStepOrientation = 1536;
        }

        if (direction == 2) {
            nextStepOrientation = 0;
        }

        if (direction == 3) {
            nextStepOrientation = 512;
        }
    }

    public void nextForcedMovementStep() {
        boolean var2 = endForceMovement == Client.tick || emoteAnimation == -1 || animationDelay != 0;
        if (!var2) {
            SequenceDefinition var3 = SequenceDefinition.sequenceDefinitions[emoteAnimation];
            if (null != var3 && !var3.usingKeyframes()) {
                var2 = emoteTimeRemaining + 1 > var3.durations[displayedEmoteFrames];
            } else {
                var2 = true;
            }
        }

        if (var2) {
            int i = endForceMovement - startForceMovement;
            int j = Client.tick - startForceMovement;
            int k = initialX * 128 + size * 64;
            int l = initialY * 128 + size * 64;
            int i1 = destinationX * 128 + size * 64;
            int j1 = destinationY * 128 + size * 64;
            x = (k * (i - j) + i1 * j) / i;
            y = (l * (i - j) + j1 * j) / i;
        }
        walkanim_pause = 0;
        if (direction == 0)
            setTurnDirection(1024);
        if (direction == 1)
            setTurnDirection(1536);
        if (direction == 2)
            setTurnDirection(0);
        if (direction == 3)
            setTurnDirection(512);
        orientation = getTurnDirection();
    }

    public void nextStep() {
        movementAnimation = idleAnimation;
        if (remainingPath == 0) {
            walkanim_pause = 0;
            return;
        }
        if (emoteAnimation != -1 && animationDelay == 0) {
            SequenceDefinition sequenceDefinition = SequenceDefinition.sequenceDefinitions[emoteAnimation];
            if (anim_delay > 0 && sequenceDefinition.moveStyle == 0) {
                walkanim_pause++;
                return;
            }
            if (anim_delay <= 0 && sequenceDefinition.idleStyle == 0) {
                walkanim_pause++;
                return;
            }
        }
        int i = x;
        int j = y;
        int k = pathX[remainingPath - 1] * 128 + size * 64;
        int l = pathY[remainingPath - 1] * 128 + size * 64;
        if (k - i > 256 || k - i < -256 || l - j > 256 || l - j < -256) {
            x = k;
            y = l;
            // Added
            remainingPath--;
            if (anim_delay > 0)
                anim_delay--;
            return;
        }
        if (i < k) {
            if (j < l)
                setTurnDirection(1280);
            else if (j > l)
                setTurnDirection(1792);
            else
                setTurnDirection(1536);
        } else if (i > k) {
            if (j < l)
                setTurnDirection(768);
            else if (j > l)
                setTurnDirection(256);
            else
                setTurnDirection(512);
        } else if (j < l)
            setTurnDirection(1024);
        else
            setTurnDirection(0);
        int i1 = getTurnDirection() - orientation & 0x7ff;
        if (i1 > 1024)
            i1 -= 2048;
        int j1 = turn180AnimIndex;
        if (i1 >= -256 && i1 <= 256)
            j1 = walkAnimIndex;
        else if (i1 >= 256 && i1 < 768)
            j1 = turn90CCWAnimIndex;
        else if (i1 >= -768 && i1 <= -256)
            j1 = turn90CWAnimIndex;
        if (j1 == -1)
            j1 = walkAnimIndex;
        movementAnimation = j1;
        int translate_delta = 4;
        // Added smoothwalk
        boolean smoothwalk = true;
        if (this instanceof Npc) {
            smoothwalk = ((Npc) this).desc.smoothwalk;
        }
        if (smoothwalk) {
            if (orientation != getTurnDirection() && -1 == interactingEntity && degreesToTurn != 0) {
                translate_delta = 2;
            }

            if (remainingPath > 2) {
                translate_delta = 6;
            }

            if (remainingPath > 3) {
                translate_delta = 8;
            }

        } else {
            if (remainingPath > 1) {
                translate_delta = 6;
            }

            if (remainingPath > 2) {
                translate_delta = 8;
            }

        }
        if (walkanim_pause > 0 && remainingPath > 1) {
            translate_delta = 8;
            --walkanim_pause;
        }

        MoveSpeed movespeed = pathRun[remainingPath - 1];
        if (movespeed == MoveSpeed.RUN) {
            translate_delta <<= 1;
        } else if (movespeed == MoveSpeed.CRAWL) {
            translate_delta >>= 1;
        }

        // Added new turns
        if (translate_delta >= 8 && movementAnimation == walkAnimIndex && runAnimIndex != -1) {
            movementAnimation = runAnimIndex;
        }

        if (translate_delta >= 8) {
            if (movementAnimation == walkAnimIndex && runAnimIndex != -1) {
                movementAnimation = runAnimIndex;
            } else if (turn180AnimIndex == movementAnimation && -1 != runanim_b) {
                movementAnimation = runanim_b;
            } else if (turn90CWAnimIndex == movementAnimation && -1 != runanim_l) {
                movementAnimation = runanim_l;
            } else if (movementAnimation == turn90CCWAnimIndex && -1 != runanim_r) {
                movementAnimation = runanim_r;
            }
        } else if (translate_delta <= 1) {
            if (movementAnimation == walkAnimIndex && -1 != crawlanim) {
                movementAnimation = crawlanim;
            } else if (movementAnimation == turn180AnimIndex && crawlanim_b != -1) {
                movementAnimation = crawlanim_b;
            } else if (movementAnimation == turn90CWAnimIndex && crawlanim_l != -1) {
                movementAnimation = crawlanim_l;
            } else if (movementAnimation == turn90CCWAnimIndex && -1 != crawlanim_r) {
                movementAnimation = crawlanim_r;
            }
        }
        if (i < k) {
            x += translate_delta;
            if (x > k)
                x = k;
        } else if (i > k) {
            x -= translate_delta;
            if (x < k)
                x = k;
        }
        if (j < l) {
            y += translate_delta;
            if (y > l)
                y = l;
        } else if (j > l) {
            y -= translate_delta;
            if (y < l)
                y = l;
        }
        if (x == k && y == l) {
            remainingPath--;
            if (anim_delay > 0)
                anim_delay--;
        }
    }

    public final void moveInDir(MoveSpeed movespeed, int direction) {
        int x = pathX[0];
        int y = pathY[0];
        if (direction == 0) {
            x--;
            y++;
        }
        if (direction == 1)
            y++;
        if (direction == 2) {
            x++;
            y++;
        }
        if (direction == 3)
            x--;
        if (direction == 4)
            x++;
        if (direction == 5) {
            x--;
            y--;
        }
        if (direction == 6)
            y--;
        if (direction == 7) {
            x++;
            y--;
        }
        if (emoteAnimation != -1 && SequenceDefinition.sequenceDefinitions[emoteAnimation].idleStyle == 1)
            emoteAnimation = -1;
        if (remainingPath < 9)
            remainingPath++;
        for (int l = remainingPath; l > 0; l--) {
            pathX[l] = pathX[l - 1];
            pathY[l] = pathY[l - 1];
            pathRun[l] = pathRun[l - 1];
        }
        pathX[0] = x;
        pathY[0] = y;
        pathRun[0] = movespeed;
    }

    public void updateAnimation() {
        isWalking = false;
        if (movementAnimation >= 65535)
            movementAnimation = -1;
        if (movementAnimation != -1) {
            SequenceDefinition seqtype = SequenceDefinition.sequenceDefinitions[movementAnimation];
            if(seqtype == null) {
                movementAnimation = -1;
            } else if (!seqtype.usingKeyframes() && seqtype.frames != null) {
                ++secondaryanimLoopsRemaining;
                if (secondaryanimFrameindex < seqtype.frames.length && secondaryanimLoopsRemaining > seqtype.durations[secondaryanimFrameindex]) {
                    secondaryanimLoopsRemaining = 1;
                    ++secondaryanimFrameindex;
                    play_frames_sound(seqtype,secondaryanimFrameindex);
                }

                if (secondaryanimFrameindex >= seqtype.frames.length) {
                    if (seqtype.loopFrameCount > 0) {
                        secondaryanimFrameindex -= seqtype.loopFrameCount;
                        if (seqtype.replay) {
                            ++secondaryanimReplaycount;
                        }

                        if (secondaryanimFrameindex < 0 || secondaryanimFrameindex >= seqtype.frames.length || seqtype.replay && secondaryanimReplaycount >= seqtype.loopCount) {
                            secondaryanimLoopsRemaining = 0;
                            secondaryanimFrameindex = 0;
                            secondaryanimReplaycount = 0;
                        }
                    } else {
                        secondaryanimLoopsRemaining = 0;
                        secondaryanimFrameindex = 0;
                    }

                    play_frames_sound(seqtype, secondaryanimFrameindex);
                }

            } else if (seqtype.usingKeyframes()) {
                ++secondaryanimFrameindex;
                int var6 = seqtype.getKeyframeDuration();
                if (secondaryanimFrameindex < var6) {
                    play_keyframes_sound(secondaryanimFrameindex, seqtype);
                } else {
                    if (seqtype.loopFrameCount > 0) {
                        secondaryanimFrameindex -= seqtype.loopFrameCount;
                        if (seqtype.replay) {
                            ++secondaryanimReplaycount;
                        }

                        if (secondaryanimFrameindex < 0 || secondaryanimFrameindex >= var6 || seqtype.replay && secondaryanimReplaycount >= seqtype.loopCount) {
                            secondaryanimFrameindex = 0;
                            secondaryanimLoopsRemaining = 0;
                            secondaryanimReplaycount = 0;
                        }
                    } else {
                        secondaryanimLoopsRemaining = 0;
                        secondaryanimFrameindex = 0;
                    }
                    play_keyframes_sound(secondaryanimFrameindex, seqtype);
                }
            } else {
                movementAnimation = -1;
            }
        }
        if (graphic != -1 && Client.tick >= graphicStartLoop) {
            if (currentAnimation < 0)
                currentAnimation = 0;
            if (SpotAnimationDefinition.cache[graphic] == null || SpotAnimationDefinition.cache[graphic].sequenceDefinitionSequence == null) {
                graphic = -1;//-1 in osrs
            } else {
                SequenceDefinition spotanim_seq = SpotAnimationDefinition.cache[graphic].sequenceDefinitionSequence;
                if (spotanim_seq != null && null != spotanim_seq.frames && !spotanim_seq.usingKeyframes()) {
                    ++graphicLoop;
                    if (currentAnimation < spotanim_seq.frames.length && graphicLoop > spotanim_seq.durations[currentAnimation]) {
                        graphicLoop = 1;
                        ++currentAnimation;
                        play_frames_sound(spotanim_seq, currentAnimation);
                    }

                    if (currentAnimation >= spotanim_seq.frames.length && (currentAnimation < 0 || currentAnimation >= spotanim_seq.frames.length)) {
                        graphic = -1;
                    }
                } else if (spotanim_seq.usingKeyframes()) {
                    ++currentAnimation;
                    int var4 = spotanim_seq.getKeyframeDuration();
                    if (currentAnimation < var4) {
                        play_keyframes_sound(currentAnimation, spotanim_seq);
                    } else if (currentAnimation < 0 || currentAnimation >= var4) {
                        graphic = -1;
                    }
                } else {
                    graphic = -1;
                }
            }

        }
        if (emoteAnimation != -1 && animationDelay <= 1) {
            if (emoteAnimation >= SequenceDefinition.sequenceDefinitions.length) {
                emoteAnimation = -1;//0 in 317
                return;
            }
            SequenceDefinition sequenceDefinition_2 = SequenceDefinition.sequenceDefinitions[emoteAnimation];
            if (sequenceDefinition_2.moveStyle == 1 && anim_delay > 0 && startForceMovement <= Client.tick
                    && endForceMovement < Client.tick) {
                animationDelay = 1;
                return;
            }
        }
        if (emoteAnimation != -1 && animationDelay == 0) {
            SequenceDefinition seqtype = SequenceDefinition.sequenceDefinitions[emoteAnimation];
            if (seqtype == null) {
                emoteAnimation = -1;
            } else if (seqtype.usingKeyframes() || seqtype.frames == null) {
                if (seqtype.usingKeyframes()) {
                    ++displayedEmoteFrames;
                    int skeletal_duration = seqtype.getKeyframeDuration();
                    if (displayedEmoteFrames < skeletal_duration) {
                        play_keyframes_sound(displayedEmoteFrames,seqtype);
                    } else {
                        displayedEmoteFrames -= seqtype.loopFrameCount;
                        ++currentAnimationLoops;
                        if (currentAnimationLoops >= seqtype.loopCount) {
                            emoteAnimation = -1;
                        } else if (displayedEmoteFrames >= 0 && displayedEmoteFrames < skeletal_duration) {
                            play_keyframes_sound(displayedEmoteFrames,seqtype);
                        } else {
                            emoteAnimation = -1;
                        }
                    }
                } else {
                    emoteAnimation = -1;
                }
            } else {
                ++emoteTimeRemaining;
                if (displayedEmoteFrames < seqtype.frames.length && emoteTimeRemaining > seqtype.durations[displayedEmoteFrames]) {
                    emoteTimeRemaining = 1;
                    ++displayedEmoteFrames;
                    play_frames_sound(seqtype, displayedEmoteFrames);
                }

                if (displayedEmoteFrames >= seqtype.frames.length) {
                    displayedEmoteFrames -= seqtype.loopFrameCount;
                    ++currentAnimationLoops;
                    if (currentAnimationLoops >= seqtype.loopCount) {
                        emoteAnimation = -1;
                    } else if (displayedEmoteFrames >= 0 && displayedEmoteFrames < seqtype.frames.length) {
                        play_frames_sound(seqtype, displayedEmoteFrames);
                    } else {
                        emoteAnimation = -1;
                    }
                }

                isWalking = seqtype.forwardRenderPadding;
            }
        }
        if (animationDelay > 0)
            animationDelay--;
    }

    private static void play_frames_sound(SequenceDefinition seqtype, int frameindex) {
        if (seqtype.getFrameSoundeffect(frameindex) != -1) {
            //entity.makeSound(seqtype.get_frame_soundeffect(frameindex));
        }
    }

    private static void play_keyframes_sound(int frameindex, SequenceDefinition seqtype) {
        if (seqtype.skeletalSounds != null && seqtype.skeletalSounds.containsKey(frameindex)) {
            //entity.makeSound((Integer) seqtype.keyframe_soundeffects.get(frameindex));
        }
    }

    public int getTurnDirection() {
        return nextStepOrientation;
    }

    public void setTurnDirection(int turnDirection) {
        this.nextStepOrientation = turnDirection;
    }

    public boolean isVisible() {
        return false;
    }
}
