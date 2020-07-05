package Authorization;

import Client.Client;
import ClientConnection.Connection;
import Data.UserShell;
import Tools.Deserializer;
import Tools.Serializer;

import java.io.IOException;
import java.net.Socket;

public class Authorization {
    public static String user;
    public static String password;
    Connection connection = new Connection();
    Socket socket = Client.socket;

    public String signIn(String user, String password) throws IOException {
        Authorization.user = user;
        Authorization.password = password;
        UserShell userShell = new UserShell(user, password, true);
        connection.write(Serializer.toSerialize(userShell), socket);
        byte[] bytes = connection.read(socket);
        String response = Deserializer.toDeserialize(bytes, String.class);
        return response;
    }

    public String signUp(String user, String password) throws IOException {
        Authorization.user = user;
        Authorization.password = password;
        UserShell userShell = new UserShell(user, password, false);
        connection.write(Serializer.toSerialize(userShell), socket);
        byte[] bytes = connection.read(socket);
        String response = Deserializer.toDeserialize(bytes, String.class);
        return response;
    }
}
