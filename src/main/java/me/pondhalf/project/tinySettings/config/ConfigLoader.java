package me.pondhalf.project.tinySettings.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public final class ConfigLoader {

    private final Logger log;

    public ConfigLoader(Logger log) {
        this.log = log;
    }

    public PageConfig loadPage(String id, File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String title = yaml.getString("title", id);
        int rawMax = yaml.getInt("max-slot", 27);
        int size = normalizeSize(rawMax);

        Map<Integer, ItemConfig> items = new LinkedHashMap<>();
        ConfigurationSection itemsSec = yaml.getConfigurationSection("items");
        if (itemsSec != null) {
            for (String key : itemsSec.getKeys(false)) {
                int slot;
                try {
                    slot = Integer.parseInt(key);
                } catch (NumberFormatException ex) {
                    log.warning("[" + id + "] non-numeric slot key: " + key);
                    continue;
                }
                if (slot < 0 || slot >= size) {
                    log.warning("[" + id + "] slot " + slot + " is outside inventory size " + size);
                    continue;
                }
                ConfigurationSection itemSec = itemsSec.getConfigurationSection(key);
                if (itemSec == null) continue;
                ItemConfig item = loadItem(id, slot, itemSec);
                if (item != null) items.put(slot, item);
            }
        }
        return new PageConfig(id, title, size, items);
    }

    private ItemConfig loadItem(String pageId, int slot, ConfigurationSection sec) {
        Material material = parseMaterial(sec.getString("material"));
        if (material == null) {
            log.warning("[" + pageId + "] slot " + slot + ": invalid material '" + sec.getString("material") + "', skipping");
            return null;
        }
        String name = sec.getString("displayname");
        List<String> lore = sec.getStringList("lore");
        Integer cmd = sec.isSet("custommodeldata") ? sec.getInt("custommodeldata") : null;
        ClickActions onClick = loadClick(sec.getConfigurationSection("on-click"));

        ConfigurationSection toggleSec = sec.getConfigurationSection("is-toggle-item");
        boolean isToggle = toggleSec != null && toggleSec.getBoolean("enable", false);
        ToggleConfig toggle = null;
        if (isToggle) {
            String def = toggleSec.getString("default-setting", "disable");
            boolean defaultEnabled = "enable".equalsIgnoreCase(def);
            ConfigurationSection changeSec = toggleSec.getConfigurationSection("change-item");
            ItemAppearance enabled = changeSec == null ? null : loadAppearance(pageId, slot, "enable",
                    changeSec.getConfigurationSection("enable"));
            ItemAppearance disabled = changeSec == null ? null : loadAppearance(pageId, slot, "disable",
                    changeSec.getConfigurationSection("disable"));
            if (enabled == null || disabled == null) {
                log.warning("[" + pageId + "] slot " + slot + ": toggle is missing enable/disable change-item, falling back to plain item");
                isToggle = false;
            } else {
                toggle = new ToggleConfig(defaultEnabled, enabled, disabled);
            }
        }

        return new ItemConfig(material, name, lore, cmd, onClick, isToggle, toggle);
    }

    private ItemAppearance loadAppearance(String pageId, int slot, String which, ConfigurationSection sec) {
        if (sec == null) return null;
        Material material = parseMaterial(sec.getString("material"));
        if (material == null) {
            log.warning("[" + pageId + "] slot " + slot + " toggle." + which + ": invalid material");
            return null;
        }
        String name = sec.getString("displayname");
        List<String> lore = sec.getStringList("lore");
        Integer cmd = sec.isSet("custommodeldata") ? sec.getInt("custommodeldata") : null;
        ClickActions onClick = loadClick(sec.getConfigurationSection("on-click"));
        return new ItemAppearance(material, name, lore, cmd, onClick);
    }

    private ClickActions loadClick(ConfigurationSection sec) {
        if (sec == null) return ClickActions.EMPTY;
        String goTo = sec.getString("goto-page");
        if (goTo != null && goTo.isBlank()) goTo = null;
        List<String> player = sec.getStringList("execute-command-as-player");
        List<String> console = sec.getStringList("execute-command-as-console");
        return new ClickActions(goTo, player, console);
    }

    private Material parseMaterial(String raw) {
        if (raw == null || raw.isBlank()) return null;
        Material m = Material.matchMaterial(raw.toUpperCase(Locale.ROOT));
        return (m == null || !m.isItem()) ? null : m;
    }

    private int normalizeSize(int raw) {
        if (raw <= 9) return 9;
        if (raw >= 54) return 54;
        int rounded = ((raw + 8) / 9) * 9;
        return Math.min(54, rounded);
    }
}
