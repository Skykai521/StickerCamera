 package com.stickercamera.app.camera.util;

 import android.graphics.PointF;
 import android.graphics.RectF;

 public class Point2D
 {
   public static double angleBetweenPoints(float[] pt0, float[] pt1)
   {
     return angleBetweenPoints(pt0[0], pt0[1], pt1[0], pt1[1], 0.0F);
   }
 
   public static double angleBetweenPoints(float x1, float y1, float x2, float y2, float snapAngle)
   {
     if ((x1 == x2) && (y1 == y2)) return 0.0D;
 
     double gradiant = Math.atan2(x1 - x2, y1 - y2);
     if (snapAngle > 0.0F) return (float)Math.round(gradiant / snapAngle) * snapAngle;
     double angle = degrees(gradiant);
     return angle360(angle);
   }
 
   public static double angle360(double angle) {
     if (angle < 0.0D)
       angle = angle % -360.0D + 360.0D;
     else {
       angle %= 360.0D;
     }
     return angle;
   }
 
   public static double angleBetweenPoints(PointF pt0, PointF pt1)
   {
     return angleBetweenPoints(pt0, pt1, 0.0F);
   }
 
   public static double angleBetweenPoints(PointF pt0, PointF pt1, float snapAngle)
   {
     return angleBetweenPoints(pt0.x, pt0.y, pt1.x, pt1.y, snapAngle);
   }
 
   public static double degrees(double radians)
   {
     return radians * 57.295779513082323D;
   }
 
   public static double distance(float[] pt1, float[] pt2)
   {
     return distance(pt1[0], pt1[1], pt2[0], pt2[1]);
   }
 
   public static double distance(PointF pt1, PointF pt2) {
     return distance(pt1.x, pt1.y, pt2.x, pt2.y);
   }
 
   public static double distance(float x2, float y2, float x1, float y1)
   {
     return Math.sqrt(Math.pow(x2 - x1, 2.0D) + Math.pow(y2 - y1, 2.0D));
   }
 
   public static double hypotenuse(RectF rect) {
     return Math.sqrt(Math.pow(rect.right - rect.left, 2.0D) + Math.pow(rect.bottom - rect.top, 2.0D));
   }
 
   public static double radians(double degree)
   {
     return degree * 0.0174532925199433D;
   }
 
   public static void rotate(PointF[] points, double angle)
   {
     for (int i = 0; i < points.length; i++)
       rotate(points[i], angle);
   }
 
   public static void rotateAroundBy(PointF position, PointF center, float angle)
   {
     double angleInRadians = angle * 0.0174532925199433D;
     double cosTheta = Math.cos(angleInRadians);
     double sinTheta = Math.sin(angleInRadians);
 
     position.x = ((float)(cosTheta * (position.x - center.x) - sinTheta * (position.y - center.y) + center.x));
     position.y = ((float)(sinTheta * (position.x - center.x) + cosTheta * (position.y - center.y) + center.y));
   }
 
   public static void rotateAroundOrigin(PointF point, PointF origin, float deg)
   {
     float rad = (float)radians(deg);
     float s = (float)Math.sin(rad);
     float c = (float)Math.cos(rad);
 
     point.x -= origin.x;
     point.y -= origin.y;
 
     float xnew = point.x * c - point.y * s;
     float ynew = point.x * s + point.y * c;
 
     point.x = (xnew + origin.x);
     point.y = (ynew + origin.y);
   }
 
   public static void rotate(PointF point, double angle)
   {
     float x = point.x;
     float y = point.y;
     double ca = Math.cos(angle);
     double sa = Math.sin(angle);
     point.x = ((float)(x * ca - y * sa));
     point.y = ((float)(x * sa + y * ca));
   }
 
   public static void translate(PointF[] points, float x, float y)
   {
     for (int i = 0; i < points.length; i++)
       translate(points[i], x, y);
   }
 
   public static void translate(PointF point, float x, float y)
   {
     point.x += x;
     point.y += y;
   }
 
   public static PointF intersection(PointF[] a, PointF[] b)
   {
     float x1 = a[0].x;
     float y1 = a[0].y;
     float x2 = a[1].x;
     float y2 = a[1].y;
 
     float x3 = b[0].x;
     float y3 = b[0].y;
     float x4 = b[1].x;
     float y4 = b[1].y;
 
     return new PointF(((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / (
       (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)), 
       ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (
       x3 * y4 - y3 * x4)) / (
       (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)));
   }
 
   public static PointF sizeOfRect(PointF[] ps)
   {
     return new PointF(ps[1].x - ps[0].x, ps[3].y - ps[0].y);
   }
 
 
   public static void getLerp(PointF pt1, PointF pt2, float t, PointF dstPoint)
   {
     dstPoint.set(pt1.x + (pt2.x - pt1.x) * t, pt1.y + (pt2.y - pt1.y) * t);
   }
 
   public static void grow(RectF rect, float offsetX, float offsetY)
   {
     rect.left -= offsetX / 2.0F;
     rect.top -= offsetY / 2.0F;
 
     rect.right += offsetX / 2.0F;
     rect.bottom += offsetY / 2.0F;
   }
 }

