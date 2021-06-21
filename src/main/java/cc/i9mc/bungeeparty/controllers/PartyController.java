package cc.i9mc.bungeeparty.controllers;

import cc.i9mc.bungeeparty.BungeeParty;
import cc.i9mc.bungeeparty.party.Party;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class PartyController implements Listener {
    private final ConcurrentHashMap<String, Party> partys = new ConcurrentHashMap<>();

    public boolean isInParty(String name) {
        for (Party p : partys.values()) {
            if (p.isCap(name)) {
                return true;
            }

            if (p.isInParty(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean isMember(String name) {
        for (Party p : partys.values()) {
            if (p.isInParty(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean isCap(String name) {
        for (Party p : partys.values()) {
            if (p.isCap(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkPartyName(String name) {
        for (Party p : partys.values()) {
            if (p.getName().toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void createParty(String name, String cap) {
        try {
            ProxiedPlayer player = BungeeParty.getInstance().getProxy().getPlayer(cap);
            if (player == null) {
                return;
            }

            if (isInParty(cap)) {
                player.sendMessage(new TextComponent("§c您在一个队伍里或者是某队伍队长！"));
                return;
            }

            if (!name.matches("^.{2,5}$")) {
                player.sendMessage(new TextComponent("§c名字过长或过短!"));
                return;
            }

            if (checkPartyName(name)) {
                player.sendMessage(new TextComponent("§c名字冲突!"));
                return;
            }

            partys.put(name, new Party(name, cap));

            player.sendMessage(new TextComponent("§3§m--------§r §6小蜜蜂 §f- §9组队系统 §3§m--------"));
            player.sendMessage(new TextComponent(" §3> §c恭喜您创建队伍成功!"));
            player.sendMessage(new TextComponent(" §3> §a你可以使用 §e/zd yq <玩家名字> §a邀请别人加入!"));
            player.sendMessage(new TextComponent(" §3> §a等待对方打开聊天栏并点击或输入接受命令接受!"));
            player.sendMessage(new TextComponent("§3§m--------------------------------------"));
        } catch (Exception e) {
            System.out.print("Create Error!");
        }
    }

    private void leaveParty(String name) {
        for (Party p : partys.values()) {
            if (p.isInParty(name)) {
                if (!p.isCap(name)) {
                    p.removeTeam(name, false);
                }
            }
        }
    }

    private void disbandParty(String name) {
        try {
            Iterator<Party> ilt = partys.values().iterator();
            while (ilt.hasNext()) {
                Party party = ilt.next();
                if (party.isCap(name)) {
                    party.getPlayers(true).forEach((proxiedPlayer -> proxiedPlayer.sendMessage(new TextComponent("§c你所在的队伍已解散"))));
                    ilt.remove();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Party getParty(String player) {
        for (Party part : partys.values()) {
            if (part.isCap(player)) {
                return part;
            }

            if (part.isInParty(player)) {
                return part;
            }
        }
        return null;
    }

    private void partyJoin(String cap, String name) {
        Party p = getParty(cap);

        try {
            p.addTeam(name);
            ProxiedPlayer pl = BungeeParty.getInstance().getProxy().getPlayer(name);

            if (pl == null) {
                return;
            }

            TextComponent tc = new TextComponent("§3§m--------§r §6小蜜蜂 §f- §9组队系统 §3§m--------" + "\n");
            tc.addExtra("§3 > §e你加入了 §6" + p.getName() + "\n");
            tc.addExtra("§3 > §e可以输入§c /zd tc §e来退出组队" + "\n");
            tc.addExtra("§3 > §c目前你的传送操作已归于队长管理!请勿操作!" + "\n");
            tc.addExtra("§3§m--------------------------------------");
            pl.sendMessage(tc);
        } catch (Exception e) {
            System.out.print("Accept Error!");
        }
    }

    private void partyInvite(String cap, String name) {
        Party party = getParty(cap);
        if (party == null) {
            return;
        }

        ProxiedPlayer capPlayer = BungeeParty.getInstance().getProxy().getPlayer(cap);
        if(capPlayer != null){
            if (!isCap(cap)) {
                capPlayer.sendMessage(new TextComponent("§c您不是队长..."));
                return;
            }

            if (party.isFull()) {
                capPlayer.sendMessage(new TextComponent("§c队伍已满人..."));
                return;
            }

            if (party.isInParty(name)) {
                capPlayer.sendMessage(new TextComponent("§c该玩家已经在您的队伍内了..."));
                return;
            }

            if (isInParty(name)) {
                capPlayer.sendMessage(new TextComponent("§c该玩家已经有队伍了..."));
                return;
            }

            if (!BungeeParty.getRedisBungeeAPI().isPlayerOnline(BungeeParty.getRedisBungeeAPI().getUuidFromName(name))) {
                capPlayer.sendMessage(new TextComponent("§4这个玩家不在线,无法邀请!"));
                return;
            }

            capPlayer.sendMessage(new TextComponent("§a邀请成功,等待对方接受§c(60秒内)§a!"));
        }else if(!isCap(cap) || party.isFull() || party.isInParty(name) || isInParty(name) || !BungeeParty.getRedisBungeeAPI().isPlayerOnline(BungeeParty.getRedisBungeeAPI().getUuidFromName(name))){
            return;
        }

        ProxiedPlayer player = BungeeParty.getInstance().getProxy().getPlayer(name);
        if (party.addInvite(name) && player != null) {
            TextComponent tc = new TextComponent("§3§m--------§r §6小蜜蜂 §f- §9组队系统 §3§m--------" + "\n");
            tc.addExtra("§3 > §e玩家 §a" + cap + " §e邀请你加入他的队伍 队伍名 §6" + party.getName() + "\n");
            tc.addExtra("§3 > §e点击这里来");
            tc.addExtra("§a§l[接受]" + "\n");
            tc.addExtra("§3 > §c不同意则无视,60秒超时!" + "\n");
            tc.addExtra("§3§m--------------------------------------");
            tc.getExtra().get(2).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zd js " + cap));
            player.sendMessage(tc);
        }
    }


    private void partyKick(String cap, String name) {
        Party party = getParty(cap);
        if (party == null) {
            return;
        }
        ProxiedPlayer capPlayer = BungeeParty.getInstance().getProxy().getPlayer(cap);
        if(capPlayer != null){
            if (!isCap(cap)) {
                capPlayer.sendMessage(new TextComponent("§c您不是队长..."));
                return;
            }

            if (!party.isInParty(name)) {
                capPlayer.sendMessage(new TextComponent("§c这个人并不是您的队员..."));
                return;
            }

            capPlayer.sendMessage(new TextComponent("§a踢出队员成功..."));
        }else if(!isCap(cap) || !party.isInParty(name)){
            return;
        }

        party.removeTeam(name, true);
    }

    public void sendPartyCreate(String cap, String name) {
        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> BungeeParty.getRedisBungeeAPI().sendChannelMessage("Party", "create|" + cap + "|" + name));
    }

    public void sendPartyLeave(String cap) {
        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> BungeeParty.getRedisBungeeAPI().sendChannelMessage("Party", "leave|" + cap));
    }

    public void sendPartyDisband(String cap) {
        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> BungeeParty.getRedisBungeeAPI().sendChannelMessage("Party", "disband|" + cap));
    }

    public void sendPartyInvite(String cap, String invitee) {
        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> BungeeParty.getRedisBungeeAPI().sendChannelMessage("Party", "invite|" + cap + "|" + invitee));
    }

    public void sendPartyInviteOk(String cap, String invitee) {
        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> BungeeParty.getRedisBungeeAPI().sendChannelMessage("Party", "ok|" + cap + "|" + invitee));
    }

    public void sendPartyKick(String cap, String kicked) {
        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> BungeeParty.getRedisBungeeAPI().sendChannelMessage("Party", "kick|" + cap + "|" + kicked));
    }

    public void sendAllServer(String serverName, String capName) {
        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> BungeeParty.getRedisBungeeAPI().sendChannelMessage("Party", "allSend|" + serverName + "|" + capName));
    }

    public void sendChat(String chat, String capName) {
        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> BungeeParty.getRedisBungeeAPI().sendChannelMessage("Party", "chat|" + chat + "|" + capName));
    }

    @EventHandler
    public void onPartyMessage(PubSubMessageEvent event) {
        if (!event.getChannel().equals("Party")) {
            return;
        }

        BungeeParty.getInstance().getProxy().getScheduler().runAsync(BungeeParty.getInstance(), () -> {
            StringTokenizer in = new StringTokenizer(event.getMessage(), "|");
            String action = in.nextToken();

            if (action.equals("create")) {
                String cap = in.nextToken();
                String name = in.nextToken();
                createParty(name, cap);
                System.out.print("Party Created " + name + "|" + cap);
                return;
            }

            if (action.equals("leave")) {
                String name = in.nextToken();
                leaveParty(name);
                System.out.print("Party Leave " + name);
                return;
            }

            if (action.equals("disband")) {
                String name = in.nextToken();
                disbandParty(name);
                System.out.print("Party Disband " + name);
                return;
            }

            if (action.equals("invite")) {
                String cap = in.nextToken();
                String name = in.nextToken();
                partyInvite(cap, name);
                return;
            }

            if (action.equals("ok")) {
                String cap = in.nextToken();
                String name = in.nextToken();
                partyJoin(cap, name);
                return;
            }

            if (action.equals("kick")) {
                String cap = in.nextToken();
                String name = in.nextToken();
                partyKick(cap, name);
                return;
            }

            if (action.equals("allSend")) {
                String serverName = in.nextToken();
                String name = in.nextToken();
                Party p = getParty(name);
                if (p == null) {
                    return;
                }

                p.allSendServer(serverName);
            }

            if (action.equals("chat")) {
                String chat = in.nextToken();
                String name = in.nextToken();
                Party p = getParty(name);
                if (p == null) {
                    return;
                }

                p.sendChat(chat);
            }
        });

    }

}
