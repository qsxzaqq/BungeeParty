package cc.i9mc.bungeeparty.commands;

import cc.i9mc.bungeeparty.BungeeParty;
import cc.i9mc.bungeeparty.party.Party;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public class ZdCommand extends Command {

    public ZdCommand() {
        super("zd");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        String playerName = player.getName();

        if (player.getServer().getInfo().getName().startsWith("Auth-")) {
            return;
        }

        if (args.length == 0) {
            helpCommand(player);
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("tc")) {
                if (BungeeParty.getPartyController().isCap(playerName)) {
                    BungeeParty.getPartyController().sendPartyDisband(playerName);
                    return;
                }

                if (BungeeParty.getPartyController().isMember(playerName)) {
                    BungeeParty.getPartyController().sendPartyLeave(playerName.replace(" ", ""));
                    player.sendMessage(new TextComponent("§a离开队伍成功..."));
                    return;
                }

                player.sendMessage(new TextComponent("§c您不在一个队伍里或者是某队伍队长！"));
                return;
            }

            if (args[0].equalsIgnoreCase("info")) {
                if (!BungeeParty.getPartyController().isInParty(playerName)) {
                    player.sendMessage(new TextComponent("§c您不在一个队伍里！"));
                    return;
                }

                BungeeParty.getPartyController().getParty(playerName).showList(player);
                return;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("cj")) {
                BungeeParty.getPartyController().sendPartyCreate(playerName, args[1]);
                return;
            }

            if (args[0].equalsIgnoreCase("yq")) {
                BungeeParty.getPartyController().sendPartyInvite(playerName, args[1]);
                return;
            }

            if (args[0].equalsIgnoreCase("js")) {
                if (!BungeeParty.getPartyController().isInParty(args[1])) {
                    player.sendMessage(new TextComponent("§c该队伍不存在"));
                    return;
                }

                Party party = BungeeParty.getPartyController().getParty(args[1]);

                if (party.isFull()) {
                    player.sendMessage(new TextComponent("§c队伍已满人..."));
                    return;
                }

                if (!party.getInvite().containsKey(playerName.toLowerCase())) {
                    player.sendMessage(new TextComponent("§c你没受到邀请..."));
                    return;
                }

                if (Math.abs(System.currentTimeMillis() - party.getInvite().getOrDefault(playerName.toLowerCase(), 0L)) > 60000) {
                    player.sendMessage(new TextComponent("§c你没受到邀请..."));
                    return;
                }

                BungeeParty.getPartyController().sendPartyInviteOk(args[1], playerName);
                player.sendMessage(new TextComponent("§a您已成功加入队伍!"));
                return;
            }

            if (args[0].equalsIgnoreCase("kick")) {
                BungeeParty.getPartyController().sendPartyKick(playerName, args[1]);
                return;
            }
        }

        helpCommand(player);
    }

    public void helpCommand(ProxiedPlayer player) {
/*        player.sendMessage(new TextComponent("§3§m--------§r §6小蜜蜂 §f- §9组队系统 §3§m--------"));
        player.sendMessage(new TextComponent(" §3> §e/zd cj <队伍名>  §3➽  §a创建组队,可中文,2-5个字"));
        player.sendMessage(new TextComponent(" §3> §e/zd tc  §3➽  §a退出当前组队(队长退出即解散)"));
        player.sendMessage(new TextComponent(" §3> §e/zd info  §3➽  §a查看当前队伍信息"));
        player.sendMessage(new TextComponent(" §3> §e/zd yq <玩家名字>  §3➽  §a邀请一个玩家加入你的组队"));
        player.sendMessage(new TextComponent(" §3> §e/zd kick <队员名字>  §3➽  §a从队伍踢出这个队员"));
        player.sendMessage(new TextComponent(" §3> §e/zd js <玩家名字> §3➽  §a接受这个队长的邀请"));
        player.sendMessage(new TextComponent(" §3> §c使用#开头可进入队伍聊天,§7每个队伍最多4人,退出游戏即退出队伍！"));
        player.sendMessage(new TextComponent(" §3> §7创建队伍后,队长邀请队员,队员点击聊天消息或者输入命令来加入!"));
        player.sendMessage(new TextComponent("§3§m--------------------------------------"));*/
        player.sendMessage(new TextComponent("§3§m--------§r §6小蜜蜂 §f- §9组队系统 §3§m--------"));
        player.sendMessage(new TextComponent(" §3> §e/zd cj <队伍名>  §3➽  §a创建组队,可中文,2-5个字"));
        player.sendMessage(new TextComponent(" §3> §e/zd tc  §3➽  §a退出当前组队(队长退出即解散)"));
        player.sendMessage(new TextComponent(" §3> §e/zd info  §3➽  §a查看当前队伍信息"));
        player.sendMessage(new TextComponent(" §3> §e/zd yq <玩家名字>  §3➽  §a邀请一个玩家加入你的组队"));
        player.sendMessage(new TextComponent(" §3> §e/zd kick <队员名字>  §3➽  §a从队伍踢出这个队员"));
        player.sendMessage(new TextComponent(" §3> §e/zd js <玩家名字> §3➽  §a接受这个队长的邀请"));
        player.sendMessage(new TextComponent(" §3> §c使用#开头可进入队伍聊天,§7每个队伍最多4人,退出游戏即退出队伍！"));
        player.sendMessage(new TextComponent(" §3> §7创建队伍后,队长邀请队员,队员点击聊天消息或者输入命令来加入!"));
        player.sendMessage(new TextComponent("§3§m--------------------------------------"));
    }
}
