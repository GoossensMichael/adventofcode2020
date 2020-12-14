import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainDay14Part1 {

    public static final String MASK = "mask = ";

    public static void main(String[] args) throws IOException {
        final String inputPath = "input.txt";
        final List<String> instructions = Files.lines(Paths.get(inputPath), StandardCharsets.UTF_8)
                .collect(Collectors.toList());

        String mask = "";
        final Map<Long, Long> memory = new HashMap<>();
        for (final String instruction : instructions) {
            if (instruction.startsWith(MASK)) {
                mask = instruction.substring(MASK.length());
            } else {
                final int memoryIndexEnd = instruction.indexOf(']');
                final long memoryIndex = Long.parseLong(instruction.substring(4, memoryIndexEnd));
                final long memoryValue = Long.parseLong(instruction.substring(memoryIndexEnd + 4));
                memory.put(memoryIndex, applyMask(mask, memoryValue));
            }
        }
        System.out.println("Sum of all: " + memory.values().stream().reduce(0L, Long::sum));
    }

    private static long applyMask(final String mask, final long memoryValue) {
        final String memoryAsValueString = padLeftZeros(Long.toBinaryString(memoryValue), 36);
        
        final StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < 36; i++) {
            if (mask.charAt(i) == 'X') {
                resultBuilder.append(memoryAsValueString.charAt(i));
            } else {
                resultBuilder.append(mask.charAt(i));
            }
        }

        return Long.parseLong(resultBuilder.toString(), 2);
    }

    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }
    
}
