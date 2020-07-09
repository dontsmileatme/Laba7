package Commands;

import Collection.HumanBeingCollection;
import Human.HumanBeing;

/**
 * Команда, удаляющая элемент коллекции по id.
 */
public class Remove_by_id implements Commandable {
    private final String name = "remove_by_id";

    public Remove_by_id() {
        Command.regist(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String execute(String arg, HumanBeing human, String user) {
        int size1 = HumanBeingCollection.getSize();
        HumanBeingCollection.getCollection().removeIf(p -> (p.getId() == Long.parseLong(arg) && p.getOwner().equals(user)));
        int size2 = HumanBeingCollection.getSize();
        if (size1 - size2 > 0) return "Человек с id '" + arg + "' был удалён." + "\n";
        else return "Человека с id '" + arg + "' не нашлось, либо же он вам не принадлежит." + "\n";
    }
}

