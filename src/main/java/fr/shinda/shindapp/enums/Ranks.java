package fr.shinda.shindapp.enums;

public enum Ranks {
    OP(60, "786599383046946816", "OP"),
    ADMIN(50, "837701463446519880", "Administrateur"),
    MOD(40, "837699681340358686", "Modérateur"),
    HELPER(30, "837699697774821397", "Chat.Mod"),
    CONTRIBUTOR(4, "839803993228312587", "Donateur"),
    BOOSTER(3, "755753152317489233", "Nitro-Booster"),
    BETA(2, "861299031288578068", "Beta"),
    PLAYER(1, null, "Joueur");

    int id;
    String rankId;
    String rankName;

    Ranks(int id, String rankId, String rankName) {
        this.id = id;
        this.rankId = rankId;
        this.rankName = rankName;
    }

    public int getId() {
        return id;
    }

    public String getRankId() {
        return rankId;
    }

    public String getRankName() {
        return rankName;
    }

}
