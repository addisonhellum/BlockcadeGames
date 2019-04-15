package us.blockcade.games.games.spleef.commands;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.format.ColorUtil;
import us.blockcade.core.util.gui.ItemStackBuilder;
import us.blockcade.core.util.userdata.rank.Rank;
import us.blockcade.core.util.userdata.rank.RankManager;
import us.blockcade.games.games.spleef.SpleefGame;
import us.blockcade.games.games.spleef.util.SpleefUtil;

public class SpleefCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (args.length == 0) {

        } else if (args.length == 1) {

        } else if (args.length == 2) {
            String arg = args[0];
            String type = args[1];

            if (arg.equalsIgnoreCase("setchestplate")) {
                if (!(s instanceof Player)) {
                    s.sendMessage(ChatUtil.format("&cConsole, stop tryna trip me up."));
                    return false;
                }
                Player player = (Player) s;

                SpleefUtil utility = SpleefGame.getUtility();

                if (utility.chestplateColors.containsKey(player.getName()))
                    utility.chestplateColors.remove(player.getName());

                Color color = ColorUtil.getColor(type);
                utility.chestplateColors.put(player.getName(), color);

                player.getInventory().setItem(0, new ItemStackBuilder(Material.LEATHER_CHESTPLATE)
                        .withName("&bChoose a Chestplate Color &7(Right Click)").withColor(color).build());
                s.sendMessage(ChatUtil.format("&aChestplate color updated to " + type));

                player.getInventory().setChestplate(new ItemStackBuilder(Material.LEATHER_CHESTPLATE)
                        .withColor(color).withName("&bSpleef Chestplate").build());
            }

            if (arg.equalsIgnoreCase("noaccess")) {
                Rank rank = RankManager.fromString(type);
                s.sendMessage(ChatUtil.format("&cYou must be rank " + rank.getChatColor() + rank.getName() +
                " &cto do this!"));
            }
        }

        return false;
    }

}
