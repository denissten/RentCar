package util;

import com.google.protobuf.Empty;
import com.grpc.RentCarGrpc;
import com.grpc.Server;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.awt.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

public class ClientUtil {
    private static HashMap<Integer, TableCar> carsHashMap = new HashMap<>();
    public static final String ADRESS = "localhost:8080";

    public static GridBagConstraints createGridBagConstrains(double weightx, double weighty, int gridx, int gridy,
                                                       int gridwidth, int gridheight, int ipadx, int ipady){
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = weightx;
        gridBagConstraints.weighty = weighty;
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        gridBagConstraints.gridwidth = gridwidth;
        gridBagConstraints.gridheight = gridheight;
        gridBagConstraints.ipadx = ipadx;
        gridBagConstraints.ipady = ipady;
        return gridBagConstraints;
    }
    public static String[] getTableColumName(){
        return new String[]{"ID", "Model", "Mileage", "Condition", "Price", "Rent", "Owner"};
    }
    public static void addTableCar(TableCar tableCar){
        carsHashMap.put(tableCar.car.getId(), tableCar);
    }
    public static HashMap<Integer, TableCar> getCarsHaspMap(){
        return carsHashMap;
    }
    public static void editCarOnServer(Server.Car car){
        ManagedChannel channel = ManagedChannelBuilder.forTarget(ADRESS).usePlaintext()
                .build();
        RentCarGrpc.RentCarBlockingStub stub = RentCarGrpc.newBlockingStub(channel);
        stub.editCar(car);
        channel.shutdownNow();
    }
    public static void addCarToServer(Server.Car car){
        ManagedChannel channel = ManagedChannelBuilder.forTarget(ADRESS).usePlaintext()
                .build();
        RentCarGrpc.RentCarBlockingStub stub = RentCarGrpc.newBlockingStub(channel);
        stub.addCarToServer(car);
        channel.shutdownNow();
    }
    public static void rentCar(int id, int carId, int days){
        ManagedChannel channel = ManagedChannelBuilder.forTarget(ADRESS).usePlaintext()
                .build();
        RentCarGrpc.RentCarBlockingStub stub = RentCarGrpc.newBlockingStub(channel);
        Server.Response response = Server.Response.newBuilder().setData(id + ":" + carId + ":" + days).build();
        stub.startRent(response);
        channel.shutdownNow();
    }
    public static boolean endRent(int carId){
        ManagedChannel channel = ManagedChannelBuilder.forTarget(ADRESS).usePlaintext()
                .build();
        RentCarGrpc.RentCarBlockingStub stub = RentCarGrpc.newBlockingStub(channel);
        Server.Response response = Server.Response.newBuilder().setData(Integer.toString(carId)).build();
        Server.Response request = stub.endRent(response);
        channel.shutdownNow();
        if (request.getHeader().equals("Success")) return true;
        else return false;
    }
    public static void getAllCars(){
        ManagedChannel channel = ManagedChannelBuilder.forTarget(ADRESS).usePlaintext()
                .build();
        RentCarGrpc.RentCarBlockingStub stub = RentCarGrpc.newBlockingStub(channel);
        Iterator<Server.Car> responseIterator = stub.getAllCars(Empty.newBuilder().build());
        while (responseIterator.hasNext()){
            Server.Car car = responseIterator.next();
            addTableCar(new TableCar(car, true));
        }
        channel.shutdownNow();
    }
    public static void disconnect(int clientId){
        ManagedChannel channel = ManagedChannelBuilder.forTarget(ADRESS).usePlaintext()
                .build();
        RentCarGrpc.RentCarBlockingStub stub = RentCarGrpc.newBlockingStub(channel);
        stub.disconnect(Server.Response.newBuilder().setData(Integer.toString(clientId)).build());
        channel.shutdownNow();
    }
    public static boolean registrationAcc(String login, String password){
        ManagedChannel channel = ManagedChannelBuilder.forTarget(ADRESS).usePlaintext()
                .build();
        RentCarGrpc.RentCarBlockingStub stub = RentCarGrpc.newBlockingStub(channel);
        Server.User user = Server.User.newBuilder().setLogin(login).setPassword(password).build();
        Server.Response request = stub.regAcc(user);
        channel.shutdownNow();
        if (request.getHeader().equals("Success")) return true;
        else return false;
    }
    public static String getMD5Hex(String string){
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(string.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        return bigInt.toString(16);
    }

    public static void setAllCarVisible(){
        for(TableCar car : carsHashMap.values()){
            car.setVisible(true);
        }
    }

    public static class TableCar{
        private Server.Car car;
        private boolean visible;

        public TableCar(Server.Car car, boolean visible) {
            this.car = car; this.visible = visible;
        }
        public boolean isVisible() {
            return visible;
        }
        public void setVisible(boolean visible){
            this.visible = visible;
        }
        public Server.Car getCar() {
            return car;
        }
    }
}
