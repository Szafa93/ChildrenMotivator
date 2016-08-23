package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.Child;

public class ChildFactory {
    public static Child create(String name, String surname, String pesel, String parentMail) {
        Child child = new Child();
        child.setName(name);
        child.setSurname(surname);
        child.setPesel(pesel);
        child.setParentEmail(parentMail);
        child.setChildActivitiesTable(ChildActivitiesTableFactory.create(child));
        return child;
    }
}
