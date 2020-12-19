package day19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MainDay19 {

    public static void main(String[] args) throws IOException {
        // sum of input_small is 26,335
        final String inputPath =
//                "src/day19/input_tiny.txt";
//                "src/day19/input_small.txt";
//                "src/day19/input.txt";
//                "src/day19/input_p2_tiny.txt";
//                "src/day19/input_p2_small.txt";
                "src/day19/input_p2.txt";
        final List<String> input = Files.readAllLines(Paths.get(inputPath));

        final Map<String, Rule> rulesByNumber = new HashMap<>();
        input.stream()
                .filter(line -> line.matches("[0-9]*:.*"))
                .forEach(rule -> configureRule(rule, rulesByNumber));

        final Rule ruleZero = rulesByNumber.get("0");
        final long count = input.stream()
                .filter(line -> !line.matches("([0-9]*:.*)|(^\\s*$)"))
                .filter(line -> isValid(line, ruleZero))
                .count();

        System.out.printf("Amount of lines matching rule 0 is %d.", count);
    }

    private static boolean isValid(final String line, final Rule rule) {
        return Arrays.stream(rule.calculateValidity(line)).anyMatch(validity -> validity == line.length());
    }

    private static void configureRule(final String rule, final Map<String, Rule> rules) {
        final int ruleNumberIndex = rule.indexOf(':');
        final String ruleNumber = rule.substring(0, ruleNumberIndex);
        if (!rules.containsKey(ruleNumber)) {
            rules.put(ruleNumber, createRule(ruleNumber, rule.substring(ruleNumberIndex + 2), rules));
        } else {
            final RuleWrapper existingRule = (RuleWrapper) rules.get(ruleNumber);
            final Rule result = createRule(ruleNumber, rule.substring(ruleNumberIndex + 2), rules);
            if (existingRule != result) {
                existingRule.setRule(result);
            }
        }
    }

    private static Rule createRule(final String ruleNumber, final String rule, final Map<String, Rule> rules) {
        final Rule result;
        final int letterIndex = rule.indexOf('"');
        final int orIndex = rule.indexOf('|');
        if (letterIndex >= 0) {
            result = new SingularRule(ruleNumber, rule.charAt(letterIndex + 1));
        } else if (orIndex > 0) {
            final Rule leftRule = createRule(ruleNumber + "-LeftOrPart", rule.substring(0, orIndex).trim(), rules);
            final Rule rightRule = createRule(ruleNumber + "-RightOrPart", rule.substring(orIndex + 1).trim(), rules);
            if (rules.containsKey(ruleNumber)) {
                final RuleWrapper ruleWrapper = (RuleWrapper) rules.get(ruleNumber);
                ruleWrapper.setRule(new OrRule(ruleNumber, leftRule, rightRule));
                result = ruleWrapper;
            } else {
                result = new OrRule(ruleNumber, leftRule, rightRule);
            }
        } else {
            final String[] multiRuleRuleNumbers = rule.split(" ");
            final Rule[] multiRules = new Rule[multiRuleRuleNumbers.length];
            int i = 0;
            for (final String multiRuleRuleNumber : multiRuleRuleNumbers) {
                rules.putIfAbsent(multiRuleRuleNumber, new RuleWrapper(multiRuleRuleNumber));
                multiRules[i++] = rules.get(multiRuleRuleNumber);
            }
            result = new MultiRule(ruleNumber, multiRules);
        }
        return result;
    }

    private static abstract class Rule {

        private final String ruleNumber;

        public Rule(final String ruleNumber) {
            this.ruleNumber = ruleNumber;
        }

        public String getRuleNumber() {
            return ruleNumber;
        }

        abstract int[] calculateValidity(final String message);
    }

    private static class RuleWrapper extends Rule {

        private Rule rule;

        public RuleWrapper(final String ruleNumber) {
            super(ruleNumber);
        }

        public void setRule(final Rule rule) {
            this.rule = rule;
        }

        @Override
        public int[] calculateValidity(final String message) {
            return rule.calculateValidity(message);
        }

        @Override
        public String toString() {
            return rule.toString();
        }
    }

    private static class SingularRule extends Rule {

        private final char letter;

        public SingularRule(final String ruleNumber, final char letter) {
            super(ruleNumber);
            this.letter = letter;
        }

        @Override
        public int[] calculateValidity(final String message) {
            return (message.length() >= 1 && message.charAt(0) == letter) ? new int[] { 1 } : new int[] { 0 };
        }

        @Override
        public String toString() {
            return String.valueOf(letter);
        }
    }

    private static class OrRule extends Rule {

        private final Rule left;
        private final Rule right;

        public OrRule(final String ruleNumber, final Rule left, final Rule right) {
            super(ruleNumber);
            this.left = left;
            this.right = right;
        }

        @Override
        public int[] calculateValidity(final String message) {
            int[] leftValidSize = left.calculateValidity(message);
            int[] rightValidSize = right.calculateValidity(message);
            return mergeArrays(leftValidSize, rightValidSize);
        }

        @Override
        public String toString() {
            return "(" + left.toString() + " | " + right.toString() + ")";
        }
    }

    private static class MultiRule extends Rule {

        private final Rule[] rules;

        public MultiRule(final String ruleNumber, final Rule... rules) {
            super(ruleNumber);
            this.rules = rules;
        }

        @Override
        public int[] calculateValidity(final String message) {
            int i = 0;
            int[] validity = new int[] { 0 };
            int[] isValid;
            do {
                final List<Integer> validityList = new ArrayList<>();
                for (int j = 0; j < validity.length; j++) {
                    final int currentValidity = validity[j];
                    isValid = rules[i].calculateValidity(message.substring(currentValidity));
                    Arrays.stream(isValid)
                            .filter(v -> v > 0)
                            .map(v -> v + currentValidity)
                            .forEach(validityList::add);
                }
                validity = validityList.stream().mapToInt(v -> v).toArray();
                i++;
            } while(i < rules.length && validity.length > 0);

            final int[] result;
            if (validity.length == 0) {
                result = new int[] { 0 };
            } else {
                result = validity;
            }
            return result;
        }

    }

    private static int[] mergeArrays(final int[] a, final int[] b) {
        int aLen = a.length;
        int bLen = b.length;
        int[] result = new int[aLen + bLen];

        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);
        return result;
    }
}
