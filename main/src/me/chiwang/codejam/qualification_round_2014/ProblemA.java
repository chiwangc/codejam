package me.chiwang.codejam.qualification_round_2014;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProblemA {
    
    private static final String DATA_FILE_NAME = "A-small-practice.in";
    
    private static final String PACKAGE_PATH = "me/chiwang/codejam/qualification_round_2014".replace("/", File.separator);
    private static final String DATA_FILE_PATH = File.separator + PACKAGE_PATH + File.separator + DATA_FILE_NAME;
    private static final String OUTPUT_FILE_PATH = "/home/chiwang/Documents/" + getOutputFilename(DATA_FILE_NAME);  
	    
    private static final int NUM_OF_THREAD = Runtime.getRuntime().availableProcessors();
    
    private static class Task implements Callable<String> {
	
	private int caseNum;
	private Integer[] firstRound;
	private Integer[] secondRound;
	
	public Task(int caseNum, Integer[] firstRound, Integer[] secondRound) {
	    this.caseNum = caseNum;
	    this.firstRound = firstRound;
	    this.secondRound = secondRound;
	}

	@Override
	public String call() throws Exception {
	    
	    int count = 0;
	    int ans = -1;
	    Set<Integer> set = new HashSet<Integer>(Arrays.asList(firstRound));
	    
	    for (Integer card : secondRound) {
		if (set.contains(card)) {
		    ans = card;
		    count++;
		}
	    }
	    
	    switch (count) {
	    	case 0:
	    	    return fr("Volunteer cheated!");
	    	case 1:
	    	    return fr(Integer.toString(ans));
	    	default:
	    	    return fr("Bad magician!");
	    }
	}
	
	private String fr(String result) {
	    return String.format("Case #%d: %s%n", caseNum, result);
	}
    }

    private static void parseInputAndProcess(ExecutorService executorService, List<Future<String>> computedTasks, InputStream is) throws NumberFormatException, IOException {
	
	BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
	
	final int T = Integer.parseInt(br.readLine());
	
	for (int caseNum = 1; caseNum <= T; caseNum++) {

	    int firstRow = Integer.parseInt(br.readLine());
	    Integer[] firstRound = new Integer[4];
	    
	    for (int i = 0; i < 4; i++) {
		if (i == firstRow - 1) {
		    String[] parts = br.readLine().split(" ");
		    firstRound[0] = Integer.parseInt(parts[0]);
		    firstRound[1] = Integer.parseInt(parts[1]);
		    firstRound[2] = Integer.parseInt(parts[2]);
		    firstRound[3] = Integer.parseInt(parts[3]);
		} else {
		    br.readLine();
		}
	    }
	    
	    int secondRow = Integer.parseInt(br.readLine());
	    Integer[] secondRound = new Integer[4];
	    
	    for (int i = 0; i < 4; i++) {
		if (i == secondRow - 1) {
		    String[] parts = br.readLine().split(" ");
		    secondRound[0] = Integer.parseInt(parts[0]);
		    secondRound[1] = Integer.parseInt(parts[1]);
		    secondRound[2] = Integer.parseInt(parts[2]);
		    secondRound[3] = Integer.parseInt(parts[3]);
		} else {
		    br.readLine();
		}
	    }
	    
	    Future<String> futureResult = executorService.submit(new Task(caseNum, firstRound, secondRound));
	    computedTasks.add(futureResult);
	}
	
	executorService.shutdown();
	br.close();
    }

    
    public static void main(String[] args) throws NumberFormatException, IOException {
	
	final long startTime = System.currentTimeMillis();
	
	ClassLoader classloader = Thread.currentThread().getContextClassLoader();
	InputStream is = classloader.getClass().getResourceAsStream(DATA_FILE_PATH);
	
	List<Future<String>> computedTasks = new ArrayList<>();
	ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREAD);
	
	parseInputAndProcess(executorService, computedTasks, is);
	
	BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH));
	
	try {
	    for (Future<String> result : computedTasks) {
		bw.write(result.get());
	    }	    
	} catch (CancellationException | ExecutionException | InterruptedException ex) {
	    ex.printStackTrace();
	    System.exit(-1);
	} finally {
	    if (bw != null) bw.close();
	}
	
	final long endTime = System.currentTimeMillis();
	
	System.out.println(String.format("Computation finished in %d seconds", (endTime - startTime) / 1000));
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
