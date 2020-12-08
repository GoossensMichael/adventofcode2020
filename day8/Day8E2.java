import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;

public class Day8E2 {

    public static void main(String[] args) throws IOException {
        final List<String> input = Files.readAllLines(Paths.get("/Users/mgoossens/personal/adventofcode/day8/input.txt"), StandardCharsets.UTF_8);

        String backupInstruction = null;
        int backupInstructionPosition = 0;
        
        final Stack<Integer> executedInstructions = new Stack<>();
        final Stack<Integer> executedInstructionsInInfiniteLoop = new Stack<>();
        boolean infiniteLoopDetected = false;
        
        int pointer = 0;
        int accumulator = 0;
        while (pointer < input.size()) {
            if (executedInstructions.contains(pointer)) {
                if (!infiniteLoopDetected) {
                    infiniteLoopDetected = true;
                    executedInstructionsInInfiniteLoop.addAll(executedInstructions);
                }

                // wrong guess? reset.
                if (backupInstruction != null) {
                    input.set(backupInstructionPosition, backupInstruction);
                    backupInstruction = null;
                }
                
                // next possible change
                while (backupInstruction == null && !executedInstructionsInInfiniteLoop.isEmpty()) {
                    final int possibleCorruptInstructionPosition = executedInstructionsInInfiniteLoop.pop();
                    final String possibleCorruptInstruction = input.get(possibleCorruptInstructionPosition);
                    if (possibleCorruptInstruction.matches("(nop.*)|(jmp.*)")) {
                        backupInstruction = possibleCorruptInstruction;
                        backupInstructionPosition = possibleCorruptInstructionPosition;
                        if (possibleCorruptInstruction.startsWith("nop")) {
                            input.set(possibleCorruptInstructionPosition, "jmp" + input.get(possibleCorruptInstructionPosition).substring(3));
                        } else {
                            input.set(possibleCorruptInstructionPosition, "nop" + input.get(possibleCorruptInstructionPosition).substring(3));
                        }
                    }
                }
                
                //restart program
                pointer = 0;
                accumulator = 0;
                executedInstructions.clear();
                
            } else {
                // infinite loop is caused due to a statement that was already executed
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
        System.out.println("Program finished with accumulator: " + accumulator);
    }


}
