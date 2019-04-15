package us.blockcade.games.gamedata.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.games.gamedata.data.GameLobby;

public class IngameDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private GameLobby lobby;
    private BPlayer player;
    private EntityDamageEvent.DamageCause cause;
    private boolean cancelled = false;

    public IngameDeathEvent(GameLobby lobby, BPlayer player, EntityDamageEvent.DamageCause cause) {
        this.lobby = lobby;
        this.player = player;
        this.cause = cause;
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

    public BPlayer getPlayer() {
        return player;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }

    public boolean isCancelled() { return cancelled; }

    public void setCancelled(boolean value) { cancelled = true; }

}
