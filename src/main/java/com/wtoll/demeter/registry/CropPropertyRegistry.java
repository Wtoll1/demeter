package com.wtoll.demeter.registry;

import com.wtoll.demeter.api.CropProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project Demeter
 */
public class CropPropertyRegistry {
    private List<CropProperty> PROPERTIES = new ArrayList<>();

    public CropPropertyRegistry() {
    }

    public CropProperty register(CropProperty property) {
        PROPERTIES.add(property);
        return property;
    }

    public void forEach(Consumer<? super CropProperty> action) {
        PROPERTIES.forEach(action);
    }

    public List<CropProperty> getProperties() {
        return new ArrayList<>(PROPERTIES);
    }
}
