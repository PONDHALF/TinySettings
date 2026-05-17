package me.pondhalf.project.tinySettings.config;

import java.util.List;

public record ClickActions(
        String gotoPage,
        List<String> playerCommands,
        List<String> consoleCommands
) {
    public static final ClickActions EMPTY = new ClickActions(null, List.of(), List.of());
}
