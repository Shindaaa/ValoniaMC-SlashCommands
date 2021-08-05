package fr.shinda.shindapp.enums;

public enum Errors {
    USER_NOT_FOUND("`%s`, l'utilisateur `%s` est introuvable ou renvoit une valeur `null`"),
    USER_IS_STAFF("`%s`, l'utilisateur `%s` est détecté commande membre du staff, vous ne pouvez donc pas le sanctionner."),
    USER_DOSENT_HAVE_PERMS("`%s`, vous n'avez pas l'authorisation d'utiliser cette commande."),
    SUGGESTION_CHANNEL_IS_NULL("Erreur: Le channel de suggestion par défaut renvoit une valeur null.\nContactez un `Administrateur`."),
    GLOBAL_TRY_CATCH_ERROR("Une erreur est survenue pendant l'utilisation de la commande !\nMerci de contacter `Shinda#0002`\n\nRapport:\nClass name: `%s`\nError message: ```%s```");

    String content;

    Errors(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
