package fr.shinda.shindapp.listeners;

import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.sql.UserData;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class GuildListeners implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof GuildMemberJoinEvent)
            this.firstTimePlayerJoin((GuildMemberJoinEvent) genericEvent);
    }

    public void firstTimePlayerJoin(GuildMemberJoinEvent event) {
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        if (!authorData.isStored())
            authorData.createData(event.getGuild());
    }

}
