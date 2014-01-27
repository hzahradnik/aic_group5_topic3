package controllers;

import models.*;
import play.Configuration;
import play.Logger;
import play.Play;
import play.api.libs.concurrent.Akka;
import play.api.libs.ws.WS;
import play.libs.F.*;
import play.mvc.*;
import play.data.Form;
import play.mvc.WebSocket;

import views.html.*;
import views.js.*;

import java.util.concurrent.Future;

import static play.data.Form.form;

public class Application extends Controller {
    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render(Secured.getUserFromSession()));
    }

    public static Result login() {
        return ok(login.render(form(Login.class)));
    }

    public static Result logout( ) {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.login());
    }

    public static Result authenticate( ) {
        Form<Login> loginForm = form(Login.class).bindFromRequest();

        if( loginForm.hasErrors( ) ) {
            return badRequest(login.render(loginForm));
        } else {
            User u = loginForm.get().getUser();

            session().clear();
            session("user_id",u.id);

            return redirect(routes.Application.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result analyzeJs() {
        return ok(analyze.render());
    }

    public static Result register() {
        return ok(register.render(form(Register.class)));
    }

    public static Result registerUser( ) {
        Form<Register> registerForm = form(Register.class).bindFromRequest();

        if( registerForm.hasErrors( ) ) {
            return badRequest(register.render(registerForm));
        } else {
            User u = new User(registerForm.get().name, registerForm.get().password1 );
            u.save();

            session().clear();
            session("user_id",u.id);

            return redirect(controllers.routes.Application.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static WebSocket<String> analyze() {
        User u = Secured.getUserFromSession();
        final String keyword = u.name;

        Configuration config = Play.application().configuration();

        final int timeoutInSeconds = config.getInt("app.timeout");
        final String webserviceUrl = config.getString("app.url");

        return new WebSocket<String>() {
            public void onReady( WebSocket.In<String> in, WebSocket.Out<String> out) {
                Analyzor.analyze( in, out, keyword, timeoutInSeconds, webserviceUrl );
            }
        };
    }
}
