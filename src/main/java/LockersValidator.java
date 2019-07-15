import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LockersValidator {

    private char lockCharacter = '{';
    private char unlockCharacter = '}';
    private String inputValidationRegex = "\\{.+?\\}+";
    private String maxLockerExtractRegex = "\\{.+\\}";

    /**
     * @description Validation of input String
     * @author  Valery Vorobei
     *
     * @param input The source String expression
     * @return  Return validate  Set<String>
     */
    public Set<String> validate(String input) {

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

        if (matcher.find() == false) {
            return resultSet;
        } else {

            // extract the longest valid locker value

            pattern = Pattern.compile(maxLockerExtractRegex);
            matcher = pattern.matcher(input);

            while (matcher.find()) {
                input = matcher.group();
            }


            lockCount = getCountChar(input, lockCharacter);
            unlockCount = getCountChar(input, unlockCharacter);
            excessLockerCount = Math.toIntExact(Math.abs((lockCount - unlockCount)));

            if (lockCount > unlockCount) {
                redundantLockersIndexes = indexsChar(input, lockCharacter);
                numberOfRedundantLockers = Math.toIntExact(lockCount);
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

    /**
     * @description Method for final validation of input String in case if there are extra locker/unlocker characters are
     * defined in the end of input
     * @author Valeriy Vorobey
     *
     * @param params ValidationParams instance to include all necessary validation parameters
     * @return Set<String> Result set of strings that contain
     */
    private Set<String> validateString(ValidationParams params) {

        Set<String> result = new HashSet<>();
        String prefix="";

        List<Integer> arr = null;

        while ((arr = generateCombinations(arr, params.excessLockerCount, params.numberOfRedundantLockers)) != null) {
            params.redundantLockersCombinations.addAll(arr);
        }

        for (int i = 0; i < params.redundantLockersCombinations.size(); i = i + params.excessLockerCount) {

            String outer = params.input;
            Pattern pattern = Pattern.compile("^[^\\{]*");
            Matcher matcher = pattern.matcher(outer);

            while (matcher.find()) {
              prefix = matcher.group();
            }

            StringBuffer stringBuffer = new StringBuffer(outer);

            int ofsetWhenDeleting = 0;

            for (int j = i; j < i + params.excessLockerCount; j++) {
                int m = params.redundantLockersIndexes.get(params.redundantLockersCombinations.get(j) - 1) - ofsetWhenDeleting;
                stringBuffer.deleteCharAt(m+prefix.length());
                ofsetWhenDeleting++;
            }

            if (isLockUnlockCharsEquals(stringBuffer.toString())) result.add(stringBuffer.toString());
        }

        return result;
    }

    // get count char in string
    private long getCountChar(String input, char c) {
        return input.chars().filter(ch -> ch == c).count();
    }


    // get indexs char in string
    private List<Integer> indexsChar(String str, Character character) {
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
     * @description Generate combinations from m at n remove the extra characters
     * @param arr
     * @param m
     * @param n
     * @return List combinations
     */
    private List<Integer> generateCombinations(List<Integer> arr, int m, int n) {
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
    private boolean isLockUnlockCharsEquals(String input){

        boolean isInputValid = false;

        Pattern pattern = Pattern.compile(maxLockerExtractRegex);
        Matcher  matcher = pattern.matcher(input);

        while (matcher.find()) {
            input = matcher.group();
        }

        long numberOfLockers = getCountChar(input,lockCharacter);
        long numberOfUnlockers = getCountChar(input,unlockCharacter);

        if (numberOfUnlockers == numberOfLockers) {
            isInputValid = true;
        }
        return isInputValid;
    }


    // inner class with validation params
    private class ValidationParams
    {
        public String input;
        public List<Integer> redundantLockersIndexes;
        public ArrayList<Integer> redundantLockersCombinations;
        public int excessLockerCount;
        public int numberOfRedundantLockers;

    }
}
