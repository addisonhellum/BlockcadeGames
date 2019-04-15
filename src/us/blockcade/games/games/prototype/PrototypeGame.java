package us.blockcade.games.games.prototype;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import us.blockcade.core.common.environment.EnvironmentHandler;
import us.blockcade.core.common.game.BlockcadeGame;
import us.blockcade.core.util.gui.ItemStackBuilder;
import us.blockcade.core.util.gui.Title;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.gamedata.events.GameEndEvent;
import us.blockcade.games.gamedata.events.GameStartEvent;
import us.blockcade.games.gamedata.events.PlayerDeathByPlayerEvent;

public class PrototypeGame implements Listener {

    private static BlockcadeGame game;
    public static BlockcadeGame getGame() {
        return game;
    }

    public static void initialize() {
        game = new BlockcadeGame("Prototype",
                new String[] {"Description of the gamemode goes here.","Blockcade Development Team."});

        game.registerHandler(new PrototypeGame());
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        GameLobby lobby = event.getLobby();

        for (Player player : lobby.getPlayers()) {
            PlayerInventory inv = player.getInventory();

            inv.setArmorContents(null);
            inv.clear();

            inv.setItem(0, new ItemStackBuilder(Material.WOOD_SWORD).build());
            inv.setItem(1, new ItemStackBuilder(Material.FISHING_ROD).build());

            inv.setHelmet(new ItemStackBuilder(Material.IRON_HELMET).build());
            inv.setChestplate(new ItemStackBuilder(Material.IRON_CHESTPLATE).build());
            inv.setLeggings(new ItemStackBuilder(Material.IRON_LEGGINGS).build());
            inv.setBoots(new ItemStackBuilder(Material.IRON_BOOTS).build());
        }

        EnvironmentHandler.setPvP(true);
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        EnvironmentHandler.setPvP(false);
    }

    @EventHandler
    public void onDeath(PlayerDeathByPlayerEvent event) {
        GameLobby lobby = event.getLobby();
        BPlayer player = event.getPlayer();
        BPlayer killer = event.getKiller();

        Title victory = new Title("&6&lVICTORY!", "&7You defeated " + player.getFormattedName());
        victory.send(killer.spigot());

        lobby.getWorld().strikeLightningEffect(player.spigot().getLocation());
        lobby.broadcast(player.getFormattedName() + " &7was killed by " + killer.getFormattedName());
        lobby.endGame();
    }

}
