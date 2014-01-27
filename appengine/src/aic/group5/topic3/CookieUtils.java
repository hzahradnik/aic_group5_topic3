package aic.group5.topic3;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {
	public static void setCookie( HttpServletResponse resp, String key, String value ) {
		Cookie cookie = new Cookie( key, value );
		cookie.setMaxAge( 60 * 60 * 24 );
		resp.addCookie( cookie );
	}

	public static String getCookie( HttpServletRequest req, String key ) {
		Cookie[] cookies = req.getCookies( );

		if( cookies != null ) {
			for( Cookie cookie : cookies ) {
				if( cookie.getName( ).equals( key ) )
					return cookie.getValue( );
			}
		}

		return null;
	}

	public static void deleteCookie( HttpServletRequest req, HttpServletResponse resp, String key ) {
		Cookie[] cookies = req.getCookies( );

		if( cookies != null ) {
			for( Cookie cookie : cookies ) {
				if( cookie.getName( ).equals( key ) ) {
					cookie.setMaxAge( 0 );
					resp.addCookie( cookie );
				}
			}
		}
	}
}
