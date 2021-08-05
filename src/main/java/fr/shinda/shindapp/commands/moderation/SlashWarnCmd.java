package fr.shinda.shindapp.commands.moderation;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.enums.*;
import fr.shinda.shindapp.sql.SanctionData;
import fr.shinda.shindapp.sql.UserData;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SlashWarnCmd extends SlashCommand {

    EventWaiter waiter;

    public SlashWarnCmd(EventWaiter waiter) {
        this.name = "warn";
        this.help = "Permet aux staff du discord d'avertir une personne mentionnée";
        this.botPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
        this.waiter = waiter;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.USER, "user", "Le membre à warn.").setRequired(true));
        data.add(new OptionData(OptionType.STRING, "reason", "La raison de cette sanction.").setRequired(false));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        UserData authorData = new UserData(Main.getConnection(), event.getMember());
        event.deferReply().queue();

        try {

            if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() >= Ranks.HELPER.getId()) {

                Member user = event.getOption("user").getAsMember();
                String reason;

                if (user == null) {
                    event.getHook().sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Colors.ERROR.getHexCode())
                                    .setDescription(String.format(Errors.USER_NOT_FOUND.getContent(), event.getMember().getUser().getName(), user.getUser().getName()))
                                    .build()
                    ).queue();
                    return;
                }

                if (event.getOption("reason") == null)
                    reason = Responses.DEFAULT_REASON.getContent();
                else
                    reason = event.getOption("reason").getAsString();

                UserData userData = new UserData(Main.getConnection(), user);

                if (userData.getRank() >= 30) {
                    event.getHook().sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Colors.ERROR.getHexCode())
                                    .setDescription(String.format(Errors.USER_IS_STAFF.getContent(), event.getMember().getUser().getName(), user.getUser().getName()))
                                    .build()
                    ).queue();
                    return;
                }

                event.getHook().sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(Colors.MAIN.getHexCode())
                                .setDescription(String.format(Responses.GLOBAL_SANCTION_WAITING.getContent(), event.getMember().getUser().getName(), "Warn", reason))
                                .build()
                ).addActionRow(
                        Button.danger(Buttons.BUTTON_SANCTION_CONFIRM.getButtonId(), Buttons.BUTTON_SANCTION_CONFIRM.getButtonName()).withEmoji(Emoji.fromMarkdown("⚠️"))
                ).addActionRow(
                        Button.secondary(Buttons.BUTTON_GLOBAL_CANCEL.getButtonId(), Buttons.BUTTON_GLOBAL_CANCEL.getButtonName()).withEmoji(Emoji.fromMarkdown("↩️"))
                ).queue(success -> {

                    waiter.waitForEvent(ButtonClickEvent.class, e -> e.getMember().getId().equals(event.getMember().getId()) && e.getChannel().getId().equals(event.getChannel().getId()), e -> {
                        e.deferReply().queue();
                        SanctionData sanctionData = new SanctionData(Main.getConnection(), user);

                        if (e.getComponentId().equals(Buttons.BUTTON_SANCTION_CONFIRM.getButtonId())) {
                            e.getChannel().deleteMessageById(success.getId()).queue();

                            try {
                                if (!sanctionData.isStored())
                                    sanctionData.createData();
                                sanctionData.addKick();
                                sanctionData.setSanctionContent("Kick", reason, e.getMember());
                                user.kick().reason(String.format(Responses.GLOBAL_SANCTION_REASON.getContent(), e.getMember().getUser().getName(), "warn", user.getUser().getName(), reason)).queue();
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
                                            .setDescription(String.format(Responses.GLOBAL_SANCTION_REASON.getContent(), e.getMember().getUser().getName(), "warn", user.getUser().getName(), reason))
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

                    }, 10, TimeUnit.MINUTES, () -> event.getHook().sendMessageEmbeds(
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
