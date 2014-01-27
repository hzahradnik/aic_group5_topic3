package aic.group5.topic3;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Login extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		if( CookieUtils.getCookie( req, "name" ) == null ) {
			req.getRequestDispatcher("/login.jsp").forward(req, resp);
		} else {
			resp.sendRedirect( "/analyze" );
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String name = req.getParameter( "name" );
		String password = req.getParameter( "password" );

		if( UserRepo.contains( name, password ) ) {
			CookieUtils.setCookie( resp, "name", name );
			resp.sendRedirect( "/analyze" );
		} else {
			req.setAttribute( "message", "Username or password wrong." );
			req.getRequestDispatcher("/login.jsp").forward(req, resp);
		}
	}
}
