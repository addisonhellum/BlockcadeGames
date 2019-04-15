package us.blockcade.games.gamedata.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import us.blockcade.core.common.game.BlockcadeTeam;
import us.blockcade.core.util.blocks.BoundedArea;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.math.Coordinate;
import us.blockcade.games.Main;

import java.util.ArrayList;
import java.util.List;

public class GameArena {

    private String name;
    private List<BlockcadeTeam> teams;

    private BoundedArea boundaries;

    public GameArena(String name) {
        this.name = name;
        registerTeams();
        registerBounds();
    }

    public String getName() {
        return ChatUtil.toTitleCase(name).replace("_", " ");
    }

    public String getConfigName() {
        return name;
    }

    public ConfigurationSection getConfigurationSection() {
        return Main.getInstance().getConfig().getConfigurationSection("game-lobbies.arenas." + getConfigName());
    }

    public boolean exists() {
        return Main.getInstance().getConfig().get("game-lobbies.arenas." + getConfigName()) != null;
    }

    private void registerBounds() {
        ConfigurationSection lowerBound = getConfigurationSection().getConfigurationSection("boundaries.lower-corner");
        ConfigurationSection upperBound = getConfigurationSection().getConfigurationSection("boundaries.upper-corner");

        double lowerX = lowerBound.getDouble("x");
        double lowerY = lowerBound.getDouble("y");
        double lowerZ = lowerBound.getDouble("z");

        double upperX = upperBound.getDouble("x");
        double upperY = upperBound.getDouble("y");
        double upperZ = upperBound.getDouble("z");

        if (getConfigurationSection().getConfigurationSection("boundaries.center") != null) {
            ConfigurationSection center = getConfigurationSection().getConfigurationSection("boundaries.center");

            double centerX = center.getDouble("x");
            double centerY = center.getDouble("y");
            double centerZ = center.getDouble("z");

            boundaries = new BoundedArea(new Coordinate(lowerX, lowerY, lowerZ),
                    new Coordinate(upperX, upperY, upperZ),
                    new Coordinate(centerX, centerY, centerZ));
            return;
        }

        boundaries = new BoundedArea(new Coordinate(lowerX, lowerY, lowerZ), new Coordinate(upperX, upperY, upperZ));
    }

    private void registerTeams() {
        List<String> teamNames = new ArrayList<>();

        for (int i = 1; i <= getSpawns().size(); i++) {
            String team = Main.getInstance().getConfig().getString("game-lobbies.arenas." +
                    getConfigName().toLowerCase() + ".spawnpoints." + i + ".team");
            if (!teamNames.contains(team)) {
                teamNames.add(team);
            }
        }

        List<BlockcadeTeam> teams = new ArrayList<>();
        for (String name : teamNames) {
            teams.add(new BlockcadeTeam(name));
        }

        this.teams = teams;
    }

    public List<BlockcadeTeam> getTeams() {
        return teams;
    }

    public BlockcadeTeam getTeam(int spawnId) {
        String team = Main.getInstance().getConfig().getString("game-lobbies.arenas." +
                getConfigName() + "." + spawnId + ".team");

        return new BlockcadeTeam(team);
    }

    public List<Coordinate> getSpawns(String team) {
        FileConfiguration config = Main.getInstance().getConfig();
        List<Coordinate> spawns = new ArrayList<>();

        String prefix = "game-lobbies.arenas." + getConfigName().toLowerCase() + ".spawnpoints";

        for (String key : config.getConfigurationSection(prefix).getKeys(true)) {
            String teamName = config.getString(prefix + "." + key + ".team");
            if (team.equalsIgnoreCase(teamName)) {
                double x = config.getDouble(prefix + "." + key + ".x");
                double y = config.getDouble(prefix + "." + key + ".y");
                double z = config.getDouble(prefix + "." + key + ".z");
                float yaw = (float) config.getDouble(prefix + "." + key + ".yaw");
                float pitch = (float) config.getDouble(prefix + "." + key + ".pitch");

                spawns.add(new Coordinate(x, y, z, yaw, pitch));
            }
        }
        return spawns;
    }

    public List<Coordinate> getSpawns() {
        FileConfiguration config = Main.getInstance().getConfig();
        List<Coordinate> spawns = new ArrayList<>();

        String prefix = "game-lobbies.arenas." + getConfigName().toLowerCase() + ".spawnpoints";

        for (String key : config.getConfigurationSection(prefix).getKeys(false)) {
            double x = config.getDouble(prefix + "." + key + ".x");
            double y = config.getDouble(prefix + "." + key + ".y");
            double z = config.getDouble(prefix + "." + key + ".z");
            float yaw = (float) config.getDouble(prefix + "." + key + ".yaw");
            float pitch = (float) config.getDouble(prefix + "." + key + ".pitch");

            spawns.add(new Coordinate(x, y, z, yaw, pitch));
        }
        return spawns;
    }

    public BoundedArea getBoundaries() {
        return boundaries;
    }

    public ConfigurationSection getMapData() {
        return getConfigurationSection().getConfigurationSection("map-data");
    }

}
