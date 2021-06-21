package cc.i9mc.bungeeparty.listeners;

import cc.i9mc.bungeeparty.BungeeParty;
import cc.i9mc.bungeeparty.party.Party;
import cc.i9mc.pluginchannel.events.BungeeCommandEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.LinkedList;

public class PartyListener implements Listener {

    @EventHandler
    public void onQuitRemoveParty(PlayerDisconnectEvent event) {
        String n = event.getPlayer().getName();
        if (BungeeParty.getPartyController().isCap(n)) {
            BungeeParty.getPartyController().sendPartyDisband(n);
            return;
        }

        if (BungeeParty.getPartyController().isMember(n)) {
            BungeeParty.getPartyController().sendPartyLeave(n);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnection(ServerConnectEvent event) {
        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> {
            String n = event.getPlayer().getName();
            if (BungeeParty.getPartyController().isCap(n)) {
                BungeeParty.getPartyController().sendAllServer(event.getTarget().getName(), n);
            }
        });
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        if (p.getServer().getInfo().getName().contains("Auth-")) {
            return;
        }

        if (!e.getMessage().startsWith("#")) {
            return;
        }

        String name = p.getName();
        if (!BungeeParty.getPartyController().isInParty(name)) {
            return;
        }

        BungeeParty.getPartyController().sendChat("§6[§a组队聊天§6] §b" + name + " §a> §f" + e.getMessage().substring(1), BungeeParty.getPartyController().getParty(name).getCap());

        e.setCancelled(true);
    }

    @EventHandler
    public void onCommand(BungeeCommandEvent e) {
        if (e.getString(0).equalsIgnoreCase("BungeeParty")) {
            if (e.getString(1).equalsIgnoreCase("data")) {
                System.out.println("Party response " + e.getString(2));

                ProxiedPlayer player = BungeeParty.getInstance().getProxy().getPlayer(e.getString(2));
                if (player == null) {
                    return;
                }

                BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> {
                    Party party = BungeeParty.getPartyController().getParty(player.getName());

                    if (party == null) {
                        return;
                    }

                    LinkedList<String> response = new LinkedList<>();

                    response.add(party.getName());
                    response.add(party.getCap());

                    response.addAll(party.getMember());

                    e.response(response.toArray(new String[0]));
                });
            }else if (e.getString(1).equalsIgnoreCase("invite")) {
                System.out.println("Party invite " + e.getString(2));

                ProxiedPlayer player = BungeeParty.getInstance().getProxy().getPlayer(e.getString(2));
                if (player == null) {
                    return;
                }

                BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> {
                    Party party = BungeeParty.getPartyController().getParty(player.getName());

                    if (party == null) {
                        return;
                    }

                    LinkedList<String> response = new LinkedList<>();

                    response.add(party.getName());
                    response.add(party.getCap());

                    response.addAll(party.getMember());

                    e.response(response.toArray(new String[0]));
                });
            }
        }
    }
}
