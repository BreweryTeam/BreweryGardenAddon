package dev.jsinco.brewery.garden.commands;

import com.dre.brewery.BreweryPlugin;
import com.dre.brewery.api.addons.AddonCommand;
import com.dre.brewery.configuration.files.Lang;
import com.dre.brewery.utility.Logging;
import dev.jsinco.brewery.garden.BreweryGarden;
import dev.jsinco.brewery.garden.commands.subcomands.GiveCommand;
import dev.jsinco.brewery.garden.commands.subcomands.GrowthStageCommand;
import dev.jsinco.brewery.garden.commands.subcomands.IsPlantCommand;
import dev.jsinco.brewery.garden.configuration.BreweryGardenConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddonCommandManager implements AddonCommand {

    private final BreweryGardenConfig config = BreweryGarden.getInstance().getAddonConfigManager().getConfig(BreweryGardenConfig.class);
    private final Map<String, AddonSubCommand> subCommands = new HashMap<>();
    {
        subCommands.put("give", new GiveCommand());
        subCommands.put("isplant", new IsPlantCommand());
        subCommands.put("growthstage", new GrowthStageCommand());
    }

    @Override
    public void execute(BreweryPlugin breweryPlugin, Lang lang, CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            Logging.msg(sender, "Provide a subcommand.");
        }

        AddonSubCommand subCommand = subCommands.get(args[1]);
        if (subCommand == null) {
            Logging.msg(sender, "Unknown subcommand.");
            return;
        }

        if (subCommand.permission() != null && !sender.hasPermission(subCommand.permission())) {
            Logging.msg(sender, "You do not have permission to execute this command.");
        } else if (subCommand.playerOnly() && !(sender instanceof Player)) {
            Logging.msg(sender, "You must be a player to execute this command.");
        }

        String[] subArgs = new String[args.length - 2];
        System.arraycopy(args, 2, subArgs, 0, args.length - 2);
        if (!subCommand.execute(BreweryGarden.getInstance(), config, sender, label, subArgs)) {
            Logging.msg(sender, subCommand.usage(label));
        }
    }

    @Override
    public List<String> tabComplete(BreweryPlugin breweryPlugin, CommandSender sender, String s, String[] args) {
        if (args.length == 2) {
            return subCommands.entrySet()
                    .stream().filter(entry -> entry.getValue().permission() == null ||
                            sender.hasPermission(entry.getValue().permission())).map(Map.Entry::getKey).toList();
        }

        AddonSubCommand subCommand = subCommands.get(args[1]);
        if (subCommand == null) return null;
        String[] subArgs = new String[args.length - 2];
        System.arraycopy(args, 2, subArgs, 0, args.length - 2);
        return subCommand.tabComplete(BreweryGarden.getInstance(), sender, s, subArgs);
    }

    @Override
    public String permission() {
        return "brewery.cmd.garden";
    }

    @Override
    public boolean playerOnly() {
        return false;
    }
}
