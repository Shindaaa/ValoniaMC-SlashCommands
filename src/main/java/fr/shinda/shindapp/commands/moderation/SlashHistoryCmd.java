package fr.shinda.shindapp.commands.moderation;

import com.jagrosh.jdautilities.command.SlashCommand;
import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.enums.Colors;
import fr.shinda.shindapp.enums.Errors;
import fr.shinda.shindapp.enums.Ranks;
import fr.shinda.shindapp.enums.Responses;
import fr.shinda.shindapp.sql.SanctionData;
import fr.shinda.shindapp.sql.UserData;
import io.sentry.Sentry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

public class SlashHistoryCmd extends SlashCommand {

    public SlashHistoryCmd() {
        this.name = "history";
        this.help = "Permet aux staff de voir l'historique des sanctions d'un membre du discord";
        this.botPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;

        this.options = Collections.singletonList(
                new OptionData(OptionType.USER, "user", "le membre à qui vous voulez voir l'historique.").setRequired(true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply(true).queue();
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        try {

            if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() >= 30) {

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

                SanctionData sanctionData = new SanctionData(Main.getConnection(), user);

                try {

                    if (!sanctionData.isStored()) {
                        sanctionData.createData();
                    }

                    int totalBan = sanctionData.getTotalBan();
                    int totalKick = sanctionData.getTotalKick();
                    int totalWarn = sanctionData.getTotalWarn();
                    String lastSanction = sanctionData.getLastSanction();
                    String lastModerator = sanctionData.getLastModerator();
                    String lastReason = sanctionData.getLastReason();

                    event.getHook().sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Colors.MAIN.getHexCode())
                                    .setThumbnail(user.getUser().getAvatarUrl())
                                    .setAuthor(String.format(Responses.HISTORY_TITLE.getContent(), user.getUser().getName()), null, user.getUser().getAvatarUrl())
                                    .setDescription(String.format(Responses.HISTORY_DESC.getContent(), user.getUser().getName(), lastSanction, lastModerator, lastReason))
                                    .addField(Responses.HISTORY_TOTAL_WARN.getContent(), String.valueOf(totalWarn), true)
                                    .addField(Responses.HISTORY_TOTAL_KICK.getContent(), String.valueOf(totalKick), true)
                                    .addField(Responses.HISTORY_TOTAL_BAN.getContent(), String.valueOf(totalBan), true)
                                    .build()
                    ).queue();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Sentry.captureException(ex);
                    event.getHook().sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Colors.ERROR.getHexCode())
                                    .setDescription(String.format(Errors.GLOBAL_TRY_CATCH_ERROR.getContent(), ex.getClass().getName(), ex.getMessage()))
                                    .build()
                    ).queue();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Sentry.captureException(ex);
        }

    }
}
