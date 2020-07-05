package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBWorker {
    private final String URL = "jdbc:postgresql://pg:5432/studs";
    private final String USER = "s285709";
    private final String PASSWORD = "******";
    //private final String URL = "jdbc:postgresql://localhost:5432/studs";
    //private final String USER = "postgres";
    //private final String PASSWORD = "pang";
    private static Connection connection;
    private static Statement statement;
    private static DBUsers users;
    private static DBHumans humans;

    public DBWorker() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (connection != null) {
            System.out.println("Подключение с базой данных установлено.");
        } else {
            System.err.println("Не удалось установить соединение с базой данных.");
        }
        users = new DBUsers(connection);
        humans = new DBHumans(connection);
    }

    public static Connection getConnection() {
        return connection;
    }

    public static Statement getStatement() {
        return statement;
    }

    public static DBUsers getUsers() {
        return users;
    }

    public static DBHumans getHumans() {
        return humans;
    }
}
