package gui;

import client.RentClient;
import com.grpc.RentCarGrpc;
import util.ClientUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JClientCustomer extends JPanel {
    private JTable jTable;
    private DefaultTableModel defaultTableModel;
    private JTextField searchModelTextField;
    private JTextField searchMileageTextField;
    private JTextField searchConditionTextField;
    private JTextField searchPriceTextField;

    public JClientCustomer(JFrame jFrame){
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
        JButton resetButton = new JButton("Reset");
        JButton searchButton = new JButton("Search");
        JButton rentButton = new JButton("Rent");
        JButton endRentButton = new JButton("End Rent");
        resetButton.addActionListener(new ResetButtonActionListener());
        searchButton.addActionListener(new SearchButtonActionListener());

        rentButton.addActionListener(new RentButtonActionListener(jFrame));
        endRentButton.addActionListener(new EndRentButtonActionListener(jFrame));

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

        jButtonPanel.add(rentButton, ClientUtil.createGridBagConstrains(10, 0, 4,0, 2, 1,30 ,0));
        jButtonPanel.add(endRentButton, ClientUtil.createGridBagConstrains(10, 0, 6,0, 2, 1,30 ,0));
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
                    Integer.toString(car.getCar().getPrice()), Boolean.toString(car.getCar().getRent()), Integer.toString(car.getCar().getOwner())};
            System.out.println("ID " + car.getCar().getId() + "  " + car.getCar().getOwner());
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
    class RentButtonActionListener implements ActionListener{
        private JFrame jFrame;
        private JClientRentDialog jClientRentDialog;
        private int row;
        public RentButtonActionListener(JFrame jFrame){
            this.jFrame = jFrame;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (jTable.getSelectedRows().length == 0) {
                return;
            }
            row = jTable.getSelectedRows()[0];
            String rent = (String) jTable.getValueAt(row, 5);
            if (Boolean.parseBoolean(rent)) {
                JOptionPane.showMessageDialog(jClientRentDialog, "Машина уже арендована!", "Внимание", JOptionPane.WARNING_MESSAGE);
                return;
            }
            TotalButtonActionListener totalButtonActionListener = new TotalButtonActionListener();
            AcceptRentButtonActionListener acceptRentButtonActionListener = new AcceptRentButtonActionListener();
            int price = Integer.parseInt((String)(jTable.getValueAt(row, 4)));
            jClientRentDialog = new JClientRentDialog(jFrame, totalButtonActionListener, acceptRentButtonActionListener,"Аренда", price);
            totalButtonActionListener.setjClientDialog(jClientRentDialog);
            acceptRentButtonActionListener.setjClientDialog(jClientRentDialog);
            jClientRentDialog.setVisible(true);
        }
        class TotalButtonActionListener implements ActionListener {
            private JClientRentDialog jClientDialog;
            public void setjClientDialog(JClientRentDialog jClientDialog){
                this.jClientDialog = jClientDialog;
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                jClientDialog.changePrice();
            }
        }
        class AcceptRentButtonActionListener implements ActionListener {
            private JClientRentDialog jClientDialog;
            public void setjClientDialog(JClientRentDialog jClientDialog){
                this.jClientDialog = jClientDialog;
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                int days = jClientDialog.getDays();
                if (days<= 0) return;
                int carId = Integer.parseInt((String)(jTable.getValueAt(row, 0)));
                ClientUtil.rentCar(RentClient.getUser().getId(), carId, days);/////////////////////////////////
                jClientDialog.dispose();
            }
        }
    }
    class EndRentButtonActionListener implements ActionListener{
        private int row;
        private JFrame jFrame;
        EndRentButtonActionListener(JFrame jFrame){
            this.jFrame = jFrame;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (jTable.getSelectedRows().length == 0) {
                return;
            }
            row = jTable.getSelectedRows()[0];
            String rent = (String) jTable.getValueAt(row, 5);
            if (!Boolean.parseBoolean(rent)) {
                return;
            }
            /*if (ClientUtil.getCarsHaspMap().get(row + 1).getCar().getOwner() != RentClient.getClientId()){

               JOptionPane.showMessageDialog(jFrame, "Машина арендована не Вами!", "Внимание", JOptionPane.WARNING_MESSAGE);
                return;
            }*/
            if (ClientUtil.endRent(row + 1))
                JOptionPane.showMessageDialog(jFrame, "Вы завершили аренду машины.", "Успешно", JOptionPane.INFORMATION_MESSAGE);
            else
                JOptionPane.showMessageDialog(jFrame, "Ошибка при завершении аренды.", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }
}
