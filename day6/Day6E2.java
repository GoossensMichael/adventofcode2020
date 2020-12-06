import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Day6E2 {
    private static final String input = "abc\n\na\nb\nc\n\nab\nac\n\na\na\na\na\n\nb";

    public static void main(String args[]) throws IOException {
        final String forms = Files.readString(Paths.get("C:\\Users\\Michael\\Documents\\My Received Files\\input.txt"));
        final String[] answerGroups = forms.split("\n\n");
        int sum = 0;
        for(final String answerGroup : answerGroups) {
            final Map<Character, Integer> letters = new HashMap<>();

            final String[] aG = answerGroup.split("\n");

            final String firstForm = aG[0];
            for (Character c : firstForm.toCharArray()) {
                boolean existsOnEveryForm = true;
                for (int i = 1; i < aG.length && existsOnEveryForm; i++) {
                    if (!aG[i].contains(c + "")) {
                        existsOnEveryForm = false;
                    }
                }
                if (existsOnEveryForm) {
                    sum++;
                }
            }
        }
        System.out.println("sum: " + sum);
    }
}
