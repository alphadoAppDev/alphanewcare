package kr.co.alphacare.listview;

import android.graphics.drawable.Drawable;

public class CustomRowItem {

    public CustomRowItem() {
        color = 0;
    }

    private Drawable icon;
    private String name;
    private String value;
    private Drawable icon2;
    private int color;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getLastIcon() {
        return icon2;
    }

    public void setLastIcon(Drawable icon2) {
        this.icon2 = icon2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return value;
    }

    public void setContents(String value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}