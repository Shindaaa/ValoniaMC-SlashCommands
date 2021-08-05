package fr.shinda.shindapp;

import fr.shinda.shindapp.client.Shindapp;
import fr.shinda.shindapp.sql.SqlConnection;
import fr.shinda.shindapp.sql.TableManager;
import fr.shinda.shindapp.utils.ConfigUtils;
import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    private static Connection connection;

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException, ClassNotFoundException, LoginException, InterruptedException {

        SqlConnection sqlConnection = new SqlConnection(ConfigUtils.getConfig("mysql.urlbase"), ConfigUtils.getConfig("mysql.host"), Integer.parseInt(ConfigUtils.getConfig("mysql.port")), ConfigUtils.getConfig("mysql.database"), ConfigUtils.getConfig("mysql.user"), ConfigUtils.getConfig("mysql.pass"));

        Sentry.init(sentryOptions -> {
            sentryOptions.setDsn(ConfigUtils.getConfig("sentry.dsn"));
            sentryOptions.setTracesSampleRate(1.0);
            sentryOptions.setDebug(true);
        });

        logger.info("Connecting to database....");

        try {
            sqlConnection.connect();
            logger.info("Connected !");
        } catch (Exception e) {
            logger.error("Unable to connect to Database! Shutting down ValoniaMC-BOT.");
            Sentry.captureException(e);
            e.printStackTrace();
        }

        connection = SqlConnection.getConnection();
        logger.info("Synchronising all MySQL Tables...");

        try {
            TableManager tableManager = new TableManager(connection);
            tableManager.checkAllDataTables();
            logger.info("Synchronization complete !");
        } catch (Exception e) {
            logger.error("An error occur when trying to synchronise all MySQL Tables.");
            Sentry.captureException(e);
            e.printStackTrace();
        }

        logger.info("Connecting to JDA...");

        try {
            Shindapp shindapp = new Shindapp();
            shindapp.initClient();
        } catch (Exception e) {
            logger.error("An error occur when trying to connect to JDA.");
            Sentry.captureException(e);
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
