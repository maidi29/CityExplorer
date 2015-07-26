package de.mareike.cityexplorer;


public class Badge {
    String title;
    String subtitle;
    int iconID;

    //Orden mit folgender Struktur erstellen
    public Badge(String title, String subtitle, int iconID) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconID = iconID;
    }

    //Getter
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
