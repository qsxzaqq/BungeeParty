package cc.i9mc.bungeeparty;

import cc.i9mc.bungeeparty.commands.ZdCommand;
import cc.i9mc.bungeeparty.controllers.PartyController;
import cc.i9mc.bungeeparty.listeners.PartyListener;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeParty extends Plugin {

    @Getter
    private static BungeeParty instance;

    @Getter
    private static RedisBungeeAPI redisBungeeAPI;

    @Getter
    private static PartyController partyController;

    @Override
    public void onEnable() {
        instance = this;

        redisBungeeAPI = RedisBungee.getApi();

        partyController = new PartyController();

        redisBungeeAPI.registerPubSubChannels("Party");

        getProxy().getPluginManager().registerCommand(this, new ZdCommand());

        //party listeners 
        getProxy().getPluginManager().registerListener(this, partyController);
        getProxy().getPluginManager().registerListener(this, new PartyListener());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
