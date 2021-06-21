package cz.larkyy.lgenerators.generators;

import cz.larkyy.lgenerators.LGenerators;
import cz.larkyy.lgenerators.config.ConfigManager;
import cz.larkyy.lgenerators.config.ConfigValues;
import cz.larkyy.lgenerators.database.Database;
import cz.larkyy.lgenerators.database.MySQL;
import cz.larkyy.lgenerators.database.SQLite;
import cz.larkyy.lgenerators.generators.objects.Generator;
import cz.larkyy.lgenerators.generators.objects.GeneratorType;
import cz.larkyy.lgenerators.generators.objects.Upgrade;
import cz.larkyy.llibrary.items.ItemUtils;
import net.minecraft.server.v1_16_R3.GeneratorSettingBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratorHandler {

    private static Map<Location, Generator> generators;
    private static Map<String, GeneratorType> generatorTypes;
    private static Database database;

    public static void addGenerator(Generator generator) {
        generators.put(generator.getLocation(), generator);
    }

    public static void remGenerator(Generator generator) {
        remGenerator(generator.getLocation());
    }

    public static void remGenerator(Location loc) {
        generators.remove(loc);
    }

    public static Generator getGenerator(Location loc) {
        return generators.get(loc);
    }

    public static void loadGenerators() {
        generatorTypes = new HashMap<>();
        generators = new HashMap<>();

        if (getCfg().getConfigurationSection("generators") == null) {
            return;
        }
        for (String type : getCfg().getConfigurationSection("generators").getKeys(false)) {
            String path = "generators." + type;

            Material block = Material.valueOf(getCfg().getString(path + ".block"));
            ItemStack display = ItemUtils.getItemStackFromConfig(getCfg(), path + ".display");
            ItemStack drop = ItemUtils.getItemStackFromConfig(getCfg(), path + ".drop");
            List<Upgrade> upgrades = new ArrayList<>();

            for (String upgrade : getCfg().getConfigurationSection(path + ".upgrades").getKeys(false)) {
                path = "generators." + type + ".upgrades." + upgrade;

                int price = getCfg().getInt(path + ".price");
                int speed = getCfg().getInt(path + ".timer");
                String amount = getCfg().getString(path + ".amount");

                upgrades.add(new Upgrade(upgrades.size(), price, speed, amount));
            }

            generatorTypes.put(type, new GeneratorType(type, block, drop, display, upgrades));
        }

        List<Generator> gens = database.getGenerators();

        for (Generator gen : gens) {
            generators.put(gen.getLocation(),gen);
            gen.spawnHologram();
        }
    }

    public static void saveGenerators() {
        generators.forEach((location, generator) -> {
            database.saveGenerator(generator);
        });
    }

    public static void setupDatabase() {
        switch (ConfigValues.database_type.toUpperCase()) {
            case "MYSQL":
                database = new MySQL();
                break;
            default:
                database = new SQLite(LGenerators.getMain());
                break;
        }
    }

    private static FileConfiguration getCfg() {
        return ConfigManager.getConfig();
    }

    public static ItemStack getGeneratorItem(GeneratorType generator) {
        ItemStack is = getPureDisplay(generator).clone();
        ItemUtils.addItemData(LGenerators.getMain(),is, PersistentDataType.INTEGER,"level",1);
        ItemMeta im = is.getItemMeta();

        if (im!=null) {
            im.setDisplayName(replacePlaceholders(generator, im.getDisplayName()));
            if (im.getLore()!=null) {
                im.setLore(replacePlaceholders(generator, im.getLore()));
            }

            is.setItemMeta(im);
        }

        return is;
    }

    public static ItemStack getGeneratorItem(Generator generator) {
        ItemStack is = getPureDisplay(generator).clone();

        ItemUtils.addItemData(LGenerators.getMain(),is, PersistentDataType.INTEGER,"level",generator.getUpgrade());
        ItemMeta im = is.getItemMeta();
        if (im!=null) {
            im.setDisplayName(replacePlaceholders(generator, im.getDisplayName()));
            if (im.getLore()!=null) {
                im.setLore(replacePlaceholders(generator, im.getLore()));
            }

            is.setItemMeta(im);
        }
        return is;
    }

    private static String replacePlaceholders(Generator generator, String str) {
        return str
                .replace("%level%", generator.getUpgrade() + "")
                .replace("%speed%", generator.getUpgrades().get(generator.getUpgrade()-1).getSpeed() + "")
                .replace("%amount%", generator.getUpgrades().get(generator.getUpgrade()-1).getAmount() + "")
                .replace("%type%", generator.getName());
    }

    private static String replacePlaceholders(GeneratorType generator, String str) {
        return str
                .replace("%level%", 1 + "")
                .replace("%speed%", generator.getUpgrades().get(1).getSpeed() + "")
                .replace("%amount%", generator.getUpgrades().get(1).getAmount() + "")
                .replace("%type%", generator.getName());
    }

    private static List<String> replacePlaceholders(Generator generator, List<String> str) {
        List<String> newList = new ArrayList<>();
        for (String string : str) {
            newList.add(replacePlaceholders(generator, string));
        }
        return newList;
    }

    private static List<String> replacePlaceholders(GeneratorType generator, List<String> str) {
        List<String> newList = new ArrayList<>();
        for (String string : str) {
            newList.add(replacePlaceholders(generator, string));
        }
        return newList;
    }

    private static ItemStack getPureDisplay(GeneratorType generator) {
        ItemStack is = generator.getDisplay();
        ItemUtils.addItemData(LGenerators.getMain(),is, PersistentDataType.STRING,"identifier","generator");
        ItemUtils.addItemData(LGenerators.getMain(),is, PersistentDataType.STRING,"type",generator.getName());

        return is;
    }

    public static Map<String, GeneratorType> getTypes() {
        return generatorTypes;
    }
    public static GeneratorType getType(String type) {
        return generatorTypes.get(type);
    }
}
