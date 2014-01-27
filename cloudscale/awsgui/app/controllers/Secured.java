package controllers;

import models.User;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created with IntelliJ IDEA.
 * User: dominik
 * Date: 23.11.13
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
public class Secured extends Security.Authenticator {
    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("user_id");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }

    public static User getUserFromSession() {
        String userId = Context.current().session().get("user_id");

        if( userId != null && userId.length() > 0 )
            return User.find.byId(userId);
        else
            return null;
    }
}