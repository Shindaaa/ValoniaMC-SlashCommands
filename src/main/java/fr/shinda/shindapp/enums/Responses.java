package fr.shinda.shindapp.enums;

public enum Responses {
    LINK_FILTER_WAITING("`%s` êtes vous sûr de vouloir modifier les paramètres de protection `contre les liens` sur le discord ?"),
    LINK_FILTER_ON("`%s` vient `d'activer` la protection du discord concernant `les liens envoyés`."),
    LINK_FILTER_OFF("`%s` vient de `désactiver` la protection du discord concernant `les liens envoyés`."),

    MANUADD_WAITING("`%s` veuillez choisir un groupe à ajouter à `%s`"),
    MANUADD_ADD_GROUPE("`%s` vient d'être ajouté au groupe `%s` par `%s`"),

    SYNC_ALL_CMD("Tout les membres du discord ont été synchronisés avec la base de données."),

    HISTORY_TITLE("Historique de sanction de %s"),
    HISTORY_DESC("Voici la dernière sanction que `%s` a reçu:\n\n`%s` par `%s` pour raison: `%s`"),
    HISTORY_TOTAL_BAN("Total Warn:"),
    HISTORY_TOTAL_KICK("Total Kick:"),
    HISTORY_TOTAL_WARN("Total Warn:"),

    SUGGESTION_WAITING_TITLE("Suggestion"),
    SUGGESTION_WAITING_DESC("Merci d'avoir voulu contribuer à l'amélioration du serveur !\n\nVoici un récapitulatif de ta suggestion:\nServeur: `%s`\nSuggestion: `%s`\n\nSi tout est bon pour toi clique sur le bouton `Confirmer`en dessous de ce message."),
    SUGGESTION_TITLE("Nouvelle suggestion !"),
    SUGGESTION_SERVER("Serveur:"),
    SUGGESTION_AUTHOR("Auteur:"),
    SUGGESTION_DESC("Description de la suggestion:"),
    SUGGESTION_FINAL("`%s`, votre suggestion a été envoyé dans le salon `%s`"),

    GLOBAL_CMD_CANCEL("`%s`, vous venez `d'annuler` la commande."),
    GLOBAL_SANCTION_WAITING("Confirmez vous la sanction suivante ?\n\n• Pseudo de la victime: `%s`\n• Type de sanction: `%s`\n• Raison: `%s`"),
    DEFAULT_REASON("No reason provided"),
    GLOBAL_SANCTION_REASON("`%s` à `%s %s` pour raison: `%s`"),
    GLOBAL_CMD_TIME_OUT("`%s`, vous avez trop long... la commande a été annulée.");

    String content;

    Responses(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
