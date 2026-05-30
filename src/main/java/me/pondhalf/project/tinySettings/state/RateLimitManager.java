package me.pondhalf.project.tinySettings.state;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RateLimitManager {

    private final Map<UUID, Long> lastClick = new ConcurrentHashMap<>();
    private final Set<UUID> bypassed = ConcurrentHashMap.newKeySet();
    private boolean globalEnabled = true;
    private long cooldownMs = 1000;

    public boolean tryClick(Player player) {
        if (!globalEnabled) return true;
        if (bypassed.contains(player.getUniqueId())) return true;

        long now = System.currentTimeMillis();
        Long last = lastClick.get(player.getUniqueId());
        if (last != null && now - last < cooldownMs) return false;

        lastClick.put(player.getUniqueId(), now);
        return true;
    }

    public void setGlobalEnabled(boolean enabled) {
        this.globalEnabled = enabled;
    }

    public boolean isGlobalEnabled() {
        return globalEnabled;
    }

    public void setCooldown(long ms) {
        this.cooldownMs = ms;
    }

    public long getCooldown() {
        return cooldownMs;
    }

    public void setBypass(UUID uuid, boolean bypass) {
        if (bypass) bypassed.add(uuid);
        else bypassed.remove(uuid);
    }

    public boolean hasBypass(UUID uuid) {
        return bypassed.contains(uuid);
    }

    public void remove(UUID uuid) {
        lastClick.remove(uuid);
    }
}
