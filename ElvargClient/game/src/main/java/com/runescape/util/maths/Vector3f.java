package com.runescape.util.maths;

public class Vector3f {
   private final float z;
   private final float x;
   private final float y;

   static {
      new Vector3f(0.0F, 0.0F, 0.0F);
      new Vector3f(1.0F, 1.0F, 1.0F);
      new Vector3f(1.0F, 0.0F, 0.0F);
      new Vector3f(0.0F, 1.0F, 0.0F);
      new Vector3f(0.0F, 0.0F, 1.0F);
   }

   Vector3f(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   final float length() {
      return (float)Math.sqrt(this.z * this.z + this.y * this.y + this.x * this.x);
   }

   public String toString() {
      return this.x + ", " + this.y + ", " + this.z;
   }
}
