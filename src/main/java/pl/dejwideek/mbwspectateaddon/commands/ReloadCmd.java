package pl.dejwideek.mbwspectateaddon.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import com.iridium.iridiumcolorapi.IridiumColorAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.dejwideek.mbwspectateaddon.SpectateAddon;

import java.io.IOException;

@SuppressWarnings("ALL")
public class ReloadCmd extends BaseCommand {

    private static SpectateAddon plugin;

    public ReloadCmd(SpectateAddon plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("spectateaddonreload|spectaddonreload|sareload")
    @Description("Reload config file")
    public void reload(CommandSender commandSender) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;
            String permission = plugin.config.getString("Permissions.Reload");
            String reloadedMsg = plugin.config.getString("Messages.Reloaded");
            String noPermsMsg = plugin.config.getString("Messages.No-Permission");

            if(p.hasPermission(permission)) {
                try {
                    plugin.config.reload();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }

                p.sendMessage(IridiumColorAPI.process(reloadedMsg));
                plugin.getLogger().info("Reloaded configuration file!");
            }
            else {
                p.sendMessage(IridiumColorAPI.process(noPermsMsg
                        .replaceAll("%permission%", permission)));
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
        return;
    }
}
