package me.pondhalf.project.tinySettings.gui;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public final class GuiHolder implements InventoryHolder {

    private final String pageId;
    private Inventory inventory;

    public GuiHolder(String pageId) {
        this.pageId = pageId;
    }

    public String pageId() {
        return pageId;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory == null) {
            throw new IllegalStateException("Inventory not yet assigned to holder for page " + pageId);
        }
        return inventory;
    }
}
