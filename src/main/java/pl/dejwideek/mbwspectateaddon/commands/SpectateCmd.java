package pl.dejwideek.mbwspectateaddon.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import com.iridium.iridiumcolorapi.IridiumColorAPI;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.remote.RemoteAPI;
import de.marcely.bedwars.api.remote.RemoteArena;
import de.marcely.bedwars.api.remote.RemotePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.dejwideek.mbwspectateaddon.SpectateAddon;

@SuppressWarnings("ALL")
public class SpectateCmd extends BaseCommand {

    private static SpectateAddon plugin;

    public SpectateCmd(SpectateAddon plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("watch|spectate|spect|spec")
    @CommandCompletion("@players")
    @Description("Watch player")
    public void spectate(CommandSender commandSender, String[] strings) {
        BedwarsAPI.onReady(() -> {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                String usageMsg = plugin.config.getString("Messages.Usage");
                String yourselfMsg = plugin.config.getString("Messages.Yourself");
                String teleportedMsg = plugin.config.getString("Messages.Teleported");
                String endingMsg = plugin.config.getString("Messages.Already-Ending");
                String noInsideMsg = plugin.config.getString("Messages.No-Inside-Arena");
                String notFoundMsg = plugin.config.getString("Messages.Not-Found");
                String noPermsMsg = plugin.config.getString("Messages.No-Permission");
                String permission = plugin.config.getString("Permissions.Spectate");

                if (p.hasPermission(permission)) {
                    if (strings.length == 0) {
                        p.sendMessage(IridiumColorAPI.process(usageMsg));
                    }
                    if (strings.length >= 1) {
                        RemotePlayer target = RemoteAPI.get().getOnlinePlayer(strings[0]);
                        RemotePlayer player = RemoteAPI.get().getOnlinePlayer(p);

                        if (target != null) {
                            if (target.asBukkit() == p) {
                                p.sendMessage(IridiumColorAPI.process(yourselfMsg));
                                return;
                            }
                            RemoteArena a = RemoteAPI.get().getArenaByPlayingPlayer(target);

                            if (a != null) {
                                ArenaStatus status = a.getStatus();

                                if (status.equals(ArenaStatus.LOBBY)) {
                                    p.sendMessage(IridiumColorAPI.process(teleportedMsg
                                            .replaceAll("%player%", target.getName())
                                            .replaceAll("%arena%", a.getName())));
                                    a.addPlayer(player);
                                }
                                if (status.equals(ArenaStatus.RUNNING)) {
                                    p.sendMessage(IridiumColorAPI.process(teleportedMsg
                                            .replaceAll("%player%", target.getName())
                                            .replaceAll("%arena%", a.getName())));
                                    a.addSpectator(player);
                                }
                                if (status.equals(ArenaStatus.END_LOBBY)) {
                                    p.sendMessage(IridiumColorAPI.process(endingMsg));
                                }
                                return;
                            }
                            if (a == null) {
                                p.sendMessage(IridiumColorAPI.process(noInsideMsg));
                            }
                        }
                        if (target == null) {
                            p.sendMessage(IridiumColorAPI.process(notFoundMsg));
                        }
                        return;
                    }
                } else {
                    p.sendMessage(IridiumColorAPI.process(noPermsMsg
                            .replaceAll("%permission%", permission)));
                    return;
                }
            }
        });
        return;
    }
}
