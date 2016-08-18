/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.szafraniec.ChildrenMotivator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * @author Maciek
 */
@Entity
public class GradeScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private int value;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GradeScheme that = (GradeScheme) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public static class GradeSchemeFactory {
        public static GradeScheme create(int value, byte[] image) {
            GradeScheme gradeScheme = new GradeScheme();
            gradeScheme.setImage(image);
            gradeScheme.setValue(value);
            return gradeScheme;
        }
    }
}
