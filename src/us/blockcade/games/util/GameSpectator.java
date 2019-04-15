package us.blockcade.games.util;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.gui.ActionBar;
import us.blockcade.games.Main;
import us.blockcade.games.gamedata.LobbyManager;
import us.blockcade.games.gamedata.data.GameLobby;

import java.util.ArrayList;
import java.util.List;

public class GameSpectator implements Listener {

    private static List<Player> spectators = new ArrayList<>();
    public static List<Player> getSpectators() { return spectators; }

    public static void initializeSpectating() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getSpectators())
                    ActionBar.sendActionBarMessage(player, ChatUtil.format("&cYou are currently &lSPECTATING"));
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    public static void makeSpectator(Player player) {
        if (isSpectator(player)) return;
        spectators.add(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 1));
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();

        if (LobbyManager.getLobby(player) != null) {
            GameLobby lobby = LobbyManager.getLobby(player);
            for (Player p : lobby.getPlayers())
                p.hidePlayer(player);
        }
    }

    public static void removeSpectator(Player player) {
        if (!isSpectator(player)) return;
        spectators.remove(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        if (LobbyManager.getLobby(player) != null) {
            GameLobby lobby = LobbyManager.getLobby(player);
            for (Player p : lobby.getPlayers())
                p.showPlayer(player);
        }
    }

    public static boolean isSpectator(Player player) {
        return getSpectators().contains(player);
    }

    @EventHandler
    public void onSpecDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isSpectator(player))
                event.setCancelled(true);
        }

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (isSpectator(player))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpecPickup(PlayerPickupItemEvent event) {
        if (isSpectator(event.getPlayer()))
            event.setCancelled(true);
    }

}
