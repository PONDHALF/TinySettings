package me.pondhalf.project.tinySettings.command;

import me.pondhalf.project.tinySettings.config.PageRegistry;
import me.pondhalf.project.tinySettings.gui.GuiManager;
import me.pondhalf.project.tinySettings.state.RateLimitManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class TinySettingsCommand implements CommandExecutor, TabCompleter {

    private final GuiManager guiManager;
    private final PageRegistry registry;
    private final RateLimitManager rateLimitManager;

    public TinySettingsCommand(GuiManager guiManager, PageRegistry registry, RateLimitManager rateLimitManager) {
        this.guiManager = guiManager;
        this.registry = registry;
        this.rateLimitManager = rateLimitManager;
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

        if (args.length >= 1 && args[0].equalsIgnoreCase("ratelimit")) {
            return handleRateLimit(sender, args);
        }

        // /tinysettings open <player> [page]
        if (args.length >= 2 && args[0].equalsIgnoreCase("open")) {
            if (!sender.hasPermission("tinysettings.open")) {
                sender.sendMessage("§cYou don't have permission.");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer '" + args[1] + "' not found.");
                return true;
            }
            String pageId = args.length >= 3 ? args[2] : "main";
            guiManager.openPage(target, pageId);
            sender.sendMessage("§aOpened GUI page §f" + pageId + "§a for §f" + target.getName() + "§a.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can open the GUI.");
            sender.sendMessage("§cUse: /tinysettings open <player> [page]");
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

    private boolean handleRateLimit(CommandSender sender, String[] args) {
        if (!sender.hasPermission("tinysettings.ratelimit")) {
            sender.sendMessage("§cYou don't have permission.");
            return true;
        }

        // /tinysettings ratelimit          → show status
        if (args.length == 1) {
            sender.sendMessage("§eRate limit: " + (rateLimitManager.isGlobalEnabled() ? "§aON" : "§cOFF")
                    + " §7(cooldown: " + rateLimitManager.getCooldown() + "ms)");
            return true;
        }

        // /tinysettings ratelimit on|off
        if (args.length == 2) {
            String toggle = args[1];
            if (toggle.equalsIgnoreCase("on")) {
                rateLimitManager.setGlobalEnabled(true);
                sender.sendMessage("§aRate limit enabled.");
            } else if (toggle.equalsIgnoreCase("off")) {
                rateLimitManager.setGlobalEnabled(false);
                sender.sendMessage("§cRate limit disabled.");
            } else {
                sender.sendMessage("§cUsage: /tinysettings ratelimit <on|off>");
                sender.sendMessage("§cUsage: /tinysettings ratelimit bypass <player> <on|off>");
                sender.sendMessage("§cUsage: /tinysettings ratelimit cooldown <ms>");
            }
            return true;
        }

        // /tinysettings ratelimit bypass <player> <on|off>
        if (args.length >= 3 && args[1].equalsIgnoreCase("bypass")) {
            Player target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage("§cPlayer '" + args[2] + "' not found.");
                return true;
            }
            boolean bypass = args.length < 4 || !args[3].equalsIgnoreCase("off");
            rateLimitManager.setBypass(target.getUniqueId(), bypass);
            sender.sendMessage((bypass ? "§aGranted" : "§cRevoked") + " rate limit bypass for §f" + target.getName() + "§7.");
            return true;
        }

        // /tinysettings ratelimit cooldown <ms>
        if (args.length >= 3 && args[1].equalsIgnoreCase("cooldown")) {
            try {
                long ms = Long.parseLong(args[2]);
                if (ms < 50) ms = 50;
                rateLimitManager.setCooldown(ms);
                sender.sendMessage("§aCooldown set to §f" + ms + "ms§a.");
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number: " + args[2]);
            }
            return true;
        }

        sender.sendMessage("§cUsage: /tinysettings ratelimit <on|off>");
        sender.sendMessage("§cUsage: /tinysettings ratelimit bypass <player> <on|off>");
        sender.sendMessage("§cUsage: /tinysettings ratelimit cooldown <ms>");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                       @NotNull String label, @NotNull String[] args) {
        List<String> out = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("tinysettings.reload")) out.add("reload");
            if (sender.hasPermission("tinysettings.ratelimit")) out.add("ratelimit");
            if (sender.hasPermission("tinysettings.open")) out.add("open");
            return out;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("open") && sender.hasPermission("tinysettings.open")) {
            Bukkit.getOnlinePlayers().forEach(p -> out.add(p.getName()));
            return out;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("open") && sender.hasPermission("tinysettings.open")) {
            registry.pageIds().forEach(out::add);
            return out;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("ratelimit") && sender.hasPermission("tinysettings.ratelimit")) {
            return List.of("on", "off", "bypass", "cooldown");
        }

        if (args[0].equalsIgnoreCase("ratelimit") && sender.hasPermission("tinysettings.ratelimit")) {
            if (args.length == 3 && args[1].equalsIgnoreCase("bypass")) {
                Bukkit.getOnlinePlayers().forEach(p -> out.add(p.getName()));
                return out;
            }
            if (args.length == 4 && args[1].equalsIgnoreCase("bypass")) {
                return List.of("on", "off");
            }
        }

        return List.of();
    }
}
