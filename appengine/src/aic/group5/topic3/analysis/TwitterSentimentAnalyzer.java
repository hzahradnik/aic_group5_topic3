/*
   Copyright 2013 Philipp Leitner

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package aic.group5.topic3.analysis;

import twitter4j.Status;

public class TwitterSentimentAnalyzer
{

	public static SentimentResult analyzeTweets(String query, TwitterQueryWrapper twitterWrapper,
			Classifier classifier )
	{
		SentimentResult result = new SentimentResult(query);

		try {
			for(Status tweet : twitterWrapper.query(query)) {
				result.acceptResult(classifier.classify(tweet.getText()));
			}

			result.setSuccess( true );
		} catch(Exception ex) {
			ex.printStackTrace();

			result.setSuccess( false );
			result.setException(ex.toString());
		}

		return result;
	}

}
