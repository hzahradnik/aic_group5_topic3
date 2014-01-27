package aic.group5.topic3;

public class AnalyzeResult {
	private final Object result;

	public AnalyzeResult( Object result ) {
		this.result = result;
	}

	public static AnalyzeResult stillAnalyzing( ) {
		return new AnalyzeResult( "analyzing" );
	}

	public static AnalyzeResult done( double result ) {
		return new AnalyzeResult( result );
	}

	public Object getResult( ) {
		return result;
	}
}
