package chat.client;

import chat.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {

    public class BotSocketThread extends SocketThread {

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (!message.contains(": ")) return;
            String[] nameAndMessage = message.split(": ");
            String msg = nameAndMessage[1];
            String frm = null;
            switch (msg) {
                case "дата":
                    frm = "d.MM.YYYY";
                    break;
                case "день":
                    frm = "d";
                    break;
                case "месяц":
                    frm = "MMMM";
                    break;
                case "год":
                    frm = "YYYY";
                    break;
                case "время":
                    frm = "H:mm:ss";
                    break;
                case "час":
                    frm = "H";
                    break;
                case "минуты":
                    frm = "m";
                    break;
                case "секунды":
                    frm = "s";
                    break;
            }
            if (frm != null) {
                SimpleDateFormat formatter = new SimpleDateFormat(frm);
                String answer = "Информация для " + nameAndMessage[0] + ": "
                        + formatter.format(Calendar.getInstance().getTime()) ;
                sendTextMessage(answer);

                //            super.processIncomingMessage(message);
            }
        }

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }
    }

    @Override
    protected SocketThread getSocketThread() { return new BotSocketThread(); }
    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        String username = "date_bot_" + (int) (Math.random() * 100);
        return username;
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
