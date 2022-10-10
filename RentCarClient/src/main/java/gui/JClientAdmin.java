package gui;

import com.grpc.Server;
import util.ClientUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JClientAdmin extends JPanel{
    private JTable jTable;
    private DefaultTableModel defaultTableModel;
    private JTextField searchModelTextField;
    private JTextField searchMileageTextField;
    private JTextField searchConditionTextField;
    private JTextField searchPriceTextField;

    public JClientAdmin(JFrame jFrame){
        this.setLayout(new BorderLayout());
        defaultTableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable = new JTable(defaultTableModel);
        jTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollbar = new JScrollPane(jTable);
        this.add(BorderLayout.CENTER, scrollbar);
        for(String columName : ClientUtil.getTableColumName()){
            defaultTableModel.addColumn(columName);
        }

        JPanel jButtonPanel = new JPanel();
        JButton searchButton = new JButton("Search");
        JButton addCarButton = new JButton("Add");
        JButton editCarButton = new JButton("Edit");
        JButton resetButton = new JButton("Reset");
        searchButton.addActionListener(new SearchButtonActionListener());
        addCarButton.addActionListener(new AddCarButtonActionListener(jFrame));
        editCarButton.addActionListener(new EditCarButtonActionListener(jFrame));
        resetButton.addActionListener(new ResetButtonActionListener());

        JPanel jSearchPanel = new JPanel();
        searchModelTextField = new JTextField();
        searchMileageTextField = new JTextField();
        searchConditionTextField = new JTextField();
        searchPriceTextField = new JTextField();

        jSearchPanel.setLayout(new GridBagLayout());
        jSearchPanel.add(new JLabel("Model"), ClientUtil.createGridBagConstrains(1, 0, 0,0, 2, 1,0 ,0));
        jSearchPanel.add(searchModelTextField, ClientUtil.createGridBagConstrains(0, 0, 2,0, 2, 1, 50,0));
        jSearchPanel.add(new JLabel("Mileage <"), ClientUtil.createGridBagConstrains(1, 0, 4,0, 2, 1, 0,0));
        jSearchPanel.add(searchMileageTextField, ClientUtil.createGridBagConstrains(0, 0, 6,0, 2, 1, 50,0));
        jSearchPanel.add(new JLabel("Condition"), ClientUtil.createGridBagConstrains(1, 0, 8,0, 2, 1, 0,0));
        jSearchPanel.add(searchConditionTextField, ClientUtil.createGridBagConstrains(0, 0, 10,0, 2, 1, 50,0));
        jSearchPanel.add(new JLabel("Price <"), ClientUtil.createGridBagConstrains(1, 0, 12,0, 2, 1, 0,0));
        jSearchPanel.add(searchPriceTextField, ClientUtil.createGridBagConstrains(0, 0, 14,0, 2, 1, 50,0));
        this.add(BorderLayout.NORTH, jSearchPanel);

        jButtonPanel.setLayout(new GridBagLayout());
        jButtonPanel.add(resetButton, ClientUtil.createGridBagConstrains(10, 0, 0,0, 2, 1,10 ,0));
        jButtonPanel.add(searchButton, ClientUtil.createGridBagConstrains(10, 0, 2,0, 2, 1,10 ,0));
        jButtonPanel.add(addCarButton, ClientUtil.createGridBagConstrains(10, 0, 4,0, 2, 1,30 ,0));
        jButtonPanel.add(editCarButton, ClientUtil.createGridBagConstrains(10, 0, 6,0, 2, 1,30 ,0));
        this.add(BorderLayout.SOUTH, jButtonPanel);

        jFrame.setSize(800, 600);
        jFrame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width/2 - jFrame.getWidth()/2,
                Toolkit.getDefaultToolkit().getScreenSize().height/2 - jFrame.getHeight()/2);

        updateCarTable();
    }
    public void clearCarTable(){
        while (defaultTableModel.getRowCount() != 0)
            defaultTableModel.removeRow(0);
    }
    public void updateCarTable(){
        clearCarTable();
        for (ClientUtil.TableCar car : ClientUtil.getCarsHaspMap().values()){
            if (!car.isVisible()) continue;
            String[] data = new String[] { Integer.toString(car.getCar().getId()),
                    car.getCar().getModel(), Integer.toString(car.getCar().getMileage()), car.getCar().getCondition(),
                    Integer.toString(car.getCar().getPrice()), Boolean.toString(car.getCar().getRent())};
            defaultTableModel.addRow(data);
        }
    }
    class SearchButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean modelFlag = searchModelTextField.getText().replace(" ", "").length() != 0,
                    conditionFlag = searchConditionTextField.getText().replace(" ", "").length() != 0,
                    mileageFlag = searchMileageTextField.getText().replace(" ", "").length() != 0,
                    priceFlag = searchPriceTextField.getText().replace(" ", "").length() != 0;
            String model = searchModelTextField.getText().toLowerCase(),
                    condition = searchConditionTextField.getText().toLowerCase();
            int mileage = 0, price = 0;
            try {
                mileage = Integer.parseInt(searchMileageTextField.getText());
                mileageFlag = true;
                price = Integer.parseInt(searchPriceTextField.getText());
                priceFlag = true;
            } catch (Exception exception){
            }
            if (!(modelFlag || conditionFlag || mileageFlag || priceFlag)) return;
            boolean visible = true;
            for (ClientUtil.TableCar car : ClientUtil.getCarsHaspMap().values()){
                if (modelFlag && car.getCar().getModel().toLowerCase().indexOf(model) != 0) visible &= false;
                if (mileageFlag && car.getCar().getMileage() > mileage) visible &= false;
                if (conditionFlag && car.getCar().getCondition().toLowerCase().indexOf(condition) != 0) visible &= false;
                if (priceFlag  && car.getCar().getPrice() > price) visible &= false;
                car.setVisible(visible);
                visible = true;
            }
            updateCarTable();
        }
    }
    class AddCarButtonActionListener implements ActionListener {
        JFrame jFrame;
        JClientAdminDialog jClientDialog;
        AddCarButtonActionListener(JFrame jFrame){
            this.jFrame = jFrame;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            jClientDialog = new JClientAdminDialog(jFrame, defaultTableModel, new AddButtonActionListener(), "Add", -1);
            jClientDialog.setVisible(true);
        }
        class AddButtonActionListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                String model = jClientDialog.getModelTextFieldValue(),
                        mileage = jClientDialog.getMileageTextFieldValue(),
                        condition = jClientDialog.getConditionTextFieldValue(),
                        price = jClientDialog.getPriceTextField();
                int mileageInt = 0, priceInt = 0;
                if (model.length() == 0 || mileage.length() == 0 || condition.length() == 0 || price.length() == 0) {
                    JOptionPane.showMessageDialog(jClientDialog, "Заполните все поля!", "Внимание", JOptionPane.WARNING_MESSAGE);
                }
                try {
                    mileageInt = Integer.parseInt(mileage);
                    priceInt = Integer.parseInt(price);
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(jClientDialog, "Проверьте правильность полей!", "Внимание", JOptionPane.WARNING_MESSAGE);
                }
                Server.Car car = Server.Car.newBuilder().setModel(model).setMileage(mileageInt).setCondition(condition).
                        setPrice(priceInt).build();

                ClientUtil.addCarToServer(car);
                jClientDialog.dispose();
            }
        }
    }
    class EditCarButtonActionListener implements ActionListener {
        private JFrame jFrame;
        private JClientAdminDialog jClientDialog;
        private int row;
        EditCarButtonActionListener(JFrame jFrame){
            this.jFrame = jFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (jTable.getSelectedRows().length == 0) {
                return;
            }
            row = jTable.getSelectedRows()[0];
            SaveEditButtonActionListener listener = new SaveEditButtonActionListener();
            jClientDialog = new JClientAdminDialog(jFrame, defaultTableModel, listener, "Edit", row);
            listener.setjClientDialog(jClientDialog);
            jClientDialog.setVisible(true);

        }
        class SaveEditButtonActionListener implements ActionListener{
            private JClientAdminDialog jClientDialog;
            public void setjClientDialog(JClientAdminDialog jClientDialog){
                this.jClientDialog = jClientDialog;
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                String model = jClientDialog.getModelTextFieldValue(),
                        mileage = jClientDialog.getMileageTextFieldValue(),
                        condition = jClientDialog.getConditionTextFieldValue(),
                        price = jClientDialog.getPriceTextField();
                int mileageInt = 0, priceInt = 0;
                if (model.length() == 0 || mileage.length() == 0 || condition.length() == 0 || price.length() == 0) {
                    JOptionPane.showMessageDialog(jClientDialog, "Заполните все поля!", "Внимание", JOptionPane.WARNING_MESSAGE);
                }
                try {
                    mileageInt = Integer.parseInt(mileage);
                    priceInt = Integer.parseInt(price);
                } catch (Exception exception){
                    JOptionPane.showMessageDialog(jClientDialog, "Проверьте правильность полей!", "Внимание", JOptionPane.WARNING_MESSAGE);
                }
                Server.Car car = Server.Car.newBuilder().setId(Integer.parseInt((String) jTable.getValueAt(row, 0))).
                        setModel(model).setMileage(mileageInt).setCondition(condition).
                        setPrice(priceInt).setRent(Boolean.parseBoolean((String)jTable.getValueAt(row, 5))).build();
                ClientUtil.editCarOnServer(car);
                jClientDialog.dispose();
            }
        }
    }
    class ResetButtonActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            ClientUtil.setAllCarVisible();
            updateCarTable();
            searchConditionTextField.setText("");
            searchMileageTextField.setText("");
            searchModelTextField.setText("");
            searchPriceTextField.setText("");
        }
    }
}
