package com.campus.helpdesk;

import java.util.List;

public final class Options {
    private Options() {}

    public static final List<String> CATEGORIES = List.of(
            "Classroom", "Laboratory", "Hostel", "Library",
            "Infrastructure", "Canteen", "Transport", "Other");
    public static final List<String> STATUSES = List.of("Pending", "In Progress", "Resolved");
    public static final List<String> PRIORITIES = List.of("Low", "Medium", "High");
}
