package chat.client;

import java.io.IOException;

public class ClientGuiController extends Client {

    private ClientGuiModel model = new ClientGuiModel();
    private ClientGuiView view = new ClientGuiView(this) ;

    public class GuiSocketThread extends SocketThread {
        @Override
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
//            super.processIncomingMessage(message);
        }

        @Override
        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
//            super.informAboutAddingNewUser(userName);
        }

        @Override
        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
//            super.informAboutDeletingNewUser(userName);
        }

        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            view.notifyConnectionStatusChanged(clientConnected);
//            super.notifyConnectionStatusChanged(clientConnected);
        }
    }


    @Override
    public void run() {
        getSocketThread().run();
    }

    @Override
    protected String getServerAddress() throws IOException {
        return view.getServerAddress();
    }

    @Override
    protected int getServerPort() throws IOException {
        return view.getServerPort();
    }

    @Override
    protected String getUserName() {
        return view.getUserName();
    }

    @Override
    protected SocketThread getSocketThread() {
        return new GuiSocketThread();
    }

    public ClientGuiModel getModel() {
        return model;
    }

    public static void main(String[] args) {
        ClientGuiController controller = new ClientGuiController();
        controller.run();
    }
}
