import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        String toCompress =
                "Let's suppose that you were able every night to dream any dream you wanted to dream, \n" +
                        "and you would naturally as you began on this adventure of dreams, you would fulfill all your wishes.\n" +
                        "You would have every kind of pleasure, you see, and after several nights you would say, \"well that was pretty great.\"\n" +
                        "But now let's have a surprise, let's have a dream which isn't under control.\n" +
                        "Well something is going to happen to me that I don't know what it's gonna be.\n" +
                        "Then you would get more and more adventurous, and you would make further and further out gambles as to what you would dream, \n" +
                        "and finally you would dream where you are now.\n" +
                        "- Alan Watts";
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

        int previousIndex = input.get(0);
        String previousValue = dictionary.get(previousIndex);
        output.append(previousValue);

        for (int i = 1; i < input.size(); i++) {
            int currentIndex = input.get(i);
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
