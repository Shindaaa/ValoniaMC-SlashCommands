package fr.shinda.shindapp.client;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.commands.moderation.SlashBanCmd;
import fr.shinda.shindapp.commands.moderation.SlashKickCmd;
import fr.shinda.shindapp.commands.op.SlashDiscordFilterCmd;
import fr.shinda.shindapp.commands.op.SlashManuaddCmd;
import fr.shinda.shindapp.commands.op.SlashSyncAllCmd;
import fr.shinda.shindapp.commands.suggestion.SlashSuggestionCmd;
import fr.shinda.shindapp.listeners.GuildListeners;
import fr.shinda.shindapp.utils.ConfigUtils;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Shindapp {

    EventWaiter eventWaiter = new EventWaiter();

    public void initClient() {

        try {

            CommandClientBuilder builder = new CommandClientBuilder();
            builder.setOwnerId(ConfigUtils.getConfig("client.ownerid"));
            builder.setCoOwnerIds(ConfigUtils.getConfig("client.coownerid"));
            builder.setPrefix("!");
            builder.setActivity(Activity.competing("play.valoniamc.eu"));
            builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

            builder.forceGuildOnly("744250667677515816");
            builder.addSlashCommands(new SlashManuaddCmd(eventWaiter));
            builder.addSlashCommands(new SlashSyncAllCmd());
            builder.addSlashCommands(new SlashKickCmd(eventWaiter));
            builder.addSlashCommands(new SlashBanCmd(eventWaiter));
            builder.addSlashCommands(new SlashSuggestionCmd(eventWaiter));
            builder.addSlashCommands(new SlashDiscordFilterCmd(eventWaiter));

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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
