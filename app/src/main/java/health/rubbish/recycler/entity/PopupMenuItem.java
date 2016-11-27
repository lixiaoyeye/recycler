package health.rubbish.recycler.entity;

public class PopupMenuItem {
    public String no;
    public String name;
    public int icon= -1;;
    public int num = 0;

    public PopupMenuItem(String no, int icon, String name, int num) {
        this.no = no;
        this.name = name;
        this.icon = icon;
        this.num = num;
    }

    public PopupMenuItem() {
    }
}
