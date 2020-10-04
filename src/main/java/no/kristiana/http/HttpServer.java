package no.kristiana.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private File contentRoot;

    //      Et eller annet problem her som jeg tror påvirker testen:
    public HttpServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

     /*   while (true) {

                Socket clientSocket = serverSocket.accept();
            }
      */
    }
//-------------------------------------------------------------------
    private void handleRequest(Socket clientSocket) throws IOException {
        String requestLine = HttpClient.readLine(clientSocket);
        System.out.println(requestLine);

        String requestTarget = requestLine.split("")[1];
        String statusCode = "200";
        String body = "Bahast\nMergim\nKean\nKnut";



        int questionPos = requestTarget.indexOf('?');
        if (questionPos != -1){

            QueryString queryString =  new QueryString(requestTarget.substring(questionPos+1));
            if(queryString.getParameter("status") != null ){
                statusCode = queryString.getParameter("status");
            }
            if(queryString.getParameter("body") != null ){
                body = queryString.getParameter("body");
            }
        } else {
            File file = new File(contentRoot, requestTarget);
            statusCode = "200";
            String contentType = "text/plain";
            if(file.getName().endsWith(".html")){
                contentType = "text/html";
            }
            String response = "HTTP/1.1" + statusCode + "OK\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "\r\n" +
                    body;
            clientSocket.getOutputStream().write(response.getBytes());

            new FileInputStream(file).transferTo(clientSocket.getOutputStream());
        }
    }

    public static void main(String [] args) throws IOException {
    HttpServer server = new HttpServer(8080);
    server.setContentRoot(new File("src/main/resources"));
    }

    public void setContentRoot(File contentRoot) {
        this.contentRoot = contentRoot;
    }
}
