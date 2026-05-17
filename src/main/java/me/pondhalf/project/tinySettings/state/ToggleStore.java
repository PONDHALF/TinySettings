package me.pondhalf.project.tinySettings.state;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class ToggleStore {

    private final JavaPlugin plugin;
    private final NamespacedKey rootKey;

    public ToggleStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.rootKey = new NamespacedKey(plugin, "toggles");
    }

    private NamespacedKey innerKey(String page, int slot) {
        return new NamespacedKey(plugin, sanitize(page) + "__" + slot);
    }

    private String sanitize(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9_.-]", "_");
    }

    private PersistentDataContainer root(Player player, boolean create) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        PersistentDataContainer inner = pdc.get(rootKey, PersistentDataType.TAG_CONTAINER);
        if (inner == null && create) {
            inner = pdc.getAdapterContext().newPersistentDataContainer();
        }
        return inner;
    }

    public boolean isEnabled(Player player, String page, int slot, boolean defaultValue) {
        PersistentDataContainer inner = root(player, false);
        if (inner == null) return defaultValue;
        Byte v = inner.get(innerKey(page, slot), PersistentDataType.BYTE);
        return v == null ? defaultValue : v != 0;
    }

    public void set(Player player, String page, int slot, boolean enabled) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        PersistentDataContainer inner = root(player, true);
        inner.set(innerKey(page, slot), PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
        pdc.set(rootKey, PersistentDataType.TAG_CONTAINER, inner);
    }
}
