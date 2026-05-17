package me.pondhalf.project.tinySettings.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class Placeholders {

    private static Boolean papiEnabled;

    private Placeholders() {}

    private static boolean papi() {
        if (papiEnabled == null) {
            papiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        }
        return papiEnabled;
    }

    public static String apply(Player player, String raw) {
        if (raw == null) return null;
        String s = raw;
        if (player != null) {
            s = s.replace("%player%", player.getName());
        }
        if (papi() && player != null) {
            s = PlaceholderAPI.setPlaceholders(player, s);
        }
        return s;
    }

    public static List<String> applyList(Player player, List<String> raw) {
        if (raw == null) return List.of();
        List<String> out = new ArrayList<>(raw.size());
        for (String line : raw) {
            out.add(apply(player, line));
        }
        return out;
    }
}
