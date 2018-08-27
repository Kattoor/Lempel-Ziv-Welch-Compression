import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class LZWServer implements Runnable {

    private ServerSocket server;

    LZWServer() throws IOException {
        server = new ServerSocket(43594);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client = server.accept();
                InputStreamReader reader = new InputStreamReader(client.getInputStream());
                while (true) {
                    char b1 = (char) reader.read();
                    char b2 = (char) reader.read();
                    char b3 = (char) reader.read();
                    char b4 = (char) reader.read();
                    int i = b1 | (b2 << 8) | (b3 << 16) | (b4 << 24);
                    System.out.print(decompress(i));
                }
            } catch (SocketException e) {
                System.out.println("Client disconnected!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> initializeDictionary() {
        List<String> dictionary = new ArrayList<>();
        for (int i = 0; i < 128; i++)
            dictionary.add(String.valueOf((char) i));
        return dictionary;
    }

    private List<String> dictionary = initializeDictionary();
    private StringBuilder output = new StringBuilder();
    private int previousIndex;
    private String previousValue;
    private Integer currentIndex;

    private String decompress(Integer input) {
        if (output.length() == 0) {
            previousIndex = input;
            previousValue = dictionary.get(previousIndex);
            output.append(previousValue);
        } else {
            currentIndex = input;
            String currentValue;
            if (currentIndex == dictionary.size())
                currentValue = previousValue + previousValue.charAt(0);
            else
                currentValue = dictionary.get(currentIndex);
            output.append(currentValue);
            char firstCharOfCurrentValue = currentValue.charAt(0);
            dictionary.add(dictionary.get(previousIndex) + firstCharOfCurrentValue);
            previousIndex = currentIndex;
        }
        System.out.println(output.toString());
        return output.toString();
    }
}
