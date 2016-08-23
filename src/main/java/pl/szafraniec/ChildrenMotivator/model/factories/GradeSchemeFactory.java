package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.GradeScheme;

public class GradeSchemeFactory {
    public static GradeScheme create(int value, byte[] image) {
        GradeScheme gradeScheme = new GradeScheme();
        gradeScheme.setImage(image);
        gradeScheme.setValue(value);
        return gradeScheme;
    }
}
