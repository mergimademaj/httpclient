package no.kristiana.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private final int statusCode;

    public HttpClient(final String hostname, int port, final String requestTarget) throws IOException {
        Socket socket = new Socket(hostname, port);

        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n" +
                "\r\n";

        socket.getOutputStream().write(request.getBytes());

        String line = readLine(socket);
        System.out.print(line);

       String[] responseLineParts =  line.split(" ");

        statusCode = Integer.parseInt(responseLineParts[1]);

        String headerLine;
        while (!(headerLine = readLine(socket)).isEmpty()){
            int colonPos = headerLine.indexOf(':');
            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos+1);
        }
    }

    private String readLine(Socket socket) throws IOException {
        StringBuilder line = new StringBuilder();

        int c;

        while ((c =socket.getInputStream().read()) != -1 ){
            if (c == '\n') break;
            line.append((char)c);
        }
        return line.toString();
    }

    public static void main(String [] args) throws IOException {
            new HttpClient("urlecho.appspot.com", 80, "/echo?status=404&Content-Type=text%2Fhtml&body=HeiKristiana");
    }

    public int getStatusCode() {
        return  statusCode;
    }


    public String getResponseHeader(String headerName) {
        return null;
    }
}
