package Commands;

import Human.HumanBeing;

import java.io.IOException;

/**
 * Команда, исполняющая команды, прописанные в скрипте.
 */
public class Execute_script implements Commandable {
    private final String name = "execute_script";

    public Execute_script() {
        Command.regist(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String execute(String arg, HumanBeing human, String user) throws IOException {
        return null;
    }
}
