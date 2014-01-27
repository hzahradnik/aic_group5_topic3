package models;

/**
 * Created with IntelliJ IDEA.
 * User: dominik
 * Date: 24.11.13
 * Time: 01:17
 * To change this template use File | Settings | File Templates.
 */
public class Register {
    public String name;
    public String password1;
    public String password2;

    public String validate() {
        if( !password1.equals( password2 ) ) {
            return "Passwords do not match!";
        }

        if( User.exists( name ) ) {
            return "Username already taken!";
        }

        return null;
    }
}
