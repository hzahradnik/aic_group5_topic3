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
package at.ac.tuwien.infosys.cloudscale.sample.sentiment;

import java.util.Hashtable;
import java.util.Map;

import at.ac.tuwien.infosys.cloudscale.annotations.ByValueParameter;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.SentimentResult;

public class ResultsDatabase {
	
	// we need some structure to collect our results
	private static final ResultsDatabase results = new ResultsDatabase();
			
	public static ResultsDatabase getInstance()
	{		
		return results;
	}
	
	public ResultsDatabase()
	{
		
	}

	private Map<String, SentimentResult> sentiments = 
			new Hashtable<String, SentimentResult>();
	private Map<String, Long> started = 
			new Hashtable<String, Long>();
	
	
	public void startTask(String theTaskWord) 
	{
		started.put(theTaskWord, System.currentTimeMillis());
	}
	
	public void stopTask(String theTaskWord) 
	{
		started.remove(theTaskWord);
	}
	
	public Long getTaskStarttime(String theTaskWord)
	{
		return started.get(theTaskWord);
	}
	
	// note the usage of @ByValueParameter, to indicate that the result should be
	// sent back to the client as deep copy
	public void storeResult(String theTaskWord, @ByValueParameter SentimentResult result) {
		
		sentiments.put(theTaskWord, result);
		
	}

	public SentimentResult lookUpResult(String theTaskWord)
	{
		//System.out.println("Result size: " + sentiments.size());
		if(sentiments.containsKey(theTaskWord))
		{
			SentimentResult result = sentiments.get(theTaskWord);
			if(System.currentTimeMillis()-3600000 <= result.getCreation())
			{
				return result;
			} 
		}
		return null;
	}
	
	
	public void clear()
	{
		started.clear();
		sentiments.clear();
	}

	public void print() {
		
		System.out.println("We have "+sentiments.size()+" results");
		
		StringBuilder sb = new StringBuilder();
		for(String key : sentiments.keySet()) {
			sb.append(key);
			sb.append(" -> ");
			sb.append(sentiments.get(key).getTotalPercentage());
			sb.append(System.lineSeparator());
		}
		System.out.println(sb.toString());
		
	}
}
