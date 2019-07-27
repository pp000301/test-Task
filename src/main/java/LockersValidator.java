import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LockersValidator {

    private static char lockCharacter = '{';
    private static char unlockCharacter = '}';
    private static String inputValidationRegex = "\\{.*\\}+";

    /**
     * @param input The source String expression
     * @return Return validate  Set<String>
     * @description Validation of input String
     * @author Valery Vorobei
     */
    public static Set<String> validate(String input) {

        long lockCount, unlockCount;
        int numberOfRedundantLockers, excessLockerCount;
        ValidationParams params = new ValidationParams();
        params.input = input;
        Set<String> resultSet = new HashSet<>();

        // list of indexes for redundant lockers in input text
        List<Integer> redundantLockersIndexes;

        // list of lockers to delete in apropriate combination
        ArrayList<Integer> redundantLockersCombinations = new ArrayList<>();

        // first check if input compatible with applying lockerunlocker logic
        Pattern pattern = Pattern.compile(inputValidationRegex);
        Matcher matcher = pattern.matcher(input);

        if (getCountChar(input, lockCharacter) == 0 & getCountChar(input, unlockCharacter) == 0) {
            resultSet.add(input);
            return resultSet;
        } else if ((getCountChar(input, lockCharacter) == 0 & getCountChar(input, unlockCharacter) != 0) | (getCountChar(input, lockCharacter) != 0 & getCountChar(input, unlockCharacter) == 0)) {
            String res = extractString(input, "[^\\}\\{]*");
            resultSet.add(res);
            return resultSet;
        } else if (matcher.find() == false) {
            resultSet.add(extractString(input, "[^\\}\\{]*"));
            return resultSet;
        } else {

            // extract the longest valid locker value

            input = extractString(input, inputValidationRegex);

            lockCount = getCountChar(input, lockCharacter);
            unlockCount = getCountChar(input, unlockCharacter);
            excessLockerCount = Math.toIntExact(Math.abs((lockCount - unlockCount)));

            if (lockCount > unlockCount) {
                redundantLockersIndexes = indexsChar(input, lockCharacter);
                numberOfRedundantLockers = Math.toIntExact(lockCount);
            } else if (lockCount == unlockCount) {
                String res = params.input;
                String prefix = extractString(res, "^[^\\{]*");
                prefix = extractString(prefix, "\\w*");
                String syf = extractString(res, "[^\\}]*$");
                syf = extractString(syf, "\\w*");
                res = prefix + extractString(res, inputValidationRegex) + syf;
                resultSet.add(res);
                return resultSet;
            } else {
                redundantLockersIndexes = indexsChar(input, unlockCharacter);
                numberOfRedundantLockers = Math.toIntExact(unlockCount);
            }

            params.excessLockerCount = excessLockerCount;
            params.numberOfRedundantLockers = numberOfRedundantLockers;
            params.redundantLockersIndexes = redundantLockersIndexes;
            params.redundantLockersCombinations = redundantLockersCombinations;

            resultSet = validateString(params);
            return resultSet;
        }

    }

    public static String extractString(String original, String s) {
        Pattern pattern;
        Matcher matcher;
        String inputPrefix = "";
        pattern = Pattern.compile(s);
        matcher = pattern.matcher(original);

        while (matcher.find()) {
            String inputPrefixTemp = matcher.group();
            if (!inputPrefixTemp.isEmpty()) {
                inputPrefix = inputPrefix + inputPrefixTemp;
            }
        }
        return inputPrefix;
    }

    /**
     * @param params ValidationParams instance to include all necessary validation parameters
     * @return Set<String> Result set of strings that contain
     * @description Method for final validation of input String in case if there are extra locker/unlocker characters are
     * defined in the end of input
     * @author Valeriy Vorobey
     */
    private static Set<String> validateString(ValidationParams params) {

        Set<String> result = new HashSet<>();
        String prefix = "";

        List<Integer> arr = null;

        while ((arr = generateCombinations(arr, params.excessLockerCount, params.numberOfRedundantLockers)) != null) {
            params.redundantLockersCombinations.addAll(arr);
        }

        for (int i = 0; i < params.redundantLockersCombinations.size(); i = i + params.excessLockerCount) {

            prefix = extractString(params.input, "^[^\\{]*");
            int longPrefix = prefix.length();

            StringBuffer stringBuffer = new StringBuffer(params.input);

            int ofsetWhenDeleting = 0;

            for (int j = i; j < i + params.excessLockerCount; j++) {
                int m = params.redundantLockersIndexes.get(params.redundantLockersCombinations.get(j) - 1) - ofsetWhenDeleting;
                stringBuffer.deleteCharAt(m + longPrefix);
                ofsetWhenDeleting++;
            }

            if (isLockUnlockCharsEquals(stringBuffer.toString())) {
                String res = stringBuffer.toString();
                prefix = extractString(res, "^[^\\{]*");
                prefix = extractString(prefix, "\\w*");
                String syf = extractString(res, "[^\\\\}^]*$");
                syf = extractString(syf, "\\w*");
                res = prefix + extractString(res, inputValidationRegex) + syf;
                result.add(res);
            }
        }

        return result;
    }

    // get count char in string
    private static long getCountChar(String input, char c) {
        return input.chars().filter(ch -> ch == c).count();
    }

    // get indexs char in string
    private static List<Integer> indexsChar(String str, Character character) {
        List<Integer> interimArray1 = new ArrayList<>();
        int l = 0;
        for (int k = 0; k < str.length(); k++) {
            Character value = new Character(str.charAt(k));

            if (value.equals(character)) {
                interimArray1.add(k);
                l++;
            }
        }
        return interimArray1;
    }

    /**
     * @param arr Intermediate collection
     * @param m
     * @param n
     * @return List combinations
     * @description Generate combinations from m at n remove the extra characters
     */
    private static List<Integer> generateCombinations(List<Integer> arr, int m, int n) {
        if (arr == null) {
            arr = new ArrayList<>();
            for (int i = 0; i < m; i++)
                arr.add(i, i + 1);
            return arr;
        }
        for (int i = m - 1; i >= 0; i--)
            if (arr.get(i) < n - m + i + 1) {
                arr.set(i, arr.get(i) + 1);
                for (int j = i; j < m - 1; j++)
                    arr.set(j + 1, (arr.get(j) + 1));
                return arr;
            }
        return null;
    }

    // isLockUnlockCharsEquals
    private static boolean isLockUnlockCharsEquals(String input) {

        boolean isInputValid = false;

        Pattern pattern = Pattern.compile(inputValidationRegex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            input = matcher.group();
        }

        long numberOfLockers = getCountChar(input, lockCharacter);
        long numberOfUnlockers = getCountChar(input, unlockCharacter);

        if (numberOfUnlockers == numberOfLockers) {
            isInputValid = true;
        }
        return isInputValid;
    }


    // inner class with validation params
    private static class ValidationParams {
        public String input;
        public List<Integer> redundantLockersIndexes;
        public ArrayList<Integer> redundantLockersCombinations;
        public int excessLockerCount;
        public int numberOfRedundantLockers;

    }
}
