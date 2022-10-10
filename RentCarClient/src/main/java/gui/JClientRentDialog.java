package gui;

import util.ClientUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class JClientRentDialog extends JDialog {
    private JTextField dayRentTextField;
    private JTextField priceTextField;
    private int price;
    public JClientRentDialog(JFrame jFrame, ActionListener totalButtonListener, ActionListener rentButtonListener, String title, int price) {
        this.setModal(true);
        this.setTitle(title);
        this.price = price;
        JButton rentButton = new JButton("Rent");
        JButton totalButton = new JButton("Total");
        rentButton.addActionListener(rentButtonListener);
        totalButton.addActionListener(totalButtonListener);
        dayRentTextField = new JTextField();
        priceTextField = new JTextField();
        priceTextField.setEnabled(false);
        this.setLayout(new GridBagLayout());
        this.add(new JLabel("Days"), ClientUtil.createGridBagConstrains(0, 0, 0,0, 2, 1,0 ,0));
        this.add(dayRentTextField, ClientUtil.createGridBagConstrains(0, 0, 2,0, 2, 1,50 ,0));
        this.add(new JLabel("Price"), ClientUtil.createGridBagConstrains(0, 0, 0,1, 2, 1,0 ,0));
        this.add(priceTextField, ClientUtil.createGridBagConstrains(0, 0, 2,1, 2, 1,50 ,0));
        this.add(rentButton, ClientUtil.createGridBagConstrains(0, 0, 0,2, 2, 1,0 ,0));
        this.add(totalButton, ClientUtil.createGridBagConstrains(0, 0, 2,2, 2, 1,0 ,0));
        this.setSize(300,175);
        this.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2,
                Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2);
    }
    public void changePrice(){
        String days = dayRentTextField.getText();
        if (days.length() < 0) {
            JOptionPane.showMessageDialog(this, "Заполните все поля!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int intDays = 0;
        try {
            intDays = Integer.parseInt(days);
        } catch (Exception exception){
            JOptionPane.showMessageDialog(this, "Проверьте правильность полей!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (intDays <= 0){
            JOptionPane.showMessageDialog(this, "Проверьте правильность полей!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        priceTextField.setText(Integer.toString(intDays * price));
    }
    public int getDays(){
        String days = dayRentTextField.getText();
        if (days.length() < 0) {
            JOptionPane.showMessageDialog(this, "Заполните все поля!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        int intDays = 0;
        try {
            intDays = Integer.parseInt(days);
        } catch (Exception exception){
            JOptionPane.showMessageDialog(this, "Проверьте правильность полей!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        if (intDays <= 0){
            JOptionPane.showMessageDialog(this, "Проверьте правильность полей!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return intDays;
    }
}
