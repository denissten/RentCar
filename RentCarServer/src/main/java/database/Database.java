package database;
import com.grpc.Server;
import grpc.RentCarImpl;

import java.sql.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringTokenizer;

public class Database {
    private static final String URL = "jdbc:mysql://localhost/rent_car";
    private static final String USERNAME = "caradmin";
    private static final String PASSWORD = "123456";

    private static Statement getStatement(){
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return connection.createStatement();
        } catch (SQLException  e) {
            System.out.println("> Error connecting to database");
            e.printStackTrace();
            return null;
        }
    }
    private static ResultSet executeQuery(String query){
        ResultSet result = null;
        try {
            result = Objects.requireNonNull(Database.getStatement()).executeQuery(query);
            return result;
        } catch (SQLException e) {
            System.out.println("> Database executeQuery error");
            e.printStackTrace();
            return null;
        }
    }
    private static boolean updateQuery(String query){
        try {
            Objects.requireNonNull(Database.getStatement()).executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("> Database executeUpdate error");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean loginIsRegistered(String login){
        String query = "SELECT COUNT(*) FROM `user_list` WHERE login='" + login + "'";
        int count;
        ResultSet result = executeQuery(query);
        if (result == null) return true;
        try {
            result.next();
            count = result.getInt(1);
            if (count >= 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return true;
        }
    }
    public static boolean registrationAcc(String login, String password) {
        String query = "INSERT INTO `user_list`(`login`, `password`) VALUES ('" + login + "','" + password + "')";
        return updateQuery(query);
    }
    public static Server.User loginAcc(String login, String password){
        String query = "SELECT * FROM `user_list` WHERE login='" + login + "' AND password='" + password + "'";
        ResultSet result = executeQuery(query);
        try {
            if (Objects.requireNonNull(result).next()) {
                int id = result.getInt("id");
                String loginServer = result.getString("login");
                String pwServer = result.getString("password");
                return Server.User.newBuilder().setId(id).setLogin(loginServer).setPassword(pwServer).build();
            } else {
                return Server.User.newBuilder().setLogin("").build();
            }
        } catch (SQLException e) {
            return Server.User.newBuilder().setLogin("").build();
        }
    }
    public static boolean getAllCars(RentCarImpl server){
        String query = "SELECT * FROM `car_list`";
        ResultSet r = executeQuery(query);
        try {
            while (Objects.requireNonNull(r).next()){
                int id = r.getInt("id");
                String model = r.getString("model");
                int mileage = r.getInt("mileage");
                String condition = r.getString("condition");
                int price = r.getInt("price");
                boolean rent = r.getBoolean("rent");
                int owner = r.getInt("owner");
                server.addCarToCarList(id, model, mileage, condition, price, rent, owner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean addCar(RentCarImpl server, Server.Car car){
        try {
            String model = car.getModel();
            int mileage = car.getMileage();
            String condition = car.getCondition();
            int price = car.getPrice();
            boolean rent = car.getRent();
            int owner = car.getOwner();
            String query = "INSERT INTO `car_list`(`model`, `mileage`, `condition`, `price`, `rent`, `owner`) VALUES(" +
                    "'" + model + "', '" + mileage + "', '" + condition + "', '" +
                    price + "', '" + (rent ? "1":"0") + "', '" + owner + "')";
            Statement statement = getStatement();
            Objects.requireNonNull(statement).executeUpdate(query);
            query = "SELECT LAST_INSERT_ID()";
            ResultSet r = statement.executeQuery(query);
            r.next();
            int carId = r.getInt(1);
            server.addCarToCarList(carId, model, mileage, condition, price, rent, owner);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
    public static void editCar(Server.Car car){
        int carId = car.getId();
        String model = car.getModel();
        int mileage = car.getMileage();
        String condition = car.getCondition();
        int price = car.getPrice();
        boolean rent = car.getRent();
        int owner = car.getOwner();
        String query = "UPDATE `car_list` SET `model`='" + model+ "', `mileage`='" + mileage + "', `condition`='" + condition + "', `price`='" + price + "', `rent`='" + (rent ? "1":"0") + "', `owner`='" + owner + "' WHERE `id`='" + carId + "'";
        updateQuery(query);
    }
    public static boolean setOwner(){
        return true;
    }
}
