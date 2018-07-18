package me.juliasson.unipath.rows;

public class ChildRow {
    private int icon;
    private String text;

    public ChildRow(int icon, String text) {
        this.icon = icon;
        this.text = text;
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
}
