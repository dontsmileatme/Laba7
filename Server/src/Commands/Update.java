package Commands;

import Collection.HumanBeingCollection;
import Human.HumanBeing;

/**
 * Команда, обновляющая элемент коллекции по id.
 */
public class Update implements Commandable {
    private final String name = "update";

    public Update() {
        Command.regist(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String execute(String arg, HumanBeing human, String user) {

        long id = Long.parseLong(arg);
        boolean flag = false;
        human.setOwner(user);
        human.setId(id);
        HumanBeing humanToRemove = null;
        try {
            for (HumanBeing h : HumanBeingCollection.getCollection()) {
                if (h.getId() == id) {
                    flag = true;
                    humanToRemove = h;
                }
            }

            if (flag) {
                if (humanToRemove.getOwner().equals(user)) {
                    HumanBeingCollection.getCollection().remove(humanToRemove);
                    HumanBeingCollection.getCollection().add(human);
                    return "Человек с id — " + id + " заменён." + "\n";
                } else return "Человек с id — " + id + " вам не принадлежит." + "\n";
            } else return "Человека с id — " + id + " не нашлось." + "\n";
        } catch (NullPointerException e) {
            return "Человека с id — " + id + " не нашлось." + "\n";
        }
    }
}
