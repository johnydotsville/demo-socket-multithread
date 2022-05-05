package johny.dotsville;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

import johny.dotsville.greetable.Greetable;

public class Server {
    public static void main(String[] args) throws IOException {
        // Максимальное количество соединений, которые должен принять сокет (по дефолту 50)
        ServerSocket server = new ServerSocket(25225, 500);

        Map<String, Greetable> handlers = loadHandlers();

        System.out.println("Сервер: сервер запущен");
        while (true) {
            Socket client = server.accept();
            new SimpleServer(client, handlers).start();
        }
    }

    private static Map<String, Greetable> loadHandlers() throws IOException {
        Properties props = new Properties();
        try (InputStream is = Server.class.getClassLoader().getResourceAsStream("server.properties")) {
            props.load(is);
        }
        Map<String, Greetable> handlers = new HashMap<>();
        for (Object key : props.keySet()) {
            String propName = key.toString();
            Greetable handler = getHandler(props.getProperty(propName));
            handlers.put(propName, handler);
        }
        return handlers;
    }

    private static Greetable getHandler(String handlerClassName) {
        try {
            return (Greetable) Class.forName(handlerClassName).getConstructor().newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null; // Говно
    }
}

class SimpleServer extends Thread {
    private Socket client;
    private Map<String, Greetable> handlers;

    public SimpleServer(Socket client, Map<String, Greetable> handlers) {
        this.client = client;
        this.handlers = handlers;
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
            String[] parsed = data.split("\\s+");
            String command = parsed[0];
            String username = parsed[1];

            String response = buildResponse(command, username);

            bwriter.write("Это результат обработки запроса " + response);
            bwriter.newLine();
            bwriter.flush();

            breader.close();
            bwriter.close();

            client.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    private String buildResponse(String command, String username) {
        Greetable handler = handlers.get(command);
        if (handler != null) {
            handler.buildResponse(username);
        }
        return "Обработчик не найден";
    }
}