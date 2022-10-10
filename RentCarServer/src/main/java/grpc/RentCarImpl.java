package grpc;

import com.google.protobuf.Empty;
import com.grpc.*;
import database.Database;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;

public class RentCarImpl extends RentCarGrpc.RentCarImplBase {
    private static final String URL = "jdbc:mysql://localhost/rent_car";
    private static final String USERNAME = "caradmin";
    private static final String PASSWORD = "123456";

    private static HashMap<Integer, Server.Car> carList = new HashMap<>();
    private static HashMap<Integer, StreamObserver<Server.Response>> userList = new HashMap<>();
    public RentCarImpl(){
        if (Database.getAllCars(this)){
            System.out.println("> Data uploaded successfully. [" + carList.size() + "]");
        };
        /*addCarToCarList("Lada", 10, "Good", 1200, false);
        addCarToCarList("Audi", 200, "Great", 50, false);
        addCarToCarList("BMW", 300, "Medium", 133, false);
        addCarToCarList("Skoda", 50, "Good", 17000, false);
        addCarToCarList("Renault", 228, "Good", 49, false);
        addCarToCarList("Ford", 1337, "Good", 553, false);
        addCarToCarList("Ford", 1532, "Good", 983, false);
        addCarToCarList("Lada", 1900234, "Great", 130000, false);
        addCarToCarList("Nissan", 931, "Medium", 2404, false);
        addCarToCarList("Lada", 2001, "Great", 999999, false);
        addCarToCarList("Lada", 1707, "Great", 1, false);*/
    }
    public void addCarToCarList(int id, String model, int mileage, String condition,
                                      int price, boolean rent, int owner){
        Server.Car car = Server.Car.newBuilder().setId(id).setModel(model).
                setMileage(mileage).setCondition(condition).setPrice(price).
                setRent(rent).setOwner(owner).build();
        carList.put(id, car);
    }
    @Override
    public void editCar(Server.Car request, StreamObserver<Empty> responseObserver) {
        Database.editCar(request);
        carList.put(request.getId(), request);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
        updateClientTable();
    }
    @Override
    public void addCarToServer(Server.Car request, StreamObserver<Empty> responseObserver) {
        Database.addCar(this, request);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
        updateClientTable();
    }
    @Override
    public void startRent(Server.Response request, StreamObserver<Server.Response> responseObserver) {
        String data[] = request.getData().split(":");
        Server.Car oldCar = carList.get(Integer.parseInt(data[1]));
        Server.Car car = Server.Car.newBuilder().setId(oldCar.getId()).setModel(oldCar.getModel()).setMileage(oldCar.getMileage()).
                setCondition(oldCar.getCondition()).setPrice(oldCar.getPrice()).setRent(true).
                setOwner(Integer.parseInt(data[0])).build();
        carList.put(Integer.parseInt(data[1]), car);
        Database.editCar(car);
        responseObserver.onNext(Server.Response.newBuilder().build());
        responseObserver.onCompleted();
        updateClientTable();
    }

    @Override
    public void endRent(Server.Response request, StreamObserver<Server.Response> responseObserver) {
        String data = request.getData();
        Server.Car oldCar = carList.get(Integer.parseInt(data));
        Server.Car car = Server.Car.newBuilder().setId(oldCar.getId()).setModel(oldCar.getModel()).setMileage(oldCar.getMileage()).
                setCondition(oldCar.getCondition()).setPrice(oldCar.getPrice()).setRent(false).
                setOwner(-1).build();
        carList.put(Integer.parseInt(data), car);
        Database.editCar(car);
        responseObserver.onNext(Server.Response.newBuilder().setHeader("Success").build());
        responseObserver.onCompleted();
        updateClientTable();
    }

    @Override
    public void disconnect(Server.Response request, StreamObserver<Empty> responseObserver) {
        userList.get(Integer.parseInt(request.getData())).onNext(Server.Response.newBuilder().setHeader("Disconnect").build());
        userList.get(Integer.parseInt(request.getData())).onCompleted();
        userList.remove(Integer.parseInt(request.getData()));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
        System.out.println("> User disconnected. [ID: " + request.getData() + "]");
    }

    public void updateClientTable(){
        for(StreamObserver<Server.Response> user : userList.values()){
            user.onNext(Server.Response.newBuilder().setHeader("Update").build());
        }
    }
    public void getAllCars(Empty empty, StreamObserver<Server.Car> cars) {
        for (Server.Car car : carList.values()) {
            cars.onNext(car);
        }
        cars.onCompleted();
    }

    @Override
    public void regAcc(Server.User request, StreamObserver<Server.Response> responseObserver) {
        String login = request.getLogin(); String password = request.getPassword();
        Server.Response response;
        if (Database.loginIsRegistered(login))
            response = Server.Response.newBuilder().setHeader("Error").build();
        else {
            if (Database.registrationAcc(login, password))
                response = Server.Response.newBuilder().setHeader("Success").build();
            else
                response = Server.Response.newBuilder().setHeader("Error").build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void connect(Server.User request, StreamObserver<Server.Response> responseObserver) {
        Server.User user = Database.loginAcc(request.getLogin(), request.getPassword());
        if (user.getLogin().length() == 0){
            Server.Response response = Server.Response.newBuilder().setHeader("LoginError").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        int userId = user.getId();
        Server.Response response = Server.Response.newBuilder().setHeader("Connected").
                setData(Integer.toString(userId))
                .build();
        userList.put(userId, responseObserver);
        userList.get(userId).onNext(response);
        response = Server.Response.newBuilder().setHeader("Update").build();
        userList.get(userId).onNext(response);
        System.out.println("> User("+ user.getLogin() + ") connected. [ID: " + userId + "]");
    }
}
