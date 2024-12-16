package com.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// @AllArgsConstructor
public class EventCategoryDTO {
    private String code;
    private String label;

    public EventCategoryDTO(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static EventCategoryDTO of(String code, String label) {
        return new EventCategoryDTO(code, label);
    }
} 