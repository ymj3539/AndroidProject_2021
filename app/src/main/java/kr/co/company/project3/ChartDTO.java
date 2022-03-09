package kr.co.company.project3;


public class ChartDTO {

    private String title;
    private String name;
    private String list;


    public ChartDTO() {
        this.title = title;
        this.name = name;
        this.list = list;

    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setList(String list) {
        this.list = list;
    }


    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getList() {
        return list;
    }
}