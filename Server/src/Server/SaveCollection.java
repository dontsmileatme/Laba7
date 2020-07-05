package Server;

import Commands.Command;
import Database.DBHumans;
import Database.DBWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SaveCollection {
    public static void checkForCommand() {
        Thread backgroundReaderThread = new Thread(() -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
                while (!Thread.interrupted()) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.equalsIgnoreCase("save")) {
                        System.out.println("Идёт сохранение...");
                        saveCollection();
                        System.out.println("Коллекция сохранена в базе данных.");
                    }
                    if (line.equalsIgnoreCase("exit")) {
                        Command command = new Command("exit", null, null, null);
                        command.execute();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        backgroundReaderThread.setDaemon(true);
        backgroundReaderThread.start();
    }

    public static void saveCollection() {
        DBHumans humans = DBWorker.getHumans();
        humans.uploadCollection();
    }
}

