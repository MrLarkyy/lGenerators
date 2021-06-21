package cz.larkyy.lgenerators.generators.objects;

import cz.larkyy.lgenerators.LGenerators;
import cz.larkyy.lgenerators.config.ConfigManager;
import cz.larkyy.llibrary.holograms.Hologram;
import cz.larkyy.llibrary.inventory.InventoryBuilder;
import cz.larkyy.llibrary.items.ItemUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator extends GeneratorType {

    private final Location location;
    private int upgrade;
    private ArmorStand as;
    private final InventoryBuilder inv;
    private BukkitRunnable runnable;
    private Hologram hologram;

    public Generator(Location loc, GeneratorType type) {
        super(type.getName(), type.getBlock(), type.getDrop(), type.getDisplay(), type.getUpgrades());

        this.location = loc;
        this.inv = new InventoryBuilder(LGenerators.getMain(), null);
        this.upgrade = 1;

        this.hologram = loadHologram();

        loadInv();

        this.runnable = makeRunnable();

        runnable.runTaskTimer(LGenerators.getMain(),30,getUpgrades().get(getUpgrade()-1).getSpeed()* 20L);
    }

    public Generator(Location loc, GeneratorType type, int level) {
        super(type.getName(), type.getBlock(), type.getDrop(), type.getDisplay(), type.getUpgrades());

        this.location = loc;
        this.inv = new InventoryBuilder(LGenerators.getMain(), null);
        this.upgrade = level;

        this.hologram = loadHologram();

        loadInv();

        this.runnable = makeRunnable();

        runnable.runTaskTimer(LGenerators.getMain(),30,getUpgrades().get(getUpgrade()-1).getSpeed()* 20L);
    }

    public void stopTask() {
        runnable.cancel();
    }

    private void runTask() {
        runnable.cancel();
        runnable = makeRunnable();

        runnable.runTaskTimer(LGenerators.getMain(),getUpgrades().get(getUpgrade()-1).getSpeed()* 20L,getUpgrades().get(getUpgrade()-1).getSpeed()* 20L);
    }

    private BukkitRunnable makeRunnable() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack is = getDrop().clone();
                Random rand = new Random();
                int max = getUpgrades().get(getUpgrade()-1).getMaxAmount();
                int min = getUpgrades().get(getUpgrade()-1).getMinAmount();
                is.setAmount(rand.nextInt(max - min) + min);
                Location location1 = location.clone().add(0,1,0);
                location1.getWorld().dropItemNaturally(location1,is);
            }
        };
    }

    private void loadInv() {
        String path = "generators."+getName()+".inventory";

        char upgrade = getCfg().getString(path+".functions.upgrade").charAt(0);
        char remove = getCfg().getString(path+".functions.remove").charAt(0);

        inv.setTitle(getCfg().getString(path+".title"))
                .setLines(getCfg().getStringList(path+".shape"))
                .addFunction(upgrade,"upgrade")
                .addFunction(remove,"remove");

        for (String keyStr : getCfg().getConfigurationSection(path+".items").getKeys(false)) {
            char key = keyStr.charAt(0);
            path = "generators."+getName()+".inventory.items."+keyStr;

            inv.addItem(
                    key,
                    ItemUtils.getItemStackFromConfig(
                            getCfg(),
                            path
                    )
            );

        }
        inv.addData("generator",this);
    }

    private FileConfiguration getCfg() {
        return ConfigManager.getConfig();
    }

    public Location getLocation() {
        return location;
    }

    public ArmorStand getAs() {
        return as;
    }

    public int getUpgrade() {
        return upgrade;
    }

    public void setAs(ArmorStand as) {
        this.as = as;
    }

    public void setUpgrade(int speedUpgrade) {
        this.upgrade = speedUpgrade;
        runTask();
        spawnHologram();
    }

    public InventoryBuilder getInv() {
        return inv;
    }

    public boolean isMaxLvl() {
        return (this.getUpgrades().size()==upgrade);
    }

    public void spawnHologram() {
        if (hologram.isSpawned()) {
            hologram.despawn();
            hologram = loadHologram();
        }

        hologram.spawn();
    }

    public Hologram getHologram() {
        return hologram;
    }

    private Hologram loadHologram() {
        String path = "generators."+getName()+".hologram.offset";

        Hologram hologram = getHologramTemplate().copy().setLocation(location.clone().add(
                ConfigManager.getConfig().getDouble(path+".x"),
                ConfigManager.getConfig().getDouble(path+".y"),
                ConfigManager.getConfig().getDouble(path+".z")
        ));

        List<String> newLines = new ArrayList<>();
        for (String line : hologram.getLines()) {
            if (line.equals("")) {
                newLines.add("&f &f");
            } else {
                newLines.add(line
                        .replace("%level%", getUpgrade() + "")
                        .replace("%speed%", getUpgrades().get(getUpgrade() - 1).getSpeed() + "")
                        .replace("%amount%", getUpgrades().get(getUpgrade() - 1).getAmount() + "")
                        .replace("%type%", getName())
                );
            }
        }
        hologram.setLines(newLines);

        return hologram;
    }
}
