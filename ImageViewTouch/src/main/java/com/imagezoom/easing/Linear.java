package com.imagezoom.easing;

public class Linear implements Easing {

    public double easeNone(double time, double start, double end, double duration) {
        return calculateValueInRange(time, start, end, duration);
    }

    @Override
    public double easeOut(double time, double start, double end, double duration) {
        return calculateValueInRange(time, start, end, duration);
    }

    @Override
    public double easeIn(double time, double start, double end, double duration) {
        return calculateValueInRange(time, start, end, duration);
    }

    @Override
    public double easeInOut(double time, double start, double end, double duration) {
        return calculateValueInRange(time, start, end, duration);
    }

    private double calculateValueInRange(double time, double start, double end, double duration) {
        return end * time / duration + start;
    }
}
