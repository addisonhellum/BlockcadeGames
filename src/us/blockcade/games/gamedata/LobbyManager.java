package us.blockcade.games.gamedata;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockcade.core.common.chat.ChatFormater;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.gui.BossBar;
import us.blockcade.core.util.gui.ItemStackBuilder;
import us.blockcade.core.util.gui.book.BookUtil;
import us.blockcade.core.util.gui.book.PageBuilder;
import us.blockcade.core.util.gui.book.TextBuilder;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.core.util.userdata.BlockcadeUsers;
import us.blockcade.core.util.userdata.rank.Rank;
import us.blockcade.games.Main;
import us.blockcade.games.gamedata.data.GameArena;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.gamedata.data.GameState;
import us.blockcade.games.util.GameSpectator;

import java.util.ArrayList;
import java.util.List;

public class LobbyManager {

    private static List<GameLobby> lobbies = new ArrayList<>();
    public static List<GameLobby> getLobbies() { return lobbies; }

    public static BossBar lobbyBar = new BossBar(Main.getInstance(), "Loading...");

    public static void initializeLobbies() {
        for (World w : Bukkit.getWorlds()) {
            if (!w.getName().equalsIgnoreCase("world")) {
                lobbies.add(new GameLobby(w));
            }
        }

        initLobbyBar();
    }

    public static GameLobby getLobby(String name) {
        for (GameLobby lobby : getLobbies())
            if (lobby.getWorld().getName().equalsIgnoreCase(name)) return lobby;
        return null;
    }

    public static GameLobby getLobby(World world) {
        return getLobby(world.getName());
    }

    public static GameLobby getLobby(Player player) {
        for (GameLobby lobby : getLobbies())
            if (lobby.getPlayers().contains(player)) return lobby;
        return null;
    }

    public static boolean hasLobby(Player player) {
        return getLobby(player) != null;
    }

    public static void joinLobby(Player player, GameLobby lobby) {
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setHealth(20);
        player.setMaxHealth(20);
        player.setFoodLevel(20);
        player.setFlying(false);

        lobby.addPlayer(player);

        if (lobby.getGameState().equals(GameState.IN_PROGRESS)) {
            GameSpectator.makeSpectator(player);
            player.teleport(lobby.getArena().getBoundaries().getCenter().makeLocation(lobby.getWorld()));
        } else {
            player.teleport(lobby.getLobbyLocation());
            giveLobbyGear(player);
        }
    }

    public static void joinLobby(Player player) {
        for (World world : Bukkit.getWorlds()) {
            if (!world.getName().equalsIgnoreCase("world")) {
                GameLobby lobby = getLobby(world);

                if (lobby.getGameState().equals(GameState.LOBBY)) {
                    if (lobby.getActivePlayers().size() <= Main.getGame().getMaximumPlayers()) {
                        joinLobby(player, lobby);
                        return;
                    }
                }
            }
        }
    }

    public static List<GameArena> getArenas() {
        List<GameArena> arenas = new ArrayList<>();
        for (String a : Main.getInstance().getConfig().getConfigurationSection("game-lobbies.arenas").getKeys(false)) {
            if (!a.equalsIgnoreCase("prototype")) {
                GameArena arena = new GameArena(a);
                arenas.add(arena);
            }
        } return arenas;
    }

    public static GameArena getArena(String name) {
        for (GameArena arena : getArenas())
            if (arena.getName().equalsIgnoreCase(name)) return arena;
        return null;
    }

    private static void initLobbyBar() {
        ChatColor[] colors = new ChatColor[] {ChatColor.WHITE, ChatColor.GREEN,
                ChatColor.RED, ChatColor.AQUA, ChatColor.GOLD};

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                lobbyBar.refreshBar();
                if (index >= colors.length) index = 0;

                ChatColor color = colors[index];
                String text = "&ePlaying " + color + "&l" + Main.getGame().getName().toUpperCase() + " &eon the "
                        + "&bBlockcade Network";

                lobbyBar.setTitle(ChatUtil.format(text));

                index++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 12);
    }

    public static void giveLobbyGear(Player player) {
        BPlayer bplayer = BlockcadeUsers.getBPlayer(player);
        PlayerInventory inv = player.getInventory();

        if (bplayer.hasAccess(Rank.ADMIN)) {
            inv.setItem(5, new ItemStackBuilder(Material.NOTE_BLOCK)
                    .withName("&6Start Game &7(Admin)").withLore("Force-start your game.").build());
        }

        inv.setItem(8, new ItemStackBuilder(Material.INK_SACK)
                .withName("&cBack to Hub").withLore("Click to return to the Hub.").withData(1));

        inv.setItem(7, createInformationBook(player));
    }

    public static ItemStack createInformationBook(Player p) {
        return BookUtil.writtenBook()
                .author("Blockcade")
                .title(ChatUtil.format("&a" + Main.getGame().getName() + " Menu"))
                .pages(
                        new PageBuilder()
                                .add(TextBuilder.of(Main.getGame().getName().toUpperCase())
                                        .color(ChatColor.DARK_BLUE).style(ChatColor.BOLD).build())
                                .newLine()
                                .add(
                                        TextBuilder.of("Click the links below for more information:")
                                                .color(ChatColor.BLACK).build()
                                )
                                .newLine().newLine()
                                .add(
                                        TextBuilder.of(" ➤ ")
                                                .color(ChatColor.DARK_GRAY).build()
                                )
                                .add(
                                        TextBuilder.of("[How to Play]")
                                                .color(ChatColor.DARK_GREEN)
                                                .onClick(TextBuilder.ClickAction.runCommand("/howtoplay"))
                                                .onHover(TextBuilder.HoverAction.showText("Click to view How to Play"))
                                                .build()
                                )
                                .newLine().newLine()
                                .add(
                                        TextBuilder.of(" ➤ ")
                                                .color(ChatColor.DARK_GRAY).build()
                                )
                                .add(
                                        TextBuilder.of("[Leaderboard]")
                                                .color(ChatColor.GOLD)
                                                .onClick(TextBuilder.ClickAction.runCommand("/leaderboard"))
                                                .onHover(TextBuilder.HoverAction.showText("Click to view Leaderboard"))
                                                .build()
                                )
                                .newLine().newLine()
                                .add(
                                        TextBuilder.of(" ➤ ")
                                                .color(ChatColor.DARK_GRAY).build()
                                )
                                .add(
                                        TextBuilder.of("[Unlockables]")
                                                .color(ChatColor.DARK_PURPLE)
                                                .onClick(TextBuilder.ClickAction.runCommand("/unlockables"))
                                                .onHover(TextBuilder.HoverAction.showText("Click to view Unlockables"))
                                                .build()
                                )
                                .newLine().newLine()
                                .add(
                                        TextBuilder.of(" ➤ ")
                                                .color(ChatColor.DARK_GRAY).build()
                                )
                                .add(
                                        TextBuilder.of("[Game Settings]")
                                                .color(ChatColor.DARK_AQUA)
                                                .onClick(TextBuilder.ClickAction.runCommand("/gamesettings"))
                                                .onHover(TextBuilder.HoverAction.showText("Click to view Game Settings"))
                                                .build()
                                )
                                .build()
                )
                .build();
    }

}
