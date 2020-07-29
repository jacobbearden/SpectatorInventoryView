package com.aefonix.spectatorinventoryview;

import com.aefonix.spectatorinventoryview.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

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
      Player player = (Player) event.getRightClicked();

      Inventory preview = InventoryUtil.renderInventory(player, spectator);

      spectator.openInventory(preview);
    }
  }
}
