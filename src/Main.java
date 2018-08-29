import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    private static int initialWidth = 7;

    private Main() {
        String toCompress =
                "Hello, my name is Jasper and I'm trying to implement the Lempel Ziv Welch compression / decompression algorithm.\n" +
                        "This is a lossless data compression algorithm created by Abraham Lempel, Jacob Ziv, and Terry Welch.\n" +
                        "It was published by Welch in 1984 as an improved implementation of the LZ78 algorithm published by Lempel and Ziv in 1978.\n" +
                        "- Wikipedia";
        List<Byte> compressed = compress(toCompress);

        List<Integer> compressedInts = new ArrayList<>();
        for (byte b : compressed)
            compressedInts.add(b & 0xFF);

        String decompressed = decompress(compressedInts);
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

    class BitsToWrite {
        private int amountOfBits;
        private int value;

        public BitsToWrite(int amountOfBits, int value) {
            this.amountOfBits = amountOfBits;
            this.value = value;
        }

        public int getAmountOfBits() {
            return amountOfBits;
        }

        public void setAmountOfBits(int amountOfBits) {
            this.amountOfBits = amountOfBits;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private List<Byte> compress(String input) {
        int currentCodeSize = initialWidth + 1;
        final List<BitsToWrite> output = new ArrayList<>();

        List<String> dictionary = initializeDictionary();

        final StringBuilder previous = new StringBuilder();
        char current;

        for (int i = 0; i < input.length(); i++) {
            current = input.charAt(i);
            if (dictionary.contains(previous.toString() + current))
                previous.append(current);
            else {
                int key = dictionary.indexOf(previous.toString());
                output.add(new BitsToWrite(currentCodeSize, key));
                dictionary.add(previous.toString() + current);
                previous.delete(0, previous.length());
                previous.append(current);
                if (dictionary.size() == Math.pow(2, currentCodeSize) - 1)
                    currentCodeSize++;
            }
        }

        int key = dictionary.indexOf(previous.toString());
        output.add(new BitsToWrite(currentCodeSize, key));


        List<Byte> bytes = fillBytesArray(output);
        List<Integer> collect = output.stream().map(BitsToWrite::getValue).collect(Collectors.toList());
        return bytes;
    }

    private List<Byte> fillBytesArray(List<BitsToWrite> list) {
        List<Byte> bytes = new ArrayList<>();
        int bitOffset = 0;
        for (BitsToWrite toWrite : list) {
            int indexToWriteToByteArray = toWrite.getValue();
            int amountOfBitsToWrite = toWrite.getAmountOfBits();

            int firstByteIndex = bitOffset / 8;
            if (bytes.size() == firstByteIndex)
                bytes.add((byte) 0);
            int firstByteValue = bytes.get(firstByteIndex);

            int amountOfBitsToWriteInFirstByte = Math.min(8 - (bitOffset % 8), amountOfBitsToWrite);
            amountOfBitsToWrite -= amountOfBitsToWriteInFirstByte;
            int amountOfBitsToWriteInSecondByte = Math.min(8, amountOfBitsToWrite);
            amountOfBitsToWrite -= amountOfBitsToWriteInSecondByte;
            int amountOfBitsToWriteInThirdByte = Math.min(8, amountOfBitsToWrite);

            byte a = (byte) (firstByteValue | ((indexToWriteToByteArray & ((int) Math.pow(2, amountOfBitsToWriteInFirstByte) - 1)) << (bitOffset % 8)));

            bytes.set(firstByteIndex, a);
            if (amountOfBitsToWriteInSecondByte > 0) {
                byte secondByteValue = (byte) ((indexToWriteToByteArray >> amountOfBitsToWriteInFirstByte) & ((int) Math.pow(2, amountOfBitsToWriteInSecondByte) - 1));
                bytes.add(secondByteValue);
            }
            if (amountOfBitsToWriteInThirdByte > 0) {
                byte thirdByteValue = (byte) (indexToWriteToByteArray >> (amountOfBitsToWriteInFirstByte + amountOfBitsToWriteInSecondByte));
                bytes.add(thirdByteValue);
            }

            bitOffset += toWrite.getAmountOfBits();
        }

        return bytes;
    }

    private String decompress(List<Integer> input) {
        StringBuilder output = new StringBuilder();
        int currentCodeSize = initialWidth + 1;
        int bitOffset = 0;

        List<String> dictionary = initializeDictionary();

        int previousIndex = input.get(0);
        String previousValue = dictionary.get(previousIndex);
        output.append(previousValue);
        bitOffset += currentCodeSize;

        do {
            String currentValue;
            int code = readBits(input, bitOffset, currentCodeSize);
            bitOffset += currentCodeSize;
            if (code == dictionary.size())
                currentValue = previousValue + previousValue.charAt(0);
            else
                currentValue = dictionary.get(code);
            output.append(currentValue);
            char firstCharOfCurrentValue = currentValue.charAt(0);
            dictionary.add(dictionary.get(previousIndex) + firstCharOfCurrentValue);
            if (dictionary.size() == Math.pow(2, currentCodeSize) - 2)
                currentCodeSize++;
            previousIndex = code;
        } while (bitOffset / 8 != input.size());

        return output.toString();
    }

    private static int readBits(List<Integer> bytes, int bitOffset, int amountOfBitsToRead) {
        int firstByteValue, secondByteValue = 0, thirdByteValue = 0;
        int byteIndex = bitOffset / 8;
        int amountOfBitsToReadInFirstByte = Math.min(8 - (bitOffset % 8), amountOfBitsToRead);
        amountOfBitsToRead -= amountOfBitsToReadInFirstByte;
        int amountOfBitsToReadInSecondByte = Math.min(8, amountOfBitsToRead);
        amountOfBitsToRead -= amountOfBitsToReadInSecondByte;
        int amountOfBitsToReadInThirdByte = Math.min(8, amountOfBitsToRead);
        firstByteValue = (bytes.get(byteIndex) >> (bitOffset % 8)) & (int) (Math.pow(2, amountOfBitsToReadInFirstByte) - 1);
        if (bytes.size() > byteIndex + 1)
            secondByteValue = bytes.get(byteIndex + 1) & (int) (Math.pow(2, amountOfBitsToReadInSecondByte) - 1);
        if (bytes.size() > byteIndex + 2)
            thirdByteValue = bytes.get(byteIndex + 2) & (int) (Math.pow(2, amountOfBitsToReadInThirdByte) - 1);
        return firstByteValue | (secondByteValue << amountOfBitsToReadInFirstByte) | (thirdByteValue << (amountOfBitsToReadInFirstByte + amountOfBitsToReadInSecondByte));
    }
}
