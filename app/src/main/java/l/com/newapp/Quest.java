package l.com.newapp;

public class Quest {
    private int id;
    private String Image;
    private String Name;

    public Quest(int id, String image, String name) {
        this.id = id;
        Image = image;
        Name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    @Override
    public String toString() {
        return "Quest{" +
                "id=" + id +
                ", Image='" + Image + '\'' +
                ", Name='" + Name + '\'' +
                '}';
    }
}
