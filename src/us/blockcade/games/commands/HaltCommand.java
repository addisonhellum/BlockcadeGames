package us.blockcade.games.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.core.util.userdata.BlockcadeUsers;
import us.blockcade.core.util.userdata.rank.Rank;
import us.blockcade.games.gamedata.LobbyManager;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.gamedata.data.GameState;

public class HaltCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("halt")) {
            if (!(s instanceof Player)) {
                s.sendMessage(ChatUtil.format("&cCalm down Console, you control freak."));
                return false;
            }
            Player player = (Player) s;
            BPlayer bplayer = BlockcadeUsers.getBPlayer(player);

            if (!bplayer.hasAccess(Rank.ADMIN)) {
                s.sendMessage(ChatUtil.format("&cYou are not allowed to do this!"));
                return false;
            }

            if (!LobbyManager.hasLobby(player)) {
                s.sendMessage(ChatUtil.format("&cYou must be in an active lobby to do this."));
                return false;
            }
            GameLobby lobby = LobbyManager.getLobby(player);

            if (!lobby.getGameState().equals(GameState.IN_PROGRESS)) {
                s.sendMessage(ChatUtil.format("&cThe game is not currently in progress."));
                return false;
            }

            lobby.broadcast("&4An administrator has force-ended the game. \n" +
                    "No stats have been altered this round.");
            lobby.endGame();
        }

        return false;
    }

}
