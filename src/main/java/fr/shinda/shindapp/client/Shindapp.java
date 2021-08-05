package fr.shinda.shindapp.client;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.commands.moderation.SlashBanCmd;
import fr.shinda.shindapp.commands.moderation.SlashHistoryCmd;
import fr.shinda.shindapp.commands.moderation.SlashKickCmd;
import fr.shinda.shindapp.commands.moderation.SlashWarnCmd;
import fr.shinda.shindapp.commands.admin.SlashLinkFilterCmd;
import fr.shinda.shindapp.commands.admin.SlashManuaddCmd;
import fr.shinda.shindapp.commands.admin.SlashSyncAllCmd;
import fr.shinda.shindapp.commands.suggestion.SlashSuggestionCmd;
import fr.shinda.shindapp.listeners.GuildListeners;
import fr.shinda.shindapp.utils.ConfigUtils;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Shindapp {

    EventWaiter eventWaiter = new EventWaiter();

    public void initClient() throws LoginException, InterruptedException {

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setOwnerId(ConfigUtils.getConfig("client.ownerid"));
        builder.setCoOwnerIds(ConfigUtils.getConfig("client.coownerid"));
        builder.setPrefix("!");
        builder.setAlternativePrefix("<@!" + ConfigUtils.getConfig("client.clientid") + "> ");
        builder.setActivity(Activity.streaming("play.valoniamc.eu", "https://www.twitch.tv/grayr0ot"));

        builder.forceGuildOnly("655074344619343873");
        builder.addSlashCommands(
                new SlashManuaddCmd(eventWaiter),
                new SlashSyncAllCmd(),
                new SlashKickCmd(eventWaiter),
                new SlashBanCmd(eventWaiter),
                new SlashWarnCmd(eventWaiter),
                new SlashSuggestionCmd(eventWaiter),
                new SlashLinkFilterCmd(eventWaiter),
                new SlashHistoryCmd()
        );

        CommandClient client = builder.build();

        JDABuilder jda = JDABuilder.createDefault(ConfigUtils.getConfig("client.key"))
                .addEventListeners(client, eventWaiter)
                .addEventListeners(new GuildListeners())
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .enableCache(CacheFlag.ACTIVITY);

        jda.build().awaitReady();

    }

}
