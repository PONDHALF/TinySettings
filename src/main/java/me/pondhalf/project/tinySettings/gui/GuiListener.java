package me.pondhalf.project.tinySettings.gui;

import me.pondhalf.project.tinySettings.config.ClickActions;
import me.pondhalf.project.tinySettings.config.ItemAppearance;
import me.pondhalf.project.tinySettings.config.ItemConfig;
import me.pondhalf.project.tinySettings.config.PageConfig;
import me.pondhalf.project.tinySettings.util.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class GuiListener implements Listener {

    private final GuiManager guiManager;

    public GuiListener(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof GuiHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof GuiHolder gui)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        PageConfig page = guiManager.registry().get(gui.pageId());
        if (page == null) {
            player.closeInventory();
            return;
        }
        ItemConfig item = page.items().get(event.getSlot());
        if (item == null) return;

        if (item.isToggle() && item.toggleConfig() != null) {
            boolean current = guiManager.toggleStore().isEnabled(player, gui.pageId(), event.getSlot(),
                    item.toggleConfig().defaultEnabled());
            boolean next = !current;
            guiManager.toggleStore().set(player, gui.pageId(), event.getSlot(), next);
            guiManager.rerenderSlot(player, event.getInventory(), gui.pageId(), event.getSlot());
            ItemAppearance app = next
                    ? item.toggleConfig().enableAppearance()
                    : item.toggleConfig().disableAppearance();
            runActions(player, event.getInventory(), app.onClick());
        } else {
            runActions(player, event.getInventory(), item.onClick());
        }
    }

    private void runActions(Player player, Inventory inv, ClickActions actions) {
        if (actions == null) return;
        for (String cmd : actions.consoleCommands()) {
            String resolved = Placeholders.apply(player, cmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), resolved);
        }
        for (String cmd : actions.playerCommands()) {
            String resolved = Placeholders.apply(player, cmd);
            player.performCommand(resolved);
        }
        if (actions.gotoPage() != null) {
            String target = Placeholders.apply(player, actions.gotoPage());
            Bukkit.getScheduler().runTask(guiManager.plugin(), () -> guiManager.openPage(player, target));
        }
    }
}
