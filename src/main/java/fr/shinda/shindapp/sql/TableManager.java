package fr.shinda.shindapp.sql;

import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TableManager {

    private final Connection connection;

    public TableManager(Connection connection) {
        this.connection = connection;
    }

    private void createGuildDataTable() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE shp_valonia_primary_data (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, guild_id VARCHAR(255), discord_link_filter BOOLEAN, debug_event BOOLEAN)");
        preparedStatement.execute();
        preparedStatement.close();
        LoggerFactory.getLogger(TableManager.class).info("Creating shp_valonia_primary_data table on MySQL Database !");
    }

    private void createUserDataTable() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE shp_user_data (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, user_id VARCHAR(255), staff_rank INT(11), isContributor BOOLEAN, isBooster BOOLEAN, isBot BOOLEAN)");
        preparedStatement.execute();
        preparedStatement.close();
        LoggerFactory.getLogger(TableManager.class).info("Creating shp_user_data table on MySQL Database !");
    }

    private void createSactionDataTable() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE shp_sanction_data (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, user_id VARCHAR(255), last_sanction VARCHAR(255), last_moderator VARCHAR(255), last_reason VARCHAR(255), total_kick INT(11), total_warn INT(11), total_ban INT(11))");
        preparedStatement.execute();
        preparedStatement.close();
        LoggerFactory.getLogger(TableManager.class).info("Creating shp_user_sanction_data table on MySQL Database !");
    }

    private boolean guildDataTableExist() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT table_name FROM information_schema.TABLES WHERE table_schema = 'shindapp' AND table_name = 'shp_valonia_primary_data'");
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            LoggerFactory.getLogger(TableManager.class).info("shp_valonia_primary_data table already exist !");
            return true;
        }

        return false;
    }

    private boolean userDataTableExist() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT table_name FROM information_schema.TABLES WHERE table_schema = 'shindapp' AND table_name = 'shp_user_data'");
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            LoggerFactory.getLogger(TableManager.class).info("shp_user_data table already exist !");
            return true;
        }

        return false;
    }

    private boolean sanctionDataTableExist() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT table_name FROM information_schema.TABLES WHERE table_schema = 'shindapp' AND table_name = 'shp_sanction_data'");
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            LoggerFactory.getLogger(TableManager.class).info("shp_user_sanction_data table already exist !");
            return true;
        }

        return false;
    }

    public void checkAllDataTables() throws SQLException {

        if (!guildDataTableExist()) {
            createGuildDataTable();
        }

        if (!userDataTableExist()) {
            createUserDataTable();
        }

        if (!sanctionDataTableExist()) {
            createSactionDataTable();
        }
    }

}
