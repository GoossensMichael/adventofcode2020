import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class MainDay18Part1 {

    public static void main(String[] args) throws IOException {
        // sum of input_small is 26,335
        final String inputPath =
//                "src/input_small.txt";
                "src/input.txt";
        System.out.printf("Sum of calculation results is: %s\n", Files.lines(Paths.get(inputPath))
                .map(expression -> expression.replaceAll("\\(", "( "))
                .map(expression -> expression.replaceAll("\\)", " )"))
                .map(expression -> expression.split(" "))
                .map(MainDay18Part1::calculate)
                .reduce(0L, Long::sum));
    }

    private static long calculate(final String[] terms) {
        final Stack<String> stack = new Stack<>();

        stack.add("");
        for (final String term : terms) {
            switch (term) {
                case "(":
                    stack.add("");
                    break;
                case ")":
                    final long result = calculate(stack.pop());
                    stack.add(stack.pop() + " " + result);
                    break;
                default:
                    stack.add(stack.pop() + " " + term);
            }
        }

        return calculate(stack.pop());
    }

    private static long calculate(final String expression) {
        final String[] terms = expression.split(" ");

        long sum = 0L;
        char operation = '+';
        for (final String term : terms) {
            switch (term) {
                case "":
                    break;
                case "+":
                    operation = '+';
                    break;
                case "*":
                    operation = '*';
                    break;
                default:
                    if (operation == '+') {
                        sum += Long.parseLong(term);
                    } else {
                        sum *= Long.parseLong(term);
                    }
                    break;
            }
        }
        return sum;
    }

}
