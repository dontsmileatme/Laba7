package Commands;

import Human.HumanBeing;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Invoker-класс, управляющий командами.
 */
public class Command {
    private static Map<String, Commandable> commands = new TreeMap<>();
    private String name;
    private String arg;
    private HumanBeing human;
    private String user;

    public Command(String name, String arg, HumanBeing human, String user) {
        this.name = name;
        this.arg = arg;
        this.human = human;
        this.user = user;
    }

    public static Map<String, Commandable> getCommands() {
        return commands;
    }

    public static void regist(String commandName, Commandable command) {
        commands.put(commandName, command);
    }

    public String execute() throws IOException {
        Commandable command = commands.get(name);
        return command.execute(arg, human, user);
    }
}
