import grpc.RentCarImpl;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.Scanner;

public class RentServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        io.grpc.Server server = ServerBuilder.forPort(8080).addService(new RentCarImpl()).build();
        server.start();
        System.out.println("> Server started");
        server.awaitTermination();
    }

}
