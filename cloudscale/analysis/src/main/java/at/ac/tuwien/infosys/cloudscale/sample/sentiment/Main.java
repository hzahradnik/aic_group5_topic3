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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import at.ac.tuwien.infosys.cloudscale.annotations.CloudScaleShutdown;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.Task;
import at.ac.tuwien.infosys.cloudscale.sample.sentiment.task.TaskRunner;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.Uri;


public class Main {
	
	public static final String TASK_FILE = "task.txt";
	public static final String SERIALIZED_MODELS = "files";
	public static final String LOAD_DEFINITION = "load.txt";
	
    public static final URI BASE_URI;
    static {
     BASE_URI = URI.create("http://0.0.0.0:8080/api/");
    }
	
	@CloudScaleShutdown
	public static void main(String[] args) throws InterruptedException,
		FileNotFoundException, IOException {
		
	    final ResourceConfig resourceConfig = new ResourceConfig(SentimentRessource.class);
	    final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig);

	    System.out.println(String.format("Server started: %s%s",
	    		BASE_URI.toString(), SentimentRessource.PATH));
		
		// load tasks from file
		List<String> tasks = readTasks(TASK_FILE);
		List<Integer> delays = readDelays(LOAD_DEFINITION);
		
		Scanner scanner = new Scanner(System.in);
		TaskRunner taskRunner = TaskRunner.getInstance();
		
		
		System.out.println("Starting CloudScale sentiment analysis sample");
		while(true) {
			System.out.print("task word> ");
			String taskWord = scanner.nextLine();
			if(taskWord.equals("EXIT"))
			{
				break;
			}
			if(taskWord.equals("CLEAR"))
			{
				ResultsDatabase.getInstance().clear();
				continue;
			}
			if(taskWord.equals("RESULT"))
			{
				ResultsDatabase.getInstance().print();
				continue;
			}
			
			System.out.println("Scheduling new task");
			taskRunner.runTask(taskWord);
			// start a new request
			// (note that we do that in a separate thread, because requests
			//  should come in despite us e.g., waiting for a host to start)
						
		}
		
		System.out.println("Wait for results");
		taskRunner.shutdown();
	
		
		System.out.println("Shutting down Sentiment Analysis example");
		System.out.println("We are done. Printing results now");
		ResultsDatabase.getInstance().print();
		server.shutdownNow();
		
	}
	
	private static List<String> readTasks(String taskfile) 
	{
		List<String> res = new ArrayList<String>();
		try
		{
			Scanner scanner = new Scanner(new FileReader(taskfile));
			try
			{
				while(scanner.hasNext())
					res.add(scanner.nextLine());
			}
			finally
			{
				scanner.close();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return res;
	}
	
	private static List<Integer> readDelays(String delaysfile) 
	{
		List<Integer> ints = new ArrayList<Integer>();
		try
		{
			Scanner scanner = new Scanner(new FileReader(delaysfile));
			try
			{
				while(scanner.hasNext())
					ints.add(Integer.parseInt(scanner.nextLine().trim()));
			}
			finally
			{
				scanner.close();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return ints;
	}
	
	private static class RequestRunner extends Thread {
		
		private List<String> tasks;
		
		public RequestRunner(List<String> tasks) {
			this.tasks = tasks;
		}
		
		@Override
		public void run() {
			Thread thread = Task.randomTask(tasks, ResultsDatabase.getInstance());
			thread.setDaemon(true);
			thread.start();
		}
		
	}
}
