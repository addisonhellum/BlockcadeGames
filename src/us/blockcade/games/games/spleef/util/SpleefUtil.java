package us.blockcade.games.games.spleef.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockcade.core.util.format.ColorUtil;
import us.blockcade.core.util.gui.ItemStackBuilder;
import us.blockcade.core.util.gui.menu.GuiMenu;

import java.util.HashMap;
import java.util.Map;

public class SpleefUtil {

    public Map<String, Color> chestplateColors = new HashMap<>();

    public void openChestplateSelection(Player player) {
        GuiMenu menu = new GuiMenu("Choose a Chestplate Color", 6);

        menu.fillRow(3, new ItemStackBuilder(Material.STAINED_GLASS_PANE).withName("&a⬆ Default Chestplates")
                .withLore(new String[] {"&a⬇ Exclusive Chestplates", "", "&eAll chestplates unlocked for &cBETA"}).withData(5));
        menu.lockRow(1).lockRow(6).lockColumn(1).lockColumn(9);

        for (Color color : ColorUtil.getColors()) {
            ItemStack icon = new ItemStackBuilder(Material.LEATHER_CHESTPLATE)
                    .withName("&b" + ColorUtil.getNameByColor(color) + " Chestplate").withColor(color)
                    .withLore(new String[]{"", "&eClick to select!"}).build();

            menu.bindLeft(icon, "spleef setchestplate " + ColorUtil.getNameByColor(color));

            menu.add(icon);
        }

        menu.freezeItems(true);
        menu.display(player);
    }

}
