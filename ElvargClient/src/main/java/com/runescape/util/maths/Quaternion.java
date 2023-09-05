package com.runescape.util.maths;

import java.util.Objects;

public final class Quaternion {
   public static Quaternion[] pool;
   public static int poolLimit = 100;
   public static int poolCursor;
   float y;
   float w;
   float x;
   float z;

   static {
      pool = new Quaternion[100];
      poolCursor = 0;
      new Quaternion();
   }

   /**
    * Returns a quaternion from the object pool, or creates a new one if the pool is empty.
    *
    * @return a quaternion from the object pool, or a new quaternion if the pool is empty
    */
   public static Quaternion getOrCreateFromPool() {
       synchronized(pool) {
          if (poolCursor == 0) {
             return new Quaternion();
          } else {
             pool[--poolCursor].reset();
             return pool[poolCursor];
          }
       }
    }

   /**
    * Releases this quaternion to the object pool.
    */
   public void releaseToPool() {
      Objects.requireNonNull(pool, "Quaternion object pool is not initialized");
      synchronized (pool) {
         if (poolCursor < poolLimit - 1) {
            pool[++poolCursor - 1] = this;
         }
      }
   }

   public Quaternion() {
      this.reset();
   }

   /**
    * Sets the components of the quaternion to the given values.
    *
    * @param x the X component to set
    * @param y the Y component to set
    * @param z the Z component to set
    * @param w the W component to set
    */
   public void set(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   /**
    * Rotates the quaternion by the given angle and axis.
    *
    * @param axisX the X component of the rotation axis
    * @param axisY the Y component of the rotation axis
    * @param axisZ the Z component of the rotation axis
    * @param angle the angle of rotation in radians
    */
   public void rotate(float axisX, float axisY, float axisZ, float angle) {
      float sinHalfAngle = (float) Math.sin(angle * 0.5F);
      float cosHalfAngle = (float) Math.cos(angle * 0.5F);
      this.x = axisX * sinHalfAngle;
      this.y = axisY * sinHalfAngle;
      this.z = axisZ * sinHalfAngle;
      this.w = cosHalfAngle;
   }

   /**
    * Sets this quaternion to default state.
    */
   public void reset() {
      this.z = 0.0F;
      this.y = 0.0F;
      this.x = 0.0F;
      this.w = 1.0F;
   }

   /**
    * Multiplies this other by the given other.
    *
    * @param other the other to multiply by
    */
   public void multiply(Quaternion other) {
      float newW = other.x * this.w + this.x * other.w + this.z * other.y - this.y * other.z;
      float newX = other.z * this.x + other.y * this.w + (other.w * this.y - this.z * other.x);
      float newY = this.z * other.w + this.y * other.x - other.y * this.x + other.z * this.w;
      float newZ = other.w * this.w - this.x * other.x - other.y * this.y - other.z * this.z;
      this.set(newW,newX,newY,newZ);
   }

   public boolean equals(Object arg0) {
      if (!(arg0 instanceof Quaternion)) {
         return false;
      } else {
         Quaternion var2 = (Quaternion)arg0;
         return var2.x == this.x && this.y == var2.y && this.z == var2.z && var2.w == this.w;
      }
   }

   public String toString() {
      return this.x + "," + this.y + "," + this.z + "," + this.w;
   }

}
