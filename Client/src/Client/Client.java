package Client;

import Authorization.Authorization;
import Authorization.Registration;
import ClientConnection.Connection;
import Controller.Controller;
import Controller.Validation;
import Data.CommandShell;
import Data.CommandShellsBundle;
import Data.CommandShellsCollection;
import Data.UserShell;
import Tools.Deserializer;
import Tools.Serializer;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client implements Runnable {
    public static Socket socket;
    Connection connection = new Connection();
    private static final int maxAttempts = 6;
    private static final int msToReconnect = 3000;
    private static int attempts = 0;
    static final int PORT = 1999;
    private static String login;
    private static String password;

    @Override
    public void run() {
        while (true) {
            try (Socket s = new Socket("localhost", PORT)) {
                socket = s;
                System.out.println("Подключение с сервером установлено.");
                while (this.isConnected()) {
                    userIdentification(socket);
                    this.begin(socket);
                }
            } catch (IOException e) {
                tryToConnect();
            }
        }
    }

    public void tryToConnect() {
        System.err.println("Ошибка подключения к серверу.");
        if (++attempts >= maxAttempts) {
            System.err.println("Превышено количество попыток подключения. Всем пока. :)");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
            System.exit(0);
        } else {
            System.err.println("Повторое подключение будет совершено через " + msToReconnect / 1000 + " секунды.");
            try {
                Thread.sleep(msToReconnect);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return (!socket.isClosed() && socket.isConnected());
    }

    public void userIdentification(Socket socket) throws IOException {
        Registration registration = new Registration();
        Authorization authorization = new Authorization();

        String response = "";
        System.out.println("Введите 'log', если вы — существующий пользователь,\n" +
                "иначе введите 'reg' для регистрации.");
        System.out.print("$ ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        if (input.equals("reg")) {
            registration.registration();
            UserShell UserToRegister = registration.getUserShell();
            response = authorization.signUp(UserToRegister.getLogin(), UserToRegister.getPassword());
        } else if (input.equals("log")) {
            registration.authorization();
            UserShell UserToLogin = registration.getUserShell();
            response = authorization.signIn(UserToLogin.getLogin(), UserToLogin.getPassword());
            this.login = UserToLogin.getLogin();
            this.password = UserToLogin.getPassword();
        } else userIdentification(socket);

        switch (response) {
            case "Авторизация прошла успешно.": {
                System.out.println("Авторизация прошла успешно.");
                break;
            }
            case "Неверный логин или пароль.": {
                System.out.println("Неверный логин или пароль. Повторите попытку.");
                userIdentification(socket);
                break;
            }
            case "Регистрация прошла успешно.": {
                System.out.println("Регистрация прошла успешно.");
                userIdentification(socket);
                break;
            }
            case "Пользователя с таким логином не найдено.": {
                System.out.println("Пользователя с таким логином не найдено.");
                userIdentification(socket);
                break;
            }
            case "Введён неверный пароль.": {
                System.out.println("Введён неверный пароль. Поторите попытку.");
                userIdentification(socket);
                break;
            }
            case "Данный логин уже занят.": {
                System.out.println("Данный логин уже занят. Поторите попытку.");
                userIdentification(socket);
                break;
            }
            case "1": {
                System.out.println("Произошла ошибка при подключении к базе данных.");
                userIdentification(socket);
                break;
            }
        }
    }

    public void begin(Socket socket) throws IOException {
        try {
            while (true) {
                System.out.println("Введите команду: ");
                System.out.print("$ ");
                Scanner scanner = new Scanner(System.in);
                String commandName = scanner.nextLine();
                if (!commandName.equals("")) {
                    Controller controller = new Controller();
                    if (controller.isCommandCorrect(commandName)) {
                        controller.getShell().setUser(login);
                        controller.getShell().setPassword(password);
                        if (!Validation.getIsScript()) {
                            if (send(controller, connection, socket)) break;
                        } else {
                            if (sendScript(connection, socket)) break;
                        }
                        if (receive(connection, socket)) break;
                    } else begin(socket);
                }
            }
        } catch (NoSuchElementException e) {
            System.exit(0);
        }
    }


    private boolean send(Controller controller, Connection connection, Socket socket) throws IOException {
        CommandShell shell = controller.getShell();
        if (Validation.isReadyForSend) {
            connection.write(Serializer.toSerialize(shell), socket);
            if (Connection.isConnected) System.out.println("Команда отправлена на сервер.");
            else return true;
        }
        return false;
    }

    private boolean sendScript(Connection connection, Socket socket) throws IOException {
        CommandShellsCollection shells = new CommandShellsCollection();
        CommandShellsBundle commandsBundle = new CommandShellsBundle();
        commandsBundle.setShellCollection(shells.getShellsCollection());
        if (Validation.isReadyForSend) {
            connection.write(Serializer.toSerialize(commandsBundle), socket);
            if (Connection.isConnected) {
                if (!(commandsBundle.getSize() == 0)) {
                    System.out.println("Команды, отправленные на сервер: ");
                    for (int i = 0; i < shells.getSize(); i++) {
                        System.out.println(shells.getShell(i).getCommandName());
                    }
                } else {
                    System.out.println("Скрипт пустой.");
                    return true;
                }
            } else return true;
        }
        Validation.setIsScript(false);
        shells.clearShellsCollection();
        return false;
    }

    private boolean receive(Connection connection, Socket socket) {
        byte[] inputtedBytes;
        inputtedBytes = connection.read(socket);
        if (inputtedBytes == null) return true;
        String answer = Deserializer.toDeserialize(inputtedBytes, String.class);
        if (answer != null) {
            System.out.println("Ответ от сервера: ");
            System.out.println(answer);
        } else return true;
        return false;
    }

    public static String getLogin() {
        return login;
    }

    public static String getPassword() {
        return password;
    }
}
