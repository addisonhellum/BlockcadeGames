package us.blockcade.games.games.payload;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockcade.core.util.effect.FireworkEffectPlayer;
import us.blockcade.games.Main;

public class PayloadEffect {

    private PayloadEffect instance;
    private FireworkEffectPlayer fplayer = new FireworkEffectPlayer();

    public PayloadEffect() {
        instance = this;
    }

    public void displayPayloadFirework(Location location) {
        FireworkEffect effect = FireworkEffect.builder().withColor(Color.WHITE).withFade(Color.GRAY)
                .with(FireworkEffect.Type.BALL).trail(false).flicker(false).build();

        try {
            fplayer.playFirework(location.getWorld(), location.clone().add(0,0.5,0), effect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayPayloadDrop(Location location) {
        for (int i = 0; i < 3; i++) {
            new BukkitRunnable() {
                Location loc = location.clone().add(0, 25, 0);
                int index = 0;

                @Override
                public void run() {
                    if (index == 25) {
                        cancel();
                    }

                    displayPayloadFirework(loc.clone().subtract(0, index, 0));
                    index++;
                }
            }.runTaskTimer(Main.getInstance(), 0, 1);
        }
    }

}
