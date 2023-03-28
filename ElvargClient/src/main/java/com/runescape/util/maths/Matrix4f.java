package com.runescape.util.maths;

import com.runescape.io.Buffer;

import java.util.Arrays;

public final class Matrix4f {
   public static final Matrix4f IDENTITY;
   static Matrix4f[] pool = new Matrix4f[0];
   static int pool_limit = 100;
   static int pool_cursor;
   public float[] values = new float[16];

   static {
      pool = new Matrix4f[100];
      pool_cursor = 0;
      IDENTITY = new Matrix4f();
   }

   public static Matrix4f take() {
      synchronized(pool) {
         if (pool_cursor == 0) {
            return new Matrix4f();
         } else {
            pool[--pool_cursor].setIdentity();
            return pool[pool_cursor];
         }
      }
   }

   public void release() {
      synchronized(pool) {
         if (pool_cursor < pool_limit - 1) {
            pool[++pool_cursor - 1] = this;
         }

      }
   }

   public Matrix4f() {
      this.setIdentity();
   }

   public Matrix4f(Matrix4f arg0) {
      this.set(arg0);
   }

   public Matrix4f(Buffer arg0, boolean arg1) {
      this.deserialise(arg0, arg1);
   }

   void deserialise(Buffer packet, boolean compact) {
      if (compact) {
         Matrix34f matrix = new Matrix34f();
         matrix.rotateX(Trigonometry14Bits.degreesToRadians(packet.readShort()));
         matrix.rotateY(Trigonometry14Bits.degreesToRadians(packet.readShort()));
         matrix.rotateZ(Trigonometry14Bits.degreesToRadians(packet.readShort()));
         matrix.translateMatrix((float)packet.readShort(), (float)packet.readShort(), (float)packet.readShort());
         this.set(matrix);
      } else {
         for(int i = 0; i < 16; ++i) {
            this.values[i] = packet.readFloat();
         }
      }

   }

   /**
    * Returns the Euler angles in YXZ order from the quaternion as a float array.
    * @return float[] containing the Euler angles in radians as [pitch (X), yaw (Y), roll (Z)].
    */
   float[] toEulerAnglesYXZ() {
      float[] eulerAngles = new float[3];
      if ((double)this.values[2] < 0.999D && (double)this.values[2] > -0.999D) {
         eulerAngles[1] = (float)(-Math.asin(this.values[2]));
         double cos = Math.cos(eulerAngles[1]);
         eulerAngles[0] = (float)Math.atan2((double)this.values[6] / cos, (double)this.values[10] / cos);
         eulerAngles[2] = (float)Math.atan2((double)this.values[1] / cos, (double)this.values[0] / cos);
      } else {
         eulerAngles[0] = 0.0F;
         eulerAngles[1] = (float)Math.atan2(this.values[2], 0.0D);
         eulerAngles[2] = (float)Math.atan2(-this.values[9], (double)this.values[5]);
      }

      return eulerAngles;
   }

   /**
    * Returns the inverse YXZ rotation euler angles corresponding to the matrix represented by this object.
    * The euler angles are returned in the format of an array containing three values: pitch, yaw, and roll.
    * @return an array of three floats representing the inverse YXZ rotation euler angles
    */
   public float[] getInverseYXZRotationEulerAngles() {
      float[] eulerAngles = new float[]{(float)(-Math.asin((double)this.values[6])), 0.0F, 0.0F};
      double cos = Math.cos((double)eulerAngles[0]);
      if (Math.abs(cos) > 0.005D) {
         double m02 = (double)this.values[2];
         double m22 = (double)this.values[10];
         double m10 = (double)this.values[4];
         double m11 = (double)this.values[5];
         eulerAngles[1] = (float)Math.atan2(m02, m22);
         eulerAngles[2] = (float)Math.atan2(m10, m11);
      } else {
         double m01 = (double)this.values[1];
         double m00 = (double)this.values[0];
         if (this.values[6] < 0.0F) {
            eulerAngles[1] = (float)Math.atan2(m01, m00);
         } else {
            eulerAngles[1] = (float)(-Math.atan2(m01, m00));
         }

         eulerAngles[2] = 0.0F;
      }

      return eulerAngles;
   }

