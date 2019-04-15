package us.blockcade.games.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockcade.core.commands.administrative.AoCommand;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.gui.ItemStackBuilder;
import us.blockcade.core.util.gui.menu.GuiMenu;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.core.util.userdata.BlockcadeUsers;
import us.blockcade.core.util.userdata.rank.Rank;
import us.blockcade.games.gamedata.LobbyManager;

public class WorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("world")) {
            if (!(s instanceof Player)) {
                s.sendMessage(ChatUtil.format("&cConsole, gtfo bish."));
                return false;
            }
            Player player = (Player) s;
            BPlayer bplayer = BlockcadeUsers.getBPlayer(player);

            if (!bplayer.hasAccess(Rank.BUILDER)) {
                player.sendMessage(ChatUtil.format("&cYou are not allowed to do this."));
                return false;
            }

            if (args.length == 0) {
                GuiMenu menu = new GuiMenu("Available Worlds", 2);

                for (World w : Bukkit.getWorlds()) {
                    if (!w.getName().equalsIgnoreCase("world") && !AoCommand.hasOverride(player)) {
                        Material mat = Material.GRASS;
                        if (w.getEnvironment().equals(World.Environment.NETHER)) mat = Material.NETHERRACK;
                        if (w.getEnvironment().equals(World.Environment.THE_END)) mat = Material.ENDER_STONE;

                        ItemStackBuilder iconBuilder = new ItemStackBuilder(mat).withName("&b" + w.getName())
                                .withLore(new String[]{
                                        "Players in world: &6" + w.getPlayers().size(),
                                        "State: &a" + LobbyManager.getLobby(w).getGameState().name()
                                });

                        if (player.getWorld().getName().equalsIgnoreCase(w.getName()))
                            iconBuilder = iconBuilder.withEnchantment(Enchantment.ARROW_INFINITE).hideEnchantments(true);

                        ItemStack icon = iconBuilder.build();

                        menu.add(icon).bindLeft(icon, "world " + w.getName());
                    }
                }

                menu.freezeItems(true);
                menu.display(player);

            } else if (args.length == 1) {
                String worldName = args[0];

                if (player.getWorld().getName().equalsIgnoreCase(worldName)) {
                    player.sendMessage(ChatUtil.format("&cYou are already in that world."));
                    return false;
                }

                for (World w : Bukkit.getWorlds()) {
                    if (w.getName().equalsIgnoreCase(worldName)) {
                        if (LobbyManager.getLobby(w) != null)
                            player.teleport(LobbyManager.getLobby(w).getLobbyLocation());
                        else player.teleport(w.getSpawnLocation());
                        player.sendMessage(ChatUtil.format("&aYou were teleported to world &6" + w.getName()));
                        return true;
                    }
                }

                player.sendMessage(ChatUtil.format("&cThere is no world by that name."));
            }
        }

        return false;
    }

}
