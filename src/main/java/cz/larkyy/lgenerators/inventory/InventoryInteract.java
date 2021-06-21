package cz.larkyy.lgenerators.inventory;

import cz.larkyy.lgenerators.LGenerators;
import cz.larkyy.lgenerators.config.ConfigValues;
import cz.larkyy.lgenerators.generators.GeneratorHandler;
import cz.larkyy.lgenerators.generators.objects.Generator;
import cz.larkyy.llibrary.inventory.InventoryBuilder;
import cz.larkyy.llibrary.inventory.events.InteractCustomInventoryEvent;
import cz.larkyy.llibrary.inventory.events.InteractFunctionItemEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryInteract implements Listener {
    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryBuilder) {
            InventoryBuilder inv = (InventoryBuilder) e.getInventory().getHolder();
            inv.setPlayer(null);
        }
    }

    @EventHandler
    public void onCustomInvClick(InteractCustomInventoryEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFunctionClick(InteractFunctionItemEvent e) {
        Player p = e.getOpener();

        Generator generator = (Generator) e.getInventory().getData().get("generator");

        switch (e.getFunction()){
            case "upgrade":
                if (generator.getUpgrades().size()> generator.getUpgrade()) {
                    int level = generator.getUpgrade() + 1;
                    int price = generator.getUpgrades().get(level-1).getPrice();

                    if (LGenerators.getEconomy().getBalance(p) >= price) {
                        LGenerators.getEconomy().withdrawPlayer(p,price);

                        generator.setUpgrade(level);
                        ConfigValues.sendMessage(p, ConfigValues.generator_upgraded.replace("%level%", level + ""));
                        InventoryHandler.openInv(p, generator);
                    } else {
                        ConfigValues.sendMessage(p,ConfigValues.no_money);
                    }
                }
                break;
            case "remove":
                p.closeInventory();
                p.getInventory().addItem(GeneratorHandler.getGeneratorItem(generator));

                generator.getLocation().getBlock().setType(Material.AIR);
                generator.stopTask();
                generator.getHologram().despawn();

                GeneratorHandler.remGenerator(generator);
                ConfigValues.sendMessage(p,ConfigValues.generator_broken);
                break;
        }
    }

}
