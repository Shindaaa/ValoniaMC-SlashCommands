package fr.shinda.shindapp.commands.admin;

import com.jagrosh.jdautilities.command.SlashCommand;
import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.enums.Colors;
import fr.shinda.shindapp.enums.Errors;
import fr.shinda.shindapp.enums.Ranks;
import fr.shinda.shindapp.enums.Responses;
import fr.shinda.shindapp.sql.UserData;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SlashSyncAllCmd extends SlashCommand {

    public SlashSyncAllCmd() {
        this.name = "syncall";
        this.help = "Permet aux administrateurs de synchroniser tout les membres du discord avec la base de données";
        this.botPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        try {

            if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() == Ranks.OP.getId()) {

                event.getGuild().loadMembers().onSuccess(members -> {
                    for (Member member : members) {
                        UserData userData = new UserData(Main.getConnection(), member);
                        String name = member.getUser().getName();
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
                    }

                    event.getHook().sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Colors.MAIN.getHexCode())
                                    .setDescription(Responses.SYNC_ALL_CMD.getContent())
                                    .build()
                    ).queue();
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
