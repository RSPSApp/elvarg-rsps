package com.runescape.cache.anim.skeleton;

import com.runescape.util.SerialEnum;

public class AnimTransform implements SerialEnum {
   public static final AnimTransform TRANSPARENCY = new AnimTransform(4, 4, 1);
   public static final AnimTransform field1210 = new AnimTransform(2, 2, 3);
   public static final AnimTransform COLOUR = new AnimTransform(3, 3, 6);
   public static final AnimTransform VERTEX = new AnimTransform(1, 1, 9);
   public static final AnimTransform field1213 = new AnimTransform(5, 5, 3);
   public static final AnimTransform NULL = new AnimTransform(0, 0, 0);
   final int field1214;
   final int id;
   final int dimensions;

   AnimTransform(int arg0, int id, int dimensions) {
      this.field1214 = arg0;
      this.id = id;
      this.dimensions = dimensions;
   }

   public int getDimensions() {
      return this.dimensions;
   }

   public int id() {
      return this.id;
   }

}
