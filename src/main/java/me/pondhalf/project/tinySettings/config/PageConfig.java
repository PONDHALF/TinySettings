package me.pondhalf.project.tinySettings.config;

import java.util.Map;

public record PageConfig(
        String id,
        String title,
        int size,
        Map<Integer, ItemConfig> items
) {}
