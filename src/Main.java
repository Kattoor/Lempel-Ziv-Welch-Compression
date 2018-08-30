import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        String toCompress =
                "Hello, my name is Jasper and I'm trying to implement the Lempel Ziv Welch compression / decompression algorithm.\n" +
                        "This is a lossless data compression algorithm created by Abraham Lempel, Jacob Ziv, and Terry Welch.\n" +
                        "It was published by Welch in 1984 as an improved implementation of the LZ78 algorithm published by Lempel and Ziv in 1978.\n" +
                        "- Wikipedia";
        List<Integer> compressed = compress(toCompress);
        String decompressed = decompress(compressed);
        System.out.println(decompressed);
        System.out.printf("\nThe original text consists of %d bytes, of which %d remain after compression.\n", decompressed.getBytes().length, compressed.size());
        System.out.printf("This is a reduction of approximately %.2f%%.\n", (1 - compressed.size() / (float) decompressed.getBytes().length) * 100);
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

    private String decompress(List<Integer> input) {
        StringBuilder output = new StringBuilder();

        List<String> dictionary = initializeDictionary();

        String previousValue = dictionary.get(input.get(0));
        output.append(previousValue);

        for (int currentIndex : input.subList(1, input.size())) {
            String currentValue = currentIndex == dictionary.size() ?
                    previousValue + previousValue.charAt(0) :
                    dictionary.get(currentIndex);

            output.append(currentValue);
            dictionary.add(previousValue + currentValue.charAt(0));

            previousValue = currentValue;
        }

        return output.toString();
    }
}
