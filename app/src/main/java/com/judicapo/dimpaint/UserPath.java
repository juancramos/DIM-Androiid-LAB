package com.judicapo.dimpaint;

import android.graphics.Path;

public class UserPath extends Path {
    public int color;
    public int strokeWidth;
    public float x, y;

    public UserPath(int color, int strokeWidth, float mX, float mY) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.x = mX;
        this.y = mY;
    }

    public void drawTriangle(int width) {
        int halfWidth = width / 2;
        this.moveTo(this.x, this.y - halfWidth); // Top
        this.lineTo(this.x - halfWidth, this.y + halfWidth); // Bottom left
        this.lineTo(this.x + halfWidth, this.y + halfWidth); // Bottom right
        this.lineTo(this.x, this.y - halfWidth); // Back to Top
        this.close();
    }
}
