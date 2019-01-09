package com.judicapo.dimpaint;

import android.graphics.Path;

import java.util.List;

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

    public void drawTriangle(List<UserPath> list) {
        this.moveTo(this.x, this.y); // Top
        double topLine = Math.sqrt(Math.pow(this.x - list.get(0).x,2) + Math.pow(this.y - list.get(0).y,2));
        this.lineTo(list.get(0).x, list.get(0).y); // Bottom
        double bottomLine = Math.sqrt(Math.pow(list.get(0).x - list.get(1).x,2) + Math.pow(list.get(0).y - list.get(1).y,2));
        this.lineTo(list.get(1).x, list.get(1).y);
        double hipLine = Math.sqrt(Math.pow(list.get(1).x - this.x,2) + Math.pow(list.get(1).y - this.y,2));
        this.lineTo(this.x, this.y); // Back to Top

        if (topLine < bottomLine && topLine < hipLine){
            this.moveTo(list.get(1).x, list.get(1).y);
            this.lineTo(list.get(1).x + 100, list.get(1).y + 100);
        } else if (bottomLine < topLine && bottomLine < hipLine){
            this.moveTo(this.x, this.y);
            this.lineTo(this.x + 100, this.y + 100);
        } else if (hipLine < topLine && hipLine < bottomLine){
            this.moveTo(list.get(0).x, list.get(0).y);
            this.lineTo(list.get(0).x + 100, list.get(0).y + 100);
        }

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
