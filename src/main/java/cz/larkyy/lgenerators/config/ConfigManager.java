package cz.larkyy.lgenerators.config;

import cz.larkyy.lgenerators.LGenerators;
import cz.larkyy.llibrary.config.Config;
import cz.larkyy.llibrary.config.annotation.ConfigAnnotationManager;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    public final static Config CONFIG = new Config(LGenerators.getMain(),"config.yml");

    public static void loadConfig() {
        CONFIG.load();

        ConfigAnnotationManager cfgAnnotation = new ConfigAnnotationManager(CfgVar.class, LGenerators.getMain(), CONFIG.getConfiguration());
        cfgAnnotation.register(ConfigValues.class);
        cfgAnnotation.update();
    }

    public static FileConfiguration getConfig() {
        return CONFIG.getConfiguration();
    }
}
