package thithiapplication.hepto.com.thithiapplication.model;

/**
 * Created by Balamurugan_G on 6/20/2016.
 */
public class Details {
    int id;
    String name;
    String date;
    String time;

    public Details()
    {
    }
    public Details(String name,String date,String time)
    {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
