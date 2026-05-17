package me.pondhalf.project.tinySettings.config;

public record ToggleConfig(
        boolean defaultEnabled,
        ItemAppearance enableAppearance,
        ItemAppearance disableAppearance
) {}
