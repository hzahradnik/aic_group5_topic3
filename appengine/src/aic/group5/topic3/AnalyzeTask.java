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
	private final Logger log = Logger.getLogger( "analyze" );

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

		log.info( "start analyze for " + name );

		SentimentResult sentimentResult = SentimentAnalysis.analyze( name );

		Entity user = UserRepo.getEntity( name );
		AnalyzeResult result = null;

		if( sentimentResult != null && sentimentResult.isSuccess( ) ) {
			double r = sentimentResult.getCalculatedResult( );
			log.info( "sentiment result = " + r );

			if( user != null ) {
				user.setProperty( "result", r );
				UserRepo.saveEntity( user );
			} else {
				Logger.getLogger( "analyze" ).info( "user " + name + " not found" );
			}

			result = AnalyzeResult.done( r );
		} else {
			log.info( "got no sentiment result" );
			result = AnalyzeResult.error( );

			if( user != null ) {
				user.setProperty( "result", "error" );
				UserRepo.saveEntity( user );
			} else {
				Logger.getLogger( "analyze" ).info( "user " + name + " not found" );
			}
		}

		log.info( "end analyze for " + name );
		resp.getWriter( ).print( new Gson( ).toJson( result ) );
	}
}
