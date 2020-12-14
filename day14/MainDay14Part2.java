import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainDay14Part2 {

    public static final String MASK = "mask = ";

    public static void main(String[] args) throws IOException {
        final String inputPath = "src/day14/input.txt";
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
                
                final List<Long> allMemoryIndices = getAllMemoryIndices(mask, memoryIndex);
                allMemoryIndices.forEach(singleMemoryIndex -> memory.put(singleMemoryIndex, memoryValue));
            }
        }
        System.out.println("Sum of all: " + memory.values().stream().reduce(0L, Long::sum));
    }

    private static List<Long> getAllMemoryIndices(final String mask, final long memoryIndex) {
        final List<Long> memoryIndices = new ArrayList<>();
        final String memoryIndexTemplate = applyMask(mask, memoryIndex);

        System.out.println("Starting to resolve.");
        resolveFloatingBits(memoryIndexTemplate, "", memoryIndices);
        memoryIndices.forEach(mem -> System.out.println("\t" + mem));
        
        return memoryIndices;
    }

    private static void resolveFloatingBits(final String memoryIndexTemplate, final String memoryIndex, final List<Long> memoryIndices) {
        if (memoryIndexTemplate.length() == memoryIndex.length()) {
            memoryIndices.add(Long.parseLong(memoryIndex, 2));
        } else if (memoryIndexTemplate.charAt(memoryIndex.length()) == 'X') {
            resolveFloatingBits(memoryIndexTemplate, memoryIndex + "0", memoryIndices);
            resolveFloatingBits(memoryIndexTemplate, memoryIndex + "1", memoryIndices);
        } else {
            resolveFloatingBits(memoryIndexTemplate, memoryIndex + memoryIndexTemplate.charAt(memoryIndex.length()), memoryIndices);
        }
    }

    private static String applyMask(final String mask, final long value) {
        final String memoryAsValueString = padLeftZeros(Long.toBinaryString(value), 36);
        
        final StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < 36; i++) {
            if (mask.charAt(i) == '0') {
                resultBuilder.append(memoryAsValueString.charAt(i));
            } else {
                resultBuilder.append(mask.charAt(i));
            }
        }

        return resultBuilder.toString();
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
