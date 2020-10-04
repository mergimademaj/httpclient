package no.kristiana.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    private final int statusCode;
    private final Map<String, String> responseHeaders = new HashMap<>();
    private String responseBody;

    public HttpClient(final String hostname, int port, final String requestTarget) throws IOException {
        Socket socket = new Socket(hostname, port);

        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n" +
                "\r\n";

        socket.getOutputStream().write(request.getBytes());

        String responseLine = readLine(socket);
        String[] responseLineParts = responseLine.split(" ");

        statusCode = Integer.parseInt(responseLineParts[1]);

        String headerLine;
        while (!(headerLine = readLine(socket)).isEmpty()){
            int colonPos = headerLine.indexOf(':');
            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos+1).trim();
            responseHeaders.put(headerName,headerValue);
        }

        int contentLength = Integer.parseInt(getResponseHeader("Content-Length"));
        StringBuffer body = new StringBuffer();
        for(int i = 0; i < contentLength; i++) {
            body.append((char)socket.getInputStream().read());
        }
        responseBody = body.toString();
    }

    public static String readLine(Socket socket) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while ((c =socket.getInputStream().read()) != -1 ){
            if (c == '\r'){
                socket.getInputStream().read(); // read and ignore the following \n
                break;
            }
            line.append((char)c);
        }
        return line.toString();
    }

    public static void main(String [] args) throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=404&Content-Type=text%2Fhtml&body=Hei+Kristiana");
        System.out.println(client.getResponseBody());
    }

    public int getStatusCode() {
        return  statusCode;
    }


    public String getResponseHeader(String headerName) {
        return responseHeaders.get(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }
}
