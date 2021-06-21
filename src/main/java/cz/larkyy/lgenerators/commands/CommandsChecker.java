package cz.larkyy.lgenerators.commands;

import cz.larkyy.lgenerators.config.ConfigValues;
import cz.larkyy.lgenerators.generators.GeneratorHandler;
import cz.larkyy.llibrary.commands.events.CustomCommandEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CommandsChecker implements Listener {

    @EventHandler
    public void onCmdSend(CustomCommandEvent e) {
        CommandSender sender = e.getSender();

        if (e.getArgs().length == 0) {
            ConfigValues.sendMessage(sender, ConfigValues.help);
            return;
        }

        switch (e.getArgs()[0]) {
            case "give":
                if (e.getArgs().length < 2) {
                    ConfigValues.sendMessage(sender, ConfigValues.commands_give_notEnoughArgs);
                    return;
                }
                Player p = (Player) sender;

                if (e.getArgs().length >= 2) {
                    if (!GeneratorHandler.getTypes().containsKey(e.getArgs()[1])) {
                        ConfigValues.sendMessage(sender, ConfigValues.commands_give_unknownType);
                        return;
                    }

                    p.getInventory().addItem(GeneratorHandler.getGeneratorItem(GeneratorHandler.getType(e.getArgs()[1])));
                    ConfigValues.sendMessage(sender, ConfigValues.commands_give_given.replace("%type%", e.getArgs()[1]));

                }

                return;
            case "help":
            default:
                ConfigValues.sendMessage(sender, ConfigValues.help);
                return;
        }
    }
}
