package aic.group5.topic3.analysis;

public class SentimentAnalysis {
	public static SentimentResult analyze( String keyword ) {
		System.out.println("Starting to analyse " + keyword);
		// create our machine learning classifier
		Classifier classifier = ClassifierFactory.createClassifier();

		System.out.println("Created classifier for " + keyword);

		try {
			// do the analysis
			SentimentResult result = TwitterSentimentAnalyzer.analyzeTweets(
					keyword, new TwitterQueryWrapper(), classifier );

			System.out.println("Received result for " + keyword);
			return result;
		} catch( Exception e ) {
			e.printStackTrace( );
			return null;
		}
	}
}
