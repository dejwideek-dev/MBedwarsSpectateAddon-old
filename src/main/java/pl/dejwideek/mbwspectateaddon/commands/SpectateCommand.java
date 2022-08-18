package pl.dejwideek.mbwspectateaddon.commands;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.dejwideek.mbwspectateaddon.SpectateAddon;

@SuppressWarnings("ALL")
public class SpectateCommand implements CommandExecutor {

    private final SpectateAddon plugin;

    public SpectateCommand(SpectateAddon plg) {
        plugin = plg;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        BedwarsAPI.onReady(() -> {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;

                if (p.hasPermission(plugin.config.getString("Permissions.Spectate"))) {
                    if (strings.length == 0) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Messages.Usage")));
                    }
                    if (strings.length >= 1) {
                        Player target = Bukkit.getPlayer(strings[0]);

                        if (target == p) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Messages.Yourself")));
                            return;
                        }
                        if (target != null) {
                            Arena a = BedwarsAPI.getGameAPI().getArenaByPlayer(target);

                            if (a != null) {
                                ArenaStatus status = a.getStatus();

                                if (status.equals(ArenaStatus.LOBBY)) {
                                    a.addPlayer(p);
                                    p.teleport(target);
                                    p.sendMessage(ChatColor.translateAlternateColorCodes(
                                            '&', plugin.config.getString("Messages.Teleported")
                                                    .replaceAll("%player%", target.getName())
                                                    .replaceAll("%arena%", a.getName())));
                                }
                                if (status.equals(ArenaStatus.RUNNING)) {
                                    a.addSpectator(p);
                                    p.teleport(target);
                                    p.sendMessage(ChatColor.translateAlternateColorCodes(
                                            '&', plugin.config.getString("Messages.Teleported")
                                                    .replaceAll("%player%", target.getName())
                                                    .replaceAll("%arena%", a.getName())));
                                }
                                if (status.equals(ArenaStatus.END_LOBBY)) {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Messages.Already-Ending")));
                                }
                                return;
                            }
                            if (a == null) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Messages.No-Inside-Arena")));
                            }
                        }
                        if (target == null) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Messages.Not-Found")));
                        }
                        return;
                    }
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', plugin.config.getString("Messages.No-Permission")
                                    .replaceAll("%permission%", plugin.config.getString("Permissions.Spectate"))));
                    return;
                }
            }
        });

        return true;
    }
}
