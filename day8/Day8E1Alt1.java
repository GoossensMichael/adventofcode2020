public class Day8E1Alt1 {

    public static void main(String[] args) throws IOException {
        final List<String> input = Files.readAllLines(Paths.get("/Users/mgoossens/personal/adventofcode/day8/input.txt"), StandardCharsets.UTF_8);

        final Set<Integer> executedInstructions = new HashSet<>();

        int pointer = 0, accumulator = 0;
        while (!executedInstructions.contains(pointer) && pointer < input.size()) {
            executedInstructions.add(pointer);

            System.out.println(input.get(pointer));
            final String[] instruction = input.get(pointer).split(" ");
            pointer += (instruction[0].equals("jmp")) ? Integer.parseInt(instruction[1]) : 1;
            accumulator += (instruction[0]).equals("acc") ? Integer.parseInt(instruction[1]) : 0;
        }
        System.out.println("Program finished with accumulator: " + accumulator);
    }
}
