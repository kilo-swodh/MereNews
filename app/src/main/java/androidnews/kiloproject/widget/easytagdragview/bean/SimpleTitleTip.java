package androidnews.kiloproject.widget.easytagdragview.bean;

public class SimpleTitleTip implements Tip {
    private int id;
    private String tip;

    public SimpleTitleTip(int id, String tip) {
        this.id = id;
        this.tip = tip;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "tip:"+ tip;
    }
}
