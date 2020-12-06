import java.util.Set;
import java.util.HashSet;

class Day6E1 { 
    
    private static final String input = "abc\n\na\nb\nc\n\nab\nac\n\na\na\na\na\n\nb";

  public static void main(String args[]) { 
      Files.readAll
    final String[] answerGroups = input.split("\n\n");
    int sum = 0;
    for(final String answerGroup : answerGroups) {
        final Set<Character> letters = new HashSet<>();
        
        final String aG = answerGroup.replaceAll("\n", "");
        
        for (Character c : aG.toCharArray()) {
            letters.add(c);
        }
        System.out.println("groupcount: " + letters.size());
        sum += letters.size();
    } 
    System.out.println("sum: " + sum);
  } 
}
