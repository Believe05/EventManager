
package za.ac.util;



public class Validator {

    public static boolean isEmailValid(String email) {
        return email != null && email.matches("^(.+)@(.+)$");
    }

    public static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }
}
