package johny.dotsville;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class Client {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new SimpleClient(i).start();
        }
    }
}

class SimpleClient extends Thread {
    private static final String[] commands = {
        "HELLO", "MORNING", "DAY", "EVENING"
    };

    private int requestId;

    public SimpleClient(int requestId) {
        this.requestId = requestId;
    }

    @Override
    public void run() {
        sendRequest();
    }

    private void sendRequest() {
        try {
            System.out.println("Клиент: начинаем оформлять запрос #" + this.requestId + ": " + LocalDateTime.now());
            Socket server = new Socket("127.0.0.1", 25225);

            InputStream sis = server.getInputStream();
            OutputStream sos = server.getOutputStream();

            BufferedReader breader = new BufferedReader(new InputStreamReader(sis));
            BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(sos));

            String username = "JohNy";
            String command = commands[requestId % commands.length];
            String request = command + " " + username;
            System.out.println("Клиент: запрос " + requestId + " отправлен на сервер: " + request);
            bwriter.write(request);
            bwriter.newLine();
            bwriter.flush();

            String response = breader.readLine();
            System.out.println("Клиент: получен ответ от сервера: " + response);

            breader.close();
            bwriter.close();

            server.close();
            System.out.println("Клиент: запрос #" + this.requestId + " обработан: " + LocalDateTime.now());
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }
}