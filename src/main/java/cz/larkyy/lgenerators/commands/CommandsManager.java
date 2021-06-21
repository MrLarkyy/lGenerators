package cz.larkyy.lgenerators.commands;

import cz.larkyy.lgenerators.config.ConfigValues;
import cz.larkyy.llibrary.commands.CommandBuilder;
import cz.larkyy.llibrary.commands.CommandUtils;
import cz.larkyy.llibrary.commands.arguments.ArgumentType;

public class CommandsManager {

    public static void registerCommands() {
        CommandUtils.addCommand(
                new CommandBuilder("lgenerators")
                        .setAliases(ConfigValues.command_aliases)
                        .addArgument(
                                "give",
                                ArgumentType.STRING,
                                ConfigValues.permission_give,
                                false
                        )
                        .build()
        );
    }

}
