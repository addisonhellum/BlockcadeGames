package us.blockcade.games.gamedata;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockcade.core.commands.administrative.AoCommand;
import us.blockcade.core.util.api.event.server.ServerLoadEvent;
import us.blockcade.core.util.api.event.server.ServerUnloadEvent;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.gui.NametagUtil;
import us.blockcade.core.util.gui.Title;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.core.util.userdata.BlockcadeUsers;
import us.blockcade.games.Main;
import us.blockcade.games.gamedata.data.GameArena;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.gamedata.data.GameState;
import us.blockcade.games.gamedata.events.*;
import us.blockcade.games.util.GameSpectator;

import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.Vector;

public class LobbyHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);

        LobbyManager.joinLobby(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);

        if (LobbyManager.getLobby(player) != null)
            LobbyManager.getLobby(player).removePlayer(player);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    LobbyManager.joinLobby(player);
                    LobbyManager.lobbyBar.addPlayer(player);
                    LobbyManager.giveLobbyGear(player);
                }
            }
        }.runTaskLater(Main.getInstance(), 20);
    }

    @EventHandler
    public void onServerUnload(ServerUnloadEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (LobbyManager.getLobby(player) != null)
                player.teleport(LobbyManager.getLobby(player).getLobbyLocation());

            player.getInventory().setArmorContents(null);
            player.getInventory().clear();

            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    @EventHandler
    public void onIllegalY(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getBlockY() > 0) return;
        if (LobbyManager.getLobby(player) == null) return;

        event.setCancelled(true);
        player.teleport(LobbyManager.getLobby(player).getLobbyLocation());
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        for (Player ap : Bukkit.getOnlinePlayers())
            player.showPlayer(ap);

        if (LobbyManager.getLobby(world) == null) return;
        GameLobby lobby = LobbyManager.getLobby(world);

        for (Player ap : Bukkit.getOnlinePlayers())
            if (!world.getPlayers().contains(ap)) player.hidePlayer(ap);

        LobbyManager.joinLobby(player, lobby);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Set<Player> recipients = event.getRecipients();

        try {
            for (Player p2 : recipients) {
                if (!player.getWorld().getName().equals(p2.getWorld().getName())) {
                    event.getRecipients().remove(p2);
                }
            }
        } catch (ConcurrentModificationException e) {}
    }

    @EventHandler
    public void onLobbyJoin(PlayerJoinLobbyEvent event) {
        Player player = event.getPlayer();
        BPlayer bplayer = BlockcadeUsers.getBPlayer(player);
        GameLobby lobby = event.getLobby();

        if (lobby.getGameState().equals(GameState.LOBBY) && !LobbyManager.lobbyBar.hasPlayer(player))
            LobbyManager.lobbyBar.addPlayer(player);

        LobbyManager.giveLobbyGear(player);

        if (lobby.getPlayers().size() >= Main.getGame().getMinimumPlayers())
            lobby.startGame();
    }

    @EventHandler
    public void onLobbyLeave(PlayerLeaveLobbyEvent event) {
        Player player = event.getPlayer();
        BPlayer bplayer = BlockcadeUsers.getBPlayer(player);
        GameLobby lobby = event.getLobby();

        if (LobbyManager.lobbyBar.hasPlayer(player))
            LobbyManager.lobbyBar.removePlayer(player);

        if (lobby.getPlayers().size() < Main.getGame().getMinimumPlayers())
            lobby.setGameState(GameState.LOBBY);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (item.getType() == null) return;
        if (item.getItemMeta().getDisplayName() == null) return;

        if (item.getType().equals(Material.NOTE_BLOCK)
                && item.getItemMeta().getDisplayName().contains("Start Game")) {

            player.getInventory().setItem(5, new ItemStack(Material.AIR));
            player.performCommand("start");
        }

        if (item.getType().equals(Material.INK_SACK)
                && item.getItemMeta().getDisplayName().contains("Back to Hub")) {

            player.performCommand("connect hub01");
        }

        if (item.getType().equals(Material.WRITTEN_BOOK)
                && item.getItemMeta().getDisplayName().contains("How to Play")) {

            player.sendMessage(ChatUtil.format("&cThis feature is coming soon!"));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GameLobby lobby = LobbyManager.getLobby(player);

        if (AoCommand.hasOverride(player)) return;

        if (lobby.getGameState().equals(GameState.LOBBY) ||
                lobby.getGameState().equals(GameState.ENDED)) {

            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onIngameLastDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();
        double damage = event.getFinalDamage();

        if (entity instanceof Player) {
            Player player = (Player) entity;

            if (LobbyManager.getLobby(player) == null) {
                event.setCancelled(true);
                return;
            }
            GameLobby lobby = LobbyManager.getLobby(player);

            if (!lobby.getGameState().equals(GameState.IN_PROGRESS)) {
                event.setCancelled(true);
                return;
            }

            if (damage >= player.getHealth()) {
                BPlayer bplayer = BlockcadeUsers.getBPlayer(player);

                IngameDeathEvent deathEvent = new IngameDeathEvent(LobbyManager.getLobby(player), bplayer, cause);
                Main.getInstance().getServer().getPluginManager().callEvent(deathEvent);

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerKillPlayer(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        double damage = event.getFinalDamage();

        if (entity instanceof Player && damager instanceof Player) {
            Player player = (Player) entity;
            Player killer = (Player) damager;

            if (LobbyManager.getLobby(player) == null) return;
            if (!LobbyManager.getLobby(player).getGameState().equals(GameState.IN_PROGRESS)) return;
            if (player.getHealth() > damage) return;

            PlayerDeathByPlayerEvent deathByPlayerEvent = new PlayerDeathByPlayerEvent(LobbyManager.getLobby(player),
                    BlockcadeUsers.getBPlayer(player), BlockcadeUsers.getBPlayer(killer));
            Main.getInstance().getServer().getPluginManager().callEvent(deathByPlayerEvent);

            GameSpectator.makeSpectator(player);
            Title gameOver = new Title("&c&lYOU DIED", "&7You were killed by " +
                    BlockcadeUsers.getBPlayer(killer).getFormattedName());
            gameOver.send(player);

            event.setCancelled(true);

        } else if (entity instanceof Player && damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) entity;
                Player killer = (Player) arrow.getShooter();

                arrow.remove();

                if (LobbyManager.getLobby(player) == null) return;
                if (!LobbyManager.getLobby(player).getGameState().equals(GameState.IN_PROGRESS)) return;
                if (player.getHealth() > damage) return;

                PlayerDeathByPlayerEvent deathByPlayerEvent = new PlayerDeathByPlayerEvent(LobbyManager.getLobby(player),
                        BlockcadeUsers.getBPlayer(player), BlockcadeUsers.getBPlayer(killer));
                Main.getInstance().getServer().getPluginManager().callEvent(deathByPlayerEvent);

                GameSpectator.makeSpectator(player);
                Title gameOver = new Title("&c&lYOU DIED", "&7You were shot by " +
                        BlockcadeUsers.getBPlayer(killer).getFormattedName());
                gameOver.send(player);

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        event.setKeepLevel(true);
    }

    @EventHandler
    public void onBounds(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!LobbyManager.hasLobby(player)) return;
        GameLobby lobby = LobbyManager.getLobby(player);

        Location to = event.getTo();
        Location from = event.getFrom();

        if (!lobby.getGameState().equals(GameState.IN_PROGRESS)) return;
        if (to.getWorld() != from.getWorld()) return;

        if (AoCommand.hasOverride(player)) return;

        GameArena arena = lobby.getArena();
        if (!arena.getBoundaries().isWithinBounds(player.getLocation())) {
            player.teleport(arena.getBoundaries().getCenter().makeLocation(lobby.getWorld()));
        }
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        GameLobby lobby = event.getLobby();

        lobby.broadcastCentered("&6&m----------------------------------------------");
        lobby.broadcastCentered("&b&l" + Main.getGame().getName().toUpperCase());
        lobby.broadcast(" ");
        for (String desc : Main.getGame().getDescription())
            lobby.broadcastCentered("&a&l" + desc);
        lobby.broadcast(" ");
        lobby.broadcastCentered("&6&m------------------------------------");
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        GameLobby lobby = event.getLobby();

        lobby.setGameState(GameState.LOBBY);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : lobby.getWorld().getPlayers()) {
                    BPlayer bplayer = BlockcadeUsers.getBPlayer(player);

                    player.setLevel(0);
                    player.setExp(0F);

                    for (PotionEffect effect : player.getActivePotionEffects())
                        player.removePotionEffect(effect.getType());

                    bplayer.giveExperience(100);
                    LobbyManager.joinLobby(player);

                    NametagUtil.setNametagColor(player, bplayer.getRank().getChatColor());

                    if (GameSpectator.isSpectator(player))
                        GameSpectator.removeSpectator(player);
                }
            }
        }.runTaskLater(Main.getInstance(), 100);
    }

    @EventHandler
    public void onSneakTest(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking()) return;

        //if (LobbyManager.getLobby(player) != null)
        //    player.teleport(LobbyManager.getLobby(player).getLobbyLocation());
    }

}
