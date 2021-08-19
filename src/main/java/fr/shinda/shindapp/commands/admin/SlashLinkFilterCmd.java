package fr.shinda.shindapp.commands.admin;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.enums.*;
import fr.shinda.shindapp.sql.GuildData;
import fr.shinda.shindapp.sql.UserData;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.concurrent.TimeUnit;

public class SlashLinkFilterCmd extends SlashCommand {

    EventWaiter waiter;

    public SlashLinkFilterCmd(EventWaiter waiter) {
        this.name = "link-filter";
        this.help = "Permet d'activer ou de désactiver la filtration des invitations envoyées sur le discord.";
        this.botPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
        this.waiter = waiter;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply(true).queue();
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        try {

            if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() >= Ranks.ADMIN.getId()) {

                event.getHook().sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(Colors.MAIN.getHexCode())
                                .setDescription(String.format(Responses.LINK_FILTER_WAITING.getContent(), event.getMember().getUser().getName()))
                                .build()
                ).addActionRow(
                        Button.success(Buttons.BUTTON_LINK_FILTER_TRUE.getButtonId(), Buttons.BUTTON_LINK_FILTER_TRUE.getButtonName()).withEmoji(Emoji.fromMarkdown("\uD83D\uDEE1️"))
                ).addActionRow(
                        Button.danger(Buttons.BUTTON_LINK_FILTER_FALSE.getButtonId(), Buttons.BUTTON_LINK_FILTER_FALSE.getButtonName()).withEmoji(Emoji.fromMarkdown("⚠️"))
                ).addActionRow(
                        Button.secondary(Buttons.BUTTON_GLOBAL_CANCEL.getButtonId(), Buttons.BUTTON_GLOBAL_CANCEL.getButtonName()).withEmoji(Emoji.fromMarkdown("↩️"))
                ).queue(success -> {

                    waiter.waitForEvent(ButtonClickEvent.class, e -> e.getMember().getId().equals(event.getMember().getId()) && e.getChannel().getId().equals(event.getChannel().getId()), e -> {
                        e.deferReply(true).queue();
                        GuildData guildData = new GuildData(Main.getConnection(), e.getGuild());

                        if (e.getComponentId().equals(Buttons.BUTTON_LINK_FILTER_TRUE.getButtonId())) {
                            e.getChannel().deleteMessageById(success.getId()).queue();

                            try {
                                if (!guildData.isStored())
                                    guildData.createData();
                                guildData.setDiscordLinkFilter(true);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Sentry.captureException(ex);
                                e.getHook().sendMessageEmbeds(
                                        new EmbedBuilder()
                                                .setColor(Colors.ERROR.getHexCode())
                                                .setDescription(String.format(Errors.GLOBAL_TRY_CATCH_ERROR.getContent(), ex.getClass().getName(), ex.getMessage()))
                                                .build()
                                ).queue();
                                return;
                            }

                            e.getHook().sendMessageEmbeds(
                                    new EmbedBuilder()
                                            .setColor(Colors.MAIN.getHexCode())
                                            .setDescription(String.format(Responses.LINK_FILTER_ON.getContent(), e.getMember().getUser().getName()))
                                            .build()
                            ).queue();
                        }

                        if (e.getComponentId().equals(Buttons.BUTTON_LINK_FILTER_FALSE.getButtonId())) {
                            e.getChannel().deleteMessageById(success.getId()).queue();

                            try {
                                if (!guildData.isStored())
                                    guildData.createData();
                                guildData.setDiscordLinkFilter(false);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Sentry.captureException(ex);
                                e.getHook().sendMessageEmbeds(
                                        new EmbedBuilder()
                                                .setColor(Colors.ERROR.getHexCode())
                                                .setDescription(String.format(Errors.GLOBAL_TRY_CATCH_ERROR.getContent(), ex.getClass().getName(), ex.getMessage()))
                                                .build()
                                ).queue();
                                return;
                            }

                            e.getHook().sendMessageEmbeds(
                                    new EmbedBuilder()
                                            .setColor(Colors.MAIN.getHexCode())
                                            .setDescription(String.format(Responses.LINK_FILTER_OFF.getContent(), e.getMember().getUser().getName()))
                                            .build()
                            ).queue();
                        }

                        if (e.getComponentId().equals(Buttons.BUTTON_GLOBAL_CANCEL.getButtonId())) {
                            e.getChannel().deleteMessageById(success.getId()).queue();

                            e.getHook().sendMessageEmbeds(
                                    new EmbedBuilder()
                                            .setColor(Colors.MAIN.getHexCode())
                                            .setDescription(String.format(Responses.GLOBAL_CMD_CANCEL.getContent(), e.getMember().getUser().getName()))
                                            .build()
                            ).queue();
                        }

                    }, 3, TimeUnit.MINUTES, () -> event.getHook().sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Colors.MAIN.getHexCode())
                                    .setDescription(String.format(Responses.GLOBAL_CMD_TIME_OUT.getContent(), event.getMember().getUser().getName()))
                                    .build()
                    ).queue());
                });
            } else {
                event.getHook().sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(Colors.ERROR.getHexCode())
                                .setDescription(String.format(Errors.USER_DOSENT_HAVE_PERMS.getContent(), event.getMember().getUser().getName()))
                                .build()
                ).queue();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Sentry.captureException(ex);
        }
    }
}
