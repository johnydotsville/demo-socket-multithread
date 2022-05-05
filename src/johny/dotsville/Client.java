package johny.dotsville;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class Client {
    public static void main(String[] args) {
        for (int i = 0; i < 2000; i++) {
            new SimpleClient(i).start();
        }
    }
}

class SimpleClient extends Thread {
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

            String requestId = Integer.toString(this.requestId);
            System.out.println("Клиент: запрос " + requestId + " отправлен на сервер");
            bwriter.write(requestId);
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