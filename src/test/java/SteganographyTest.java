import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runners.Parameterized;

import java.io.*;

class SteganographyTest {

    private static final Logger LOGGER = Logger.getLogger(SteganographyTest.class.getName());
    private static Steganography steganography;
    private Steganography steganographyTestResult = new Steganography();
    private Steganography steganographyResultLine = new Steganography();

    SteganographyTest() throws IOException {
    }

    @Parameterized.Parameters
    @MethodSource("decodeLineResult")
    static String getDecodeText() {
        StringBuilder stringBuilderFileResult = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/test/java/decode.txt"));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilderFileResult.append(line);
            }
        } catch (IOException e) {
            LOGGER.info("Error reading file");
        }

        return stringBuilderFileResult.toString();
    }

    @Parameterized.Parameters
    @MethodSource("decodeRusLetter")
    static String getLetterDecode() throws IOException {
        StringBuilder stringBuilderFileResult = new StringBuilder();
        BufferedWriter bufferedWriterLetterDecode = new BufferedWriter(new FileWriter("src/test/java/rusletter.txt"));

        bufferedWriterLetterDecode.write(steganography.encodeMessageBySymbolAndBits(0, 8, 0));

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/test/java/rusletter.txt"));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilderFileResult.append(line);
            }
        } catch (IOException e) {
            LOGGER.info("Error reading file");
        }

        return stringBuilderFileResult.toString();
    }

    @BeforeEach
    void initialization() throws IOException {
        steganography = new Steganography();
        steganographyTestResult.encode();
        steganographyResultLine.encode();
    }

    @Test
    void decodeLineResult() throws IOException {
        LOGGER.info("Decode text: " + steganographyResultLine.decode());
        assertEquals(steganographyTestResult.decode(), getDecodeText());
    }

    @Test
    void decodeRusLetter() throws IOException {
        assertEquals(getLetterDecode(), steganography.encodeMessageBySymbolAndBits(0, 8, 0));
    }
}
