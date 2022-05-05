package johny.dotsville;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class Server {
    public static void main(String[] args) throws IOException {
        // Максимальное количество соединений, которые должен принять сокет (по дефолту 50)
        ServerSocket server = new ServerSocket(25225, 500);
        System.out.println("Сервер: сервер запущен");
        while (true) {
            Socket client = server.accept();
            new SimpleServer(client).start();
        }
    }
}

class SimpleServer extends Thread {
    private Socket client;

    public SimpleServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        handleRequest();
    }

    private void handleRequest() {
        try {
            InputStream cis = client.getInputStream();
            OutputStream cos = client.getOutputStream();

            BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(cos));
            BufferedReader breader = new BufferedReader(new InputStreamReader(cis));

            String data = breader.readLine();
            System.out.println("Сервер: сервер получил запрос: " + data + ". Время: " + LocalDateTime.now());
            Thread.sleep(2000);

            bwriter.write("Это результат обработки запроса " + data);
            bwriter.newLine();
            bwriter.flush();

            breader.close();
            bwriter.close();

            client.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
}