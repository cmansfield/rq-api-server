package com.reliaquest.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonNaming(Employee.PrefixNamingStrategy.class)
public record Employee(UUID id, String name, Integer salary, Integer age, String title, String email) {
    static class PrefixNamingStrategy extends PropertyNamingStrategies.NamingBase {
        public static final String PREFIX = "employee_";

        @Override
        public String translate(String propertyName) {
            if ("id".equals(propertyName)) {
                return propertyName;
            }
            if (propertyName.startsWith(PREFIX)) {
                return propertyName.substring(PREFIX.length());
            }
            return PREFIX + propertyName;
        }
    }
}
