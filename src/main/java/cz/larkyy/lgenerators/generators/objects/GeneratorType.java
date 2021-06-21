package cz.larkyy.lgenerators.generators.objects;

import cz.larkyy.lgenerators.config.ConfigManager;
import cz.larkyy.llibrary.holograms.Hologram;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GeneratorType {

    private final String name;
    private final Material block;
    private final ItemStack drop;
    private final ItemStack display;
    private final List<Upgrade> upgrades;
    private final Hologram hologramTemplate;

    public GeneratorType(String name, Material block, ItemStack drop, ItemStack display, List<Upgrade> upgrades) {
        this.name = name;
        this.block = block;
        this.drop = drop;
        this.display = display;
        this.upgrades = upgrades;

        String path = "generators."+getName();

        List<String> holoLines = new ArrayList<>();
        if (ConfigManager.getConfig().getBoolean(path+".hologram.enabled")) {
            holoLines = ConfigManager.getConfig().getStringList(path+".hologram.lines");
        }

        this.hologramTemplate = new Hologram(
                holoLines
        );
    }

    public Hologram getHologramTemplate() {
        return hologramTemplate;
    }

    public ItemStack getDrop() {
        return drop;
    }

    public ItemStack getDisplay() {
        return display;
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public Material getBlock() {
        return block;
    }

    public String getName() {
        return name;
    }
}
