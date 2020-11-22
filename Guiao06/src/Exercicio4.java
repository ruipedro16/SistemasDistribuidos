import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

class Register { // Estado partilhado
    private final ReentrantLock lock = new ReentrantLock();
    private int n;
    private int sum = 0;

    public void add(int value) {
        lock.lock();
        try {
            sum += value;
            n++;
        } finally {
            lock.unlock();
        }
    }

    public double avg() {
        lock.lock();
        try {
            if (n < 1) return 0;
            return (double) sum / n;
        } finally {
            lock.unlock();
        }
    }
}

class ServerWorker implements Runnable {
    private Register register;
    private Socket socket;

    public ServerWorker(Socket socket, Register register) {
        this.socket = socket;
        this.register = register;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            int sum = 0;
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    int value = Integer.parseInt(line);
                    sum += value;
                    register.add(value);
                } catch (NumberFormatException e) {
                    // Ignore invalid integers
                }

                out.println(sum);
                out.flush();
            }

            out.println(register.avg());
            out.flush();

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Exercicio4 { // Server
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12345);
        Register register = new Register();

        while (true) {
            Socket socket = ss.accept();
            Thread worker = new Thread(new ServerWorker(socket, register));
            worker.start();
        }
    }
}
