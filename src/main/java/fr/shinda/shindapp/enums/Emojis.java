package fr.shinda.shindapp.enums;

public enum Emojis {
    ERROR(1, "", "", ""),
    SUCCESS(2, "", "", "");

    int id;
    String emojiCode;
    String emojiReact;
    String emojiLink;

    Emojis(int id, String emojiCode, String emojiReact, String emojiLink) {
        this.id = id;
        this.emojiCode = emojiCode;
        this.emojiReact = emojiReact;
        this.emojiLink = emojiLink;
    }

    public int getId() {
        return id;
    }

    public String getEmojiCode() {
        return emojiCode;
    }

    public String getEmojiReact() {
        return emojiReact;
    }

    public String getEmojiLink() {
        return emojiLink;
    }

}
