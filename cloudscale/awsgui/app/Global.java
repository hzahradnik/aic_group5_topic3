import models.User;
import play.Application;
import play.GlobalSettings;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dominik
 * Date: 19.11.13
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        new User("test", "123").save();
    }
}

