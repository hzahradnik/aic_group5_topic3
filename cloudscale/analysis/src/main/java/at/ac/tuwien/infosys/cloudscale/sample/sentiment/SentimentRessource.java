package at.ac.tuwien.infosys.cloudscale.sample.sentiment;

import java.util.Hashtable;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.SentimentResult;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.Task;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.TaskRunner;

@Path( SentimentRessource.PATH )
public class SentimentRessource {
    public static final String PATH = "sentiment";
    public static final long timeout = 60000;
    
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public void getSentiment(@QueryParam("taskword") String taskWord, @Suspended AsyncResponse ar)
	{
		System.out.println("GET " + Main.BASE_URI + PATH + "?taskword=" + taskWord + " " + System.currentTimeMillis());
		
		ResponseWait.getInstance().addNewTaskWord(taskWord, ar);
		
		System.out.println("END GET " + Main.BASE_URI + PATH + "?taskword=" + taskWord + " " + System.currentTimeMillis());
	}
	
	public static class ResultDTO
	{
		private String result;
		private int positive, negative, neutral;
		
		public ResultDTO(SentimentResult result) {
			this.result = String.format("%.2f", result.getCalculatedResult());
			this.positive = result.getPositive();
			this.neutral = result.getNeutral();
			this.negative = result.getNegative();
		}
		
		public int getPositive() {
			return positive;
		}

		public void setPositive(int positive) {
			this.positive = positive;
		}

		public int getNegative() {
			return negative;
		}

		public void setNegative(int negative) {
			this.negative = negative;
		}

		public int getNeutral() {
			return neutral;
		}

		public void setNeutral(int neutral) {
			this.neutral = neutral;
		}
		
		public String getResult() {
			return this.result;
		}
		
		public void setResult(String result)
		{
			this.result = result;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("{ \"%2$s\": \"%1$s\", \"%4$s\": \"%3$s\", \"%6$s\": \"%5$s\", \"%8$s\": \"%7$s\" }", 
					getResult(), "result", getPositive(), "positive", getNeutral(), "neutral", getNegative(), "negative");
		}
	}
	
	public static class ErrorDTO
	{
		private String message;
		
		public ErrorDTO(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return this.message;
		}
		
		public void setMessage(String message)
		{
			this.message = message;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("{ \"%2$s\": \"%1$s\" }", getMessage(), "error");
		}
	}
}
