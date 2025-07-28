package com.urban.carbon.api.geoservice.constants;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public enum GeoServiceFileType {

    TIF("TIF",List.of("image/png", "image/jpeg", "image/jpg")),

    SHP("SHP", List.of("image/png", "image/jpeg", "image/jpg"));

    private final String name;

    private final List<String> allowFormatTypes;

    GeoServiceFileType(String name, List<String> allowFormatTypes) {
        this.name = name;
        this.allowFormatTypes = allowFormatTypes;
    }
}
