package cz.larkyy.lgenerators.generators;

import cz.larkyy.lgenerators.LGenerators;
import cz.larkyy.lgenerators.config.ConfigValues;
import cz.larkyy.lgenerators.generators.objects.Generator;
import cz.larkyy.lgenerators.generators.objects.GeneratorType;
import cz.larkyy.lgenerators.inventory.InventoryHandler;
import cz.larkyy.llibrary.inventory.InventoryBuilder;
import cz.larkyy.llibrary.items.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class Listeners implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {

        if (ItemUtils.hasItemData(LGenerators.getMain(),e.getItemInHand(), PersistentDataType.STRING,"identifier")) {

            Player p = e.getPlayer();
            ItemStack is = e.getItemInHand();

            if (!ItemUtils.getItemData(LGenerators.getMain(), is, PersistentDataType.STRING, "identifier").equals("generator")) {
                return;
            }

            int level = (int) ItemUtils.getItemData(LGenerators.getMain(), is, PersistentDataType.INTEGER, "level");

            GeneratorType type = GeneratorHandler.getType(
                    (String) ItemUtils.getItemData(
                            LGenerators.getMain(),
                            is,
                            PersistentDataType.STRING,
                            "type"
                    )
            );
            new BukkitRunnable() {
                @Override
                public void run() {
                    e.getBlockPlaced().setType(type.getBlock());
                }
            }.runTaskLater(LGenerators.getMain(),1);
            Generator generator = new Generator(e.getBlockPlaced().getLocation(),type);
            generator.setUpgrade(level);
            GeneratorHandler.addGenerator(generator);
            generator.spawnHologram();

            ConfigValues.sendMessage(p,ConfigValues.generator_placed.replace("%type%",type.getName()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (GeneratorHandler.getGenerator(e.getBlock().getLocation())!=null) {
            e.setCancelled(true);

            Player p = e.getPlayer();
            Generator generator = GeneratorHandler.getGenerator(e.getBlock().getLocation());

            InventoryHandler.openInv(p,generator);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {

        if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
            return;
        }

        if (e.getHand()==null) {
            return;
        }

        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }

        if (e.getClickedBlock()==null) {
            return;
        }

        if (GeneratorHandler.getGenerator(e.getClickedBlock().getLocation())!=null) {
            e.setCancelled(true);

            Player p = e.getPlayer();
            Generator generator = GeneratorHandler.getGenerator(e.getClickedBlock().getLocation());

            InventoryHandler.openInv(p,generator);
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof InventoryBuilder)) {
            return;
        }

        InventoryBuilder inv = (InventoryBuilder) e.getInventory().getHolder();
        Generator generator = (Generator) inv.getData().get("generator");
        generator.getInv().setPlayer(null);
    }
}
