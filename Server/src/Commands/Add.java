package Commands;

import Collection.HumanBeingCollection;
import Database.DBHumans;
import Database.DBWorker;
import Human.HumanBeing;

/**
 * Команда, добавляющая элемент в коллекцию.
 */
public class Add implements Commandable {
    private final String name = "add";

    public Add() {
        Command.regist(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String execute(String arg, HumanBeing human, String user) {
        human.setOwner(user);
        HumanBeingCollection.addHuman(human);
        DBHumans humans = DBWorker.getHumans();
        humans.uploadCollection();
        return "Человек [" + human.getName() + "] добавлен в коллекцию." + "\n";
    }
}
