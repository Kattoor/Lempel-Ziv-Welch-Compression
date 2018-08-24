import java.util.ArrayDeque;
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
        ArrayDeque<Integer> compressed = compress(toCompress);
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

    private ArrayDeque<Integer> compress(String input) {
        final ArrayDeque<Integer> output = new ArrayDeque<>();

        List<String> dictionary = initializeDictionary();

        final StringBuilder previous = new StringBuilder();
        char current;

        for (int i = 0; i < input.length(); i++) {
            current = input.charAt(i);
            if (dictionary.contains(previous.toString() + current))
                previous.append(current);
            else {
                int key = dictionary.indexOf(previous.toString());
                output.offerLast(key);
                dictionary.add(previous.toString() + current);
                previous.delete(0, previous.length());
                previous.append(current);
            }
        }

        int key = dictionary.indexOf(previous.toString());
        output.offerLast(key);

        return output;
    }

    private String decompress(ArrayDeque<Integer> input) {
        StringBuilder output = new StringBuilder();

        List<String> dictionary = initializeDictionary();

        int previousIndex = input.pollFirst();
        String previousValue = dictionary.get(previousIndex);
        output.append(previousValue);

        Integer currentIndex;
        while ((currentIndex = input.pollFirst()) != null) {
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

        return output.toString();
    }
}
