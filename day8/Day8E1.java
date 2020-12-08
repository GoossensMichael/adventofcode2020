import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day8E1 {

    public static void main(String[] args) throws IOException {
        final List<String> input = Files.readAllLines(Paths.get("/Users/mgoossens/personal/adventofcode/day8/input.txt"), StandardCharsets.UTF_8);
        
        final Set<Integer> executedInstructions = new HashSet<>();
        boolean infiniteLoopDetected = false;
        int pointer = 0;
        int accumulator = 0;
        while (!infiniteLoopDetected) {
            if (executedInstructions.contains(pointer)) {
                infiniteLoopDetected = true;
                System.out.println("Infinite loop detected, value of acc: " + accumulator);
            } else {
                executedInstructions.add(pointer);

                final String instruction = input.get(pointer);
                final String opCode = instruction.substring(0, 3);
                final int opValue = Integer.parseInt(instruction.substring(4));
                switch (opCode) {
                    case "nop":
                        pointer++;
                        break;
                    case "acc":
                        accumulator += opValue;
                        pointer++;
                        break;
                    case "jmp":
                        pointer += opValue;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operation: '" + opCode + "'");
                }
            }
        }
    }


}
