package me.pondhalf.project.tinySettings.command;

import me.pondhalf.project.tinySettings.config.PageRegistry;
import me.pondhalf.project.tinySettings.gui.GuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class TinySettingsCommand implements CommandExecutor, TabCompleter {

    private final GuiManager guiManager;
    private final PageRegistry registry;

    public TinySettingsCommand(GuiManager guiManager, PageRegistry registry) {
        this.guiManager = guiManager;
        this.registry = registry;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                              @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("tinysettings.reload")) {
                sender.sendMessage("§cYou don't have permission.");
                return true;
            }
            registry.reload();
            sender.sendMessage("§aTinySettings pages reloaded.");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can open the GUI.");
            return true;
        }
        if (!player.hasPermission("tinysettings.use")) {
            sender.sendMessage("§cYou don't have permission.");
            return true;
        }
        String pageId = args.length >= 1 ? args[0] : "main";
        guiManager.openPage(player, pageId);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                       @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> out = new java.util.ArrayList<>();
            if (sender.hasPermission("tinysettings.reload")) out.add("reload");
            return out;
        }
        return List.of();
    }
}
