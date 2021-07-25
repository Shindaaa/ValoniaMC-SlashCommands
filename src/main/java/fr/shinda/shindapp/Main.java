package fr.shinda.shindapp;

import fr.shinda.shindapp.client.Shindapp;
import fr.shinda.shindapp.sql.SqlConnection;
import fr.shinda.shindapp.sql.TableManager;
import fr.shinda.shindapp.utils.ConfigUtils;

import java.sql.Connection;

public class Main {

    private static Connection connection;
    private static SqlConnection sqlConnection;
    private static TableManager tableManager;
    private static Shindapp shindapp;

    public static void main(String[] args) {

        sqlConnection = new SqlConnection(ConfigUtils.getConfig("mysql.urlbase"), ConfigUtils.getConfig("mysql.host"), Integer.parseInt(ConfigUtils.getConfig("mysql.port")), ConfigUtils.getConfig("mysql.database"), ConfigUtils.getConfig("mysql.user"), ConfigUtils.getConfig("mysql.pass"));

        try {
            sqlConnection.connect();
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }

        connection = SqlConnection.getConnection();
        tableManager = new TableManager(connection);
        tableManager.checkAllDataTables();
        shindapp = new Shindapp();

        try {
            shindapp.initClient();
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }

    }

    public static Connection getConnection() {
        return connection;
    }
}
