package pl.szafraniec.ChildrenMotivator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class BackgroundImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false)
    private byte[] image;

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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public static class BackgroundImageFactory {
        public static BackgroundImage create(String name, byte[] image) {
            BackgroundImage backgroundImage = new BackgroundImage();
            backgroundImage.setImage(image);
            backgroundImage.setName(name);
            return backgroundImage;
        }
    }

}
