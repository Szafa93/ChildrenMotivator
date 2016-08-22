/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.szafraniec.ChildrenMotivator.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Arrays;

/**
 * @author Maciek
 */
@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String name;

    @Lob
    @Column(nullable = false, length = 64000)
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

    public static class ActivityFactory {
        public static Activity create(String name, byte[] image) {
            Activity activity = new Activity();
            activity.setName(name);
            activity.setImage(image);
            return activity;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Activity activity = (Activity) o;

        if (id != activity.id)
            return false;
        if (!name.equals(activity.name))
            return false;
        return Arrays.equals(image, activity.image);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
