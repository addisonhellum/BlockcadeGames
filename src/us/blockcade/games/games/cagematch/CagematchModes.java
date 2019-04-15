package us.blockcade.games.games.cagematch;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import us.blockcade.core.util.gui.ItemStackBuilder;
import us.blockcade.games.util.Loadout;

import java.util.Random;

public class CagematchModes {

    public static String[] getModes() {
        return new String[] { "iron", "op", "bow" };
    }

    public static Loadout getRandomLoadout() {
        Random r = new Random();
        String mode = getModes()[r.nextInt(getModes().length)];

        return getLoadout(mode);
    }

    public static Loadout getLoadout(String name) {
        Loadout lo = new Loadout(name);

        if (name.equalsIgnoreCase("iron")) {
            new ItemStackBuilder(Material.IRON_HELMET).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

            return lo.withHelmet(new ItemStackBuilder(Material.IRON_HELMET).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                    .withChestplate(new ItemStackBuilder(Material.IRON_CHESTPLATE).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                    .withLeggings(new ItemStackBuilder(Material.IRON_LEGGINGS).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                    .withBoots(new ItemStackBuilder(Material.IRON_BOOTS).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                    .withItem(0, new ItemStackBuilder(Material.IRON_SWORD).build())
                    .withItem(1, new ItemStackBuilder(Material.BOW).build())
                    .withItem(2, new ItemStack(Material.GOLDEN_APPLE, 4))
                    .withItem(9, new ItemStack(Material.ARROW, 8))
                    .withName("Iron PvP");

        } else if (name.equalsIgnoreCase("op")) {
            return lo.withHelmet(new ItemStackBuilder(Material.DIAMOND_HELMET)
                            .withDurability().withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build())
                    .withChestplate(new ItemStackBuilder(Material.DIAMOND_CHESTPLATE)
                            .withDurability().withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build())
                    .withLeggings(new ItemStackBuilder(Material.DIAMOND_LEGGINGS)
                            .withDurability().withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build())
                    .withBoots(new ItemStackBuilder(Material.DIAMOND_BOOTS)
                            .withDurability().withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build())
                    .withItem(0, new ItemStackBuilder(Material.DIAMOND_SWORD).withEnchantment(Enchantment.DAMAGE_ALL, 5)
                        .withEnchantment(Enchantment.FIRE_ASPECT, 2).build())
                    .withItem(1, new ItemStackBuilder(Material.BOW).withEnchantment(Enchantment.ARROW_DAMAGE, 5)
                        .withEnchantment(Enchantment.ARROW_FIRE, 1).build())
                    .withItem(2, new ItemStack(Material.GOLDEN_APPLE, 64, (byte) 1))
                    .withItem(3, new ItemStack(Material.POTION, 1, (byte) 8226))
                    .withItem(4, new ItemStack(Material.POTION, 1, (byte) 8201))
                    .withItem(5, new ItemStack(Material.POTION, 1, (byte) 8226))
                    .withItem(6, new ItemStack(Material.POTION, 1, (byte) 8201))
                    .withItem(7, new ItemStack(Material.POTION, 1, (byte) 8226))
                    .withItem(8, new ItemStack(Material.POTION, 1, (byte) 8201))
                    .withItem(9, new ItemStack(Material.ARROW, 64))
                    .withName("Overpowered PvP");

        } else if (name.equalsIgnoreCase("bow")) {
            return lo.withHelmet(new ItemStackBuilder(Material.LEATHER_HELMET)
                            .withColor(Color.YELLOW).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                    .withChestplate(new ItemStackBuilder(Material.LEATHER_CHESTPLATE)
                            .withColor(Color.YELLOW).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                    .withLeggings(new ItemStackBuilder(Material.LEATHER_LEGGINGS)
                            .withColor(Color.YELLOW).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                    .withBoots(new ItemStackBuilder(Material.LEATHER_BOOTS)
                            .withColor(Color.YELLOW).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build())
                    .withItem(0, new ItemStackBuilder(Material.BOW).withEnchantment(Enchantment.ARROW_INFINITE).build())
                    .withItem(1, new ItemStackBuilder(Material.STICK).withEnchantment(Enchantment.KNOCKBACK, 2).build())
                    .withItem(2, new ItemStack(Material.ENDER_PEARL, 4))
                    .withItem(9, new ItemStack(Material.ARROW))
                    .withName("Bow PvP").withData(new String[] {"noregen"});

        }

        return lo;
    }

}
