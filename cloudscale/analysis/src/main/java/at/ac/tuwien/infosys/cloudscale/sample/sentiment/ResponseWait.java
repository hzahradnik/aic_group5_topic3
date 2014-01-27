package at.ac.tuwien.infosys.cloudscale.sample.sentiment;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import at.ac.tuwien.infosys.cloudscale.sample.sentiment.SentimentRessource.ErrorDTO;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.SentimentRessource.ResultDTO;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.SentimentResult;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.Task;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.TaskRunner;

public class ResponseWait extends Thread
{
	private static ResponseWait instance;
	
	public static ResponseWait getInstance() {
		if(instance == null)
		{
			instance = new ResponseWait();
			instance.start();
		}
		
		return instance;
	}
	
	private Hashtable<String, List<AsyncResponse>> responseMap;
	private boolean shutdown = false;
	
	public ResponseWait() {
		responseMap = new Hashtable<String, List<AsyncResponse>>();
	}
	
	private void shutdown()
	{
		shutdown = true;
		instance = null;
	}
	
	public void addNewTaskWord(String taskWord, AsyncResponse ar) 
	{
		String error = "Unknown error";
		Status status = Status.INTERNAL_SERVER_ERROR; 
		
		if(taskWord != null && !taskWord.isEmpty())
		{			
			
			System.out.println("Start task with taskword:  " + taskWord);
			TaskRunner.getInstance().runTask(taskWord);
		
			synchronized(responseMap)
			{
				if(responseMap.containsKey(taskWord))
				{
					responseMap.get(taskWord).add(ar);
				}
				else
				{
					List<AsyncResponse> l = new LinkedList<AsyncResponse>();
					l.add(ar);
					responseMap.put(taskWord, l);
				}
			}
			
		}
		else
		{
			status = Status.BAD_REQUEST;
			error = "No taskword was specified";
			
			ErrorDTO errorDTO = new ErrorDTO(error);
			System.out.println(status.getStatusCode() + " GET " + Main.BASE_URI + SentimentRessource.PATH + "?taskword=" + taskWord + " " + System.currentTimeMillis());
			ar.resume(Response.status(status).entity(errorDTO.toString()).build());
		}
	}
	
	@Override
	public void run()
	{
		while(!shutdown)
		{
			try
			{
				Thread.sleep(1000);
				ResultsDatabase resultsDB = ResultsDatabase.getInstance();
				Set<String> keys = new HashSet<String>(responseMap.keySet());
	    		for(String taskWord : keys)
	    		{
	    			//System.out.println("Look for result of task: " + taskWord);				
	    			SentimentResult result = resultsDB.lookUpResult(taskWord);
	    			Long startTime = resultsDB.getTaskStarttime(taskWord);
	    			
	    			//System.out.println("Result is " + (result == null ? "null" : "not null ") + " and startTime is " + (startTime == null ? "null" : startTime));
	    			
		    		if(result != null && startTime == null)
					{
						//System.out.println("Task is finished & Result was found");

						ResultDTO resultDTO = new ResultDTO(result);
						synchronized(responseMap)
						{
							
							List<AsyncResponse> l = responseMap.remove(taskWord);
							System.out.println("Send this result to " + l.size() + " clients" );
							for(AsyncResponse ar : l)
							{
								System.out.println("200 GET " + Main.BASE_URI + SentimentRessource.PATH + "?taskword=" + taskWord + " " + System.currentTimeMillis());
								ar.resume(Response.status(Status.OK).entity(resultDTO.toString()).build());
							}
						}
					}
	    		}
			}
			catch(InterruptedException ex)
			{
				ex.printStackTrace();
			}
    		
		}
	}
}