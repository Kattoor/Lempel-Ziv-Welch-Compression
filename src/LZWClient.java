import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LZWClient {

    private Socket socket;

    public static void main(String[] args) throws IOException {
        new LZWClient();
    }

    public LZWClient() throws IOException {
        socket = new Socket("localhost", 43594);
        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
        List<Integer> compressed = compress("Hello, World!");
        char[] bytes = new char[compressed.size() * 4];
        for (int i = 0; i < compressed.size(); i++) {
            int c = compressed.get(i);
            System.out.print(c + " ");
            bytes[i * 4] = (char) (c & 0xff);
            bytes[i * 4 + 1] = (char) ((c >> 8) & 0xff);
            bytes[i * 4 + 2] = (char) ((c >> 16) & 0xff);
            bytes[i * 4 + 3] = (char) (c >> 24);
        }
        writer.write(bytes);
        writer.flush();
    }

    private List<String> initializeDictionary() {
        List<String> dictionary = new ArrayList<>();
        for (int i = 0; i < 128; i++)
            dictionary.add(String.valueOf((char) i));
        return dictionary;
    }

    private List<Integer> compress(String input) {
        final List<Integer> output = new ArrayList<>();

        List<String> dictionary = initializeDictionary();

        final StringBuilder previous = new StringBuilder();
        char current;

        for (int i = 0; i < input.length(); i++) {
            current = input.charAt(i);
            if (dictionary.contains(previous.toString() + current))
                previous.append(current);
            else {
                int key = dictionary.indexOf(previous.toString());
                output.add(key);
                dictionary.add(previous.toString() + current);
                previous.delete(0, previous.length());
                previous.append(current);
            }
        }

        int key = dictionary.indexOf(previous.toString());
        output.add(key);

        return output;
    }
}
