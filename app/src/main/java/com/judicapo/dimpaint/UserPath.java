package com.judicapo.dimpaint;

import android.graphics.Path;

public class UserPath {
    public int color;
    public int strokeWidth;
    public float x, y;
    public Path path;

    public UserPath(int color, int strokeWidth, float mX, float mY, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.x = mX;
        this.y = mY;
        this.path = path;
    }
}
