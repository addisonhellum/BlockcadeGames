package us.blockcade.games.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockcade.core.common.game.BlockcadeTeam;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.gui.ItemStackBuilder;
import us.blockcade.core.util.gui.menu.GuiMenu;
import us.blockcade.core.util.math.Coordinate;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.core.util.userdata.BlockcadeUsers;
import us.blockcade.core.util.userdata.rank.Rank;
import us.blockcade.games.gamedata.LobbyManager;
import us.blockcade.games.gamedata.data.GameArena;
import us.blockcade.games.gamedata.data.GameLobby;

public class ArenaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("arena")) {
            if (!(s instanceof Player)) {
                s.sendMessage(ChatUtil.format("&aListing loaded arenas..."));
                for (GameArena arena : LobbyManager.getArenas()) {
                    s.sendMessage("- " + arena.getName() + " (" + arena.getSpawns().size() + " spawns)");
                }
            } else {
                Player player = (Player) s;
                BPlayer bplayer = BlockcadeUsers.getBPlayer(player);

                if (!bplayer.hasAccess(Rank.BUILDER)) {
                    player.sendMessage(ChatUtil.format("&cYou are not allowed to do this."));
                    return false;
                }

                if (args.length == 0) {
                    GuiMenu menu = new GuiMenu("Available Arenas");
                    menu.lockColumn(1).lockColumn(8).lockRow(1).lockRow(3);

                    menu.set(4, new ItemStackBuilder(Material.ENDER_PEARL).withName("&aBack to Lobby")
                            .withLore("Click to return to the lobby.").build(), true).bindLeft(4, "arena lobby");

                    for (GameArena arena : LobbyManager.getArenas()) {
                        ItemStack icon = new ItemStackBuilder(Material.EMPTY_MAP)
                                .withName("&b" + arena.getName() + " &7(" + arena.getSpawns().size() + ")")
                                .withLore(new String[] {
                                        "Warning: May be a game in progress.", "", "&a&lLeft Click &7to visit arena",
                                        "&2&lRight Click &7to list spawns"
                                }).build();

                        menu.add(icon).bindLeft(icon, "arena " + arena.getName())
                                .bindRight(icon, "arena " + arena.getName() + " spawns");
                    }

                    menu.freezeItems(true);
                    menu.display(player);

                } else if (args.length == 1) {
                    String arenaName = args[0];

                    if (LobbyManager.getLobby(player) == null) {
                        player.sendMessage(ChatUtil.format("&cYou must be in a valid game lobby to do this."));
                        return false;
                    }
                    GameLobby lobby = LobbyManager.getLobby(player);

                    if (arenaName.equalsIgnoreCase("lobby")) {
                        player.teleport(lobby.getLobbyLocation());
                        return false;
                    }

                    if (LobbyManager.getArena(arenaName) == null) {
                        player.sendMessage(ChatUtil.format("&cThat arena does not exist."));
                        return false;
                    }
                    GameArena arena = LobbyManager.getArena(arenaName);

                    player.sendMessage(ChatUtil.format("&aTeleported to arena " + arena.getName()));
                    player.teleport(arena.getSpawns().get(0).makeLocation(lobby.getWorld()));

                } else if (args.length == 2) {
                    String arenaName = args[0];
                    String function = args[1];

                    if (LobbyManager.getLobby(player) == null) {
                        player.sendMessage(ChatUtil.format("&cYou must be in a valid game lobby to do this."));
                        return false;
                    }
                    GameLobby lobby = LobbyManager.getLobby(player);

                    if (LobbyManager.getArena(arenaName) == null) {
                        player.sendMessage(ChatUtil.format("&cThat arena does not exist."));
                        return false;
                    }
                    GameArena arena = LobbyManager.getArena(arenaName);

                    if (function.equalsIgnoreCase("spawns")) {
                        GuiMenu menu = new GuiMenu("Spawns: " + arena.getName());

                        menu.lockColumn(1).lockColumn(8).lockRow(1).lockRow(3);

                        menu.set(0, new ItemStackBuilder(Material.ARROW).withName("&c< Go Back")
                                .withLore("Go back to Arenas").build(), true).bindLeft(0, "arenas");

                        for (int i = 1; i <= arena.getSpawns().size(); i++) {
                            Coordinate spawn = arena.getSpawns().get(i - 1);
                            BlockcadeTeam team = arena.getTeam(i - 1);

                            ItemStack icon = new ItemStackBuilder(Material.PAPER)
                                    .withName("&b" + arena.getName() + " &7(Spawn #" + i + ")")
                                    .withLore(new String[] {
                                            "&7Team: " + team.getSpigotColor() + team.getName(), "",
                                            "&7X: &b" + spawn.getX(),
                                            "&7Y: &b" + spawn.getY(),
                                            "&7Z: &b" + spawn.getZ(),
                                            "&7Yaw: &3" + spawn.getYaw(),
                                            "&7Pitch: &3" + spawn.getPitch(), "",
                                            "&a&lLeft Click &7to visit spawn",
                                            "&2&lRight Click &7to set spawn"
                                    }).build();

                            menu.add(icon).bindLeft(icon, "tp " + spawn.getX() + " " + spawn.getY() + " " + spawn.getZ())
                                    .bindRight(icon, "arena " + arena.getName() + " setspawn " + i);
                        }

                        menu.freezeItems(true);
                        menu.display(player);
                    }
                }
            }
        }

        return false;
    }

}
