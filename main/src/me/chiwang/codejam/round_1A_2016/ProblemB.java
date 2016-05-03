package me.chiwang.codejam.round_1A_2016;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProblemB {

    private static final String DATA_FILE_NAME = "B-large-practice.in";
    
    private static final String PACKAGE_PATH = "me.chiwang.codejam.round_1A_2016".replace(".", File.separator);
    private static final String DATA_FILE_PATH = File.separator + PACKAGE_PATH + File.separator + DATA_FILE_NAME;
    private static final String OUTPUT_FILE_PATH = "/home/chiwang/Documents/codejam/" + getOutputFilename(DATA_FILE_NAME);  
	    
    private static final int NUM_OF_THREAD = Runtime.getRuntime().availableProcessors();
    
    private static class Task implements Callable<String> {
	
	private int caseNum;
	private int N;
	private List<String> lists;
	
	public Task(int caseNum, int N, List<String> lists) {
	    this.caseNum = caseNum;
	    this.N = N;
	    this.lists = lists;
	}

	@Override
	public String call() throws Exception {
	    Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	    
	    for (String list : lists) {
		String[] heights = list.split(" ");
		for (String height : heights) {
		    Integer h = Integer.parseInt(height);
		    if (map.containsKey(h)) {
			map.put(h, map.get(h) + 1);
		    } else {
			map.put(h, 1);
		    }
		}
	    }
	    
	    int[] ans = new int[N];
	    int count = 0;
	    for (Entry<Integer, Integer> entry : map.entrySet()) {
		if (entry.getValue() % 2 == 1) {
		    ans[count++] = entry.getKey();
		}
	    }
	    Arrays.sort(ans);
	    StringBuilder sb = new StringBuilder();
	    
	    for (int i = 0; i < ans.length; i++) {
		sb.append(ans[i]);
		sb.append(" ");
	    }
	    
	    return fr(sb.toString());
	}
	
	private String fr(String result) {
	    return String.format("Case #%d: %s%n", caseNum, result);
	}
    }

    private static void parseInputAndProcess(ExecutorService executorService, List<Future<String>> computedTasks, InputStream is) {
	
	try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
	    final int T = Integer.parseInt(br.readLine());
    	
	    for (int caseNum = 1; caseNum <= T; caseNum++) {
		final int N = Integer.parseInt(br.readLine());
		List<String> lists = new LinkedList<String>();
		for (int i = 0; i < 2 * N - 1; i++) {
		    lists.add(br.readLine());
		}
    	    	Future<String> futureResult = executorService.submit(new Task(caseNum, N, lists));
    	    	computedTasks.add(futureResult);
    	    }
	    
	    executorService.shutdown();
	    
	} catch (NumberFormatException | IOException ex) {
	    ex.printStackTrace();
	    System.exit(-1);
	}
    }

    
    public static void main(String[] args) {
	
	final long startTime = System.currentTimeMillis();
	
	ClassLoader classloader = Thread.currentThread().getContextClassLoader();
	InputStream is = classloader.getClass().getResourceAsStream(DATA_FILE_PATH);
	
	List<Future<String>> computedTasks = new ArrayList<>();
	ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREAD);
	
	parseInputAndProcess(executorService, computedTasks, is);
		
	try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {	    
	    for (Future<String> result : computedTasks) {
		bw.write(result.get());
	    }	    
	} catch (CancellationException | ExecutionException | InterruptedException | IOException ex) {
	    ex.printStackTrace();
	    System.exit(-1);
	}
	
	final long endTime = System.currentTimeMillis();
	
	System.out.println(String.format("Computation finished in %d seconds.%n", (endTime - startTime) / 1000));
    }
    
    private static String getOutputFilename(String inputFilename) {
	int idx = inputFilename.lastIndexOf('.');
	
	if (idx != -1) {
	    return inputFilename.substring(0, idx) + ".out";
	} else {
	    return inputFilename + ".out";
	}
    }
}
