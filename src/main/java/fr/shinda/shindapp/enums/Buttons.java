package fr.shinda.shindapp.enums;

public enum Buttons {
    BUTTON_LINK_FILTER_TRUE("button.link-filter.true", "Activer la protection"),
    BUTTON_LINK_FILTER_FALSE("button.link-filter.false", "Désactiver la protection"),

    BUTTON_MANUADD_ADMIN("button.manuadd.admin", "Ajouter au groupe: [Administrateur]"),
    BUTTON_MANUADD_MOD("button.manuadd.mod", "Ajouter au groupe: [Modérateur]"),
    BUTTON_MANUADD_HELPER("button.manuadd.helper", "Ajouter au groupe: [Chat.Mod]"),
    BUTTON_MANUADD_PLAYER("button.manuadd.player", "Ajouter au groupe: [Joueur]"),

    BUTTON_SANCTION_CONFIRM("button.sanction.confirm", "Confirmer la sanction"),
    BUTTON_SUGGESTION_CONFIRM("button.suggestion.confirm", "Confirmer la suggestion"),

    BUTTON_GLOBAL_CANCEL("button.global.cancel", "Annuler");

    String buttonId;
    String buttonName;

    Buttons(String buttonId, String buttonName) {
        this.buttonId = buttonId;
        this.buttonName = buttonName;
    }

    public String getButtonId() {
        return buttonId;
    }

    public String getButtonName() {
        return buttonName;
    }
}
