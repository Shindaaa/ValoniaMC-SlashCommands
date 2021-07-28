package fr.shinda.shindapp.commands.moderation;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.enums.Colors;
import fr.shinda.shindapp.enums.Ranks;
import fr.shinda.shindapp.sql.SanctionData;
import fr.shinda.shindapp.sql.UserData;
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
        event.deferReply().queue();
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() >= 30) {

            Member user = event.getOption("user").getAsMember();

            if (user == null) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(Colors.MAIN.getHexCode())
                        .setDescription("La personne mentionné est introuvable ou renvoit une valeur null");
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            SanctionData sanctionData = new SanctionData(Main.getConnection(), user);

            if (!sanctionData.isStored()) {
                sanctionData.createData();
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Colors.MAIN.getHexCode())
                    .setAuthor("Historique de sanction de " + user.getUser().getName(), null, user.getUser().getAvatarUrl())
                    .setThumbnail(user.getUser().getAvatarUrl())
                    .setDescription("Voici la dernière sanction que `" + user.getUser().getName() + "` a reçu:\n\n`" + sanctionData.getLastSanction() + "` par `" + sanctionData.getLastModerator() + "` pour raison: `" + sanctionData.getLastReason() + "`")
                    .addField("Total Warn:", String.valueOf(sanctionData.getTotalWarn()), true)
                    .addField("Total Kick", String.valueOf(sanctionData.getTotalKick()), true)
                    .addField("Total Ban", String.valueOf(sanctionData.getTotalBan()), true);
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }

    }
}
