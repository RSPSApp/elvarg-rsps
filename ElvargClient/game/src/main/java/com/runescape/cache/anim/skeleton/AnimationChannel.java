package com.runescape.cache.anim.skeleton;

import com.runescape.util.SerialEnum;

public class AnimationChannel implements SerialEnum {
   static final AnimationChannel TRANSLATE_X = new AnimationChannel(4, 4, 3);
   static final AnimationChannel SCALE_X = new AnimationChannel(7, 7, 6);
   static final AnimationChannel ROTATE_Y = new AnimationChannel(2, 2, 1);
   static final AnimationChannel field2343 = new AnimationChannel(12, 12, 2);
   static final AnimationChannel TRANSLATE_Y = new AnimationChannel(5, 5, 4);
   static final AnimationChannel TRANSLATE_Z = new AnimationChannel(6, 6, 5);
   static final AnimationChannel NULL = new AnimationChannel(0, 0, -1);
   static final AnimationChannel SCALE_Y = new AnimationChannel(8, 8, 7);
   static final AnimationChannel field2341 = new AnimationChannel(10, 10, 0);
   static final AnimationChannel field2342 = new AnimationChannel(11, 11, 1);
   static final AnimationChannel SCALE_Z = new AnimationChannel(9, 9, 8);
   static final AnimationChannel ROTATE_Z = new AnimationChannel(3, 3, 2);
   static final AnimationChannel field2344 = new AnimationChannel(14, 14, 4);
   static final AnimationChannel TRANSPARENCY = new AnimationChannel(16, 16, 0);
   static final AnimationChannel field2347 = new AnimationChannel(13, 13, 3);
   static final AnimationChannel ROTATE_X = new AnimationChannel(1, 1, 0);
   static final AnimationChannel field2345 = new AnimationChannel(15, 15, 5);

   private final int ordinal;
   private final int id;
   private final int component;

   private static AnimationChannel[] values() {
      return new AnimationChannel[]{NULL, ROTATE_X, ROTATE_Y, ROTATE_Z, TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z, SCALE_X, SCALE_Y, SCALE_Z, field2341, field2342, field2343, field2347, field2344, field2345, TRANSPARENCY};
   }

   AnimationChannel(int ordinal, int id, int component) {
      this.ordinal = ordinal;
      this.id = id;
      this.component = component;
   }

   public static AnimationChannel lookup(int id) {
      AnimationChannel animationChannel = (AnimationChannel) SerialEnum.forId(values(), id);
      if (animationChannel == null) {
         animationChannel = NULL;
      }

      return animationChannel;
   }

   public int id() {
      return this.id;
   }

   public int getComponent() {
      return this.component;
   }

}
