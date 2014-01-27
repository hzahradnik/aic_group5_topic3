package aic.group5.topic3;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Dispatcher extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if( CookieUtils.getCookie( req, "name" ) == null ) {
			resp.sendRedirect( "/login" );
		} else {
			resp.sendRedirect( "/analyze" );
		}
	}
}
