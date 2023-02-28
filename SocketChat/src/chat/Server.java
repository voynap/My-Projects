package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String,Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message) {
        for (String name : connectionMap.keySet()) {
            try {
                connectionMap.get(name).send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Сообщение не доставлено");
            }
        }
    }


    public static void main(String[] args)  {

        ConsoleHelper.writeMessage("Введите адрес порта");
        int port = ConsoleHelper.readInt();

        try ( ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Сервер запущен");
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {

            boolean conditions = false;
            Message message;
            do {
                connection.send(new Message(MessageType.NAME_REQUEST));

                message = connection.receive();
                if (message.getType() == MessageType.USER_NAME)
                    if (!message.getData().isEmpty())
                        if (!connectionMap.containsKey(message.getData()))
                            conditions = true;

            } while (!conditions);

            connectionMap.put(message.getData(), connection);
            connection.send(new Message(MessageType.NAME_ACCEPTED));
            return message.getData();

        }

        private void notifyUsers(Connection connection, String username) throws IOException {
            for (String name : connectionMap.keySet()) {
                if (!name.equals(username)) {
                    Message message = new Message(MessageType.USER_ADDED, name);
                    connection.send(message);
                }
            }
        }

        private void serverMainLoop(Connection connection, String username) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, username + ": " + message.getData()));
                } else {
                    ConsoleHelper.writeMessage("Произошла ошибка");
                }
            }
        }

        public void run() {
            SocketAddress sa = socket.getRemoteSocketAddress();

            ConsoleHelper.writeMessage("Установлено соединение с сервером " + sa.toString());

            String userName = null;

            try (Connection connection = new Connection(socket)) {

                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);


            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом");
            }

            if (userName != null) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }

        }

    }
}
