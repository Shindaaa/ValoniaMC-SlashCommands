package fr.shinda.shindapp.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigUtils {

    private static final Dotenv dotenv = Dotenv.load();

    public static String getConfig(String config) {
        return dotenv.get(config);
    }

}
