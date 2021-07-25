package fr.shinda.shindapp.commands.moderation;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.enums.Colors;
import fr.shinda.shindapp.enums.Ranks;
import fr.shinda.shindapp.sql.UserData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;
import java.util.List;

public class SlashKickCmd extends SlashCommand {

    EventWaiter waiter;
    private String messageID;

    public SlashKickCmd(EventWaiter waiter) {
        this.name = "ban";
        this.help = "Permet aux staff du discord de bannir une personne mentionné";
        this.botPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
        this.waiter = waiter;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.USER, "user", "Le membre à kick.").setRequired(true));
        data.add(new OptionData(OptionType.STRING, "reason", "La raison de cette sanction.").setRequired(false));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        UserData authorData = new UserData(Main.getConnection(), event.getMember());
        event.deferReply().queue();

        if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() >= Ranks.HELPER.getId()) {

            Member user = event.getOption("user").getAsMember();
            String reason;

            if (user == null) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(Colors.MAIN.getHexCode())
                        .setDescription("La personne mentionné est introuvable ou renvoit une valeur null");
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            if (event.getOption("reason") == null) {
                reason = "No reason provided";
            } else {
                reason = event.getOption("reason").getAsString();
            }

            UserData userData = new UserData(Main.getConnection(), user);

            if (userData.getRank() >= 30 || user.getRoles().contains(event.getGuild().getRoleById(Ranks.HELPER.getRankId()))
                || user.getRoles().contains(event.getGuild().getRoleById(Ranks.MOD.getRankId()))
                || user.getRoles().contains(event.getGuild().getRoleById(Ranks.ADMIN.getRankId()))) {
                EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Colors.MAIN.getHexCode())
                    .setDescription("Vous ne pouvez pas sanctionner un membre du staff.");
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Colors.MAIN.getHexCode())
                    .setDescription("Confirmez vous la sanction suivante ?\n\n• Pseudo de la victime: `" + user.getUser().getName() + "`\n• Type de sanction: `Kick`\n• Raison: `" + reason + "`");
            event.getHook().sendMessageEmbeds(embed.build()).addActionRow(
                    Button.danger("button.kick.confirm", "Confirmer la sanction").withEmoji(Emoji.fromMarkdown("⚠️"))
            ).addActionRow(
                    Button.secondary("button.kick.cancel", "Annuler").withEmoji(Emoji.fromMarkdown("↩️"))
            ).queue(success -> this.messageID = success.getId());

            waiter.waitForEvent(ButtonClickEvent.class, e -> e.getMember().getId().equals(event.getMember().getId()) && e.getChannel().getId().equals(event.getChannel().getId()), e -> {
                e.deferReply().queue();

                if (e.getComponentId().equals("button.kick.confirm")) {
                    e.getChannel().deleteMessageById(messageID).queue();

                    user.kick().reason("Kick by: " + e.getMember().getUser().getName() + " pour raison: " + reason).queue();

                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription("`" + user.getUser().getName() + "` vient d'être kick du discord par `" + e.getMember().getUser().getName() + "`");
                    e.getHook().sendMessageEmbeds(waiterEmbed.build()).queue();
                }

                if (e.getComponentId().equals("button.kick.cancel")) {
                    e.getChannel().deleteMessageById(messageID).queue();

                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription("Vous venez d'annuler la commande.");
                    e.getHook().sendMessageEmbeds(waiterEmbed.build()).queue();
                }
            });

        } else {
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Colors.MAIN.getHexCode())
                    .setDescription("Vous n'avez pas l'authorisation d'utiliser cette commande.");
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }

    }
}
