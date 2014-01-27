package aic.group5.topic3;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.*;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class Analyze extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String name = CookieUtils.getCookie( req, "name" );

		req.setAttribute( "name", name );
		req.getRequestDispatcher("/analyze.jsp").forward(req, resp);
	}

	private boolean isUpToDateResult( Entity user ) {
		Object stamp = user.getProperty( "timestamp" );

		if( stamp instanceof Long ) {
			long cmp = System.currentTimeMillis( ) - 1 * 60 * 1000;

			return (Long)stamp > cmp;
		}

		return false;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String name = CookieUtils.getCookie( req, "name" );

		if( name == null ) {
			name = req.getParameter( "name" );
		}

		Entity user = UserRepo.getEntity( name );
		Gson gson = new Gson( );

		Object result = user.getProperty( "result" );

		if( "analyzing".equals( result ) ) {
			resp.getWriter().print( gson.toJson( AnalyzeResult.stillAnalyzing( ) ) );

		} else if( result instanceof Double && isUpToDateResult( user ) ) {
			resp.getWriter( ).print( gson.toJson( AnalyzeResult.done( (Double) result ) ) );

		} else {
			user.setProperty( "result", "analyzing" );
			user.setProperty( "timestamp", System.currentTimeMillis( ) );
			UserRepo.saveEntity( user );

			Queue queue = QueueFactory.getQueue( "analyze" );
			TaskOptions taskOptions = TaskOptions.Builder.withUrl( "/analyze_task" );
			taskOptions.method( Method.POST );
			taskOptions.param( "name", name );

			queue.add( taskOptions );

			resp.getWriter().print( gson.toJson( AnalyzeResult.stillAnalyzing( ) ) );
		}
	}
}
