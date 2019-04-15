package us.blockcade.games.games.cagematch;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import us.blockcade.core.common.game.BlockcadeGame;
import us.blockcade.core.util.gui.Title;
import us.blockcade.games.gamedata.LobbyManager;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.gamedata.events.GameEndEvent;
import us.blockcade.games.gamedata.events.GameStartEvent;
import us.blockcade.games.gamedata.events.PlayerDeathByPlayerEvent;
import us.blockcade.games.util.Loadout;

public class CagematchGame implements Listener {

    private static BlockcadeGame game;
    public static BlockcadeGame getGame() {
        return game;
    }

    private static Loadout mode = CagematchModes.getRandomLoadout();

    public static void initialize() {
        game = new BlockcadeGame("Cage Matches",
                new String[] {"You have 5 minutes to fight to the death.", "Good luck! May the better fighter win.",
                              "", "&f&lMode: &6" + mode.getName()});

        game.registerHandler(new CagematchGame());
        game.setMinimumPlayers(2);
        game.setMaximumPlayers(2);

        game.setInstructions("Cage matches is a duel-type game where 2 players fight to be victorious. " +
                "There are different modes, with varying kits & abilities to enrich the fighting experience.");
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        GameLobby lobby = event.getLobby();

        for (Player player : lobby.getPlayers())
            mode.give(player);
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        mode = CagematchModes.getRandomLoadout();
        game.setDescription(new String[] {"You have 5 minutes to fight to the death.", "Good luck! May the better fighter win.",
                "", "&f&lMode: &6" + mode.getName()});
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) return;
        Player player = (Player) entity;

        if (LobbyManager.getLobby(player) == null) return;

        if (mode.getData()[0] == "noregen")
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathByPlayerEvent event) {
        GameLobby lobby = event.getLobby();

        lobby.getWorld().strikeLightningEffect(event.getPlayer().spigot().getLocation());
        Title victory = new Title("&6&lVICTORY!", "&7You defeated " + event.getPlayer().getFormattedName());
        victory.send(event.getKiller().spigot());

        lobby.endGame();
    }

}
