/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.szafraniec.ChildrenMotivator.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Maciek
 */
@Entity
public class TableCell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private GradeScheme gradeScheme;

    private String gradeComment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GradeScheme getGradeScheme() {
        return gradeScheme;
    }

    public void setGradeScheme(GradeScheme gradeScheme) {
        this.gradeScheme = gradeScheme;
    }

    public String getGradeComment() {
        return gradeComment;
    }

    public void setGradeComment(String gradeComment) {
        this.gradeComment = gradeComment;
    }

    public static class TableCellBuilder {
        public static TableCell create() {
            TableCell tableCell = new TableCell();
            return tableCell;
        }
    }
}
