package Authorization;

import Data.UserShell;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Registration {
    private String login;
    private String password;
    private UserShell userShell;

    public void registration() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Придумайте логин:");
        System.out.print("$ ");
        this.login = scanner.nextLine();
        if (this.login == null || this.login.equals("")) this.registration();
        System.out.println("Придумайте пароль:");
        System.out.print("$ ");
        String first = scanner.nextLine();
        if (first == null || first.equals("")) this.registration();
        System.out.println("Повторите введённый пароль:");
        System.out.print("$ ");
        String second = scanner.nextLine();
        if (!first.equals(second)) {
            System.out.println("Введённые пароли не совпадают. Попробуйте ещё раз.");
            this.registration();
        } else {
            this.password = toHashPassword(first);
            userShell = new UserShell(this.login, this.password, false);
        }
    }

    public void authorization() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите логин:");
        System.out.print("$ ");
        this.login = scanner.nextLine();
        System.out.println("Введите пароль:");
        System.out.print("$ ");
        String pass = scanner.nextLine();
        this.password = toHashPassword(pass);
        userShell = new UserShell(this.login, this.password, true);
    }

    public static String toHashPassword(String pass) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] bytes = messageDigest.digest(pass.getBytes());
            BigInteger big = new BigInteger(1, bytes);
            String hash = big.toString(16);
            while (hash.length() < 32) {
                hash = "0" + hash;
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public UserShell getUserShell() {
        return userShell;
    }
}
