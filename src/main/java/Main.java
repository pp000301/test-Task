import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Set<String> resultSet;
        String text = "{cc}}{x}}x}asa";

        LockersValidator validator = new LockersValidator();
        resultSet = validator.validate(text);
        for (String result : resultSet) {
            System.out.println(result);
        }
    }
}
