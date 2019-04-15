package us.blockcade.games;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import us.blockcade.core.common.game.BlockcadeGame;
import us.blockcade.core.common.game.GameManager;
import us.blockcade.core.util.format.ChatUtil;
import us.blockcade.games.commands.*;
import us.blockcade.games.gamedata.LobbyHandler;
import us.blockcade.games.gamedata.LobbyManager;
import us.blockcade.games.gamedata.data.GameLobby;
import us.blockcade.games.games.cagematch.CagematchGame;
import us.blockcade.games.games.payload.PayloadGame;
import us.blockcade.games.games.prototype.PrototypeGame;
import us.blockcade.games.games.spleef.SpleefGame;
import us.blockcade.games.games.spleef.commands.SpleefCommand;
import us.blockcade.games.util.GameSpectator;
import us.blockcade.games.util.WorldGeneration;

public class Main extends JavaPlugin {

    private static Plugin main;
    public static Plugin getInstance() { return main; }

    private static BlockcadeGame game;
    public static BlockcadeGame getGame() { return game; }

    @Override
    public void onEnable() {
        main = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        WorldGeneration.generateGameLobbies();
        LobbyManager.initializeLobbies();
        GameSpectator.initializeSpectating();

        PrototypeGame.initialize();
        PayloadGame.initialize();
        CagematchGame.initialize();
        SpleefGame.initialize();

        getServer().getPluginManager().registerEvents(new LobbyHandler(), this);
        getServer().getPluginManager().registerEvents(new GameSpectator(), this);

        for (BlockcadeGame g : GameManager.getGames()) {
            if (g.getName().equalsIgnoreCase(getGameName())) {
                game = g;

                Bukkit.broadcastMessage(ChatUtil.format("&f[Blockcade Games] &aSuccessfully loaded game: &b&l" + getGame().getName()));
                for (Listener handler : g.getHandlers()) {
                    getServer().getPluginManager().registerEvents(handler, this);
                }
            }
        }

        getCommand("start").setExecutor(new StartCommand());
        getCommand("halt").setExecutor(new HaltCommand());
        getCommand("arena").setExecutor(new ArenaCommand());
        getCommand("world").setExecutor(new WorldCommand());
        getCommand("menu").setExecutor(new MenuCommand());
        getCommand("about").setExecutor(new AboutCommand());
        getCommand("leaderboard").setExecutor(new LeaderboardCommand());
        getCommand("test").setExecutor(new TestCommand());

        getCommand("spleef").setExecutor(new SpleefCommand());
    }

    @Override
    public void onDisable() {
        for (GameLobby lobby : LobbyManager.getLobbies())
            lobby.endGame();
    }

    private static String getGameName() {
        return Main.getInstance().getConfig().getString("enabled-game");
    }

}
