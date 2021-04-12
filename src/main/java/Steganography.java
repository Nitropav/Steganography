import java.io.*;

public class Steganography {

    private static final String RUS_LETTERS = "КАМОНВЕРХСТорухаес";
    private static final String ENG_LETTERS = "KAMOHBEPXCTopyxaec";
    private static final File FILE_TEXT = new File("src/test/java/task.txt");
    private static final File FILE_TO_ENCODE = new File("src/test/java/text_to_encode.txt");
    private static final File FILE_ENCODE = new File("src/test/java/text_encode.txt");
    private static final File FILE_DECODE = new File("src/test/java/decode.txt");
    private BufferedReader bufferedReaderToEncode = new BufferedReader(new FileReader(FILE_TO_ENCODE));
    private BufferedReader bufferedReaderFileText = new BufferedReader(new FileReader(FILE_TEXT));
    private BufferedReader bufferedReaderFileToEncode = new BufferedReader(new FileReader(FILE_TO_ENCODE));
    private BufferedReader bufferedReaderFileEncode = new BufferedReader(new FileReader(FILE_ENCODE));
    private BufferedWriter bufferedWriterFileEncode = new BufferedWriter(new FileWriter(FILE_ENCODE));
    private BufferedWriter bufferedWriterFileDecode = new BufferedWriter(new FileWriter(FILE_DECODE));

    public Steganography() throws IOException {
    }

    public void encode() throws IOException {
        int symbolFromFileTxt = 0;
        int letterForEncode = 0;
        int encodedBits = 8;

        encodeMessageBySymbolAndBits(symbolFromFileTxt, encodedBits, letterForEncode);

        while ((symbolFromFileTxt = bufferedReaderFileText.read()) != -1) {
            bufferedWriterFileEncode.write(symbolFromFileTxt);
        }

        bufferedReaderFileText.close();
        bufferedReaderFileToEncode.close();
        bufferedWriterFileEncode.close();
    }

    public String encodeMessageBySymbolAndBits(int symbolFromFileTxt, int encodedBits, int letterForEncode) throws IOException {
        StringBuilder rusLettersStringBuilder = new StringBuilder();

        while (symbolFromFileTxt != -1) {
            symbolFromFileTxt = bufferedReaderFileText.read();

            if (ENG_LETTERS.indexOf(symbolFromFileTxt) != -1) {
                if (encodedBits == 8) {
                    letterForEncode = bufferedReaderFileToEncode.read();

                    if (letterForEncode == -1) {
                        bufferedWriterFileEncode.write(symbolFromFileTxt);
                        break;
                    }

                    encodedBits = 0;
                }

                int bitFromLetter = (letterForEncode & 0b10000000) >> 7;

                if (bitFromLetter == 1) {
                    symbolFromFileTxt = RUS_LETTERS.charAt(ENG_LETTERS.indexOf(symbolFromFileTxt));
                    rusLettersStringBuilder.append((char) symbolFromFileTxt);
                }

                letterForEncode <<= 1;
                letterForEncode %= 256;
                encodedBits += 1;
            }

            bufferedWriterFileEncode.write(symbolFromFileTxt);
        }

        return rusLettersStringBuilder.toString();
    }

    public String decode() throws IOException {
        StringBuilder builderResultLine = new StringBuilder();
        int sizeToEncode = bufferedReaderToEncode.readLine().length();

        bufferedReaderToEncode.close();

        int counterReading = 0;
        int bitsRead = 0;
        byte bytes = 0;

        decodeMessageBySymbolsAndBits(counterReading, sizeToEncode, bitsRead, bytes, builderResultLine);

        bufferedReaderFileEncode.close();
        bufferedWriterFileDecode.close();

        return builderResultLine.toString();
    }

    public void decodeMessageBySymbolsAndBits(int counterReading, int sizeToEncode, int bitsRead,
                                              byte bytes, StringBuilder builderResultLine) throws IOException {
        while (counterReading < sizeToEncode) {
            int symbol = bufferedReaderFileEncode.read();
            if (symbol == -1) {
                break;
            }

            if (ENG_LETTERS.indexOf(symbol) != -1) {
                bytes <<= 1;
                bitsRead += 1;
            } else if (RUS_LETTERS.indexOf(symbol) != -1) {
                bytes <<= 1;
                bytes |= 1;
                bitsRead += 1;
            }

            if (bitsRead == 8) {
                builderResultLine.append((char) bytes);
                bufferedWriterFileDecode.write((char) bytes);
                counterReading += 1;
                bitsRead = 0;
                bytes = 0;
            }
        }
    }
}