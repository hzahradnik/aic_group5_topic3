package aic.group5.topic3;

public class AnalyzeResult {
	private final Object result;
	private boolean error = false;

	public AnalyzeResult( Object result ) {
		this.result = result;
	}

	public static AnalyzeResult stillAnalyzing( ) {
		return new AnalyzeResult( "analyzing" );
	}

	public static AnalyzeResult done( double result ) {
		return new AnalyzeResult( Math.round( result * 100.0 ) / 100.0 );
	}

	public static AnalyzeResult error( ) {
		AnalyzeResult rs = new AnalyzeResult( null );
		rs.error = true;

		return rs;
	}

	public boolean hasError( ) {
		return error;
	}

	public Object getResult( ) {
		return result;
	}
}
