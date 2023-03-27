import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        System.out.println("Client!");
        Scanner scanner = null;
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 8300);

            // Set up input and output streams
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            // Initialize scanner for user input
            scanner = new Scanner(System.in);

            // Main loop for user interaction
            while (true) {
                // Display menu options
                System.out.println("--------------------------------------------");
                System.out.println("Choose one option:");
                System.out.println("1. GET all pets");
                System.out.println("2. GET pets by species");
                System.out.println("3. Quit");
                System.out.println("--------------------------------------------");

                int option = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                if (option == 3) {
                    break;
                }

                // Handle user selection
                switch (option) {
                    case 1 -> {
                        // Prepare GET request for all pets
                        String getRequest = "GET / HTTP/1.1\r\n";
                        getRequest += "Connection: keep-alive\r\n";
                        getRequest += "\r\n";
                        bufferedWriter.write(getRequest);
                        bufferedWriter.flush();
                    }
                    case 2 -> {
                        // Prompt user for species and prepare GET request
                        System.out.println("Enter species:");
                        String species = scanner.nextLine();
                        String getRequest = "GET /pets?species=" + species + " HTTP/1.1\r\n";
                        getRequest += "Connection: keep-alive\r\n";
                        getRequest += "\r\n";
                        bufferedWriter.write(getRequest);
                        bufferedWriter.flush();
                    }
                    default -> System.out.println("Invalid option");
                }

                // Read and display the server respons

                String line;
                StringBuilder response = new StringBuilder();

                int contentLength = -1;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
                    }

                    if (line.isEmpty()) {
                        break;
                    }
                }

                if (contentLength > 0) {
                    char[] contentBuffer = new char[contentLength];
                    bufferedReader.read(contentBuffer);
                    response.append(new String(contentBuffer));
                }
                System.out.println(response);

                // Clear the response StringBuilder for the next iteration
                response.setLength(0);
            }

            // Close resources
            bufferedReader.close();
            bufferedWriter.close();
            inputStreamReader.close();
            outputStreamWriter.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}