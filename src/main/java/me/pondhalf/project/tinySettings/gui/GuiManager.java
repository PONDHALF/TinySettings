package me.pondhalf.project.tinySettings.gui;

import me.pondhalf.project.tinySettings.config.ItemAppearance;
import me.pondhalf.project.tinySettings.config.ItemConfig;
import me.pondhalf.project.tinySettings.config.PageConfig;
import me.pondhalf.project.tinySettings.config.PageRegistry;
import me.pondhalf.project.tinySettings.state.ToggleStore;
import me.pondhalf.project.tinySettings.util.ColorUtil;
import me.pondhalf.project.tinySettings.util.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class GuiManager {

    private final JavaPlugin plugin;
    private final PageRegistry registry;
    private final ToggleStore toggleStore;

    public GuiManager(JavaPlugin plugin, PageRegistry registry, ToggleStore toggleStore) {
        this.plugin = plugin;
        this.registry = registry;
        this.toggleStore = toggleStore;
    }

    public PageRegistry registry() {
        return registry;
    }

    public ToggleStore toggleStore() {
        return toggleStore;
    }

    public void openPage(Player player, String pageId) {
        PageConfig page = registry.get(pageId);
        if (page == null) {
            player.sendMessage(ColorUtil.parse("&cPage '" + pageId + "' not found."));
            return;
        }
        GuiHolder holder = new GuiHolder(pageId);
        String title = Placeholders.apply(player, page.title());
        Inventory inv = Bukkit.createInventory(holder, page.size(), ColorUtil.parse(title));
        holder.setInventory(inv);

        for (var entry : page.items().entrySet()) {
            int slot = entry.getKey();
            ItemConfig item = entry.getValue();
            inv.setItem(slot, buildItem(player, pageId, slot, item));
        }
        player.openInventory(inv);
    }

    public void rerenderSlot(Player player, Inventory inv, String pageId, int slot) {
        PageConfig page = registry.get(pageId);
        if (page == null) return;
        ItemConfig item = page.items().get(slot);
        if (item == null) return;
        inv.setItem(slot, buildItem(player, pageId, slot, item));
    }

    public ItemStack buildItem(Player player, String pageId, int slot, ItemConfig item) {
        if (item.isToggle() && item.toggleConfig() != null) {
            boolean enabled = toggleStore.isEnabled(player, pageId, slot, item.toggleConfig().defaultEnabled());
            ItemAppearance app = enabled
                    ? item.toggleConfig().enableAppearance()
                    : item.toggleConfig().disableAppearance();
            return buildStack(player, app.material(), app.displayName(), app.lore(), app.customModelData());
        }
        return buildStack(player, item.material(), item.displayName(), item.lore(), item.customModelData());
    }

    private ItemStack buildStack(Player player, org.bukkit.Material material, String name,
                                  java.util.List<String> lore, Integer cmd) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.displayName(ColorUtil.parseItemName(Placeholders.apply(player, name)));
            }
            if (lore != null && !lore.isEmpty()) {
                meta.lore(ColorUtil.parseLore(Placeholders.applyList(player, lore)));
            }
            if (cmd != null) {
                meta.setCustomModelData(cmd);
            }
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public JavaPlugin plugin() {
        return plugin;
    }
}
