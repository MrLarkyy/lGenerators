package cz.larkyy.lgenerators.config;

import cz.larkyy.llibrary.chat.ChatUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ConfigValues {

    @CfgVar("messages.prefix")
    public static String plugin_prefix;

    @CfgVar("messages.pluginEnabled")
    public static String plugin_enabled;

    @CfgVar("messages.pluginDisabled")
    public static String plugin_disabled;

    @CfgVar("messages.help")
    public static List<String> help;

    @CfgVar("messages.generatorPlaced")
    public static String generator_placed;

    @CfgVar("messages.generatorBroken")
    public static String generator_broken;

    @CfgVar("messages.generatorUpgraded")
    public static String generator_upgraded;

    @CfgVar("messages.alreadyOpened")
    public static String already_opened;

    @CfgVar("messages.notEnoughMoney")
    public static String no_money;

    //
    //  COMMANDS
    //

    //GIVE

    @CfgVar("messages.commands.give.notEnoughArgs")
    public static String commands_give_notEnoughArgs;

    @CfgVar("messages.commands.give.given")
    public static String commands_give_given;

    @CfgVar("messages.commands.give.unknownType")
    public static String commands_give_unknownType;

    @CfgVar("settings.commandAliases")
    public static List<String> command_aliases;

    @CfgVar("settings.permissions.give")
    public static String permission_give;

    //
    //  DATABASE
    //

    @CfgVar("settings.database.type")
    public static String database_type;

    @CfgVar("settings.database.login.host")
    public static String database_host;

    @CfgVar("settings.database.login.port")
    public static int database_port;

    @CfgVar("settings.database.login.user")
    public static String database_user;

    @CfgVar("settings.database.login.password")
    public static String database_password;

    @CfgVar("settings.database.login.database")
    public static String database_database;

    @CfgVar("settings.database.login.table")
    public static String database_table;

    public static String replacePlaceholders(String str) {
        return str.replace("%prefix%",plugin_prefix);
    }

    public static void sendMessage(CommandSender p, String msg) {
        p.sendMessage(ChatUtils.format(replacePlaceholders(msg)));
    }

    public static void sendMessage(CommandSender p, List<String> msgs) {
        for (String msg : msgs) {
            p.sendMessage(ChatUtils.format(replacePlaceholders(msg)));
        }
    }
}
