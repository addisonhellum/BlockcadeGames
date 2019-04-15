package us.blockcade.games.games.payload;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockcade.core.common.game.BlockcadeGame;

import us.blockcade.core.common.game.BlockcadeTeam;
import us.blockcade.core.util.blocks.BoundedArea;
import us.blockcade.core.util.blocks.RegionEditor;
import us.blockcade.core.util.effect.ExpCountdown;
import us.blockcade.core.util.gui.Title;
import us.blockcade.core.util.npc.VillagerNPC;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.games.Main;
import us.blockcade.games.gamedata.LobbyManager;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.gamedata.events.GameEndEvent;
import us.blockcade.games.gamedata.events.GameStartEvent;
import us.blockcade.games.gamedata.events.PlayerDeathByPlayerEvent;
import us.blockcade.games.util.GameSpectator;

import java.util.*;

public class PayloadGame implements Listener {

    private static BlockcadeGame game;
    public static BlockcadeGame getGame() {
        return game;
    }

    private static PayloadUtil util;
    public PayloadUtil getUtility() { return util; }

    private static PayloadEffect effect;
    public PayloadEffect getEffectManager() { return effect; }

    public static void initialize() {
        game = new BlockcadeGame("Payload",
                new String[] {"Capture the payload in the center of the map", "and deliver it to the enemy base to" +
                        " detonate it.", "", "First team to win 5 rounds is victorious."});

        game.setMinimumPlayers(4);
        game.setMaximumPlayers(8);
        game.registerHandler(new PayloadGame());

        game.setInstructions("Capture the Payload from the middle of the map and deliver it to your opponents' base. " +
                "Be careful though, as you only have 5 lives. First team to win 5 rounds is victorious!");

        util = new PayloadUtil();
        effect = new PayloadEffect();
    }

    private List<RegionEditor> barrierEditors = new ArrayList<>();
    private ExpCountdown countdown = new ExpCountdown(10);

    @EventHandler
    public void onStart(GameStartEvent event) {
        for (Player player : event.getLobby().getPlayers()) {
            getUtility().giveTeamGear(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 0));
            countdown.run(player);

            getUtility().setLives(player, 5);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                GameLobby lobby = event.getLobby();

                for (BlockcadeTeam team : lobby.getArena().getTeams()) {
                    Location loc = getUtility().getMerchantLocation(lobby.getArena(), team).makeLocation(lobby.getWorld());

                    BoundedArea barrier = getUtility().getBarrierArea(lobby.getArena(), team);
                    barrierEditors.add(new RegionEditor(barrier.getFirstPosition().makeLocation(lobby.getWorld()),
                            barrier.getSecondPosition().makeLocation(lobby.getWorld())));

                    VillagerNPC npc = new VillagerNPC(loc, team.getSpigotColor() + team.getName() + " Merchant",
                            ChatColor.AQUA + "" + ChatColor.BOLD + "OPEN SHOP");
                    npc.setCommand("payload shop");
                    npc.spawn();

                    lobby.broadcastTitle(new Title("", "&ePurchase upgrades from the Merchant"));

                    npc.despawn(190);
                    getUtility().spawnPayload(lobby.getArena().getBoundaries().getCenter().makeLocation(lobby.getWorld()));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (RegionEditor editor : barrierEditors)
                                editor.replace(Material.STAINED_GLASS, Material.AIR);

                            lobby.broadcastTitle(new Title("", "&a&lFight!"));
                            lobby.broadcastSound(Sound.ENDERDRAGON_GROWL);

                            getEffectManager().displayPayloadDrop(lobby.getArena().getBoundaries()
                                    .getCenter().makeLocation(lobby.getWorld()));
                        }
                    }.runTaskLater(Main.getInstance(), 200);
                }
            }
        }.runTaskLater(Main.getInstance(), 20);
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        GameLobby lobby = event.getLobby();

        for (RegionEditor editor : barrierEditors)
            editor.restore();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() == null) return;
        Material type = event.getClickedBlock().getType();

        if (type.equals(Material.BIRCH_FENCE_GATE))
            event.setCancelled(true);
    }

    @EventHandler
    public void onFriendFire(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damage = event.getDamager();

        if (entity instanceof Player && damage instanceof Player) {
            Player player = (Player) entity;
            Player damager = (Player) damage;

            if (LobbyManager.hasLobby(player)) {
                GameLobby lobby = LobbyManager.getLobby(player);
                if (lobby.getTeam(player) == lobby.getTeam(damager))
                    event.setCancelled(true);
            }
        }

        if (entity instanceof Player && damage instanceof Arrow) {
            Arrow arrow = (Arrow) damage;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) entity;
                Player damager = (Player) arrow.getShooter();

                if (LobbyManager.hasLobby(player)) {
                    GameLobby lobby = LobbyManager.getLobby(player);
                    if (lobby.getTeam(player) == lobby.getTeam(damager))
                        event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onKillEffect(PlayerDeathByPlayerEvent event) {
        GameLobby lobby = event.getLobby();

        BPlayer player = event.getPlayer();
        BPlayer killer = event.getKiller();

        int livesLeft = getUtility().getLives(player.spigot());

        Random r = new Random();
        String[] killMessages = new String[] {
                "was killed by", "fought to the end with", "got stomped on by", "was no match for",
                "was slain by the hand of", "was bamboozled by", "stood no chance against", "dropped their jaw to",
                "got oof'd by", "was vaporized by"
        };

        String tagOn = "";
        if (livesLeft == 1) tagOn = " &b&lFINAL KILL!";

        lobby.broadcast("&7⚔ " + player.getFormattedName() + " &7" +
                killMessages[r.nextInt(killMessages.length)] + " " + killer.getFormattedName() + tagOn);
        lobby.getWorld().strikeLightningEffect(event.getPlayer().spigot().getLocation());

        if (livesLeft > 1) {
            getUtility().setLives(player.spigot(), livesLeft - 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (livesLeft == 2) new Title("", "&c" + (livesLeft - 1) + " &blife remaining").send(player.spigot());
                    else new Title("", "&c" + (livesLeft - 1) + " &blives remaining").send(player.spigot());
                    new ExpCountdown(5).run(player.spigot());
                }
            }.runTaskLater(Main.getInstance(), 20);

            new BukkitRunnable() {
                @Override
                public void run() {
                    GameSpectator.removeSpectator(player.spigot());
                    getUtility().giveTeamGear(player.spigot());
                    player.spigot().teleport(lobby.getArena().getSpawns(lobby.getTeam(player.spigot())
                            .getName()).get(0).makeLocation(lobby.getWorld()));
                }
            }.runTaskLater(Main.getInstance(), 140);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    new Title("&c&lEliminated!", "&bYou're out for this round.").send(player.spigot());
                }
            }.runTaskLater(Main.getInstance(), 20);
        }
    }

}
