package fr.shinda.shindapp.sql;

import net.dv8tion.jda.api.entities.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SanctionData {

    private Connection connection;
    private Member user;

    public SanctionData(Connection connection, Member user) {
        this.connection = connection;
        this.user = user;
    }

    public boolean isStored() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_sanction_data WHERE user_id = ?");
            preparedStatement.setString(1, user.getId());
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

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO shp_sanction_data (user_id, last_sanction, last_moderator, last_reason, total_kick, total_warn, total_ban) VALUES (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, user.getId());
            preparedStatement.setString(2, "---");
            preparedStatement.setString(3, "---");
            preparedStatement.setString(4, "---");
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, 0);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addWarn() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE shp_sanction_data SET total_warn = total_warn + ? WHERE user_id = ?");
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, user.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addKick() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE shp_sanction_data SET total_kick = total_kick + ? WHERE user_id = ?");
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, user.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBan() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE shp_sanction_data SET total_ban = total_ban + ? WHERE user_id = ?");
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, user.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSacntionContent(String sanction, String reason, Member moderator) {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE shp_sanction_data SET last_sanction = ? WHERE user_id = ?");
            preparedStatement.setString(1, sanction);
            preparedStatement.setString(2, user.getId());
            preparedStatement.executeUpdate();

            PreparedStatement preparedStatement1 = connection.prepareStatement("UPDATE shp_sanction_data SET last_reason = ? WHERE user_id = ?");
            preparedStatement1.setString(1, reason);
            preparedStatement1.setString(2, user.getId());
            preparedStatement1.executeUpdate();

            PreparedStatement preparedStatement2 = connection.prepareStatement("UPDATE shp_sanction_data SET last_moderator = ? WHERE user_id = ?");
            preparedStatement2.setString(1, moderator.getUser().getName());
            preparedStatement2.setString(2, user.getId());
            preparedStatement2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotalWarn() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_sanction_data WHERE user_id = ?");
            preparedStatement.setString(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            int totalWarn = 0;

            if (resultSet.next()) {
                totalWarn = resultSet.getInt("total_warn");
            }

            return totalWarn;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalKick() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_sanction_data WHERE user_id = ?");
            preparedStatement.setString(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            int totalKick = 0;

            if (resultSet.next()) {
                totalKick = resultSet.getInt("total_kick");
            }

            return totalKick;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalBan() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_sanction_data WHERE user_id = ?");
            preparedStatement.setString(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            int totalBan = 0;

            if (resultSet.next()) {
                totalBan = resultSet.getInt("total_ban");
            }

            return totalBan;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String getLastSanction() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_sanction_data WHERE user_id = ?");
            preparedStatement.setString(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            String lastSanction = "---";

            if (resultSet.next()) {
                lastSanction = resultSet.getString("last_sanction");
            }

            return lastSanction;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "---";
    }

    public String getLastReason() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_sanction_data WHERE user_id = ?");
            preparedStatement.setString(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            String lastReason = "---";

            if (resultSet.next()) {
                lastReason = resultSet.getString("last_reason");
            }

            return lastReason;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "---";
    }

    public String getLastModerator() {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shp_sanction_data WHERE user_id = ?");
            preparedStatement.setString(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            String lastModerator = "---";

            if (resultSet.next()) {
                lastModerator = resultSet.getString("last_moderator");
            }

            return lastModerator;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "---";
    }

}
