package us.blockcade.games.gamedata.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.blockcade.games.gamedata.data.GameLobby;

public class GameStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private GameLobby lobby;
    private boolean cancelled = false;

    public GameStartEvent(GameLobby lobby) {
        this.lobby = lobby;
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

    public boolean isCancelled() { return cancelled; }

    public void setCancelled(boolean value) { cancelled = true; }

}
