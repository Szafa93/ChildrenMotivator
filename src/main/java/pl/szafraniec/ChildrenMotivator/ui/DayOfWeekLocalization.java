package pl.szafraniec.ChildrenMotivator.ui;

import java.time.DayOfWeek;

public interface DayOfWeekLocalization {
    static String getDayOfWeekName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "Poniedziałek";
            case TUESDAY:
                return "Wtorek";
            case WEDNESDAY:
                return "Środa";
            case THURSDAY:
                return "Czwartek";
            case FRIDAY:
                return "Piątek";
            case SATURDAY:
                return "Sobota";
            case SUNDAY:
                return "Niedziela";
        }
        return "";
    }
}
