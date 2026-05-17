package me.pondhalf.project.tinySettings.config;

import org.bukkit.Material;

import java.util.List;

public record ItemAppearance(
        Material material,
        String displayName,
        List<String> lore,
        Integer customModelData,
        ClickActions onClick
) {}
