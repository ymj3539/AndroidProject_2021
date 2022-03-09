package kr.co.company.project3.DataClass;

public class ItemData {
    public String idx = "";
    public String reg_user = "";
    public String profile = "";
    public String title = "";
    public String image = "";
    public String summary = "";
    public int heart = 0;
    public int reply = 0;
    public String more = "";
    public int count = 0;
    public String reg_date = "";
    public String user_id = "";


    public ItemData() {}//생성자
    public ItemData(String user_id, String idx, String reg_user, String profile, String title, String image, String summary, int heart, int reply, String more, int count, String reg_date)
    {
        this.user_id = user_id;
        this.idx = idx;
        this.reg_user = reg_user;
        this.profile = profile;
        this.title = title;
        this.image = image;
        this.summary = summary;
        this.heart = heart;
        this.reply = reply;
        this.more = more;
        this.count = count;
        this.reg_date = reg_date;

    }
}
