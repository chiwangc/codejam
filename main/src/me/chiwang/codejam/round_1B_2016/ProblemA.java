package me.chiwang.codejam.round_1B_2016;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProblemA {

    private static final String DATA_FILE_NAME = "A-large-practice.in";
    
    private static final String PACKAGE_PATH = "me.chiwang.codejam.round_1B_2016".replace(".", File.separator);
    private static final String DATA_FILE_PATH = File.separator + PACKAGE_PATH + File.separator + DATA_FILE_NAME;
    private static final String OUTPUT_FILE_PATH = "/home/chiwang/Documents/codejam/" + getOutputFilename(DATA_FILE_NAME);  
	    
    private static final int NUM_OF_THREAD = Runtime.getRuntime().availableProcessors();
    
    private static class Task implements Callable<String> {
	
	private int caseNum;
	private String phoneNumber;
	
	public Task(int caseNum, String phoneNumber) {
	    this.caseNum = caseNum;
	    this.phoneNumber = phoneNumber;
	}

	@Override
	public String call() throws Exception {
	    Map<Character, Integer> map = new HashMap<Character, Integer>();
	    
	    for (int i = 0; i < phoneNumber.length(); i++) {
		char c = phoneNumber.charAt(i);
		if (map.containsKey(c)) {
		    map.put(c, map.get(c) + 1);
		} else {
		    map.put(c, 1);
		}
	    }
	    
	    int[] counts = new int[10];
	    counts[0] = map.containsKey('Z') ? map.get('Z') : 0;
	    counts[2] = map.containsKey('W') ? map.get('W') : 0;
	    counts[4] = map.containsKey('U') ? map.get('U') : 0;
	    counts[6] = map.containsKey('X') ? map.get('X') : 0;
	    counts[8] = map.containsKey('G') ? map.get('G') : 0;
	    counts[1] = map.containsKey('O') ? map.get('O') - counts[0] - counts[2] - counts[4] : 0;
	    counts[3] = map.containsKey('R') ? map.get('R') - counts[0] - counts[4] : 0;
	    counts[5] = map.containsKey('F') ? map.get('F') - counts[4] : 0;
	    counts[7] = map.containsKey('S') ? map.get('S') - counts[6] : 0;
	    counts[9] = map.containsKey('N') ? (map.get('N') - counts[1] - counts[7]) / 2 : 0;
	    
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < 10; i++) {
		for (int j = 0; j < counts[i]; j++) {
		    sb.append(i);
		}
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
		String phoneNumber = br.readLine();
    	    	Future<String> futureResult = executorService.submit(new Task(caseNum, phoneNumber));
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
