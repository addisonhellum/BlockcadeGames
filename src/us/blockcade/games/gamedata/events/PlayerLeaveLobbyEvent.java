package us.blockcade.games.gamedata.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.core.util.userdata.BlockcadeUsers;
import us.blockcade.games.gamedata.data.GameLobby;

public class PlayerLeaveLobbyEvent extends org.bukkit.event.Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private GameLobby lobby;
    private boolean cancelled = false;

    public PlayerLeaveLobbyEvent(Player player, GameLobby lobby) {
        this.player = player;
        this.lobby = lobby;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public BPlayer getBPlayer() { return BlockcadeUsers.getBPlayer(player); }

    public GameLobby getLobby() {
        return lobby;
    }

    public boolean isCancelled() { return cancelled; }

    public void setCancelled(boolean value) { cancelled = true; }

}
