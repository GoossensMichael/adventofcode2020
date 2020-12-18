import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class MainDay18Part2 {

    public static void main(String[] args) throws IOException {
        // sum of input_small is 693,942
        final String inputPath =
//                "src/input_small_p2.txt";
                "src/input.txt";
        System.out.printf("Sum of calculation results is: %s\n", Files.lines(Paths.get(inputPath))
                .map(MainDay18Part2::preprocess)
                // Making sure that brackets are surrounded only by spaces to facilitate a correct split
                .map(expression -> expression.replaceAll("\\(", "( "))
                .map(expression -> expression.replaceAll("\\)", " )"))
                .map(expression -> expression.split(" "))
                .map(MainDay18Part2::calculate)
                .reduce(0L, Long::sum));
    }

    // Preprocessing the expressions by adding brackets everywhere to
    // ensure + precedence.
    private static String preprocess(final String expression) {
        final StringBuilder stringBuilder = new StringBuilder("(");

        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '*') {
                stringBuilder.append(") ");
            }
            if (expression.charAt(i) == '(') {
                stringBuilder.append('(');
            }
            stringBuilder.append(expression.charAt(i));
            if (expression.charAt(i) == '*') {
                stringBuilder.append(" (");
            }
            if (expression.charAt(i) == ')') {
                stringBuilder.append(')');
            }
        }

        return stringBuilder.append(')').toString();
    }

    // Using a stack to split out every subcalculation
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

        System.out.println("###############################");
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

        System.out.printf("Sum of expression '%s' is %d.\n", expression, sum);
        return sum;
    }

}
