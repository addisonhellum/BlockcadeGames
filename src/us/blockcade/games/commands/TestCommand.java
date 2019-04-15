package us.blockcade.games.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockcade.games.Main;

public class TestCommand implements CommandExecutor {

    ArmorStand armorStand = null;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        Player p = (Player)sender;
        ItemStack skull = new ItemStack(Material.TNT, 1, (byte) 3);
        ArmorStand e = (ArmorStand) Bukkit.getWorld(p.getWorld().getName()).spawnEntity(Bukkit.getWorld(p.getWorld().getName()).getSpawnLocation(), EntityType.ARMOR_STAND);
        e.setVisible(false);
        e.setGravity(false);
        e.teleport(e.getLocation());
        e.getEquipment().setHelmet(skull);
        e.setSmall(true);
        armorStand=e;
        new BukkitRunnable(){
            boolean state = false;
            boolean top = false;
            double ticks = 0;
            Location sameLocation = e.getLocation();
            @Override
            public void run(){
                Location location = e.getLocation();
                ticks++;
                if (!state) {
                    location.add(0, Math.cos(ticks/10)*0.025, 0);
                    location.setYaw((location.getYaw() + 5F));

                    armorStand.teleport(location);

                    if (armorStand.getLocation().getY() > (0.1 + sameLocation.getY()))
                        state = true;
                }
                else {
                    top =  false;
                    location.subtract(0, Math.cos(ticks/10)*0.025, 0);
                    location.setYaw((location.getYaw() + 5F));

                    armorStand.teleport(location);

                    if (armorStand.getLocation().getY() < (-0.1 + sameLocation.getY()))
                        state = false;
                }

                //if(diff>1){y=location.getY();location.setY(location.getY()-0.5);}
                armorStand.teleport(location);
            }
        }.runTaskTimerAsynchronously(Main.getInstance(),0, 1);
        return false;
    }

}
