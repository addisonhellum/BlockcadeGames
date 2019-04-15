package us.blockcade.games.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.gui.book.BookUtil;
import us.blockcade.games.gamedata.LobbyManager;

public class MenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("menu")) {
            if (!(s instanceof Player)) {
                s.sendMessage(ChatUtil.format("&cOof, not for Console."));
                return false;
            }
            Player player = (Player) s;

            BookUtil.openBook(player, LobbyManager.createInformationBook(player));
        }

        return false;
    }

}