   void setIdentity() {
      this.values[0] = 1.0F;
      this.values[1] = 0.0F;
      this.values[2] = 0.0F;
      this.values[3] = 0.0F;
      this.values[4] = 0.0F;
      this.values[5] = 1.0F;
      this.values[6] = 0.0F;
      this.values[7] = 0.0F;
      this.values[8] = 0.0F;
      this.values[9] = 0.0F;
      this.values[10] = 1.0F;
      this.values[11] = 0.0F;
      this.values[12] = 0.0F;
      this.values[13] = 0.0F;
      this.values[14] = 0.0F;
      this.values[15] = 1.0F;
   }

   public void setZero() {
      this.values[0] = 0.0F;
      this.values[1] = 0.0F;
      this.values[2] = 0.0F;
      this.values[3] = 0.0F;
      this.values[4] = 0.0F;
      this.values[5] = 0.0F;
      this.values[6] = 0.0F;
      this.values[7] = 0.0F;
      this.values[8] = 0.0F;
      this.values[9] = 0.0F;
      this.values[10] = 0.0F;
      this.values[11] = 0.0F;
      this.values[12] = 0.0F;
      this.values[13] = 0.0F;
      this.values[14] = 0.0F;
      this.values[15] = 0.0F;
   }

   public void set(Matrix4f other) {
      System.arraycopy(other.values, 0, this.values, 0, 16);
   }

   public void setScale(float scale) {
      this.setScale(scale, scale, scale);
   }

   public void setScale(float x, float y, float z) {
      this.setIdentity();
      this.values[0] = x;
      this.values[5] = y;
      this.values[10] = z;
   }

   public void add(Matrix4f other) {
      for(int var3 = 0; var3 < this.values.length; ++var3) {
         this.values[var3] += other.values[var3];
      }

   }

   public void multiply(Matrix4f lhs) {
      float var3 = lhs.values[12] * this.values[3] + this.values[0] * lhs.values[0] + this.values[1] * lhs.values[4] + this.values[2] * lhs.values[8];
      float var4 = lhs.values[13] * this.values[3] + this.values[2] * lhs.values[9] + this.values[0] * lhs.values[1] + lhs.values[5] * this.values[1];
      float var5 = this.values[3] * lhs.values[14] + this.values[0] * lhs.values[2] + lhs.values[6] * this.values[1] + lhs.values[10] * this.values[2];
      float var6 = this.values[2] * lhs.values[11] + this.values[0] * lhs.values[3] + lhs.values[7] * this.values[1] + lhs.values[15] * this.values[3];
      float var7 = this.values[7] * lhs.values[12] + lhs.values[8] * this.values[6] + lhs.values[0] * this.values[4] + lhs.values[4] * this.values[5];
      float var8 = this.values[6] * lhs.values[9] + lhs.values[5] * this.values[5] + this.values[4] * lhs.values[1] + this.values[7] * lhs.values[13];
      float var9 = lhs.values[14] * this.values[7] + this.values[4] * lhs.values[2] + lhs.values[6] * this.values[5] + this.values[6] * lhs.values[10];
      float var10 = lhs.values[15] * this.values[7] + lhs.values[3] * this.values[4] + this.values[5] * lhs.values[7] + this.values[6] * lhs.values[11];
      float var11 = lhs.values[12] * this.values[11] + this.values[9] * lhs.values[4] + lhs.values[0] * this.values[8] + lhs.values[8] * this.values[10];
      float var12 = this.values[9] * lhs.values[5] + this.values[8] * lhs.values[1] + this.values[10] * lhs.values[9] + this.values[11] * lhs.values[13];
      float var13 = this.values[9] * lhs.values[6] + lhs.values[2] * this.values[8] + this.values[10] * lhs.values[10] + this.values[11] * lhs.values[14];
      float var14 = this.values[11] * lhs.values[15] + this.values[10] * lhs.values[11] + this.values[9] * lhs.values[7] + this.values[8] * lhs.values[3];
      float var15 = lhs.values[12] * this.values[15] + lhs.values[4] * this.values[13] + lhs.values[0] * this.values[12] + this.values[14] * lhs.values[8];
      float var16 = lhs.values[13] * this.values[15] + this.values[12] * lhs.values[1] + this.values[13] * lhs.values[5] + lhs.values[9] * this.values[14];
      float var17 = lhs.values[2] * this.values[12] + lhs.values[6] * this.values[13] + lhs.values[10] * this.values[14] + lhs.values[14] * this.values[15];
      float var18 = lhs.values[15] * this.values[15] + lhs.values[11] * this.values[14] + this.values[13] * lhs.values[7] + lhs.values[3] * this.values[12];
      this.values[0] = var3;
      this.values[1] = var4;
      this.values[2] = var5;
      this.values[3] = var6;
      this.values[4] = var7;
      this.values[5] = var8;
      this.values[6] = var9;
      this.values[7] = var10;
      this.values[8] = var11;
      this.values[9] = var12;
      this.values[10] = var13;
      this.values[11] = var14;
      this.values[12] = var15;
      this.values[13] = var16;
      this.values[14] = var17;
      this.values[15] = var18;
   }

