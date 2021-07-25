package fr.shinda.shindapp.commands.op;

import com.jagrosh.jdautilities.command.SlashCommand;
import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.enums.Colors;
import fr.shinda.shindapp.enums.Ranks;
import fr.shinda.shindapp.sql.UserData;
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

        if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() == Ranks.OP.getId()) {

            StringBuilder string = new StringBuilder();

            event.getGuild().loadMembers().onSuccess(members -> {

                for (Member member : members) {
                    UserData userData = new UserData(Main.getConnection(), member);
                    String name = member.getUser().getName();
                    if (!userData.isStored()) {
                        userData.createData(event.getGuild());
                        string.append("`").append(name).append("`, ");
                    }
                }

                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(Colors.SUCCESS.getHexCode())
                        .setDescription("Tout les membres du discord ont été synchronisés avec la base de données.\n\nLes membres en question:\n\n" + string);

                event.getHook().sendMessageEmbeds(embed.build()).queue(success -> {}, throwable -> {
                    if (throwable instanceof IllegalArgumentException) {
                        EmbedBuilder embedException = new EmbedBuilder()
                                .setColor(Colors.SUCCESS.getHexCode())
                                .setDescription("Tout les membres du discord ont été synchronisés avec la base de données.");
                        event.getHook().sendMessageEmbeds(embedException.build()).queue();
                    }
                });

            });
        }
    }
}
