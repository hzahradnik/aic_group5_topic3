package aic.group5.topic3;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Register extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		if( CookieUtils.getCookie( req, "name" ) == null ) {
			req.getRequestDispatcher("/register.jsp").forward(req, resp);
		} else {
			resp.sendRedirect( "/analyze" );
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String name = req.getParameter( "name" );
		String pw1 = req.getParameter( "password1" );
		String pw2 = req.getParameter( "password2" );

		if( pw1.equals( pw2 ) && pw1.length() > 2 && name.length() > 0 && UserRepo.register( name, pw1 ) ) {
			resp.sendRedirect( "/login" );
		} else {
			String msg;

			if( !pw1.equals( pw2 ) ) {
				msg = "passwords do not match";

			} else if( pw1.length( ) <= 2 ) {
				msg = "password needs to be at least 3 characters";

			} else if( name.length( ) == 0 ) {
				msg = "company name not given";

			} else {
				msg = "company '" + name + "' already exists.";

			}

			req.setAttribute( "message", msg );
			req.setAttribute( "name", name );
			req.getRequestDispatcher("/register.jsp").forward(req, resp);
		}
	}
}
