package models;

import play.Logger;
import play.api.libs.ws.*;
import play.mvc.Result;
import play.libs.F;
import play.libs.F.*;
import play.mvc.Results;
import play.mvc.WebSocket;
import scala.Function1;
import scala.concurrent.Await;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scala.concurrent.Future;
import scala.Function1;

import static play.mvc.Results.async;
import static play.mvc.Results.ok;

/**
 * Created with IntelliJ IDEA.
 * User: dominik
 * Date: 25.11.13
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
public class Analyzor {
    public static void analyze( final WebSocket.In<String> in, final WebSocket.Out<String> out, final String keyword, final int timeoutInSeconds, final String url ) {
        in.onMessage( new F.Callback<String>() {
            public void invoke(String event) {
                out.write( TestAnalyze.analyze( keyword, timeoutInSeconds, url ) );
            }
        } );

        in.onClose( new F.Callback0() {
            public void invoke() {
                out.close();
            }
        } );
    }
}
