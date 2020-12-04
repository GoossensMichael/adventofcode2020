public class Day4 {

    public static void main(String[] args) throws IOException {
        final String[] passports = Files.lines(Paths.get("/Users/mgoossens/personal/adventofcode/day4/input.txt"), StandardCharsets.UTF_8)
                .collect(Collectors.joining(StringUtils.LF))
                .replaceAll(StringUtils.LF + StringUtils.LF, "~")
                .replaceAll(StringUtils.LF, StringUtils.SPACE)
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
        System.out.println("passport: " + passport);
        for (final String passportField : passportFields) {
            System.out.println("\tpassportfield: '" + passportField + "'");
        }
        System.out.println();
        
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
                final String value = passportField.substring(requiredPassportField.length());
                System.out.println("=======" + value + " cuz " + StringUtils.compare("2020", "2025"));
                final boolean isValid;
                switch (requiredPassportField) {
                    case "byr:":
                        isValid = value.length() == 4 && value.matches("[0-9]{4}") && StringUtils.compare("1920", value) <= 0 && StringUtils.compare("2002", value) >= 0;
                        break;
                    case "iyr:":
                        isValid = value.length() == 4 && value.matches("[0-9]{4}") && StringUtils.compare("2010", value) <= 0 && StringUtils.compare("2020", value) >= 0;
                        break;
                    case "eyr:":
                        isValid = value.length() == 4 && value.matches("[0-9]{4}") && StringUtils.compare("2020", value) <= 0 && StringUtils.compare("2030", value) >= 0;
                        break;
                    case "hgt:":
                        if (value.substring(0, value.length() - 2).matches("[0-9]*")) {
                            final int height = Integer.parseInt(value.substring(0, value.length() - 2));
                            if (value.endsWith("cm")) {
                                isValid = height >= 150 && height <= 193;
                            } else if (value.endsWith("in")) {
                                isValid = height >= 59 && height <= 76;
                            } else {
                                isValid = false;
                            }
                        } else {
                            isValid = false;
                        }
                        break;
                    case "hcl:":
                        isValid = value.matches("#[0-9a-f]{6}");
                        break;
                    case "ecl:":
                        isValid = value.matches("amb|blu|brn|gry|grn|hzl|oth");
                        break;
                    case "pid:":
                        isValid = value.matches("[0-9]{9}");
                        break;
                    default:
                        isValid = false;
                }
                System.out.println(passportField + " is valid? " + isValid);
                return isValid;
            }
        }
        return false;
    }
}
