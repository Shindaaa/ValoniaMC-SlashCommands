package fr.shinda.shindapp.commands.suggestion;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.enums.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Collections;

public class SlashSuggestionAddCmd extends SlashCommand {

    EventWaiter waiter;
    String messageID;

    public SlashSuggestionAddCmd(EventWaiter waiter) {
        this.name = "suggestion-add";
        this.help = "Permet aux membres du discord de créer des suggestions.";
        this.botPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
        this.waiter = waiter;

        this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "content", "Ecrivez ici le contenu de votre suggestion.").setRequired(true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();

        String content = event.getOption("content").getAsString();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.MAIN.getHexCode())
                .setDescription("Récapitulatif du contenu de votre suggestion:\n\n" + content);
        event.getHook().sendMessageEmbeds(embed.build()).addActionRow(
                Button.primary("button.suggestion.add.confirm", "COnfirmer la suggestion")
        ).addActionRow(
                Button.secondary("button.suggestion.add.cancel", "Annuler")
        ).queue(success -> this.messageID = success.getId());

        waiter.waitForEvent(ButtonClickEvent.class, e -> e.getMember().getId().equals(event.getMember().getId()) && e.getChannel().getId().equals(event.getChannel().getId()), e -> {
            e.deferReply().queue();

            if (e.getComponentId().equals("button.suggestion.add.confirm")) {
                e.getChannel().deleteMessageById(messageID).queue();
            }

            if (e.getComponentId().equals("button.suggestion.add.cancel")) {
                e.getChannel().deleteMessageById(messageID).queue();
            }

        });
    }
}
