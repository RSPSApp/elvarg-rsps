package com.runescape.cache.anim.skeleton;

import com.runescape.io.Buffer;

public class SkeletalAnimBase {
   AnimationBone[] bones;
   int maxConnections;

   public SkeletalAnimBase(Buffer packet, int count) {
      this.bones = new AnimationBone[count];
      this.maxConnections = packet.readUnsignedByte();

      for(int boneIndex = 0; boneIndex < this.bones.length; ++boneIndex) {
         AnimationBone animationBone = new AnimationBone(this.maxConnections, packet, false);
         this.bones[boneIndex] = animationBone;
      }

      this.init();
   }

   void init() {
      for(int boneIndex = 0; boneIndex < this.bones.length; ++boneIndex) {
         AnimationBone bone = this.bones[boneIndex];
         if (bone.parentId >= 0) {
            bone.parent = this.bones[bone.parentId];
         }
      }
   }

   public int boneCount() {
      return this.bones.length;
   }

   public AnimationBone getBone(int id) {
      return id >= this.boneCount() ? null : this.bones[id];
   }

   AnimationBone[] getBones() {
      return this.bones;
   }

   public void animate(AnimKeyFrameSet arg0, int arg1) {
      this.animate(arg0, arg1, null, false);
   }

   public void animate(AnimKeyFrameSet animKeyFrameSet, int tick, boolean[] flowControl, boolean tweening) {
      int frame = animKeyFrameSet.getKeyframeid();
      int count = 0;
      AnimationBone[] animationBones = this.getBones();

      for(int boneIndex = 0; boneIndex < animationBones.length; ++boneIndex) {
         AnimationBone bone = animationBones[boneIndex];
         if (flowControl == null || flowControl[count] == tweening) {
            animKeyFrameSet.applyTransforms(tick, bone, count, frame);
         }
         ++count;
      }

   }

}
