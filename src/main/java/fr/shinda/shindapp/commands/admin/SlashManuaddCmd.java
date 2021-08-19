package fr.shinda.shindapp.commands.admin;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.enums.*;
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
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SlashManuaddCmd extends SlashCommand {

    EventWaiter waiter;

    public SlashManuaddCmd(EventWaiter waiter) {
        this.name = "manuadd";
        this.help = "Permet aux administrateurs de définir le groupe d'une personne mentionnée";
        this.botPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
        this.waiter = waiter;

        this.options = Collections.singletonList(
                new OptionData(OptionType.USER, "user", "le membre à qui définir un groupe.").setRequired(true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply(true).queue();
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        try {

            if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() >= Ranks.ADMIN.getId()) {

                Member user = event.getOption("user").getAsMember();

                if (user == null) {
                    event.getHook().sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Colors.ERROR.getHexCode())
                                    .setDescription(String.format(Errors.USER_NOT_FOUND.getContent(), event.getMember().getUser().getName(), user.getUser().getName()))
                                    .build()
                    ).queue();
                    return;
                }

                UserData userData = new UserData(Main.getConnection(), user);

                try {
                    if (!userData.isStored())
                        userData.createData(event.getGuild());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Sentry.captureException(ex);
                    event.getHook().sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Colors.ERROR.getHexCode())
                                    .setDescription(String.format(Errors.GLOBAL_TRY_CATCH_ERROR.getContent(), ex.getClass().getName(), ex.getMessage()))
                                    .build()
                    ).queue();
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder().setColor(Colors.MAIN.getHexCode()).setDescription("Veuillez choisir un groupe à ajouter à `" + user.getUser().getName() + "`");
                event.getHook().sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(Colors.MAIN.getHexCode())
                                .setDescription(String.format(Responses.MANUADD_WAITING.getContent(), event.getMember().getUser().getName(), user.getUser().getName()))
                                .build()
                        ).addActionRow(
                                Button.danger(Buttons.BUTTON_MANUADD_ADMIN.getButtonId(), Buttons.BUTTON_MANUADD_ADMIN.getButtonName()).withEmoji(Emoji.fromMarkdown("⚠️"))
                        ).addActionRow(
                                Button.primary(Buttons.BUTTON_MANUADD_MOD.getButtonId(), Buttons.BUTTON_MANUADD_MOD.getButtonName()).withEmoji(Emoji.fromMarkdown("⚠️"))
                        ).addActionRow(
                                Button.primary(Buttons.BUTTON_MANUADD_HELPER.getButtonId(), Buttons.BUTTON_MANUADD_HELPER.getButtonName()).withEmoji(Emoji.fromMarkdown("⚠️"))
                        ).addActionRow(
                                Button.success(Buttons.BUTTON_MANUADD_PLAYER.getButtonId(), Buttons.BUTTON_MANUADD_PLAYER.getButtonName()).withEmoji(Emoji.fromMarkdown("\uD83D\uDC65"))
                        ).addActionRow(
                                Button.secondary(Buttons.BUTTON_GLOBAL_CANCEL.getButtonId(), Buttons.BUTTON_GLOBAL_CANCEL.getButtonName()).withEmoji(Emoji.fromMarkdown("↩️"))
                ).queue(success -> {

                    waiter.waitForEvent(ButtonClickEvent.class, e -> e.getMember().getId().equals(event.getMember().getId()) && e.getChannel().getId().equals(event.getChannel().getId()), e -> {
                        e.deferReply(true).queue();

                        if (e.getComponentId().equals(Buttons.BUTTON_MANUADD_ADMIN.getButtonId())) {
                            e.getChannel().deleteMessageById(success.getId()).queue();

                            try {
                                if (user.getRoles().contains(e.getGuild().getRoleById(Ranks.HELPER.getRankId())))
                                    e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.HELPER.getRankId())).queue();
                                if (user.getRoles().contains(e.getGuild().getRoleById(Ranks.MOD.getRankId())))
                                    e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.MOD.getRankId())).queue();
                                userData.setRank(Ranks.ADMIN.getId());
                                e.getGuild().addRoleToMember(user, e.getGuild().getRoleById(Ranks.ADMIN.getRankId())).queue();
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
                                            .setDescription(String.format(Responses.MANUADD_ADD_GROUPE.getContent(), user.getUser().getName(), Ranks.ADMIN.getRankName(), e.getMember().getUser().getName()))
                                            .build()
                            ).queue();
                            return;
                        }

                        if (e.getComponentId().equals(Buttons.BUTTON_MANUADD_MOD.getButtonId())) {
                            e.getChannel().deleteMessageById(success.getId()).queue();

                            try {
                                if (user.getRoles().contains(e.getGuild().getRoleById(Ranks.ADMIN.getRankId())))
                                    e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.ADMIN.getRankId())).queue();
                                if (user.getRoles().contains(e.getGuild().getRoleById(Ranks.HELPER.getRankId())))
                                    e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.HELPER.getRankId())).queue();
                                userData.setRank(Ranks.MOD.getId());
                                e.getGuild().addRoleToMember(user, e.getGuild().getRoleById(Ranks.MOD.getRankId())).queue();
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
                                            .setDescription(String.format(Responses.MANUADD_ADD_GROUPE.getContent(), user.getUser().getName(), Ranks.MOD.getRankName(), e.getMember().getUser().getName()))
                                            .build()
                            ).queue();
                            return;
                        }

                        if (e.getComponentId().equals(Buttons.BUTTON_MANUADD_HELPER.getButtonId())) {
                            e.getChannel().deleteMessageById(success.getId()).queue();

                            try {
                                if (user.getRoles().contains(e.getGuild().getRoleById(Ranks.ADMIN.getRankId())))
                                    e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.ADMIN.getRankId())).queue();
                                if (user.getRoles().contains(e.getGuild().getRoleById(Ranks.MOD.getRankId())))
                                    e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.MOD.getRankId())).queue();
                                userData.setRank(Ranks.HELPER.getId());
                                e.getGuild().addRoleToMember(user, e.getGuild().getRoleById(Ranks.HELPER.getRankId())).queue();
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
                                            .setDescription(String.format(Responses.MANUADD_ADD_GROUPE.getContent(), user.getUser().getName(), Ranks.HELPER.getRankName(), e.getMember().getUser().getName()))
                                            .build()
                            ).queue();
                            return;
                        }

                        if (e.getComponentId().equals(Buttons.BUTTON_MANUADD_PLAYER.getButtonId())) {
                            e.getChannel().deleteMessageById(success.getId()).queue();

                            try {
                                if (user.getRoles().contains(e.getGuild().getRoleById(Ranks.ADMIN.getRankId())))
                                    e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.ADMIN.getRankId())).queue();
                                if (user.getRoles().contains(e.getGuild().getRoleById(Ranks.MOD.getRankId())))
                                    e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.MOD.getRankId())).queue();
                                if (user.getRoles().contains(e.getGuild().getRoleById(Ranks.HELPER.getRankId())))
                                    e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.HELPER.getRankId())).queue();
                                userData.setRank(Ranks.PLAYER.getId());
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
                                            .setDescription(String.format(Responses.MANUADD_ADD_GROUPE.getContent(), user.getUser().getName(), Ranks.PLAYER.getRankName(), e.getMember().getUser().getName()))
                                            .build()
                            ).queue();
                            return;
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
