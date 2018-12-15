package com.judicapo.dimpaint;

import android.graphics.Path;

public class UserPath {
    public int color;
    public int strokeWidth;
    public Path path;

    public UserPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
