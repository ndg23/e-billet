package com.gestion.model;

public enum EventCategory {
    MUSIC("Musique"),
    SPORTS("Sports"),
    ARTS("Arts"),
    BUSINESS("Business"),
    EDUCATION("Education"),
    ENTERTAINMENT("Divertissement"),
    OTHER("Autre");

    private final String label;

    EventCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}