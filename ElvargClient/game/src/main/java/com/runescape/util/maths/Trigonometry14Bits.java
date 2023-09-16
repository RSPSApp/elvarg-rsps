package com.runescape.util.maths;

public class Trigonometry14Bits {

   public static final double TAU = Math.PI * 2.0;
   public static final int ANGLE_BASE = 11;
   public static final int ANGLE_SCALE = 3;
   public static final int ANGLE_BITS = ANGLE_BASE + ANGLE_SCALE;
   public static final int ANGLE_FULL = 1 << ANGLE_BITS;
   public static final int ANGLE_MASK = ANGLE_FULL - 1;
   public static final double RADIANS_TO_JAG = ANGLE_FULL / TAU;
   public static final double JAG_TO_RADIANS = TAU / ANGLE_FULL;
   static final int[] COS = new int[ANGLE_FULL];
   static final int[] SIN = new int[ANGLE_FULL];

   static {
      double ratio = JAG_TO_RADIANS;

      for(int an = 0; an < ANGLE_FULL; ++an) {
         SIN[an] = (int)((double) ANGLE_FULL * Math.sin((double)an * ratio));
         COS[an] = (int)((double) ANGLE_FULL * Math.cos((double)an * ratio));
      }

   }

   /**
    * Converts degrees angle in degrees to radians.
    *
    * @param degrees the angle in degrees to convert
    * @return the angle in radians
    */
   public static float degreesToRadians(int degrees) {
      int jangle = degrees & ANGLE_MASK;
      return (float)(TAU * (double)((float)jangle / ANGLE_FULL));
   }

   public static int atan2(int y, int x) {
      return (int)Math.round(Math.atan2(y, x) * RADIANS_TO_JAG) & ANGLE_MASK;
   }

   public static int sin(int an) {
      return SIN[an & ANGLE_MASK];
   }

   public static int cos(int an) {
      return COS[an & ANGLE_MASK];
   }
}
