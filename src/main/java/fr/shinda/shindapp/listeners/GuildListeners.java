package fr.shinda.shindapp.listeners;

import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.sql.GuildData;
import fr.shinda.shindapp.sql.UserData;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildListeners implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof GuildMemberJoinEvent)
            this.firstTimePlayerJoin((GuildMemberJoinEvent) genericEvent);

        if (genericEvent instanceof GuildMessageReceivedEvent)
            this.onDiscordLinkReceived((GuildMessageReceivedEvent) genericEvent);
    }

    public void firstTimePlayerJoin(GuildMemberJoinEvent event) {
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        if (!authorData.isStored())
            authorData.createData(event.getGuild());
    }

    public void onDiscordLinkReceived(GuildMessageReceivedEvent event) {

        final String regex = "(https?:\\/\\/)?(www\\.)?(discord\\.(gg|io|me|li)|discordapp\\.com\\/invite)\\/.+[a-z]";
        final String content = event.getMessage().getContentRaw();

        GuildData guildData = new GuildData(Main.getConnection(), event.getGuild());
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(content);

        if (!guildData.isStored())
            guildData.createData();

        if (guildData.discordFilterIsActivated()) {
            if (matcher.find()) {
                if (authorData.getRank() <= 29) {
                    event.getMessage().delete().queue();
                }
            }
        }

    }

}
