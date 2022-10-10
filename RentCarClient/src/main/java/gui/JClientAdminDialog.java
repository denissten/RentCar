package gui;
import util.ClientUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class JClientAdminDialog extends JDialog {
    private JTextField modelTextField;
    private JTextField mileageTextField;
    private JTextField conditionTextField;
    private JTextField priceTextField;
    public JClientAdminDialog(JFrame jFrame, DefaultTableModel jTable, ActionListener actionListener, String buttonName, int row){
        super(jFrame);
        this.setModal(true);
        this.setLayout(new GridBagLayout());
        JLabel modelLabel = new JLabel("Model");
        JLabel mileageLabel = new JLabel("Mileage");
        JLabel conditionLabel = new JLabel("Condition");
        JLabel priceLabel = new JLabel("Price");
        modelTextField = new JTextField(row != -1 ? (String) jTable.getValueAt(row, 1) : "");
        mileageTextField = new JTextField(row != -1 ? (String) jTable.getValueAt(row, 2) : "");
        conditionTextField = new JTextField(row != -1 ? (String) jTable.getValueAt(row, 3) : "");
        priceTextField = new JTextField(row != -1 ? (String) jTable.getValueAt(row, 4) : "");
        JButton saveEditButton = new JButton(buttonName);
        saveEditButton.addActionListener(actionListener);
        this.add(modelLabel, ClientUtil.createGridBagConstrains(0, 0, 0,0, 2, 1,0 ,0));
        this.add(mileageLabel, ClientUtil.createGridBagConstrains(0, 0, 0,1, 2, 1,0 ,0));
        this.add(conditionLabel, ClientUtil.createGridBagConstrains(0, 0, 0,2, 2, 1,0 ,0));
        this.add(priceLabel, ClientUtil.createGridBagConstrains(0, 0, 0,3, 2, 1,0 ,0));
        this.add(modelTextField, ClientUtil.createGridBagConstrains(0, 0, 2,0, 2, 1,50 ,0));
        this.add(mileageTextField, ClientUtil.createGridBagConstrains(0, 0, 2,1, 2, 1,50 ,0));
        this.add(conditionTextField, ClientUtil.createGridBagConstrains(0, 0, 2,2, 2, 1,50 ,0));
        this.add(priceTextField, ClientUtil.createGridBagConstrains(0, 0, 3,3, 2, 1,50 ,0));
        this.add(saveEditButton, ClientUtil.createGridBagConstrains(0, 0, 0,4, 4, 1,75 ,0));
        this.setSize(300,175);
        this.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2,
                Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2);
    }
    public String getModelTextFieldValue(){
        return modelTextField.getText();
    }
    public String getMileageTextFieldValue(){
        return mileageTextField.getText();
    }
    public String getConditionTextFieldValue(){
        return conditionTextField.getText();
    }
    public String getPriceTextField(){
        return priceTextField.getText();
    }
}
