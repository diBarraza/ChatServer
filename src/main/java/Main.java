/*
 * Copyright (c) 2019 Diego Urrutia-Astorga http://durrutia.cl.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * The Web Server.
 *
 * @author Diego Urrutia-Astorga.
 * @version 0.0.1
 */
public final class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final int PORT = 9000;

    private static List<ChatMessage> messages = new LinkedList<ChatMessage>();



    /**
     * The Ppal.
     */
    public static void main(final String[] args) throws IOException {

        log.debug("Starting the Main ..");

        // The Server Socket
        final ServerSocket serverSocket = new ServerSocket(PORT);

        // serverSocket.setReuseAddress(true);
        log.debug("Server started in port {}, waiting for connections ..", PORT);

        // Forever serve.
        while (true) {

            // One socket by request (try with resources).
            try (final Socket socket = serverSocket.accept()) {

                // The remote connection address.
                final InetAddress address = socket.getInetAddress();

                log.debug("========================================================================================");
                log.debug("Connection from {} in port {}.", address.getHostAddress(), socket.getPort());
                processConnection(socket);

            } catch (IOException e) {
                log.error("Error", e);
                throw e;
            }

        }

    }

    /**
     * Process the connection.
     *
     * @param socket to use as source of data.
     */
    private static void processConnection(final Socket socket) throws IOException {

        // Reading the inputstream
        final List<String> lines = readSocketInput(socket);
        //log.debug(lines.toString());
        final String request = lines.get(0);
        log.debug("Request: {}", request);

        final PrintWriter pw = new PrintWriter(socket.getOutputStream());


        if (request.contains("GET")) {

            log.debug("GET REQUEST");
            //final PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println("HTTP/1.1 200 OK");
            pw.println("Server: DSM v0.0.1");
            pw.println("Date: " + new Date());
            pw.println("Content-Type: text/html; charset=UTF-8");
            pw.println();
            pw.println(pageForm());
            pw.println();
            pw.flush();

        } else if (request.contains("POST")) {
            log.debug("POST REQUEST");

            log.debug("Input body read");

            for (int i = 0; i < lines.size(); i++) {
                log.debug("***** " + lines.get(i));
                //if(lines.get(i).contains("username"))addMessage(lines);
            }


            if (addMessage(lines)) {

                pw.println("HTTP/1.1 200 OK");
                pw.println("Server: DSM v0.0.1");
                pw.println("Date: " + new Date());
                pw.println("Content-Type: text/html; charset=UTF-8");
                pw.println();
                pw.println(pageForm());
                pw.println();
                pw.flush();

                log.debug("MESSAGE ADDED SUCCESFULLY");

            } else {

                pw.println("HTTP/1.1 400 ERROR");
                pw.println("Server: DSM v0.0.1");
                pw.println();
                pw.flush();

            }

        } else {
            log.debug("ERROR REQUEST");
            //final PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println("HTTP/1.1 400 ERROR");
            pw.println("Server: DSM v0.0.1");
            pw.println();
            pw.flush();
        }


        log.debug("Process ended.");

    }

    /**
     * Read all the input stream.
     *
     * @param socket to use to read.
     * @return all the string readed.
     *
     */
    private static List<String> readInputStreamByLines(final Socket socket) throws IOException {

        final InputStream is = socket.getInputStream();

        // The list of string readed from inputstream.
        final List<String> lines = new ArrayList<>();

        // The Scanner
        final Scanner s = new Scanner(is).useDelimiter("\\A");
        log.debug("Reading the Inputstream ..");


        while (true) {


            final String line = s.nextLine();
            // log.debug("Line: [{}].", line);

            if (line.length() == 0) {
                break;
            } else {
                lines.add(line);
            }
        }
        // String result = s.hasNext() ? s.next() : "";

        // final List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
        return lines;

    }

    /**
     * This method
     *
     * @return : A String that is the page.
     *
     */

    private static String pageForm()throws IOException{

        List<String> messageV = new LinkedList<>();
        List<String> userNameV = new LinkedList<>();

        StringBuffer stringBuffer= new StringBuffer();
        stringBuffer.append("<html lang=\"en\">" );
        stringBuffer.append("<head>");
        stringBuffer.append("<meta charset=\"UTF-8\">");
        stringBuffer.append("<title>Chat</title>");
        stringBuffer.append("</head>");
        stringBuffer.append("<body>");
        stringBuffer.append("<div id=\"chat-window\">");
        String html = stringBuffer.toString();
        for (ChatMessage i : messages) {
            String message = i.getMessage();
            String userName = i.getUserName();
            messageV.add(message);
            userNameV.add(userName);
        }
        String[] userNameVec = new String[userNameV.size()];
        String[] messageVec = new String[messageV.size()];
        for (int i = 0 ; i < userNameV.size() ; i++){
            userNameVec[i]= userNameV.get(i);
            messageVec[i]=messageV.get(i);
        }
        if (userNameV.size()== 0 ){
            stringBuffer.append("<p> write something .</p>\n");
        html += stringBuffer.toString();
        }
        else{
            for (int i = 0 ; i < userNameV.size()-1 ; i++){
                if(userNameVec[i+1].equals(userNameVec[i])){

                }else{
                    html += "<p style=\"color:red;\">"+ userNameVec[i+1] +" </p>\n";
                    html += "<p style=\"color:blue;\">"+ messageVec[i+1] +" </p>\n";
                }


            }
        }
        stringBuffer.append("</div>\n ");
        stringBuffer.append("<div id=\"chat-input\">\n");
        stringBuffer.append("<form action=\"/\" method=\"post\" >\n");
        stringBuffer.append("<label for=\"name\">username:</label>");
        stringBuffer.append("<input type=\"text\" name=\"username\">\n");
        stringBuffer.append("<label for=\"name\">message:</label>");
        stringBuffer.append("<input type=\"text\" name=\"message\">\n");
        stringBuffer.append("<input type=\"submit\" value=\"Send\">\n");
        stringBuffer.append("</form>\n");
        stringBuffer.append("</div>\n");
        stringBuffer.append("</body>\n");
        stringBuffer.append("</html>\n");
                      html+=stringBuffer.toString();

        return html;

    }

    /**
     * This method read the input stream from a socket.
     *
     * @param socket : The socket to be readed.
     * @return : A List<String> with the lines readed.
     * @throws IOException .
     */
    public static List<String> readSocketInput(Socket socket) throws IOException {

        List<String> input = new ArrayList<String>();
        InputStream is = socket.getInputStream();
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        String line = "";

        while (true) {

            line = bf.readLine();

            boolean isOpen = true;

            try {
                isOpen = bf.ready();
            } catch (Exception e) {
                isOpen = false;
            }

            if ((line == null || line.isEmpty()) && !isOpen) {

                log.debug(" * LINE:" + line + " BF STATUS" + bf.ready());
                break;

            } else if (line.isEmpty() && isOpen) {

                /*
                log.debug(" * SENDING CONTINUE * ");
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.println("HTTP/1.1 100 Continue");
                pw.println();
                pw.flush();
                */

                int contentLength = 0;

                for (String s : input) {
                    if (s.contains("Content-Length:")) {
                        contentLength = Integer.parseInt(s.substring(16));
                    }
                }


                // int contentLength = Integer.parseInt(input.get(3).substring(16));

                log.debug("CONTENT LENGTH: " + contentLength);


                char[] chars = new char[contentLength];

                for (int i = 0; i < contentLength; i++) {
                    chars[i] = (char) bf.read();

                }

                input.add(new String(chars));


                log.debug("CLOSING CONNECTION");
                break;

            } else {
                log.debug("LINE:" + line + " BF STATUS" + bf.ready());
                input.add(line);
            }

        }

        if (input.isEmpty()) {
            input.add("ERROR");
        }
        return input;
    }

    /**
     * This method read the input stream from a socket.
     *
     * @param input : The input that enter from a screen.
     * @return : A boolean datum, if added a message.
     *
     */
    public static boolean addMessage(List<String> input) {

        if (input.isEmpty()) {
            return false;
        }

        String bodyContent = input.get(input.size() - 1);
        bodyContent = bodyContent.replace("username=", "");
        bodyContent = bodyContent.replace("message=", "");

        String username = bodyContent.substring(0, bodyContent.indexOf('&'));
        String message = bodyContent.substring(bodyContent.indexOf('&') + 1, bodyContent.length());

        message = message.replace('+', ' ');


        log.debug("USERNAME: " + username + " MESSAGE: " + message);

        ChatMessage newMessage = new ChatMessage(username, message);
        messages.add(newMessage);

        return true;
    }

}
