package com.runescape.cache.anim.skeleton;

public class InterpolationChain {
   public float[] weights;
   public int interpolationCount;

   public InterpolationChain(float[] weights, int count) {
      this.weights = weights;
      this.interpolationCount = count;
   }

   public static float calculateWeightedSum(float[] weights, int count, float multiplier) {
      float weightedSum = weights[count];

      for(int index = count - 1; index >= 0; --index) {
         weightedSum = multiplier * weightedSum + weights[index];
      }

      return weightedSum;
   }
   static float[] scaleWeights(int keyframeCount, float[] weights) {
      float[] weightsTemp = new float[1 + keyframeCount];

      for(int i = 1; i <= keyframeCount; ++i) {
         weightsTemp[i - 1] = weights[i] * (float)i;
      }
      return weightsTemp;
   }

}
