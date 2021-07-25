package fr.shinda.shindapp.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {

    private static Connection connection;
    private final int port;
    private final String urlBase, host, database, user, password;

    public SqlConnection(String urlBase, String host, int port, String database, String user, String password) {
        this.urlBase = urlBase;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public void connect() {

        try {

            if (connection != null && !connection.isClosed()) {
                return;
            }

            synchronized (this) {

                if (connection != null && !connection.isClosed()) {
                    return;
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(this.urlBase + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true&wait_timeout=86400&serverTimezone=CET", this.user, this.password);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void disconnect() {

        if (isConnected()) {

            try {

                connection.close();
                System.out.println("Successfully disconnected to SQL Database !");

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    public boolean isConnected() {
        return connection != null;
    }

    public static Connection getConnection() {
        return connection;
    }

}
