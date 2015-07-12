 package com.stickercamera.app.camera.util;
 
 import android.graphics.Matrix;

 public class MatrixUtils
 {
   public static void mapPoints(Matrix matrix, float[] points)
   {
     float[] m = { 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F };
     matrix.getValues(m);
 
     points[0] = (points[0] * m[0] + m[2]);
     points[1] = (points[1] * m[4] + m[5]);
 
     if (points.length == 4) {
       points[2] = (points[2] * m[0] + m[2]);
       points[3] = (points[3] * m[4] + m[5]);
     }
   }
 
   public static float[] getScale(Matrix matrix)
   {
     float[] points = new float[9];
     matrix.getValues(points);
     return new float[] { points[0], points[4] };
   }
 
   public static float[] getValues(Matrix m)
   {
     float[] values = new float[9];
     m.getValues(values);
     return values;
   }
 }