   public void setRotation(Quaternion other) {
      float ww = other.w * other.w;
      float wx = other.w * other.x;
      float yw = other.y * other.w;
      float zw = other.z * other.w;
      float xx = other.x * other.x;
      float yx = other.y * other.x;
      float zx = other.z * other.x;
      float yy = other.y * other.y;
      float zy = other.z * other.y;
      float zz = other.z * other.z;
      this.values[0] = xx + ww - zz - yy;
      this.values[1] = zw + yx + zw + yx;
      this.values[2] = zx - yw - yw + zx;
      this.values[4] = yx - zw - zw + yx;
      this.values[5] = ww + yy - xx - zz;
      this.values[6] = wx + zy + wx + zy;
      this.values[8] = yw + zx + zx + yw;
      this.values[9] = zy - wx - wx + zy;
      this.values[10] = zz + ww - yy - xx;
   }

   void set(Matrix34f arg0) {
      this.values[0] = arg0.m00;
      this.values[1] = arg0.m10;
      this.values[2] = arg0.m20;
      this.values[3] = 0.0F;
      this.values[4] = arg0.m01;
      this.values[5] = arg0.m11;
      this.values[6] = arg0.m21;
      this.values[7] = 0.0F;
      this.values[8] = arg0.m02;
      this.values[9] = arg0.m12;
      this.values[10] = arg0.m22;
      this.values[11] = 0.0F;
      this.values[12] = arg0.x;
      this.values[13] = arg0.y;
      this.values[14] = arg0.z;
      this.values[15] = 1.0F;
   }

   float determinant() {
      return this.values[12] * this.values[6] * this.values[3] * this.values[9] + (this.values[5] * this.values[2] * this.values[11] * this.values[12] + (this.values[15] * this.values[9] * this.values[2] * this.values[4] + this.values[8] * this.values[6] * this.values[1] * this.values[15] + this.values[15] * this.values[10] * this.values[5] * this.values[0] - this.values[11] * this.values[5] * this.values[0] * this.values[14] - this.values[15] * this.values[6] * this.values[0] * this.values[9] + this.values[13] * this.values[11] * this.values[6] * this.values[0] + this.values[14] * this.values[9] * this.values[7] * this.values[0] - this.values[10] * this.values[0] * this.values[7] * this.values[13] - this.values[15] * this.values[1] * this.values[4] * this.values[10] + this.values[11] * this.values[1] * this.values[4] * this.values[14] - this.values[6] * this.values[1] * this.values[11] * this.values[12] - this.values[14] * this.values[7] * this.values[1] * this.values[8] + this.values[12] * this.values[10] * this.values[1] * this.values[7] - this.values[13] * this.values[4] * this.values[2] * this.values[11] - this.values[15] * this.values[8] * this.values[5] * this.values[2]) + this.values[2] * this.values[7] * this.values[8] * this.values[13] - this.values[12] * this.values[9] * this.values[7] * this.values[2] - this.values[14] * this.values[4] * this.values[3] * this.values[9] + this.values[13] * this.values[10] * this.values[4] * this.values[3] + this.values[14] * this.values[8] * this.values[5] * this.values[3] - this.values[12] * this.values[3] * this.values[5] * this.values[10] - this.values[13] * this.values[6] * this.values[3] * this.values[8]);
   }

