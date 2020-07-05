package Database;

import Data.UserShell;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUsers {
    Statement statement;
    Connection connection;

    public DBUsers(Connection connection) {
        try {
            this.connection = connection;
            this.statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String login(UserShell user){
        try {
            if (user.isExist()) {
                return authorization(statement, user.getLogin(), user.getPassword());
            } else {
                return registration(statement, user.getLogin(), user.getPassword());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return "1";
        }
    }

    public String registration(Statement statement, String login, String password) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select * from users where login = '" + login + "'");
        if (!resultSet.next()) {
            statement.executeUpdate("insert into users(login, password) values ('" + login + "', '" + password + "')");
            return "Регистрация прошла успешно.";
        } else return "Данный логин уже занят.";
    }

    public String authorization(Statement statement, String login, String password) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select * from users where login = '" + login + "'");

        if (!resultSet.next()) {
            return "Пользователя с таким логином не найдено.";
        } else {
            do {
                String pass = resultSet.getString("password");
                if (password.equals(pass)) {
                    return "Авторизация прошла успешно.";
                } else return "Введён неверный пароль.";
            } while (resultSet.next());
        }
    }
}

