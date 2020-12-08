import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day8E2Alt1 {

    public static void main(String[] args) throws IOException {
        final List<String> input = Files.readAllLines(Paths.get("/Users/mgoossens/personal/adventofcode/day8/input.txt"), StandardCharsets.UTF_8);

        
        final AtomicInteger lineCounter = new AtomicInteger(0);
        final Map<Integer, Instruction> instructions = input.stream()
                .map(instructionLine ->  new Instruction(lineCounter.getAndIncrement(), instructionLine.split(" ")))
                .collect(Collectors.toMap(Instruction::getLine, Function.identity()));
        
        // part 1
        final Set<Integer> instructionsUntilLoop = execute(instructions);

        // part 2
        repair(instructions, instructionsUntilLoop);
        execute(instructions);
    }

    private static void repair(final Map<Integer, Instruction> instructions, final Set<Integer> topDownInstructions) {
        final Set<Integer> keys = new HashSet<Integer>() {{ addAll(instructions.keySet()); }};
        // first line is always reachable
        keys.remove(0);
        
        // Make complete graph of all instructions
        // An instruction is reachable by other instructions
        // An instruction is reaches one other instruction
        instructions.values().forEach(instruction -> {
            // Calculate the next line
            final int nextLine = (instruction.opCode != OpCode.JMP) ? instruction.line + 1 : instruction.line + instruction.arg;
            // When the next line is the end of the program do nothing
            if (nextLine < instructions.size()) {
                final Instruction nextInstruction = instructions.get(nextLine);
                instruction.nextInstruction = nextInstruction;
                nextInstruction.getPreviousInstructions().add(instruction);
                keys.remove(nextLine);
            }
        });

        // Get the highest instruction
        final Integer finalInstruction = instructions.keySet().stream().max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("Input data is not correct, no final instruction found."));
        // Make a complete list of instructions (the line numbers) that can reach the end.
        final Set<Integer> instructionsToTheEnd = new HashSet<>();
        final Stack<Instruction> workToDo = new Stack<>();
        workToDo.add(instructions.get(finalInstruction));
        while (!workToDo.isEmpty()) {
            final Instruction pop = workToDo.pop();
            instructionsToTheEnd.add(pop.line);
            pop.getPreviousInstructions().stream()
                    .filter(instruction -> !instructionsToTheEnd.contains(instruction.getLine()))
                    .forEach(workToDo::add);
        }
        
        // Search in the list of instructions from the beginning of the program until the infinite loop
        // that can reach one of the instructions that can reach the end of the program when it would be "repaired".
        // Also include the case when a JMP would be made to a line outside the program.
        for (final Integer topDownInstructionLine : topDownInstructions) {
            final Instruction topDownInstruction = instructions.get(topDownInstructionLine);
            if (topDownInstruction.opCode != OpCode.ACC) {
                final int pointer = (topDownInstruction.opCode == OpCode.JMP) ? topDownInstruction.getLine() + 1 : topDownInstruction.getLine() + topDownInstruction.arg;
                if (instructionsToTheEnd.contains(pointer) || pointer >= instructions.size()) {
                    System.out.printf("Found the corrupt instruction: %s.\n", topDownInstruction);
                    topDownInstruction.opCode = (topDownInstruction.opCode == OpCode.JMP) ? OpCode.NOP : OpCode.JMP;
                    System.out.printf("Repaired the corrupt instruction to: %s.\n", topDownInstruction);
                    break;
                }
            }
        }
    }

    private enum OpCode {
        ACC, JMP, NOP
    }
    
    private static final class Instruction {
        private final int line;
        private OpCode opCode;
        private final int arg;
        private Instruction nextInstruction;
        private Set<Instruction> previousInstructions;

        public Instruction(final int line, final String[] instruction) {
            this.line = line;
            this.opCode = OpCode.valueOf(instruction[0].toUpperCase());
            this.arg = Integer.parseInt(instruction[1]);
        }

        public Integer getLine() {
            return line;
        }
        
        public Set<Instruction> getPreviousInstructions() {
            if (previousInstructions == null) {
                previousInstructions = new HashSet<>();
            }
            return previousInstructions;
        }
        
        public String toString() {
            return String.format("%d - %s %d", line, opCode, arg);
        }
    }
    
    public static Set<Integer> execute(final Map<Integer, Instruction> instructions) {
        final Set<Integer> executedInstructions = new HashSet<>();

        int pointer = 0, accumulator = 0;
        while (!executedInstructions.contains(pointer) && pointer < instructions.size()) {
            executedInstructions.add(pointer);
            
            final Instruction instruction = instructions.get(pointer);
            //System.out.printf("Executing instruction: %s\n", instruction);
            pointer += (instruction.opCode == OpCode.JMP) ? instruction.arg : 1;
            accumulator += (instruction.opCode == OpCode.ACC) ? instruction.arg : 0;
        }
        System.out.println("Program finished with accumulator: " + accumulator);
        return executedInstructions;
    }


}
