import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Set<String> resultSet;
        String text = "asas{ccc{cc{cc}}sdff";
        resultSet = LockersValidator.validate(text);
        System.out.println(resultSet.size());
        for (String result : resultSet) {
            System.out.println(result);
        }


    }
}
