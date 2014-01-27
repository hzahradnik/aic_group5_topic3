package models;

import play.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: dominik
 * Date: 23.11.13
 * Time: 21:16
 * To change this template use File | Settings | File Templates.
 */
public class Login {
    public String name;
    public String password;

    public User getUser() {
        return User.authenticate(name, password);
    }

    public String validate() {
        if( getUser( ) == null ) {
            return "Invalid username or password!";
        }

        return null;
    }
}
