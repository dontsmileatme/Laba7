package Commands;

import Collection.HumanBeingCollection;
import Human.HumanBeing;

import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * Команда, очищающая коллекцию.
 */
public class Clear implements Commandable {
    private final String name = "clear";

    public Clear() {
        Command.regist(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String execute(String arg, HumanBeing human, String user) {
        if (!HumanBeingCollection.getCollection().isEmpty()) {
            long deleted = 0;
            if (HumanBeingCollection.getCollection().stream().anyMatch(h -> h.isOwner(user))) {
                deleted = HumanBeingCollection.getCollection().stream()
                        .filter(h -> h.isOwner(user))
                        .count();
                HumanBeingCollection.getCollection().removeIf(h -> (h.getOwner().equals(user)));

                return "Количество удалённых элементов из коллекции: " + deleted + "\n";
            } else return "В коллекции нет элементов, принадлежащих вам. \n";
        } else return "Коллекция пустая." + "\n";
    }

}
