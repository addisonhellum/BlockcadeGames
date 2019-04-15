package us.blockcade.games.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Loadout {

    private String name;

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    private Map<Integer, ItemStack> contents = new HashMap<>();
    private List<PotionEffect> effects = new ArrayList<>();
    private String[] data;

    private static Map<UUID, Loadout> loadouts = new HashMap<>();
    public static Map<UUID, Loadout> getLoadouts() { return loadouts; }

    public Loadout(String name) {
        this.name = name;
        this.data = new String[] {};
    }

    public String getName() { return name; }

    public Map<Integer, ItemStack> getContents() {
        return contents;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public String[] getData() {
        return data;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public Loadout withName(String name) {
        this.name = name;
        return this;
    }

    public Loadout withData(String[] data) {
        this.data = data;
        return this;
    }

    public Loadout withHelmet(ItemStack item) {
        this.helmet = item;
        return this;
    }

    public Loadout withChestplate(ItemStack item) {
        this.chestplate = item;
        return this;
    }

    public Loadout withLeggings(ItemStack item) {
        this.leggings = item;
        return this;
    }

    public Loadout withBoots(ItemStack item) {
        this.boots = item;
        return this;
    }

    public Loadout withItem(int slot, ItemStack item) {
        if (contents.containsKey(slot))
            contents.remove(slot);

        contents.put(slot, item);
        return this;
    }

    public Loadout addEffect(PotionEffect effect) {
        effects.add(effect);
        return this;
    }

    public Loadout addEffect(PotionEffectType effectType) {
        effects.add(new PotionEffect(effectType, 10000000, 0));
        return this;
    }

    public void give(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerInventory inv = player.getInventory();

        inv.setArmorContents(null);
        inv.clear();

        inv.setHelmet(getHelmet());
        inv.setChestplate(getChestplate());
        inv.setLeggings(getLeggings());
        inv.setBoots(getBoots());

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, getContents().get(i));
        }

        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }

        if (loadouts.containsKey(uuid))
            loadouts.remove(uuid);

        loadouts.put(uuid, this);
    }

}
