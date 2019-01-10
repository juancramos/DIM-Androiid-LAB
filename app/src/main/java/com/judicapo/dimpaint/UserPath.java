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

        double ans = calculateAngle(this.x, this.y, list.get(0).x, list.get(0).y, list.get(1).x, list.get(1).y);
        if (topLine < bottomLine && topLine < hipLine){
            double endX = list.get(1).x + 200 * Math.sin(ans);
            double endY = list.get(1).y + 200 * Math.cos(ans);
            this.moveTo(list.get(1).x, list.get(1).y);
            this.lineTo((int)endX, (int)endY);
        } else if (bottomLine < topLine && bottomLine < hipLine){
            double endX = this.x + 200 * Math.sin(ans);
            double endY = this.y + 200 * Math.cos(ans);
            this.moveTo(this.x, this.y);
            this.lineTo((int)endX, (int)endY);
        } else if (hipLine < topLine && hipLine < bottomLine){
            double endX = list.get(0).x + 200 * Math.sin(ans);
            double endY = list.get(0).y + 200 * Math.cos(ans);
            this.moveTo(list.get(0).x, list.get(0).y);
            this.lineTo((int)endX, (int)endY);
        }

        this.close();
    }

    public void drawRectangle() {
        this.moveTo(this.x + 200, this.y + 200);
        this.lineTo(this.x + 200, this.y - 200);
        this.lineTo(this.x - 200, this.y - 200);
        this.moveTo(this.x - 200, this.y - 200);
        this.lineTo(this.x - 200, this.y + 200);
        this.lineTo(this.x + 200, this.y + 200);
        this.close();
    }

    private double calculateAngle(double P1X, double P1Y, double P2X, double P2Y, double P3X, double P3Y){
        double angle = Math.atan2(P1X - P2X, P1Y - P2Y);
        double angle1 = Math.atan2(P2X - P3X, P2Y - P3Y);
        double asn = angle1 - angle;
        if (asn < 0){
            return asn + Math.PI;
        }else{
            return asn;
        }
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
