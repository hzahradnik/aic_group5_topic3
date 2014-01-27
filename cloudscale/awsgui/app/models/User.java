package models;

import play.db.ebean.Model;
import play.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: dominik
 * Date: 23.11.13
 * Time: 21:11
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class User extends Model {
    @Id
    public String id;

    public String name;

    public String password;

    public User( String name, String password ) {
        this.name = name;
        this.password = password;
    }

    public static Finder<String,User> find = new Finder<String,User>(
        String.class, User.class
    );

    public static User authenticate( String name, String password ) {
        User user = find
            .where()
            .eq("name", name)
            .eq("password", password)
            .findUnique();

        return user;
    }

    public static boolean exists( String name ) {
        User user = find
            .where()
            .eq("name", name)
            .findUnique();

        return user != null;
    }
}
