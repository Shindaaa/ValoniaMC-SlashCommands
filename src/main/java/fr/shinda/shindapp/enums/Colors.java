package fr.shinda.shindapp.enums;

public enum Colors {
    MAIN(1, 0xfc0052),
    ERROR(2, 0xff0000),
    SUCCESS(3, 0x23ffb6);

    int id;
    int HexCode;

    Colors(int id, int hexCode) {
        this.id = id;
        this.HexCode = hexCode;
    }

    public int getId() {
        return id;
    }

    public int getHexCode() {
        return HexCode;
    }

}
