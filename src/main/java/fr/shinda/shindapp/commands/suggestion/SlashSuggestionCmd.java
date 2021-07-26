package fr.shinda.shindapp.commands.suggestion;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.enums.Colors;
import fr.shinda.shindapp.utils.ConfigUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;
import java.util.List;

public class SlashSuggestionCmd extends SlashCommand {

    EventWaiter waiter;
    String messageID;

    public SlashSuggestionCmd(EventWaiter waiter) {
        this.name = "suggestion";
        this.help = "Permet aux membres du discord de créer des suggestions.";
        this.botPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
        this.waiter = waiter;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "server", "Le serveur qui concerne votre suggestion.").setRequired(true));
        data.add(new OptionData(OptionType.STRING, "content", "Le contenu de votre suggestion.").setRequired(true));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();

        String server = event.getOption("server").getAsString();
        String content = event.getOption("content").getAsString();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.MAIN.getHexCode())
                .setAuthor("Suggestion", null, event.getMember().getUser().getAvatarUrl())
                .setDescription("Merci d'avoir voulu contribuer à l'amélioration du serveur !\n\nVoici un récapitulatif de ta suggestion:\nServeur: `" + server + "`\nSuggestion: `" + content + "`\n\nSi tout est bon pour toi clique sur le bouton `Confirmer`en dessous de ce message.");
        event.getHook().sendMessageEmbeds(embed.build()).addActionRow(
                Button.danger("button.suggestion.add.confirm", "Confirmer la suggestion").withEmoji(Emoji.fromMarkdown("⚠️"))
        ).addActionRow(
                Button.secondary("button.suggestion.add.cancel", "Annuler").withEmoji(Emoji.fromMarkdown("↩️"))
        ).queue(success -> this.messageID = success.getId());

        waiter.waitForEvent(ButtonClickEvent.class, e -> e.getMember().getId().equals(event.getMember().getId()) && e.getChannel().getId().equals(event.getChannel().getId()), e -> {
            e.deferReply().queue();
            MessageChannel waitingChannel = e.getGuild().getTextChannelById(ConfigUtils.getConfig("suggestion.channel.id"));

            if (e.getComponentId().equals("button.suggestion.add.confirm")) {
                e.getChannel().deleteMessageById(messageID).queue();

                if (waitingChannel == null) {
                    EmbedBuilder waitingChannelIsNull = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setDescription("Erreur: Le channel de suggestion par défaut renvoit une valeur null.\nContactez un `Administrateur`.");
                    e.getHook().sendMessageEmbeds(waitingChannelIsNull.build()).queue();
                    return;
                }

                EmbedBuilder waiterEmbed = new EmbedBuilder()
                        .setColor(Colors.MAIN.getHexCode())
                        .setThumbnail(e.getMember().getUser().getAvatarUrl())
                        .setAuthor("Nouvelle suggestion !", null, e.getMember().getUser().getAvatarUrl())
                        .addField("Auteur:", e.getMember().getUser().getName(), true)
                        .addField("Serveur:", server, true)
                        .addField("Description de la suggestion:", content, false);
                waitingChannel.sendMessageEmbeds(waiterEmbed.build()).queue();

                EmbedBuilder waiterEmbedFinal = new EmbedBuilder()
                        .setColor(Colors.MAIN.getHexCode())
                        .setDescription(e.getMember().getUser().getName() + ", votre suggestion a été envoyé dans le salon `" + waitingChannel.getName() + "`");
                e.getHook().sendMessageEmbeds(waiterEmbedFinal.build()).queue();
            }

            if (e.getComponentId().equals("button.suggestion.add.cancel")) {
                e.getChannel().deleteMessageById(messageID).queue();
            }

        });
    }
}
