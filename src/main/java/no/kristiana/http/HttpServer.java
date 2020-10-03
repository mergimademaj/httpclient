package no.kristiana.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public HttpServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
            while (true) {
                try {
                    handleRequest(serverSocket.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
            handleRequest(serverSocket.accept());
        }

    }

    private void handleRequest(Socket clientSocket) throws IOException {
        String requestLine = HttpClient.readLine(clientSocket);
        System.out.println(requestLine);

        String requestTarget = requestLine.split("")[1];
        String statusCode = "200";

        int questionPos = requestTarget.indexOf('?');
        if (questionPos != -1){
            String queryString = requestTarget.substring(questionPos+1);
            int equalPos = queryString.indexOf('=');
            String parameterName = queryString.substring(0,equalPos);
            String parameterValue = queryString.substring(questionPos+1);
            statusCode = parameterValue;
        }

        String response = "HTTP/1.1" + statusCode + "OK\r\n" +
                "Content-Length: 23\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "Bahast\nMergim\nKean\nKnut";

        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String [] args) throws IOException {
    new HttpServer(8080);
    }
}
