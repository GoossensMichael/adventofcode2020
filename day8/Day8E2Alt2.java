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

public class Day8E2Alt2 {

    public static void main(String[] args) throws IOException {
        final List<String> input = Files.readAllLines(Paths.get("/Users/mgoossens/personal/adventofcode/day8/input.txt"), StandardCharsets.UTF_8);

        
        final AtomicInteger lineCounter = new AtomicInteger(0);
        final Map<Integer, Instruction> instructions = input.stream()
                .map(instructionLine ->  new Instruction(lineCounter.getAndIncrement(), instructionLine.split(" ")))
                .collect(Collectors.toMap(Instruction::getLine, Function.identity()));
        enrich(instructions);
        
        // part 1
        final Set<Instruction> topDownInstructions = execute(instructions);

        // part 2
        repair(instructions, topDownInstructions);
        execute(instructions);
    }
    
    private static void enrich(final Map<Integer, Instruction> instructions) {
        // Enriches the instructions to know to which instruction they are going and from which instructions it can be reached.
        instructions.values().forEach(instruction -> {
            // Calculate the next line
            final int nextLine = (instruction.opCode != OpCode.JMP) ? instruction.line + 1 : instruction.line + instruction.arg;
            // When the next line is the end of the program do nothing
            if (nextLine < instructions.size()) {
                final Instruction nextInstruction = instructions.get(nextLine);
                instruction.nextInstruction = nextInstruction;
                nextInstruction.getPreviousInstructions().add(instruction);
            }
        });
    }

    private static void repair(final Map<Integer, Instruction> instructions, final Set<Instruction> topDownInstructions) {
        final Integer finalInstruction = instructions.keySet().stream().max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("Input data is not correct, no final instruction found."));

        // Actually getting the bottom up instructions (at least the lines).
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
        
        for (final Instruction topDownInstruction : topDownInstructions) {
            if (topDownInstruction.opCode != OpCode.ACC) {
                final int pointer = (topDownInstruction.opCode == OpCode.JMP) ? topDownInstruction.getLine() + 1 : topDownInstruction.getLine() + topDownInstruction.arg;
                if (instructionsToTheEnd.contains(pointer) || pointer >= instructions.size()) {
                    System.out.printf("Found the corrupt instruction: %s.\n", topDownInstruction);
                    // Switching the opcode (i.e. the repairing). JMP to NOP or vice versa.
                    topDownInstruction.opCode = (topDownInstruction.opCode == OpCode.JMP) ? OpCode.NOP : OpCode.JMP;
                    // Correcting the instructions by calculating the correct next and previous instruction to fix the graph.
                    final int nextLine = (topDownInstruction.opCode == OpCode.JMP) ? topDownInstruction.getLine() + topDownInstruction.arg : topDownInstruction.getLine() + 1;
                    topDownInstruction.nextInstruction.getPreviousInstructions().remove(topDownInstruction);
                    topDownInstruction.nextInstruction = instructions.get(nextLine);
                    topDownInstruction.nextInstruction.getPreviousInstructions().add(topDownInstruction);
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
    
    public static Set<Instruction> execute(final Map<Integer, Instruction> instructions) {
        final Set<Instruction> executedInstructions = new HashSet<>();

        int accumulator = 0;
        Instruction instruction = instructions.get(0);
        while (!executedInstructions.contains(instruction) && instruction != null) {
            executedInstructions.add(instruction);
            accumulator += (instruction.opCode == OpCode.ACC) ? instruction.arg : 0;
            instruction = instruction.nextInstruction;
        }
        System.out.println("Program finished with accumulator: " + accumulator);
        return executedInstructions;
    }


}
