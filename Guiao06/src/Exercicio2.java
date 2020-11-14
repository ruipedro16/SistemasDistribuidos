import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Exercicio2 {

    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            String serverResponse;
            while ((userInput = systemIn.readLine()) != null) {
                out.println(userInput);
                out.flush();

                serverResponse = in.readLine();
                System.out.println("The current sum is " + serverResponse);
            }

            socket.shutdownOutput();

            serverResponse = in.readLine();
            System.out.println("The average is " + serverResponse);

            socket.shutdownInput();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
