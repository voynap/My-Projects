package chat.client;


import chat.*;
import java.io.IOException;
import java.net.Socket;

public class Client {

    protected Connection connection;

    private volatile boolean clientConnected = false;

    public class SocketThread extends Thread {

        public void run() {
            try {
                String address = getServerAddress();
                int port = getServerPort();
                java.net.Socket socket = new Socket(address, port);
                Client.this.connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();

            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
                e.printStackTrace();
            }

        }

        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + " присоединился к чату. Поприветствуем.");
            Server.sendBroadcastMessage(new Message(MessageType.TEXT, userName + " присоединился к чату. Поприветствуем."));
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + " покинул чат.");
            Server.sendBroadcastMessage(new Message(MessageType.TEXT, userName + " покинул чат"));
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST) {
                    String username = getUserName();
                    connection.send(new Message(MessageType.USER_NAME, username));
                } else if (message.getType() == MessageType.NAME_ACCEPTED ) {

                    notifyConnectionStatusChanged(true);
                    return;
                } else {
                    throw new IOException("Unexpected chat.MessageType");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {

            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());
                } else if (message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(message.getData());
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());
                } else {
                    throw new IOException("Unexpected chat.MessageType");
                }
            }
        }

    }

    public void run() {

        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            ConsoleHelper.writeMessage("Непредвиденная ошибка. Поток был прерван");
            return;
        }

        if (clientConnected) {
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }

        while (clientConnected) {
            String text = ConsoleHelper.readString();
            if (text.equals("exit")) {
                clientConnected = false;
                break;
            }
            if (shouldSendTextFromConsole()) {
                sendTextMessage(text);
            }
        }
    }

    protected String getServerAddress() throws IOException {

        ConsoleHelper.writeMessage("Введите адрес сервера");
        String address = ConsoleHelper.readString();

        return address;
    }

    protected int getServerPort() throws IOException {

        ConsoleHelper.writeMessage("Введите порт сервера");
        int port = ConsoleHelper.readInt();

        return port;

    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Введите ваше имя");
        String username = ConsoleHelper.readString();
        return username;
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {

        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Произошла ошибка при отправке сообщения");
            clientConnected = false;

        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
