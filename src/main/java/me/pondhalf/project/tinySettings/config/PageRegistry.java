package me.pondhalf.project.tinySettings.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class PageRegistry {

    private final JavaPlugin plugin;
    private final ConfigLoader loader;
    private final Map<String, PageConfig> pages = new HashMap<>();

    public PageRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loader = new ConfigLoader(plugin.getLogger());
    }

    public void reload() {
        pages.clear();
        File dir = new File(plugin.getDataFolder(), "pages");
        if (!dir.isDirectory()) {
            plugin.getLogger().warning("No pages/ directory found at " + dir.getAbsolutePath());
            return;
        }
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".yml"));
        if (files == null) return;

        for (File f : files) {
            String id = f.getName().substring(0, f.getName().length() - 4);
            try {
                pages.put(id, loader.loadPage(id, f));
            } catch (Exception ex) {
                plugin.getLogger().warning("Failed to load page " + id + ": " + ex.getMessage());
            }
        }
        validateGotoRefs();
        plugin.getLogger().info("Loaded " + pages.size() + " page(s).");
    }

    private void validateGotoRefs() {
        for (PageConfig page : pages.values()) {
            for (var entry : page.items().entrySet()) {
                ItemConfig item = entry.getValue();
                checkRef(page.id(), entry.getKey(), item.onClick());
                if (item.isToggle() && item.toggleConfig() != null) {
                    checkRef(page.id(), entry.getKey(), item.toggleConfig().enableAppearance().onClick());
                    checkRef(page.id(), entry.getKey(), item.toggleConfig().disableAppearance().onClick());
                }
            }
        }
    }

    private void checkRef(String pageId, int slot, ClickActions actions) {
        if (actions == null || actions.gotoPage() == null) return;
        if (!pages.containsKey(actions.gotoPage())) {
            plugin.getLogger().warning("[" + pageId + "] slot " + slot
                    + ": goto-page references unknown page '" + actions.gotoPage() + "'");
        }
    }

    public PageConfig get(String id) {
        return pages.get(id);
    }

    public boolean has(String id) {
        return pages.containsKey(id);
    }
}
