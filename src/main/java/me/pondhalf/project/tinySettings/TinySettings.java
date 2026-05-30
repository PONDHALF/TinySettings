package me.pondhalf.project.tinySettings;

import me.pondhalf.project.tinySettings.command.TinySettingsCommand;
import me.pondhalf.project.tinySettings.config.PageRegistry;
import me.pondhalf.project.tinySettings.gui.GuiListener;
import me.pondhalf.project.tinySettings.gui.GuiManager;
import me.pondhalf.project.tinySettings.state.RateLimitManager;
import me.pondhalf.project.tinySettings.state.ToggleStore;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class TinySettings extends JavaPlugin {

    private PageRegistry pageRegistry;
    private ToggleStore toggleStore;
    private GuiManager guiManager;
    private RateLimitManager rateLimitManager;

    @Override
    public void onEnable() {
        saveResource("pages/main.yml", false);

        this.rateLimitManager = new RateLimitManager();
        this.toggleStore = new ToggleStore(this);
        this.pageRegistry = new PageRegistry(this);
        this.pageRegistry.reload();
        this.guiManager = new GuiManager(this, pageRegistry, toggleStore);

        getServer().getPluginManager().registerEvents(new GuiListener(guiManager, rateLimitManager), this);

        TinySettingsCommand cmd = new TinySettingsCommand(guiManager, pageRegistry, rateLimitManager);
        PluginCommand pc = getCommand("tinysettings");
        if (pc != null) {
            pc.setExecutor(cmd);
            pc.setTabCompleter(cmd);
        }
    }

    @Override
    public void onDisable() {
    }
}
