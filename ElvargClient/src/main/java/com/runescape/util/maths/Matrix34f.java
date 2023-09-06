package com.runescape.util.maths;

public class Matrix34f {
   float m01;
   float m10;
   float m20;
   float m12;
   float m11;
   float m21;
   float m02;
   float m00;
   float m22;
   float x;
   float y;
   float z;

   public static final Matrix34f IDENTITY = new Matrix34f();
   Matrix34f() {
      this.setIdentity();
   }

   void setIdentity() {
      this.z = 0.0F;
      this.y = 0.0F;
      this.x = 0.0F;
      this.m12 = 0.0F;
      this.m02 = 0.0F;
      this.m21 = 0.0F;
      this.m01 = 0.0F;
      this.m20 = 0.0F;
      this.m10 = 0.0F;
      this.m22 = 1.0F;
      this.m11 = 1.0F;
      this.m00 = 1.0F;
   }

   /**
    * Rotates this matrix by the given angle (in degrees) around the X-axis.
    *
    * @param angle the angle to rotate by, in degrees
    */
   void rotateX(float angle) {
      float cos = (float)Math.cos((double)angle);
      float sin = (float)Math.sin((double)angle);
      float var5 = this.m10;
      float var6 = this.m11;
      float var7 = this.m12;
      float var8 = this.y;
      this.m10 = cos * var5 - this.m20 * sin;
      this.m20 = sin * var5 + cos * this.m20;
      this.m11 = cos * var6 - sin * this.m21;
      this.m21 = sin * var6 + this.m21 * cos;
      this.m12 = cos * var7 - this.m22 * sin;
      this.m22 = this.m22 * cos + sin * var7;
      this.y = var8 * cos - sin * this.z;
      this.z = sin * var8 + cos * this.z;
   }

   /**
    * Rotates this matrix by the given angle (in degrees) around the Y-axis.
    *
    * @param angle the angle to rotate by, in degrees
    */
   void rotateY(float angle) {
      float cos = (float)Math.cos((double)angle);
      float sin = (float)Math.sin((double)angle);
      float var5 = this.m00;
      float var6 = this.m01;
      float var7 = this.m02;
      float var8 = this.x;
      this.m00 = cos * var5 + this.m20 * sin;
      this.m20 = this.m20 * cos - sin * var5;
      this.m01 = var6 * cos + this.m21 * sin;
      this.m21 = this.m21 * cos - sin * var6;
      this.m02 = this.m22 * sin + var7 * cos;
      this.m22 = this.m22 * cos - var7 * sin;
      this.x = sin * this.z + var8 * cos;
      this.z = cos * this.z - sin * var8;
   }

   /**
    * Rotates this matrix by the given angle (in degrees) around the Z-axis.
    *
    * @param angle the angle to rotate by, in degrees
    */
   void rotateZ(float angle) {
      float cos = (float)Math.cos((double)angle);
      float sin = (float)Math.sin((double)angle);
      float var5 = this.m00;
      float var6 = this.m01;
      float var7 = this.m02;
      float var8 = this.x;
      this.m00 = cos * var5 - sin * this.m10;
      this.m10 = sin * var5 + this.m10 * cos;
      this.m01 = cos * var6 - this.m11 * sin;
      this.m11 = var6 * sin + cos * this.m11;
      this.m02 = cos * var7 - this.m12 * sin;
      this.m12 = this.m12 * cos + var7 * sin;
      this.x = var8 * cos - sin * this.y;
      this.y = var8 * sin + this.y * cos;
   }

   /**
    * Translates the matrix by the given amounts in the x, y, and z directions.
    *
    * @param x the amount to translate in the x direction
    * @param y the amount to translate in the y direction
    * @param z the amount to translate in the z direction
    */
   void translateMatrix(float x, float y, float z) {
      this.x += x;
      this.y += y;
      this.z += z;
   }

   public String toString() {
      return this.m00 + "," + this.m01 + "," + this.m02 + "," + this.x + "\n" + this.m10 + "," + this.m11 + "," + this.m12 + "," + this.y + "\n" + this.m20 + "," + this.m21 + "," + this.m22 + "," + this.z;
   }

}
