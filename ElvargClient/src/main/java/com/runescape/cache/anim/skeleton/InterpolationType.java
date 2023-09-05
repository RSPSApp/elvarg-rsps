package com.runescape.cache.anim.skeleton;

import com.runescape.util.SerialEnum;

public class InterpolationType implements SerialEnum {
   public static final InterpolationType STEP_INTERPOLATION = new InterpolationType(0, 0);
   public static final InterpolationType LINEAR_INTERPOLATION = new InterpolationType(1, 1);
   public static final InterpolationType FORWARDS_INTERPOLATION = new InterpolationType(4, 4);
   public static final InterpolationType CUBICSPLINE_INTERPOLATION = new InterpolationType(3, 3);
   public static final InterpolationType BACKWARDS_INTERPOLATION = new InterpolationType(2, 2);
   final int ordinal;
   final int id;

   static InterpolationType[] values() {
      return new InterpolationType[]{STEP_INTERPOLATION, LINEAR_INTERPOLATION, BACKWARDS_INTERPOLATION, CUBICSPLINE_INTERPOLATION, FORWARDS_INTERPOLATION};
   }

   InterpolationType(int ordinal, int id) {
      this.ordinal = ordinal;
      this.id = id;
   }

   public static InterpolationType lookupById(int id) {
      InterpolationType interpolationType = (InterpolationType) SerialEnum.forId(values(), id);
      if (null == interpolationType) {
         interpolationType = STEP_INTERPOLATION;
      }

      return interpolationType;
   }

   public int id() {
      return this.id;
   }
}
