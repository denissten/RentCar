package client;

import com.grpc.*;
import gui.JClientAdmin;
import gui.JClientCustomer;
import gui.JClientLogin;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import util.ClientUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;

public class RentClient {
    private JFrame jFrame;
    private JClientAdmin jClientAdmin = null;
    private JClientCustomer jClientCustomer= null;
    private JClientLogin jClientLogin = null;
    private static Server.User user;

    public static void main(String[] args) {
        RentClient client = new RentClient();
    }

    public RentClient(){
        jFrame = new JFrame();
        jFrame.addWindowListener(new WindowsActionListener());
        jFrame.setTitle("RentCarClient");
        jClientLogin = new JClientLogin(this);
        jFrame.getContentPane().add(BorderLayout.CENTER, jClientLogin);
        jFrame.setSize(400, 400);
        jFrame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width/2 - jFrame.getWidth()/2,
                Toolkit.getDefaultToolkit().getScreenSize().height/2 - jFrame.getHeight()/2);
        jFrame.setVisible(true);
    }
    public void connection(String login, String password){
        setUser(Server.User.newBuilder().setLogin(login).setPassword(password).build());
        Thread thread = new Thread(new CheckThread(this));
        thread.start();
    }
    public void setUser(Server.User user){
        RentClient.user = user;
    }

    public static Server.User getUser() {
        return user;
    }

    public void switchWindow(){
        int clientId = user.getId();
        if (clientId < 0){
            ClientUtil.getAllCars();
            jFrame.setTitle("RentCarClient | Admin " + " | " + user.getLogin() + " | ID: " + clientId);
            jFrame.getContentPane().removeAll();
            jClientAdmin = new JClientAdmin(jFrame);
            jFrame.getContentPane().add(BorderLayout.CENTER, jClientAdmin);
            jClientAdmin.updateCarTable();
            jFrame.revalidate();
            return;
        }
        if (clientId >= 0){
            ClientUtil.getAllCars();
            jFrame.setTitle("RentCarClient | Customer " + " | " + user.getLogin() + " | ID: " + clientId);
            jFrame.getContentPane().removeAll();
            jClientCustomer = new JClientCustomer(jFrame);
            jFrame.getContentPane().add(BorderLayout.CENTER, jClientCustomer);
            jClientCustomer.updateCarTable();
            jFrame.revalidate();
            return;
        }
    }
    public void update(){
        ClientUtil.getCarsHaspMap().clear();
        ClientUtil.getAllCars();
        int clientId = user.getId();
        if (clientId == 0){
            jClientAdmin.updateCarTable();
            jFrame.revalidate();
            return;
        }
        if (clientId == 1){
            jClientCustomer.updateCarTable();
            jFrame.revalidate();
            return;
        }
    }

    class CheckThread implements Runnable{
        private  RentClient rentClient;
        private RentCarGrpc.RentCarBlockingStub stub;
        CheckThread(RentClient rentClient){
            this.rentClient = rentClient;
        }

        @Override
        public void run() {
            ManagedChannel channel = ManagedChannelBuilder.forTarget(ClientUtil.ADRESS).usePlaintext()
                    .build();
            stub = RentCarGrpc.newBlockingStub(channel);
            Iterator<Server.Response> responseIterator = stub.connect(user);

            boolean flag = true;
            while (flag){
                if (responseIterator.hasNext()){
                    Server.Response response = responseIterator.next();
                    switch (response.getHeader()){
                        case "LoginError":
                            jClientLogin.setStatusLabel("Неверный логин или пароль.", Color.red);
                            flag = false;
                            channel.shutdownNow();
                            break;
                        case "Connected":
                            int id = Integer.parseInt(response.getData());
                            String login = user.getLogin();
                            String password = user.getPassword();
                            setUser(Server.User.newBuilder().setId(id).setLogin(login)
                                    .setPassword(password).build());
                            rentClient.switchWindow();
                            break;
                        case "Update":
                            rentClient.update();
                            break;
                        case "Disconnect":
                            flag = false;
                            channel.shutdownNow();
                            break;
                        default:
                            break;
                    }
                }

            }
        }
    }
    class WindowsActionListener implements WindowListener{

        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (user != null)
                ClientUtil.disconnect(user.getId());
            System.exit(1);
        }

        @Override
        public void windowClosed(WindowEvent e) {
            System.exit(1);
        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }
}
