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

public class StartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("start")) {
            if (!(s instanceof Player)) {
                s.sendMessage(ChatUtil.format("&cConsole, don't try to start beef with me. You'll lose."));
                return false;
            }
            Player player = (Player) s;
            BPlayer bplayer = BlockcadeUsers.getBPlayer(player);

            if (!bplayer.hasAccess(Rank.ADMIN)) {
                player.sendMessage(ChatUtil.format("&cYou are not allowed to do this."));
                return false;
            }

            if (LobbyManager.getLobby(player) == null) {
                player.sendMessage(ChatUtil.format("&cYou must be in an active lobby to do this."));
                return false;
            }
            GameLobby lobby = LobbyManager.getLobby(player);

            if (!lobby.getGameState().equals(GameState.LOBBY)) {
                player.sendMessage(ChatUtil.format("&cYour lobby is already started or has already begun."));
                return false;
            }

            lobby.broadcast("&6The game has been force-started by an administrator.");
            lobby.startGame();
        }

        return false;
    }

}
