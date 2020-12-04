public class Day4E1 {

    public static void main(String[] args) throws IOException {
        final String[] passports = Files.lines(Paths.get("/Users/mgoossens/personal/adventofcode/day4/input.txt"), StandardCharsets.UTF_8)
                .collect(Collectors.joining(StringUtils.LF))
                .replaceAll(StringUtils.LF + StringUtils.LF, "~")
                .replaceAll(StringUtils.LF, StringUtils.EMPTY)
                .replaceAll("~", StringUtils.LF)
                .split(StringUtils.LF);

        System.out.println(Arrays.stream(passports)
                .parallel()
                .filter(ServiceProvider::isValid)
                .count());

    }

    private static boolean isValid(final String passport) {
        final String[] requiredPassportFields = {"byr:", "iyr:", "eyr:", "hgt:", "hcl:", "ecl:", "pid:"};
        final String[] passportFields = passport.split(" ");

        for (final String requiredPassportField : requiredPassportFields) {
            if (!passportContainsField(passportFields, requiredPassportField)) {
                return false;
            }
        }
        return true;
    }

    private static boolean passportContainsField(final String[] passportFields, final String requiredPassportField) {
        for (final String passportField : passportFields) {
            if (passportField.contains(requiredPassportField)) {
                return true;
            }
        }
        return false;
    }
    
}
