package us.blockcade.games.gamedata.data;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockcade.core.common.game.BlockcadeTeam;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.core.util.gui.NametagUtil;
import us.blockcade.core.util.gui.Title;
import us.blockcade.core.util.math.Coordinate;
import us.blockcade.core.util.userdata.BPlayer;
import us.blockcade.core.util.userdata.BlockcadeUsers;
import us.blockcade.games.Main;
import us.blockcade.games.gamedata.LobbyManager;
import us.blockcade.games.gamedata.events.*;
import us.blockcade.games.util.GameSpectator;

import java.util.*;

public class GameLobby {

    private World world;
    private GameState gameState = GameState.LOBBY;

    private List<Player> players = new ArrayList<>();
    private Map<Player, BlockcadeTeam> teams = new HashMap<>();
    private GameArena arena;

    private FileConfiguration config = Main.getInstance().getConfig();

    public GameLobby(World world) {
        this.world = world;

        getWorld().getPlayers().forEach(player -> { players.add(player); });
    }

    public World getWorld() {
        return world;
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getActivePlayers() {
        List<Player> players = new ArrayList<>();

        for (Player player : getPlayers())
            if (!GameSpectator.isSpectator(player)) players.add(player);

        return players;
    }

    public Location getLobbyLocation() {
        double x = config.getDouble("game-lobbies.lobby-location.x");
        double y = config.getDouble("game-lobbies.lobby-location.y");
        double z = config.getDouble("game-lobbies.lobby-location.z");
        float yaw = (float) config.getDouble("game-lobbies.lobby-location.yaw");
        float pitch = (float) config.getDouble("game-lobbies.lobby-location.pitch");

        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    public void setGameState(GameState gameState) {
        GameStateChangeEvent stateChangeEvent = new GameStateChangeEvent(gameState, this);
        Main.getInstance().getServer().getPluginManager().callEvent(stateChangeEvent);

        this.gameState = gameState;
    }

    public GameArena getArena() {
        return arena;
    }

    public void selectRandomArena() {
        Random r = new Random();
        List<GameArena> arenas = LobbyManager.getArenas();

        GameArena randArena = arenas.get(r.nextInt(arenas.size()));
        this.arena = randArena;
    }

    public void assignTeams() {
        List<BlockcadeTeam> teams = getArena().getTeams();

        int index = 0;
        for (Player player : getPlayers()) {
            if (index == teams.size()) index = 0;

            BlockcadeTeam team = teams.get(index);
            this.teams.put(player, team);
            team.addPlayer(player);

            if (teams.size() > 1)
                NametagUtil.setNametagColor(player, team.getSpigotColor());

            index++;
        }
    }

    public void teleportToArena() {
        if (getArena() == null) selectRandomArena();

        for (BlockcadeTeam team : getArena().getTeams()) {
            int index = 0;

            for (UUID uuid : team.getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                List<Coordinate> spawns = getArena().getSpawns(team.getName());
                if (index == spawns.size()) index = 0;

                Location spawn = spawns.get(index).makeLocation(getWorld());
                player.teleport(spawn);

                index++;
            }
        }
    }

    public BlockcadeTeam getTeam(Player player) {
        return teams.get(player);
    }

    public void startGame() {
        if (getGameState().equals(GameState.STARTING)) return;

        setGameState(GameState.STARTING);
        new BukkitRunnable() {
            int index = 10;

            @Override
            public void run() {
                if (index <= 0) {
                    if (!getGameState().equals(GameState.STARTING)) {
                        broadcast("&cCountdown stopped. Waiting for more players.");
                        broadcastSound(Sound.NOTE_BASS_DRUM);
                        cancel();
                        return;
                    }

                    selectRandomArena();
                    assignTeams();
                    teleportToArena();

                    setGameState(GameState.IN_PROGRESS);

                    broadcastSound(Sound.LEVEL_UP);

                    GameStartEvent startEvent = new GameStartEvent(LobbyManager.getLobby(getWorld()));
                    Main.getInstance().getServer().getPluginManager().callEvent(startEvent);

                    cancel();
                } else {
                    broadcastTitle(new Title("&a" + index, ""));
                    broadcastSound(Sound.NOTE_STICKS);

                    if (index <= 5) broadcast("&eThe game will begin in &b" + index + " &eseconds!");
                }

                index--;

            }
        }.runTaskTimer(Main.getInstance(), 5, 20);
    }

    public void endGame() {
        setGameState(GameState.ENDED);
        this.arena = null;

        for (Player player : getPlayers())
            if (GameSpectator.isSpectator(player)) GameSpectator.removeSpectator(player);

        GameEndEvent endEvent = new GameEndEvent(this);
        Main.getInstance().getServer().getPluginManager().callEvent(endEvent);
    }

    public void broadcast(String message) {
        for (Player player : getPlayers())
            player.sendMessage(ChatUtil.format(message));
    }

    public void broadcastCentered(String message) {
        for (Player player : getPlayers())
            ChatUtil.sendCenteredMessage(player, message);
    }

    public void broadcastSound(Sound sound) {
        for (Player player : getPlayers())
            player.playSound(player.getLocation(), sound, 2, 1);
    }

    public void broadcastTitle(Title title) {
        for (Player player : getPlayers())
            title.send(player);
    }

    public void addPlayer(Player player) {
        if (getPlayers().contains(player)) return;
        BPlayer bplayer = BlockcadeUsers.getBPlayer(player);
        players.add(player);

        for (Player p : getPlayers()) {
            p.showPlayer(player);
            player.showPlayer(p);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(getLobbyLocation());
                broadcast("&e[+] " + bplayer.getFormattedName() + " &ejoined! (&b" +
                        getPlayers().size() + "&e/&b" + Main.getGame().getMaximumPlayers() + "&e)");

                PlayerJoinLobbyEvent joinLobbyEvent = new PlayerJoinLobbyEvent(player, LobbyManager.getLobby(getWorld()));
                Main.getInstance().getServer().getPluginManager().callEvent(joinLobbyEvent);
            }
        }.runTaskLater(Main.getInstance(), 2);
    }

    public void removePlayer(Player player) {
        if (!getPlayers().contains(player)) return;
        BPlayer bplayer = BlockcadeUsers.getBPlayer(player);
        players.remove(player);

        for (Player p : getPlayers())
            p.hidePlayer(player);

        for (Player p : Bukkit.getOnlinePlayers())
            player.hidePlayer(p);

        broadcast("&e[-] " + bplayer.getFormattedName() + " &eleft! (&b" +
                getPlayers().size() + "&e/&b" + Main.getGame().getMaximumPlayers() + "&e)");

        PlayerLeaveLobbyEvent leaveLobbyEvent = new PlayerLeaveLobbyEvent(player, this);
        Main.getInstance().getServer().getPluginManager().callEvent(leaveLobbyEvent);
    }

}
