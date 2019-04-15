package us.blockcade.games.gamedata.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.gamedata.data.GameState;

public class GameStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private GameState state;
    private GameLobby lobby;
    private boolean cancelled = false;

    public GameStateChangeEvent(GameState state, GameLobby lobby) {
        this.state = state;
        this.lobby = lobby;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GameState getGameState() {
        return state;
    }

    public GameLobby getLobby() {
        return lobby;
    }

    public boolean isCancelled() { return cancelled; }

    public void setCancelled(boolean value) { cancelled = true; }

}
