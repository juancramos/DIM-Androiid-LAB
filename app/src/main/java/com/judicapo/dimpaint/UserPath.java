package com.judicapo.dimpaint;

import android.graphics.Path;

public class UserPath extends Path implements Comparable {
    public int color;
    public int strokeWidth;
    public float x, y;

    public UserPath(int color, int strokeWidth, float mX, float mY) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.x = mX;
        this.y = mY;
    }

    public void drawTriangle(UserPath match) {
        int halfWidth = (int)Math.sqrt(Math.pow(this.x - match.x, 2) + Math.pow(this.y - match.y, 2));
        this.moveTo(match.x, match.y); // Top
        this.lineTo((int)(halfWidth * (Math.cos(45))) - match.x, (int)(halfWidth * (Math.sin(45))) -  match.y); // Bottom
        this.lineTo((int)(halfWidth * (Math.cos(-45))) - match.x, (int)(halfWidth * (Math.sin(-45))) -  match.y);
        this.lineTo(match.x, match.y); // Back to Top
        this.close();
    }


    @Override
    public int compareTo(Object o) {
        double own = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
        double other = Math.sqrt(Math.pow(((UserPath) o).x, 2) + Math.pow(((UserPath) o).y, 2));
        if (own == other) return 0;
        else if (own > other) return -1;
        else return 1;
    }

    @Override
    public String toString() {
        return "[ mx=" + this.x + ", my=" + this.y + "]";
    }
}
