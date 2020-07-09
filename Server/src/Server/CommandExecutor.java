package Server;

import Collection.HumanBeingCollection;
import Commands.Command;
import Data.CommandShell;
import Data.CommandShellsBundle;
import Data.UserShell;
import Database.DBUsers;
import Database.DBWorker;
import Human.HumanBeing;
import Tools.Deserializer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class CommandExecutor implements Callable<String> {
    int flag;
    byte[] bytes;

    public CommandExecutor(int flag, byte[] bytes) {
        this.flag = flag;
        this.bytes = bytes;
    }

    public String call() throws IOException {
        if (flag == 1) {
            return executeCommand();
        } else if (flag == 2) {
            return executeScript();
        } else if (flag == 3) {
            return login();
        } else if (flag == 4) {
            return id();
        } else return null;
    }

    public String executeCommand() throws IOException {
        CommandShell shell = Deserializer.toDeserialize(bytes, CommandShell.class);
        try {
            Statement statement = DBWorker.getStatement();
            ResultSet rs = statement.executeQuery("select * from users");
            boolean isAuthorized = false;
            while (rs.next()) {
                String name = rs.getString("login");
                String pass = rs.getString("password");
                if (shell.getPassword().equals(pass) && shell.getUser().equals(name)) isAuthorized = true;
            }
            if (isAuthorized) {
                Command command = new Command(shell.getCommandName(), shell.getCommandArg(), shell.getHuman(), shell.getUser());
                return command.execute();
            } else return "У вас нет прав на выполнение команды.";
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return "Не удалось выполнить команду из-за проблем с базой данных.";
        }
    }

    public String executeScript() throws IOException {
        String result = "";
        CommandShellsBundle shellsBundle = Deserializer.toDeserialize(bytes, CommandShellsBundle.class);
        for (int i = 0; i < shellsBundle.getSize(); i++) {
            CommandShell shell = shellsBundle.getShell(i);
            Command command = new Command(shell.getCommandName(), shell.getCommandArg(), shell.getHuman(), shell.getUser());
            result += command.execute();
        }
        return result;
    }

    public String login() {
        DBUsers users = DBWorker.getUsers();
        UserShell userShell = Deserializer.toDeserialize(bytes, UserShell.class);
        return users.login(userShell);
    }

    public String id() {
        ArrayList<String> uai = Deserializer.toDeserialize(bytes, ArrayList.class);
        String user = uai.get(0);
        long id = Long.parseLong(uai.get(1));
        boolean exist = false;
        boolean belong = false;

        for (HumanBeing h : HumanBeingCollection.getCollection()) {
            if (h.getId() == id) {
                exist = true;
                if (h.getOwner().equals(user)) belong = true;
            }
        }
        String res = "";
        if (exist && belong) res = "1";
        if (exist && !belong) res = "2";
        if (!exist) res = "3";

        return res;
    }
}
