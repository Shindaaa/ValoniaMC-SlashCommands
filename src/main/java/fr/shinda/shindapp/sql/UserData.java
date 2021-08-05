package fr.shinda.shindapp.sql;

import fr.shinda.shindapp.enums.Ranks;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserData {

    private final Connection connection;
    private final Member user;

    public UserData(Connection connection, Member user) {
        this.connection = connection;
        this.user = user;
    }

    public boolean isStored() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_user_data WHERE user_id = ?");
        preparedStatement.setString(1, user.getId());
        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }

    public void createData(Guild guild) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO shp_user_data (user_id, staff_rank, isContributor, isBooster, isBot) VALUES (?, ?, ?, ?, ?)");
        preparedStatement.setString(1, user.getId());
        preparedStatement.setInt(2, Ranks.PLAYER.getId());
        preparedStatement.setBoolean(3, user.getRoles().contains(guild.getRoleById(Ranks.CONTRIBUTOR.getRankId())));
        preparedStatement.setBoolean(4, user.getRoles().contains(guild.getRoleById(Ranks.BOOSTER.getRankId())));
        preparedStatement.setBoolean(5, user.getUser().isBot());
        preparedStatement.execute();
    }

    public void setRank(int value) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE shp_user_data SET staff_rank = ? WHERE user_id = ?");
        preparedStatement.setInt(1, value);
        preparedStatement.setString(2, user.getId());
        preparedStatement.executeUpdate();
    }

    public int getRank() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_user_data WHERE user_id = ?");
        preparedStatement.setString(1, user.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        int rankId = 0;

        if (resultSet.next()) {
            rankId = resultSet.getInt("staff_rank");
        }

        return rankId;
    }

    public boolean isContributor() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_user_data WHERE user_id = ?");
        preparedStatement.setString(1, user.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean isContributor = false;

        if (resultSet.next()) {
            isContributor = resultSet.getBoolean("isContributor");
        }

        return isContributor;
    }

    public boolean isBooster() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_user_data WHERE user_id = ?");
        preparedStatement.setString(1, user.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean isBooster = false;

        if (resultSet.next()) {
            isBooster = resultSet.getBoolean("isBooster");
        }

        return isBooster;
    }

    public boolean isBot() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_user_data WHERE user_id = ?");
        preparedStatement.setString(1, user.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean isBot = false;

        if (resultSet.next()) {
            isBot = resultSet.getBoolean("isBot");
        }

        return isBot;
    }
}