   public void invert() {
      float var2 = 1.0F / this.determinant();
      float var3 = (this.values[10] * this.values[5] * this.values[15] - this.values[14] * this.values[11] * this.values[5] - this.values[9] * this.values[6] * this.values[15] + this.values[13] * this.values[11] * this.values[6] + this.values[7] * this.values[9] * this.values[14] - this.values[10] * this.values[7] * this.values[13]) * var2;
      float var4 = var2 * (this.values[15] * this.values[10] * -this.values[1] + this.values[14] * this.values[11] * this.values[1] + this.values[15] * this.values[2] * this.values[9] - this.values[13] * this.values[2] * this.values[11] - this.values[9] * this.values[3] * this.values[14] + this.values[10] * this.values[3] * this.values[13]);
      float var5 = var2 * (this.values[3] * this.values[5] * this.values[14] + this.values[15] * this.values[6] * this.values[1] - this.values[7] * this.values[1] * this.values[14] - this.values[15] * this.values[2] * this.values[5] + this.values[2] * this.values[7] * this.values[13] - this.values[6] * this.values[3] * this.values[13]);
      float var6 = (this.values[5] * this.values[2] * this.values[11] + this.values[11] * this.values[6] * -this.values[1] + this.values[10] * this.values[1] * this.values[7] - this.values[7] * this.values[2] * this.values[9] - this.values[10] * this.values[3] * this.values[5] + this.values[6] * this.values[3] * this.values[9]) * var2;
      float var7 = (this.values[15] * this.values[6] * this.values[8] + this.values[11] * this.values[4] * this.values[14] + -this.values[4] * this.values[10] * this.values[15] - this.values[6] * this.values[11] * this.values[12] - this.values[14] * this.values[7] * this.values[8] + this.values[10] * this.values[7] * this.values[12]) * var2;
      float var8 = var2 * (this.values[15] * this.values[0] * this.values[10] - this.values[0] * this.values[11] * this.values[14] - this.values[15] * this.values[2] * this.values[8] + this.values[2] * this.values[11] * this.values[12] + this.values[14] * this.values[8] * this.values[3] - this.values[12] * this.values[10] * this.values[3]);
      float var9 = var2 * (this.values[15] * -this.values[0] * this.values[6] + this.values[14] * this.values[0] * this.values[7] + this.values[4] * this.values[2] * this.values[15] - this.values[2] * this.values[7] * this.values[12] - this.values[14] * this.values[3] * this.values[4] + this.values[3] * this.values[6] * this.values[12]);
      float var10 = var2 * (this.values[3] * this.values[4] * this.values[10] + this.values[6] * this.values[0] * this.values[11] - this.values[0] * this.values[7] * this.values[10] - this.values[11] * this.values[4] * this.values[2] + this.values[2] * this.values[7] * this.values[8] - this.values[3] * this.values[6] * this.values[8]);
      float var11 = var2 * (this.values[13] * this.values[8] * this.values[7] + this.values[12] * this.values[5] * this.values[11] + (this.values[4] * this.values[9] * this.values[15] - this.values[11] * this.values[4] * this.values[13] - this.values[8] * this.values[5] * this.values[15]) - this.values[12] * this.values[9] * this.values[7]);
      float var12 = (this.values[12] * this.values[3] * this.values[9] + (this.values[9] * -this.values[0] * this.values[15] + this.values[0] * this.values[11] * this.values[13] + this.values[15] * this.values[1] * this.values[8] - this.values[1] * this.values[11] * this.values[12] - this.values[13] * this.values[3] * this.values[8])) * var2;
      float var13 = var2 * (this.values[4] * this.values[3] * this.values[13] + this.values[7] * this.values[1] * this.values[12] + (this.values[5] * this.values[0] * this.values[15] - this.values[13] * this.values[0] * this.values[7] - this.values[1] * this.values[4] * this.values[15]) - this.values[3] * this.values[5] * this.values[12]);
      float var14 = var2 * (this.values[8] * this.values[5] * this.values[3] + (this.values[4] * this.values[1] * this.values[11] + this.values[11] * -this.values[0] * this.values[5] + this.values[9] * this.values[0] * this.values[7] - this.values[8] * this.values[7] * this.values[1] - this.values[9] * this.values[3] * this.values[4]));
      float var15 = var2 * (this.values[5] * this.values[8] * this.values[14] + this.values[4] * this.values[10] * this.values[13] + this.values[14] * this.values[9] * -this.values[4] - this.values[12] * this.values[5] * this.values[10] - this.values[13] * this.values[8] * this.values[6] + this.values[12] * this.values[6] * this.values[9]);
      float var16 = (this.values[2] * this.values[8] * this.values[13] + this.values[0] * this.values[9] * this.values[14] - this.values[10] * this.values[0] * this.values[13] - this.values[14] * this.values[1] * this.values[8] + this.values[12] * this.values[1] * this.values[10] - this.values[12] * this.values[2] * this.values[9]) * var2;
      float var17 = (this.values[4] * this.values[1] * this.values[14] + this.values[6] * this.values[0] * this.values[13] + -this.values[0] * this.values[5] * this.values[14] - this.values[12] * this.values[1] * this.values[6] - this.values[2] * this.values[4] * this.values[13] + this.values[12] * this.values[5] * this.values[2]) * var2;
      float var18 = var2 * (this.values[9] * this.values[2] * this.values[4] + this.values[10] * this.values[5] * this.values[0] - this.values[6] * this.values[0] * this.values[9] - this.values[1] * this.values[4] * this.values[10] + this.values[1] * this.values[6] * this.values[8] - this.values[8] * this.values[2] * this.values[5]);
      this.values[0] = var3;
      this.values[1] = var4;
      this.values[2] = var5;
      this.values[3] = var6;
      this.values[4] = var7;
      this.values[5] = var8;
      this.values[6] = var9;
      this.values[7] = var10;
      this.values[8] = var11;
      this.values[9] = var12;
      this.values[10] = var13;
      this.values[11] = var14;
      this.values[12] = var15;
      this.values[13] = var16;
      this.values[14] = var17;
      this.values[15] = var18;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.getInverseYXZRotationEulerAngles();
      this.toEulerAnglesYXZ();

      for(int var2 = 0; var2 < 4; ++var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            if (var3 > 0) {
               var1.append("\t");
            }

            float var4 = this.values[var3 + 4 * var2];
            if (Math.sqrt((double)(var4 * var4)) < 9.999999747378752E-5D) {
               var4 = 0.0F;
            }

            var1.append(var4);
         }

         var1.append("\n");
      }

      return var1.toString();
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      return var2 * 31 + Arrays.hashCode(this.values);
   }

   public boolean equals(Object arg0) {
      if (!(arg0 instanceof Matrix4f)) {
         return false;
      } else {
         Matrix4f var2 = (Matrix4f)arg0;

         for(int var3 = 0; var3 < 16; ++var3) {
            if (var2.values[var3] != this.values[var3]) {
               return false;
            }
         }

         return true;
      }
   }

   public float[] getScale() {
      float[] scale = new float[3];
      Vector3f x_axis = new Vector3f(this.values[0], this.values[1], this.values[2]);
      Vector3f y_axis = new Vector3f(this.values[4], this.values[5], this.values[6]);
      Vector3f z_axis = new Vector3f(this.values[8], this.values[9], this.values[10]);
      scale[0] = x_axis.length();
      scale[1] = y_axis.length();
      scale[2] = z_axis.length();
      return scale;
   }
}
