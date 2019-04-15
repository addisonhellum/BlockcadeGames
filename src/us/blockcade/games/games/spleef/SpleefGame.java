package us.blockcade.games.games.spleef;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockcade.core.common.game.BlockcadeGame;
import us.blockcade.core.util.api.event.server.ServerLoadEvent;
import us.blockcade.core.util.gui.ItemStackBuilder;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.core.util.userdata.BlockcadeUsers;
import us.blockcade.games.Main;
import us.blockcade.games.gamedata.LobbyManager;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.gamedata.data.GameState;
import us.blockcade.games.gamedata.events.GameEndEvent;
import us.blockcade.games.gamedata.events.GameStartEvent;
import us.blockcade.games.gamedata.events.PlayerJoinLobbyEvent;
import us.blockcade.games.gamedata.events.PlayerLeaveLobbyEvent;
import us.blockcade.games.games.spleef.util.SpleefUtil;
import us.blockcade.games.util.GameSpectator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpleefGame implements Listener {

    private static BlockcadeGame game;
    public static BlockcadeGame getGame() {
        return game;
    }

    private static SpleefUtil utility;
    public static SpleefUtil getUtility() { return utility; }

    private List<Player> remaining = new ArrayList<>();
    private List<Location> snowBlocks = new ArrayList<>();

    public static void initialize() {
        game = new BlockcadeGame("Spleef",
                new String[] {"Use your trusty shovel to break the blocks", "beneath your enemies. Don't fall or you will",
                        "be eliminated. Last player standing wins."});

        utility = new SpleefUtil();

        game.setMinimumPlayers(2);
        game.setMaximumPlayers(16);
        game.registerHandler(new SpleefGame());

        game.setInstructions("Break the blocks beneath your opponent to send them falling. Use snowballs to shoot blocks" +
                " from a distance. Last player standing on the platform wins.");
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        GameLobby lobby = event.getLobby();

        lobby.getPlayers().forEach(player -> {
            PlayerInventory inv = player.getInventory();
            BPlayer bplayer = BlockcadeUsers.getBPlayer(player);

            remaining.add(player);
            player.setGameMode(GameMode.SURVIVAL);

            inv.setChestplate(new ItemStackBuilder(Material.LEATHER_CHESTPLATE)
                    .withColor(getUtility().chestplateColors.get(player.getName())).build());
            inv.setItem(0, new ItemStackBuilder(Material.DIAMOND_SPADE).withName("&bSpleef Spade")
                    .withEnchantment(Enchantment.DIG_SPEED, 5).build());
        });
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEliminated(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        BPlayer bplayer = BlockcadeUsers.getBPlayer(player);

        if (!LobbyManager.hasLobby(player)) return;
        GameLobby lobby = LobbyManager.getLobby(player);

        if (!lobby.getGameState().equals(GameState.IN_PROGRESS)) return;

        if (remaining.size() == 1) {
            BPlayer bp = BlockcadeUsers.getBPlayer(remaining.get(0));
            lobby.setGameState(GameState.ENDED);

            lobby.broadcast(" ");
            lobby.broadcastCentered("&e&lWINNER: " + bp.getFormattedName());
            lobby.broadcast(" ");
            lobby.endGame();
            return;
        }

        try {
            if (!remaining.contains(player)) return;
            if (player.getLocation().getY() < lobby.getArena().getBoundaries().getCenter().getY()) {
                remaining.remove(player);
                lobby.broadcast(bplayer.getFormattedName() + " &7was eliminated! &b(" + remaining.size() +
                        " players left)");

                GameSpectator.makeSpectator(player);
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!LobbyManager.hasLobby(player)) {
            event.setCancelled(true);
            return;
        }

        GameLobby lobby = LobbyManager.getLobby(player);
        if (!lobby.getGameState().equals(GameState.IN_PROGRESS)) {
            event.setCancelled(true);
            return;
        }

        if (!event.getBlock().getType().equals(Material.SNOW_BLOCK)) {
            event.setCancelled(true);
            return;
        }

        player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
        snowBlocks.add(event.getBlock().getLocation());
    }

    @EventHandler
    public void onSnowball(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        ProjectileSource shooter = event.getEntity().getShooter();

        if (shooter instanceof Player && proj instanceof Snowball) {
            Player player = (Player) shooter;
            Snowball ball = (Snowball) proj;

            ball.getLocation().getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        remaining.clear();

        event.getLobby().setGameState(GameState.LOBBY);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location location : snowBlocks)
                    location.getBlock().setType(Material.SNOW_BLOCK);
            }
        }.runTaskLater(Main.getInstance(), 100);
    }

    @EventHandler
    public void onJoin(PlayerJoinLobbyEvent event) {
        Player player = event.getPlayer();
        BPlayer bplayer = event.getBPlayer();

        if (!event.getLobby().getGameState().equals(GameState.LOBBY)) return;

        Color rankColor = bplayer.getRank().getColor();
        getUtility().chestplateColors.put(player.getName(), rankColor);

        player.getInventory().setItem(0, new ItemStackBuilder(Material.LEATHER_CHESTPLATE)
                .withName("&bChoose a Chestplate Color &7(Right Click)").withColor(rankColor).build());
    }

    @EventHandler
    public void onLeave(PlayerLeaveLobbyEvent event) {
        Player player = event.getPlayer();
        getUtility().chestplateColors.remove(player.getName());
    }

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    BPlayer bplayer = BlockcadeUsers.getBPlayer(player);

                    player.getInventory().setItem(0, new ItemStackBuilder(Material.LEATHER_CHESTPLATE)
                            .withName("&bChoose a Chestplate Color &7(Right Click)").withColor(bplayer.getRank().getColor()).build());
                }
            }
        }.runTaskLater(Main.getInstance(), 20);
    }

    @EventHandler
    public void onChestplateColor(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (item.getItemMeta() == null) return;

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (item.getType().equals(Material.LEATHER_CHESTPLATE)) {
                getUtility().openChestplateSelection(player);
                event.setCancelled(true);
            }
        }
    }

}
