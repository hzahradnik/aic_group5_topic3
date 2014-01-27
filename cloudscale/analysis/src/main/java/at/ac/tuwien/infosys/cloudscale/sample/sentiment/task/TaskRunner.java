package at.ac.tuwien.infosys.cloudscale.sample.sentiment.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.espertech.esper.epl.generated.EsperEPL2GrammarParser.newAssign_return;

import at.ac.tuwien.infosys.cloudscale.sample.sentiment.ResultsDatabase;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.Task.TaskThread;

public class TaskRunner {
	private static TaskRunner taskRunner;
	
	private ExecutorService executor;
	
	public static TaskRunner getInstance()
	{
		if(taskRunner == null)
			taskRunner = new TaskRunner();
		
		return taskRunner;
		
	}
	
	private TaskRunner()
	{
		executor = Executors.newCachedThreadPool();
	}
	
	// runs task in an executor pool
	public void runTask(String taskWord)
	{
		ResultsDatabase resultsDB = ResultsDatabase.getInstance();
		System.out.println("Check if result is already there");
		if(resultsDB.lookUpResult(taskWord) != null)
		{
			return;
		}
		if(resultsDB.getTaskStarttime(taskWord) != null)
		{
			return;
		}

		new TaskStarter(taskWord).start();
		
	}
	
	class TaskStarter extends Thread
	{
		private String taskWord;
		public TaskStarter(String t) {
			taskWord = t;
		}
		
		@Override
		public void run() {
			
			ResultsDatabase resultsDB = ResultsDatabase.getInstance();
			Task task = new Task(taskWord);
			resultsDB.startTask(taskWord);
			Task.TaskThread taskThread = task.new TaskThread(resultsDB);
			synchronized(executor)
			{
				executor.submit(taskThread);
			}
			
		}
	}
	
	public void shutdown() throws InterruptedException
	{
		executor.shutdown();
		while(!executor.isTerminated())
			executor.awaitTermination(1, TimeUnit.MINUTES);
	}

}
