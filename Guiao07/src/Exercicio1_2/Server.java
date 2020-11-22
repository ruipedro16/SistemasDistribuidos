import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


class ContactList { // Estado partilhado
    private List<Contact> contacts;
    private ReentrantLock lock;

    public ContactList() {
        contacts = new ArrayList<>();
        lock = new ReentrantLock();

        contacts.add(new Contact("John", 20, 253123321, null, new ArrayList<>(Arrays.asList("john@mail.com"))));
        contacts.add(new Contact("Alice", 30, 253987654, "CompanyInc.", new ArrayList<>(Arrays.asList("alice.personal@mail.com", "alice.business@mail.com"))));
        contacts.add(new Contact("Bob", 40, 253123456, "Comp.Ld", new ArrayList<>(Arrays.asList("bob@mail.com", "bob.work@mail.com"))));
    }

    public void addContact(DataInputStream in) throws IOException {
        Contact contact = Contact.deserialize(in);
        lock.lock();
        try {
            contacts.add(contact);
        } finally {
            lock.unlock();
        }
    }

    // debugging
    public void printContacts() throws IOException {
        for (Contact contact : this.contacts)
            System.out.println(contact.toString());
    }

}

class ServerWorker implements Runnable {
    private Socket socket;
    private ContactList contacts;

    public ServerWorker(Socket socket, ContactList contactList) {
        this.socket = socket;
        this.contacts = contactList;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            boolean isOpen = true;

            while (isOpen)
                contacts.addContact(in);

            contacts.printContacts();

            socket.shutdownInput();
            socket.close();

            System.out.println("Connection closed ...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactList contactList = new ContactList();

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, contactList));
            worker.start();
        }
    }
}
