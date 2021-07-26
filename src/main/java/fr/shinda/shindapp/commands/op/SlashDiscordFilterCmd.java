package fr.shinda.shindapp.commands.op;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.Main;
import fr.shinda.shindapp.enums.Colors;
import fr.shinda.shindapp.enums.Ranks;
import fr.shinda.shindapp.sql.GuildData;
import fr.shinda.shindapp.sql.UserData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class SlashDiscordFilterCmd extends SlashCommand {

    EventWaiter waiter;
    private String messageId;

    public SlashDiscordFilterCmd(EventWaiter waiter) {
        this.name = "discord-filter";
        this.help = "Permet d'activer ou de désactiver la filtration des invitations envoyées sur le discord.";
        this.botPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
        this.waiter = waiter;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();
        UserData authorData = new UserData(Main.getConnection(), event.getMember());

        if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Ranks.OP.getRankId())) || authorData.getRank() >= Ranks.ADMIN.getId()) {

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Colors.MAIN.getHexCode())
                    .setDescription("Vous êtes sûr de vouloir modifier les paramètres de protection contre les invitations vers d'autre discord ?");
            event.getHook().sendMessageEmbeds(embed.build()).addActionRow(
                    Button.success("button.discordfilter.true", "Activer la protection").withEmoji(Emoji.fromMarkdown("\uD83D\uDEE1️"))
            ).addActionRow(
                    Button.danger("button.discordfilter.false", "Désactiver la protection").withEmoji(Emoji.fromMarkdown("⚠️"))
            ).addActionRow(
                    Button.secondary("button.discordfilter.cancel", "Annuler").withEmoji(Emoji.fromMarkdown("↩️"))
            ).queue(success -> this.messageId = success.getId());

            waiter.waitForEvent(ButtonClickEvent.class, e -> e.getMember().getId().equals(event.getMember().getId()) && e.getChannel().getId().equals(event.getChannel().getId()), e -> {
                e.deferReply().queue();
                GuildData guildData = new GuildData(Main.getConnection(), e.getGuild());

                if (e.getComponentId().equals("button.discordfilter.true")) {
                    e.getChannel().deleteMessageById(messageId).queue();

                    if (!guildData.isStored()) {
                        guildData.createData();
                    }

                    guildData.setDiscordLinkFilter(true);

                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription(e.getMember().getUser().getName() + " vient d'`activer` la protection contre les invitations vers d'autre discord");
                    e.getHook().sendMessageEmbeds(waiterEmbed.build()).queue();
                }

                if (e.getComponentId().equals("button.discordfilter.false")) {
                    e.getChannel().deleteMessageById(messageId).queue();

                    if (!guildData.isStored()) {
                        guildData.createData();
                    }

                    guildData.setDiscordLinkFilter(false);

                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription(e.getMember().getUser().getName() + " vient de `désactiver` la protection contre les invitations vers d'autre discord");
                    e.getHook().sendMessageEmbeds(waiterEmbed.build()).queue();
                }

                if (e.getComponentId().equals("button.discordfilter.cancel")) {
                    e.getChannel().deleteMessageById(messageId).queue();

                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription(e.getMember().getUser().getName() + ", vous venez d'annuler la commande.");
                    e.getHook().sendMessageEmbeds(waiterEmbed.build()).queue();
                }

            });
        }
    }
}
