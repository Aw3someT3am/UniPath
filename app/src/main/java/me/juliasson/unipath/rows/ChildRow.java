package me.juliasson.unipath.rows;

import me.juliasson.unipath.model.College;

public class ChildRow {
    private int icon;
    private String text;
    private College college;

    public ChildRow(int icon, String text) {
        this.icon = icon;
        this.text = text;
        //this.college = college;
    }

    public int getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setText(String text) {
        this.text = text;
    }

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }
}
