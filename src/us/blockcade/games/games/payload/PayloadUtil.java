package us.blockcade.games.games.payload;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import us.blockcade.core.common.game.BlockcadeTeam;
import us.blockcade.core.util.blocks.BoundedArea;
import us.blockcade.core.util.gui.ItemStackBuilder;
import us.blockcade.core.util.gui.hologram.FloatingBlock;
import us.blockcade.core.util.math.Coordinate;
import us.blockcade.games.gamedata.LobbyManager;
import us.blockcade.games.gamedata.data.GameArena;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.gamedata.data.GameState;

import java.util.HashMap;
import java.util.Map;

public class PayloadUtil {

    private PayloadUtil instance;

    public PayloadUtil() {
        instance = this;
    }

    public void giveTeamGear(Player player) {
        GameLobby lobby = LobbyManager.getLobby(player);

        if (lobby.getGameState().equals(GameState.LOBBY)) return;
        BlockcadeTeam team = lobby.getTeam(player);

        PlayerInventory inv = player.getInventory();
        inv.setArmorContents(null);
        inv.clear();

        inv.setChestplate(new ItemStackBuilder(Material.LEATHER_CHESTPLATE)
                .withName(team.getSpigotColor() + team.getName() + " Team")
                .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)
                .hideEnchantments(true).withColor(team.getColor()).build());
        inv.setLeggings(new ItemStackBuilder(Material.LEATHER_LEGGINGS)
                .withName(team.getSpigotColor() + team.getName() + " Team")
                .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)
                .hideEnchantments(true).withColor(team.getColor()).build());
        inv.setBoots(new ItemStackBuilder(Material.LEATHER_BOOTS)
                .withName(team.getSpigotColor() + team.getName() + " Team")
                .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)
                .hideEnchantments(true).withColor(team.getColor()).build());

        inv.setItem(0, new ItemStackBuilder(Material.STONE_SWORD)
                .withName(team.getSpigotColor() + team.getName() + " Blade").build());
    }

    public Coordinate getMerchantLocation(GameArena arena, BlockcadeTeam team) {
        String teamName = team.getName().toLowerCase();

        ConfigurationSection section = arena.getMapData().getConfigurationSection("npc-locations." + teamName);
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        return new Coordinate(x, y, z, yaw, pitch);
    }

    public BoundedArea getBarrierArea(GameArena arena, BlockcadeTeam team) {
        String teamName = team.getName().toLowerCase();
        ConfigurationSection section = arena.getMapData().getConfigurationSection("barriers." + teamName);

        double lowerX = section.getDouble("lower.x");
        double lowerY = section.getDouble("lower.y");
        double lowerZ = section.getDouble("lower.z");

        double upperX = section.getDouble("upper.x");
        double upperY = section.getDouble("upper.y");
        double upperZ = section.getDouble("upper.z");

        return new BoundedArea(new Coordinate(lowerX, lowerY, lowerZ), new Coordinate(upperX, upperY, upperZ));
    }

    private Map<String, Integer> playerLives = new HashMap<>();

    public int getLives(Player player) {
        return playerLives.get(player.getName());
    }

    public void setLives(Player player, int amount) {
        if (playerLives.containsKey(player.getName()))
            playerLives.remove(player.getName());

        playerLives.put(player.getName(), amount);
    }

    public void spawnPayload(Location location) {
        FloatingBlock payload = new FloatingBlock(Material.TNT, "&c&lPAYLOAD", "&bDeliver to Enemy Base!");
        payload.spawn(location);
    }

}
