package pl.dejwideek.mbwspectateaddon.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.dejwideek.mbwspectateaddon.SpectateAddon;

import java.io.IOException;

@SuppressWarnings("ALL")
public class ReloadCommand implements CommandExecutor {

    private final SpectateAddon plugin;

    public ReloadCommand(SpectateAddon plg) {
        plugin = plg;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;

            if(p.hasPermission(plugin.config
                    .getString("Permissions.Reload"))) {
                try {
                    plugin.config.reload();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }

                p.sendMessage(ChatColor
                        .translateAlternateColorCodes('&',
                                plugin.config
                                        .getString("Messages.Reloaded")));
                plugin.getLogger().info("Reloaded configuration file!");
            }
            else {
                p.sendMessage(ChatColor
                        .translateAlternateColorCodes('&',
                                plugin.config
                                        .getString("Messages.No-Permission")
                                        .replaceAll("%permission%",
                                                plugin.config
                                                        .getString("Permissions.Reload"))));
            }
        }

        if(commandSender instanceof ConsoleCommandSender) {
            try {
                plugin.config.reload();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            plugin.getLogger().info("Reloaded configuration file!");
        }
        return true;
    }
}
