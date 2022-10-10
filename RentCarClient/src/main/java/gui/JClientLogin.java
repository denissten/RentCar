package gui;
import client.RentClient;
import com.grpc.Server;
import util.ClientUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JClientLogin extends JPanel {
    private JTextField loginTextField;
    private JTextField passwordTextField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    private RentClient client;

    public JClientLogin(RentClient client){
        this.client = client;
        this.setSize(300, 300);

        loginTextField = new JTextField();
        passwordTextField = new JTextField();
        loginTextField.setColumns(12);
        passwordTextField.setColumns(12);
        loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginActionListener());
        registerButton = new JButton("Registration");
        registerButton.addActionListener(new RegistrationActionListener());
        statusLabel = new JLabel();

        this.setLayout(new GridBagLayout());
        this.add(new JLabel("Login "), ClientUtil.createGridBagConstrains(0, 0, 0,0, 2, 1,0 ,0));
        this.add(loginTextField, ClientUtil.createGridBagConstrains(0, 0, 2,0, 2, 1,0 ,0));

        this.add(new JLabel("Password "), ClientUtil.createGridBagConstrains(0, 0, 0,1, 2, 1,0 ,0));
        this.add(passwordTextField, ClientUtil.createGridBagConstrains(0, 0, 2,1, 2, 1, 0,0));
        this.add(loginButton, ClientUtil.createGridBagConstrains(0, 0, 0, 2, 2, 1,50,0));
        this.add(registerButton, ClientUtil.createGridBagConstrains(0, 0, 2, 2, 2, 1,30,0));
        this.add(statusLabel, ClientUtil.createGridBagConstrains(0, 0, 0, 3, 4, 1,0,0));
    }

    public void setStatusLabel(String text, Color color){
        statusLabel.setForeground(color);
        statusLabel.setText(text);
    }
    class RegistrationActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String login = loginTextField.getText();
            String password = passwordTextField.getText();
            if (login.length() == 0 || password.length() == 0){
                setStatusLabel("Введите логин и пароль.", Color.red);
                return;
            }
            String md5Password = ClientUtil.getMD5Hex(password);
            if (ClientUtil.registrationAcc(login, md5Password)){
                setStatusLabel("Аккаунт успешно зарегистрирован.", Color.GREEN);
                return;
            } else {
                setStatusLabel("Логин уже зарегистрирован.", Color.red);
                return;
            }
        }
    }

    class LoginActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String login = loginTextField.getText();
            String password = passwordTextField.getText();
            if (login.length() == 0 || password.length() == 0){
                setStatusLabel("Введите логин и пароль.", Color.red);
                return;
            }
            String md5Password = ClientUtil.getMD5Hex(password);
            client.connection(login, md5Password);
        }
    }
}
