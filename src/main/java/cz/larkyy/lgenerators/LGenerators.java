package cz.larkyy.lgenerators;

import cz.larkyy.lgenerators.commands.CommandsChecker;
import cz.larkyy.lgenerators.commands.CommandsManager;
import cz.larkyy.lgenerators.config.ConfigManager;
import cz.larkyy.lgenerators.config.ConfigValues;
import cz.larkyy.lgenerators.generators.GeneratorHandler;
import cz.larkyy.lgenerators.generators.Listeners;
import cz.larkyy.lgenerators.inventory.InventoryInteract;
import cz.larkyy.llibrary.chat.ChatUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class LGenerators extends JavaPlugin {

    private static LGenerators main;
    private static Economy economy;

    @Override
    public void onEnable() {
        main = this;
        ConfigManager.loadConfig();

        CommandsManager.registerCommands();

        new BukkitRunnable() {
            @Override
            public void run() {

                GeneratorHandler.setupDatabase();
                GeneratorHandler.loadGenerators();

                getServer().getPluginManager().registerEvents(new CommandsChecker(),LGenerators.this);
                getServer().getPluginManager().registerEvents(new InventoryInteract(),LGenerators.this);
                getServer().getPluginManager().registerEvents(new Listeners(),LGenerators.this);

                ChatUtils.sendConsoleMsg(LGenerators.this, ConfigValues.replacePlaceholders(ConfigValues.plugin_enabled));
            }
        }.runTaskLater(this,50);

        new BukkitRunnable() {
            @Override
            public void run() {
                GeneratorHandler.saveGenerators();
            }
        }.runTaskTimer(this,100,300);

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
    }

    @Override
    public void onDisable() {
        ChatUtils.sendConsoleMsg(this, ConfigValues.replacePlaceholders(ConfigValues.plugin_disabled));
    }

    public static LGenerators getMain() {
        return main;
    }

    public static Economy getEconomy() {
        return economy;
    }
}
