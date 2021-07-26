package fr.shinda.shindapp.sql;

import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildData {

    private Connection connection;
    private Guild guild;

    public GuildData(Connection connection, Guild guild) {
        this.connection = connection;
        this.guild = guild;
    }

    public boolean isStored() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_valonia_primary_data WHERE guild_id = ?");
            preparedStatement.setString(1, guild.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void createData() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO shp_valonia_primary_data (guild_id, discord_link_filter) VALUES (?, ?)");
            preparedStatement.setString(1, guild.getId());
            preparedStatement.setBoolean(2, false);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDiscordLinkFilter(boolean value) {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE shp_valonia_primary_data SET discord_link_filter = ? WHERE guild_id = ?");
            preparedStatement.setBoolean(1, value);
            preparedStatement.setString(2, guild.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean discordFilterIsActivated() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_valonia_primary_data WHERE guild_id = ?");
            preparedStatement.setString(1, guild.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isActivated = false;

            if (resultSet.next()) {
                isActivated = resultSet.getBoolean("discord_link_filter");
            }

            return isActivated;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
