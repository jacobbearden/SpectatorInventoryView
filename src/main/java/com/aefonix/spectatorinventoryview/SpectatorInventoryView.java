package com.aefonix.spectatorinventoryview;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SpectatorInventoryView extends JavaPlugin implements Listener {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
  }

  @Override
  public void onDisable() {}

  @EventHandler
  public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
    if(event.getPlayer().getGameMode() == GameMode.SPECTATOR && event.getRightClicked().getType().equals(EntityType.PLAYER)) {
      Player spectator = event.getPlayer();
      Player player = (Player)event.getRightClicked();

      PlayerInventory inventory = player.getInventory();

      Inventory preview = Bukkit.createInventory(spectator, 45, player.getName());

      // map items
      for (int i = 0; i <= 35; i++) {
        preview.setItem(this.getInventorySlot(i), inventory.getItem(i));
      }

      // armor
      preview.setItem(0, inventory.getHelmet());
      preview.setItem(1, inventory.getChestplate());
      preview.setItem(2, inventory.getLeggings());
      preview.setItem(3, inventory.getBoots());

      // potion effects
      boolean hasPotions = player.getActivePotionEffects().size() > 0;
      ItemStack potions = new ItemStack(hasPotions ? Material.POTION : Material.GLASS_BOTTLE);
      ItemMeta potionMeta = potions.getItemMeta();
      potionMeta.setDisplayName(
        ChatColor.AQUA.toString()
          + ChatColor.ITALIC
          + "Potion effects");
      List<String> potionLore = Lists.newArrayList();
      if (hasPotions) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
          potionLore.add(
            ChatColor.YELLOW
              + this.potionEffectTypeName(effect.getType())
              + " "
              + (effect.getAmplifier() + 1));
        }
      } else {
        potionLore.add(ChatColor.YELLOW + "No potion effects");
      }
      potionMeta.setLore(potionLore);
      potions.setItemMeta(potionMeta);
      preview.setItem(5, potions);

      // experience points
      ItemStack experience = new ItemStack(Material.EXP_BOTTLE, (int) player.getLevel());
      ItemMeta experienceMeta = experience.getItemMeta();
      experienceMeta.setDisplayName(
        ChatColor.AQUA.toString()
          + ChatColor.ITALIC
          + "Experience level");
      List<String> experienceLore = Lists.newArrayList();
      if(player.getLevel() == 1) {
        experienceLore.add(ChatColor.YELLOW + "Level 1");
      }
      experienceLore.add(
        ChatColor.YELLOW
          + Integer.toString(Math.round(player.getExp() * 100))
          + "% to next level");
      experienceMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      experienceMeta.setLore(experienceLore);
      experience.setItemMeta(experienceMeta);
      preview.setItem(6, experience);

      // health
      ItemStack health = new ItemStack(Material.REDSTONE, (int) player.getHealth());
      ItemMeta healthMeta = health.getItemMeta();
      healthMeta.setDisplayName(
        ChatColor.AQUA.toString()
          + ChatColor.ITALIC
          + "Health level");
      healthMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      health.setItemMeta(healthMeta);
      preview.setItem(7, health);

      // hunger
      ItemStack hunger = new ItemStack(Material.COOKED_BEEF, player.getFoodLevel());
      ItemMeta hungerMeta = hunger.getItemMeta();
      hungerMeta.setDisplayName(
        ChatColor.AQUA.toString()
          + ChatColor.ITALIC
          + "Hunger level");
      List<String> hungerLore = Lists.newArrayList();
      String saturation = player.getSaturation() > 0 ? Float.toString(player.getSaturation()) : "No";
      hungerLore.add(
        ChatColor.YELLOW
          + saturation
          + " Saturation");
      hungerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      hungerMeta.setLore(hungerLore);
      hunger.setItemMeta(hungerMeta);
      preview.setItem(8, hunger);

      inventory.setContents(player.getInventory().getContents());

      spectator.openInventory(preview);
    }
  }

  public static int getInventorySlot(int slot) {
    if (slot < 9) {
      return slot + 36;
    }
    if (slot < 36) {
      return slot;
    }

    return slot;
  }

  public static String potionEffectTypeName(final PotionEffectType type) {
    String name = POTION_EFFECT_MAP.get(type);
    if (name != null) {
      return name;
    } else {
      return "Unknown";
    }
  }

  public static Map<PotionEffectType, String> POTION_EFFECT_MAP =
    ImmutableMap.<PotionEffectType, String>builder()
      .put(PotionEffectType.BLINDNESS, "Blindness")
      .put(PotionEffectType.CONFUSION, "Nausea")
      .put(PotionEffectType.DAMAGE_RESISTANCE, "Resistance")
      .put(PotionEffectType.FAST_DIGGING, "Haste")
      .put(PotionEffectType.FIRE_RESISTANCE, "Fire Resistance")
      .put(PotionEffectType.HARM, "Instant Damage")
      .put(PotionEffectType.HEAL, "Instant Health")
      .put(PotionEffectType.HUNGER, "Hunger")
      .put(PotionEffectType.INCREASE_DAMAGE, "Strength")
      .put(PotionEffectType.INVISIBILITY, "Invisibility")
      .put(PotionEffectType.JUMP, "Jump Boost")
      .put(PotionEffectType.NIGHT_VISION, "Night Vision")
      .put(PotionEffectType.POISON, "Poison")
      .put(PotionEffectType.REGENERATION, "Regeneration")
      .put(PotionEffectType.SLOW, "Slowness")
      .put(PotionEffectType.SLOW_DIGGING, "Mining Fatigue")
      .put(PotionEffectType.SPEED, "Speed")
      .put(PotionEffectType.WATER_BREATHING, "Water Breathing")
      .put(PotionEffectType.WEAKNESS, "Weakness")
      .put(PotionEffectType.WITHER, "Wither")
      .put(PotionEffectType.HEALTH_BOOST, "Health Boost")
      .put(PotionEffectType.ABSORPTION, "Absorption")
      .put(PotionEffectType.SATURATION, "Saturation")
      .build();
}
