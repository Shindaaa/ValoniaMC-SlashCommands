package fr.shinda.shindapp.commands.op;

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

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SlashManuaddCmd extends SlashCommand {

    EventWaiter waiter;
    private String messageId;

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
        event.deferReply().queue();
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() >= Ranks.ADMIN.getId()) {

            Member user = event.getOption("user").getAsMember();

            if (user == null) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(Colors.ERROR.getHexCode())
                        .setDescription("La personne mentionné est introuvable ou renvoit une valeur null.");
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            UserData userData = new UserData(Main.getConnection(), user);

            if (!userData.isStored()) {
                userData.createData(event.getGuild());
            }

            EmbedBuilder embed = new EmbedBuilder().setColor(Colors.MAIN.getHexCode()).setDescription("Veuillez choisir un groupe à ajouter à `" + user.getUser().getName() + "`");
            event.getHook().sendMessageEmbeds(embed.build())
                    .addActionRow(
                            Button.danger("button.manuadd.admin", "Ajouter au groupe: [Administrateur]").withEmoji(Emoji.fromMarkdown("⚠️"))
                    ).addActionRow(
                            Button.primary("button.manuadd.mod", "Ajouter au groupe: [Modérateur]").withEmoji(Emoji.fromMarkdown("⚠️"))
                    ).addActionRow(
                            Button.primary("button.manuadd.helper", "Ajouter au groupe: [Chat.Mod]").withEmoji(Emoji.fromMarkdown("⚠️"))
                    ).addActionRow(
                            Button.secondary("button.manuadd.cancel", "Annuler").withEmoji(Emoji.fromMarkdown("↩️"))
            ).queue(success -> this.messageId = success.getId());

            waiter.waitForEvent(ButtonClickEvent.class, e -> e.getMember().getId().equals(event.getMember().getId()) && e.getChannel().getId().equals(event.getChannel().getId()), e -> {
                e.deferReply().queue();

                if (e.getComponentId().equals("button.manuadd.admin")) {
                    e.getChannel().deleteMessageById(messageId).queue();

                    try {
                        userData.setRank(50);
                        e.getGuild().addRoleToMember(user, e.getGuild().getRoleById(Ranks.ADMIN.getRankId())).queue();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    try {
                        e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.HELPER.getRankId())).queue();
                        e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.MOD.getRankId())).queue();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription(e.getMember() + ", vous venez d'ajouter `" + user.getUser().getName() + "` au groupe: `" + Ranks.ADMIN.getRankName() + "`");
                    e.getHook().sendMessageEmbeds(waiterEmbed.build()).queue();
                    return;
                }

                if (e.getComponentId().equals("button.manuadd.mod")) {
                    e.getChannel().deleteMessageById(messageId).queue();

                    try {
                        userData.setRank(40);
                        e.getGuild().addRoleToMember(user, e.getGuild().getRoleById(Ranks.MOD.getRankId())).queue();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    try {
                        e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.ADMIN.getRankId())).queue();
                        e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.HELPER.getRankId())).queue();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription(e.getMember() + ", vous venez d'ajouter `" + user.getUser().getName() + "` au groupe: `" + Ranks.MOD.getRankName() + "`");
                    e.getHook().sendMessageEmbeds(waiterEmbed.build()).queue();
                    return;
                }

                if (e.getComponentId().equals("button.manuadd.helper")) {
                    e.getChannel().deleteMessageById(messageId).queue();

                    try {
                        userData.setRank(30);
                        e.getGuild().addRoleToMember(user, e.getGuild().getRoleById(Ranks.HELPER.getRankId())).queue();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    try {
                        e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.ADMIN.getRankId())).queue();
                        e.getGuild().removeRoleFromMember(user, e.getGuild().getRoleById(Ranks.MOD.getRankId())).queue();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription(e.getMember() + ", vous venez d'ajouter `" + user.getUser().getName() + "` au groupe: `" + Ranks.HELPER.getRankName() + "`");
                    e.getHook().sendMessageEmbeds(waiterEmbed.build()).queue();
                    return;
                }

                if (e.getComponentId().equals("button.manuadd.cancel")) {
                    e.getChannel().deleteMessageById(messageId).queue();
                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription(e.getMember() + ", vous venez d'annuler la commande.");
                    e.getHook().sendMessageEmbeds(waiterEmbed.build()).queue();
                }

            }, 3, TimeUnit.MINUTES, () -> event.getHook().sendMessage(event.getMember() + ", vous avez été trop lent.").queue());
        }
    }
}
