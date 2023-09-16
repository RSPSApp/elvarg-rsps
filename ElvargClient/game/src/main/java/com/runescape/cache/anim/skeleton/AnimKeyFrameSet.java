package com.runescape.cache.anim.skeleton;

import com.runescape.util.SerialEnum;
import com.runescape.util.maths.Quaternion;
import com.runescape.Client;
import com.runescape.cache.anim.Skeleton;
import com.runescape.io.Buffer;
import com.runescape.util.maths.Matrix4f;

public class AnimKeyFrameSet {
   boolean modifiesTrans;
   AnimationKeyFrame[][] skeletalTransforms = null;
   int frameID;
   int keyID = 0;
   public AnimationKeyFrame[][] transforms = null;
   public Skeleton base;


   public static AnimKeyFrameSet keyFrameCache[];

   public static AnimKeyFrameSet getFrames(int skeletalId) {
      try {
         int file = skeletalId >>> 16;

         if(keyFrameCache[file] == null) {
            Client.instance.resourceProvider.provide(1, file);
            return null;
         }

         return keyFrameCache[file];
      } catch(Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static void init() {
      keyFrameCache = new AnimKeyFrameSet[4000];
   }

   public static void load(int group, byte[] fileData){
      try {
         Buffer frameBuffer = new Buffer(fileData);
         int type = frameBuffer.readUShort();

         int baseSize = frameBuffer.readInt();//need to write this
         byte[] baseData = new byte[baseSize];
         frameBuffer.readBytes(baseSize, 0, baseData);
         Buffer baseBuffer = new Buffer(baseData);

         int version = frameBuffer.readUnsignedByte();
         int base_id = frameBuffer.readUShort();

         AnimKeyFrameSet keyframe = keyFrameCache[group] = new AnimKeyFrameSet();
         keyframe.frameID = group;
         try {
            keyframe.base = new Skeleton(baseBuffer, baseSize);
         } catch (RuntimeException e) {
            keyFrameCache[group] = null;
            System.err.println(e.getMessage());
            System.err.println("Error unpacking base for keyframe " + group);
            e.printStackTrace();
         }
         try {
            keyframe.decode(frameBuffer);
         } catch(RuntimeException exception) {
            keyFrameCache[group] = null;
            System.err.println("Error unpacking keyframes " + group + " file size from cache = " + fileData.length);
            exception.printStackTrace();
         }
      } catch(Exception exception) {
         System.err.println("Error unpacking keyframes " + group);
         exception.printStackTrace();
      }
   }


   void decode(Buffer packet) {
      int frameSize = packet.readInt();
      int beforeRead = packet.currentPosition;
      packet.readShort();
      packet.readShort();
      this.keyID = packet.readUnsignedByte();
      int totalCount = packet.readUShort();
      this.skeletalTransforms = new AnimationKeyFrame[this.base.getSkeletalBase().boneCount()][];
      this.transforms = new AnimationKeyFrame[this.base.transformsCount()][];

      for(int index = 0; index < totalCount; ++index) {
         int id = packet.readUnsignedByte();
         AnimTransform[] values = new AnimTransform[]{AnimTransform.NULL, AnimTransform.VERTEX, AnimTransform.field1210, AnimTransform.COLOUR, AnimTransform.TRANSPARENCY, AnimTransform.field1213};
         AnimTransform animTransform = (AnimTransform) SerialEnum.forId(values, id);
         if (animTransform == null) {
            animTransform = AnimTransform.NULL;
         }

         int transformLocation = packet.readSmartByteorshort();
         AnimationChannel animationChannel = AnimationChannel.lookup(packet.readUnsignedByte());
         AnimationKeyFrame animationKeyFrame = new AnimationKeyFrame();
         animationKeyFrame.deserialise(packet);
         int count = animTransform.getDimensions();
         AnimationKeyFrame[][] transforms;
         if (AnimTransform.VERTEX == animTransform) {
            transforms = this.skeletalTransforms;
         } else {
            transforms = this.transforms;
         }

         if (transforms[transformLocation] == null) {
            transforms[transformLocation] = new AnimationKeyFrame[count];
         }

         transforms[transformLocation][animationChannel.getComponent()] = animationKeyFrame;
         if (AnimTransform.TRANSPARENCY == animTransform) {
            this.modifiesTrans = true;
         }
      }
      int readSize = packet.currentPosition - beforeRead;

      if(readSize != frameSize) {
         throw new RuntimeException("AnimKeyFrameSet size mismatch! keyframe " + keyID + ", frame size: " + frameSize + ", actual read: " + readSize);
      }

   }

   public int getKeyframeid() {
      return this.keyID;
   }

   public boolean modifiesAlpha() {
      return this.modifiesTrans;
   }

   public void applyTransforms(int tick, AnimationBone bone, int transform_index, int keyframe) {
      Matrix4f boneMatrix = Matrix4f.take();
      this.applyRotationTransforms(boneMatrix, transform_index, bone, tick);
      this.applyScaleTransforms(boneMatrix, transform_index, bone, tick);
      this.applyTranslationTransforms(boneMatrix, transform_index, bone, tick);
      bone.setBoneTransform(boneMatrix);
      boneMatrix.release();
   }

   void applyRotationTransforms(Matrix4f transMatrix, int transformIndex, AnimationBone bone, int tick) {
      float[] rotations = bone.getRotation(this.keyID);
      float x = rotations[0];
      float y = rotations[1];
      float z = rotations[2];
      if (null != this.skeletalTransforms[transformIndex]) {
         AnimationKeyFrame xrotTransform = this.skeletalTransforms[transformIndex][0];
         AnimationKeyFrame yrotTransform = this.skeletalTransforms[transformIndex][1];
         AnimationKeyFrame zrotTransform = this.skeletalTransforms[transformIndex][2];
         if (xrotTransform != null) {
            x = xrotTransform.getValue(tick);
         }

         if (yrotTransform != null) {
            y = yrotTransform.getValue(tick);
         }

         if (zrotTransform != null) {
            z = zrotTransform.getValue(tick);
         }
      }

      Quaternion xrot = Quaternion.getOrCreateFromPool();
      xrot.rotate(1.0F, 0.0F, 0.0F, x);
      Quaternion yrot = Quaternion.getOrCreateFromPool();
      yrot.rotate(0.0F, 1.0F, 0.0F, y);
      Quaternion zrot = Quaternion.getOrCreateFromPool();
      zrot.rotate(0.0F, 0.0F, 1.0F, z);
      Quaternion other = Quaternion.getOrCreateFromPool();
      other.multiply(zrot);
      other.multiply(xrot);
      other.multiply(yrot);
      Matrix4f var14 = Matrix4f.take();
      var14.setRotation(other);
      transMatrix.multiply(var14);
      xrot.releaseToPool();
      yrot.releaseToPool();
      zrot.releaseToPool();
      other.releaseToPool();
      var14.release();
   }

   void applyTranslationTransforms(Matrix4f matrix4f, int transformIndex, AnimationBone bone, int tick) {
      float[] goneTranslation = bone.getTranslation(this.keyID);
      float translateX = goneTranslation[0];
      float translateY = goneTranslation[1];
      float translateZ = goneTranslation[2];
      if (this.skeletalTransforms[transformIndex] != null) {
         AnimationKeyFrame animationKeyFrame3 = this.skeletalTransforms[transformIndex][3];
         AnimationKeyFrame animationKeyFrame4 = this.skeletalTransforms[transformIndex][4];
         AnimationKeyFrame animationKeyFrame5 = this.skeletalTransforms[transformIndex][5];
         if (animationKeyFrame3 != null) {
            translateX = animationKeyFrame3.getValue(tick);
         }

         if (null != animationKeyFrame4) {
            translateY = animationKeyFrame4.getValue(tick);
         }

         if (null != animationKeyFrame5) {
            translateZ = animationKeyFrame5.getValue(tick);
         }
      }

      matrix4f.values[12] = translateX;
      matrix4f.values[13] = translateY;
      matrix4f.values[14] = translateZ;
   }

   void applyScaleTransforms(Matrix4f transformMatrix, int transformIndex, AnimationBone bone, int tick) {
      float[] boneScale = bone.getScale(this.keyID);
      float scaleX = boneScale[0];
      float scaleY = boneScale[1];
      float scaleZ = boneScale[2];
      if (this.skeletalTransforms[transformIndex] != null) {
         AnimationKeyFrame xscaleOp = this.skeletalTransforms[transformIndex][6];
         AnimationKeyFrame yscaleOp = this.skeletalTransforms[transformIndex][7];
         AnimationKeyFrame zscaleOp = this.skeletalTransforms[transformIndex][8];
         if (xscaleOp != null) {
            scaleX = xscaleOp.getValue(tick);
         }

         if (yscaleOp != null) {
            scaleY = yscaleOp.getValue(tick);
         }

         if (zscaleOp != null) {
            scaleZ = zscaleOp.getValue(tick);
         }
      }

      Matrix4f matrix4f = Matrix4f.take();
      matrix4f.setScale(scaleX, scaleY, scaleZ);
      transformMatrix.multiply(matrix4f);
      matrix4f.release();
   }

}
