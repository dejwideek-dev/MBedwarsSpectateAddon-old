package pl.dejwideek.mbwspectateaddon;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ALL")
public class SpectateAddon extends JavaPlugin implements CommandExecutor {

    public YamlDocument config;

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("MBedwars") != null) {
            final int supportedAPIVersion = 1;

            try {
                Class apiClass = Class.forName("de.marcely.bedwars.api.BedwarsAPI");
                int apiVersion = (int) apiClass.getMethod("getAPIVersion").invoke(null);

                if (apiVersion < supportedAPIVersion)
                    throw new IllegalStateException();
            } catch(Exception e) {
                this.getLogger().warning("Your MBedwars version is not supported. Supported version: 5.0 or higher!");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            try {
                config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                        GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(),
                        DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            this.getCommand("watch").setExecutor(this::onCommand);
        }
        else {
            this.getLogger().warning("MBedwars is not enabled!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;

            if(p.hasPermission(config.getString("Permission"))) {
                if(strings.length == 0) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Messages.Usage")));
                }
                if(strings.length >= 1) {
                    Player target = Bukkit.getPlayer(strings[0]);

                    if(target == p) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Messages.Yourself")));
                        return true;
                    }
                    if(target != null) {
                        Arena a = BedwarsAPI.getGameAPI().getArenaByPlayer(target);

                        if(a != null) {
                            ArenaStatus status = a.getStatus();

                            if(status.equals(ArenaStatus.LOBBY)) {
                                a.addPlayer(p);
                                p.teleport(target);
                                p.sendMessage(ChatColor.translateAlternateColorCodes(
                                        '&', config.getString("Messages.Teleported")
                                                .replaceAll("%player%", target.getName())
                                                .replaceAll("%arena%", a.getName())));
                            }
                            if(status.equals(ArenaStatus.RUNNING)) {
                                a.addSpectator(p);
                                p.teleport(target);
                                p.sendMessage(ChatColor.translateAlternateColorCodes(
                                        '&', config.getString("Messages.Teleported")
                                                .replaceAll("%player%", target.getName())
                                                .replaceAll("%arena%", a.getName())));
                            }
                            if(status.equals(ArenaStatus.END_LOBBY)) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Messages.Already-Ending")));
                            }
                            return true;
                        }
                        if(a == null) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Messages.No-Inside-Arena")));
                        }
                    }
                    if(target == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("Messages.Not-Found")));
                    }
                    return true;
                }
            }
            else {
                p.sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', config.getString("Messages.No-Permission")
                                .replaceAll("%permission%", config.getString("Permission"))));
                return true;
            }
        }
        return false;
    }
}
