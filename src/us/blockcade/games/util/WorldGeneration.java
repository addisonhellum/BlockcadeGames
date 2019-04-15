package us.blockcade.games.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import us.blockcade.games.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WorldGeneration {

    private static void copyFileStructure(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean unloadWorld(World world) {
        return world != null && Bukkit.getServer().unloadWorld(world, false);
    }

    public static void copyWorld(World originalWorld, String newWorldName) {
        copyFileStructure(originalWorld.getWorldFolder(), new File(Bukkit.getWorldContainer(), newWorldName));
        new WorldCreator(newWorldName).createWorld();
    }

    public static void generateGameLobbies() {
        FileConfiguration config = Main.getInstance().getConfig();
        for (int i = 1; i <= config.getInt("game-lobbies.generation-amount"); i++) {
            if (Bukkit.getWorld("game" + i) == null) {
                System.out.println("[Lobby Generator]: Started generation of lobby \"game" + i + "\".");
                copyWorld(Bukkit.getWorld(config.getString("game-lobbies.base-world")), "game" + i);
            }
        }
    }

}
