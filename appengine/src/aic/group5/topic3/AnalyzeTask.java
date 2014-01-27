package aic.group5.topic3;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import aic.group5.topic3.analysis.SentimentAnalysis;
import aic.group5.topic3.analysis.SentimentResult;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class AnalyzeTask extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		both( req, resp );
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		both( req, resp );
	}

	public void both( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException {
		String name = req.getParameter( "name" );

		Logger.getLogger( "bla" ).info( "in AnalyzeTask = " + name );

		SentimentResult sentimentResult = SentimentAnalysis.analyze( name );

		Entity user = UserRepo.getEntity( name );
		double result = sentimentResult.getCalculatedResult( );

		if( user != null ) {
			user.setProperty( "result", result );
			UserRepo.saveEntity( user );
		} else {
			Logger.getLogger( "analyze" ).info( "user " + name + " not found" );
		}

		resp.getWriter( ).print( new Gson().toJson( AnalyzeResult.done( result ) ) );
	}
}
