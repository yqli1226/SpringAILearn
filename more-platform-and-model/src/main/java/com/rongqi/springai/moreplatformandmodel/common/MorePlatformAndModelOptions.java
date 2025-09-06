package com.rongqi.springai.moreplatformandmodel.common;

import lombok.Data;

@Data
public class MorePlatformAndModelOptions {
    private final Double DEFAULT_TEMPERATURE = 0.8D;

    private String platform;
    private String model;
    private Double temperature;

    public Double getTemperatureOrDefault() {
        return temperature == null ? DEFAULT_TEMPERATURE : temperature;
    }

}
