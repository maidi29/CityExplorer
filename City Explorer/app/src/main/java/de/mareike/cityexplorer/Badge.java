package de.mareike.cityexplorer;


public class Badge {
    String title;
    String subtitle;
    int iconID;

    public Badge(String title, String subtitle, int iconID) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconID = iconID;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getIconID() {
        return iconID;
    }

}
