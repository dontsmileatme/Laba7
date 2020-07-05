package Data;

import java.io.Serializable;

public class UserShell implements Serializable {
    private static final long serialVersionUID = -2219888957938979756L;
    private String login = null;
    private String password = null;
    private boolean isExist; //true - авторизация, false - регистрация


    public UserShell(String login, String password, boolean isExist) {
        this.login = login;
        this.password = password;
        this.isExist = isExist;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean isExist() {
        return isExist;
    }
}
