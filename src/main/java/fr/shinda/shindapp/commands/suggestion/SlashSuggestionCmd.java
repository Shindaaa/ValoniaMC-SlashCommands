package fr.shinda.shindapp.commands.suggestion;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.shinda.shindapp.enums.Buttons;
import fr.shinda.shindapp.enums.Colors;
import fr.shinda.shindapp.enums.Errors;
import fr.shinda.shindapp.enums.Responses;
import fr.shinda.shindapp.utils.ConfigUtils;
import io.sentry.Sentry;
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

        event.getHook().sendMessageEmbeds(
                new EmbedBuilder()
                        .setColor(Colors.MAIN.getHexCode())
                        .setAuthor(Responses.SUGGESTION_WAITING_TITLE.getContent(), null, event.getGuild().getSelfMember().getUser().getAvatarUrl())
                        .setDescription(String.format(Responses.SUGGESTION_WAITING_DESC.getContent(), server, content))
                        .build()
        ).addActionRow(
                Button.danger(Buttons.BUTTON_SUGGESTION_CONFIRM.getButtonId(), Buttons.BUTTON_SUGGESTION_CONFIRM.getButtonName()).withEmoji(Emoji.fromMarkdown("⚠️"))
        ).addActionRow(
                Button.secondary(Buttons.BUTTON_GLOBAL_CANCEL.getButtonId(), Buttons.BUTTON_GLOBAL_CANCEL.getButtonName()).withEmoji(Emoji.fromMarkdown("↩️"))
        ).queue(success -> {

            waiter.waitForEvent(ButtonClickEvent.class, e -> e.getMember().getId().equals(event.getMember().getId()) && e.getChannel().getId().equals(event.getChannel().getId()), e -> {
                e.deferReply().queue();
                MessageChannel waitingChannel = e.getGuild().getTextChannelById(ConfigUtils.getConfig("suggestion.channel.id"));

                if (e.getComponentId().equals(Buttons.BUTTON_SUGGESTION_CONFIRM.getButtonId())) {
                    e.getChannel().deleteMessageById(success.getId()).queue();

                    if (waitingChannel == null) {
                        e.getHook().sendMessageEmbeds(
                                new EmbedBuilder()
                                        .setColor(Colors.ERROR.getHexCode())
                                        .setDescription(Errors.SUGGESTION_CHANNEL_IS_NULL.getContent())
                                        .build()
                        ).queue();
                        return;
                    }

                    EmbedBuilder waiterEmbed = new EmbedBuilder()
                            .setColor(Colors.MAIN.getHexCode())
                            .setThumbnail(e.getMember().getUser().getAvatarUrl())
                            .setAuthor("Nouvelle suggestion !", null, e.getMember().getUser().getAvatarUrl())
                            .addField("Auteur:", e.getMember().getUser().getName(), true)
                            .addField("Serveur:", server, true)
                            .addField("Description de la suggestion:", content, false);

                    try {
                        waitingChannel.sendMessageEmbeds(
                                new EmbedBuilder()
                                        .setColor(Colors.MAIN.getHexCode())
                                        .setAuthor(Responses.SUGGESTION_TITLE.getContent(), null, e.getMember().getUser().getAvatarUrl())
                                        .addField(Responses.SUGGESTION_AUTHOR.getContent(), e.getMember().getUser().getName(), true)
                                        .addField(Responses.SUGGESTION_SERVER.getContent(), server, true)
                                        .addField(Responses.SUGGESTION_DESC.getContent(), content, false)
                                        .build()
                        ).queue(suggestion -> {
                            suggestion.addReaction("a:aayes:726735611414839356").queue();
                            suggestion.addReaction("a:aano:726735731153829928").queue();
                        });
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
                                    .setDescription(String.format(Responses.SUGGESTION_FINAL.getContent(), e.getMember().getUser().getName(), waitingChannel.getName()))
                                    .build()
                    ).queue();
                }

                if (e.getComponentId().equals(Buttons.BUTTON_GLOBAL_CANCEL.getButtonId())) {
                    e.getChannel().deleteMessageById(success.getId()).queue();
                }

            });

        });
    }
}
