package us.blockcade.games.gamedata.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.games.gamedata.data.GameLobby;

public class PlayerDeathByPlayerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private GameLobby lobby;
    private BPlayer player;
    private BPlayer killer;
    private boolean cancelled = false;

    public PlayerDeathByPlayerEvent(GameLobby lobby, BPlayer player, BPlayer killer) {
        this.lobby = lobby;
        this.player = player;
        this.killer = killer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GameLobby getLobby() {
        return lobby;
    }

    public BPlayer getPlayer() { return player; }

    public BPlayer getKiller() { return killer; }

    public boolean isCancelled() { return cancelled; }

    public void setCancelled(boolean value) { cancelled = true; }

}
