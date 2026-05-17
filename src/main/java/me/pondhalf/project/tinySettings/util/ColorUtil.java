package me.pondhalf.project.tinySettings.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public final class ColorUtil {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .hexCharacter('#')
            .build();

    private ColorUtil() {}

    public static Component parse(String raw) {
        if (raw == null) return Component.empty();
        return SERIALIZER.deserialize(raw);
    }

    public static Component parseItemName(String raw) {
        return parse(raw).decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> parseLore(List<String> raw) {
        if (raw == null) return List.of();
        List<Component> out = new ArrayList<>(raw.size());
        for (String line : raw) {
            out.add(parse(line).decoration(TextDecoration.ITALIC, false));
        }
        return out;
    }
}
