package com.hzjytech.coffeeme.entities;

import android.graphics.Color;
import android.graphics.Rect;

/**
 * Created by hehongcan on 2016/12/12.
 */
public class RectColor {
    private Rect rect;
    private int color;

    public RectColor() {
    }

    public RectColor(Rect rect, int color) {
        this.rect = rect;
        this.color = color;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "RectColor{" +
                "rect=" + rect +
                ", color=" + color +
                '}';
    }
}
