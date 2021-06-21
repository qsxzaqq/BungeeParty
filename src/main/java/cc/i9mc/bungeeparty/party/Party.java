package cc.i9mc.bungeeparty.party;

import cc.i9mc.bungeeparty.BungeeParty;
import lombok.Data;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

@Data
public class Party {
    private String name;
    private String cap;

    private List<String> member;
    private Map<String, Long> invite;

    public Party(String name, String cap) {
        member = new ArrayList<>();
        invite = new HashMap<>();

        this.name = name;
        this.cap = cap;
    }


    public boolean isInParty(String player) {
        return member.contains(player);
    }

    public boolean addInvite(String player) {
        if (Math.abs(System.currentTimeMillis() - invite.getOrDefault(player.toLowerCase(), 0L)) < 60000) {
            return false;
        }

        invite.remove(player.toLowerCase());
        invite.put(player.toLowerCase(), System.currentTimeMillis());
        return true;
    }

    public void addTeam(String player) {
        if (member.size() >= 3) {
            return;
        }

        member.add(player);

        getPlayers(true).forEach((proxiedPlayer) -> proxiedPlayer.sendMessage(new TextComponent("§e玩家 §b" + player + " §e加入了队伍   §a(" + (member.size() + 1) + "/4)")));

        invite.remove(player.toLowerCase());

    }

    public void removeTeam(String player, boolean kicked) {
        member.remove(player);

        getPlayers(true).forEach((proxiedPlayer) -> {
            if (kicked) {
                proxiedPlayer.sendMessage(new TextComponent("§e玩家 §b" + player + " §e被踢出了队伍   §a(" + (member.size() + 1) + "/4)"));
            } else {
                proxiedPlayer.sendMessage(new TextComponent("§e玩家 §b" + player + " §e离开了队伍  §a(" + (member.size() + 1) + "/4)"));
            }
        });
    }

    public void showList(ProxiedPlayer player) {
        StringBuilder memberShow = new StringBuilder(" §3> §e队员:§a ");

        for (String s : member) {
            memberShow.append(s).append(" - ");
        }

        player.sendMessage(new TextComponent("§3§m--------§r §6小蜜蜂 §f- §9组队系统 §3§m--------"));
        player.sendMessage(new TextComponent(" §3> §e队伍名称:§a " + name));
        player.sendMessage(new TextComponent(" §3> §e队长:§a " + cap));
        player.sendMessage(new TextComponent(memberShow.toString().substring(0, memberShow.toString().length() - 3)));
        player.sendMessage(new TextComponent("§3§m--------------------------------------"));
    }

    public boolean isFull() {
        return member.size() >= 3;
    }

    public boolean isCap(String cap) {
        return cap.equals(this.cap);
    }

    public void allSendServer(String serverName) {
        ServerInfo serverInfo = BungeeParty.getInstance().getProxy().getServerInfo(serverName);

        getPlayers(false).forEach((player) -> {
            player.connect(serverInfo);
            player.sendMessage(new TextComponent("§a你正在跟随队长传送,§c如退出组队请输入 §6/zd tc"));
        });
    }

    public void sendChat(String chat) {
        getPlayers(true).forEach((player) -> {
            player.sendMessage(new TextComponent(chat));
        });
    }

    public LinkedList<ProxiedPlayer> getPlayers(boolean capShow) {
        LinkedList<ProxiedPlayer> players = new LinkedList<>();

        ProxiedPlayer player = BungeeParty.getInstance().getProxy().getPlayer(cap);

        if (player != null && capShow) {
            players.add(player);
        }

        for (String m : member) {
            player = BungeeParty.getInstance().getProxy().getPlayer(m);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }
}
