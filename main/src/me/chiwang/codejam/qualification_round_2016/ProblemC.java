package me.chiwang.codejam.qualification_round_2016;

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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProblemC {

    private static final String DATA_FILE_NAME = "C-large-attempt0.in";
    
    private static final String PACKAGE_PATH = "me.chiwang.codejam.qualification_round_2016".replace(".", File.separator);
    private static final String DATA_FILE_PATH = File.separator + PACKAGE_PATH + File.separator + DATA_FILE_NAME;
    private static final String OUTPUT_FILE_PATH = "/home/chiwang/Documents/codejam/" + getOutputFilename(DATA_FILE_NAME);  
	    
    private static final int NUM_OF_THREAD = Runtime.getRuntime().availableProcessors();
    
    private static final int[] smallPrimes = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};
    
    private static int[][][] coefficients;    // coefficients[i][j][k] - mod (i + 1)-th prime, base j, power k
    
    private static List<String> results = new LinkedList<String>();

    private static class Task {
	
	private int[] candidate;
	
	public Task(int[] candidate) {
	    this.candidate = candidate;	    
	}

	public String compute() {
	    
	    int[] factors = new int[9];
	    
	    for (int base = 2; base <= 10; base++) {	
		int factor = getSmallFactor(base);
		
		if (factor == -1)
		    return null;
		else
		    factors[base - 2] = factor;
	    }
	    
	    return fr(factors);
	}
	
	private String getStringRepresentation() {
	    StringBuilder sb = new StringBuilder();
	    
	    for (int i = candidate.length - 1; i >= 0; i--) {
		sb.append(candidate[i]);
	    }
	    
	    return sb.toString();
	}
	
	private int getSmallFactor(int base) {
	    for (int i = 0; i < smallPrimes.length; i++) {
		
		int sum = 0;		
		for (int k = 0; k < candidate.length; k++) {
		    sum = (sum + candidate[k] * coefficients[i][base][k]) % smallPrimes[i];
		}
		
		if (sum == 0) {
		    return smallPrimes[i];
		}
	    }
	    return -1;
	}
	
	private String fr(int[] factors) {
	    return String.format("%s %d %d %d %d %d %d %d %d %d %n", getStringRepresentation(), factors[0], factors[1], factors[2], factors[3], factors[4], factors[5], factors[6], factors[7], factors[8]);
	}
	
    }

    private static void parseInputAndProcess(ExecutorService executorService, List<Future<String>> computedTasks, InputStream is) {
	
	try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
	    
	    final int T = Integer.parseInt(br.readLine());	// T should always be 1
	    
	    String[] parts = br.readLine().split(" ");
	    final int N = Integer.parseInt(parts[0]);
	    final int J = Integer.parseInt(parts[1]);
	    
	    // initiate coefficients for factorization lookup
	    coefficients = new int[smallPrimes.length][11][N];
	    
	    for (int i = 0; i < smallPrimes.length; i++) {
		final int modPrime = smallPrimes[i];
		
		for (int j = 2; j < 11; j++) {	// For base 2 to 10
		    int a = 1;
		    
		    for (int k = 0; k < N; k++) {
			coefficients[i][j][k] = a;
			a = (a * j) % modPrime;
		    }
		}
	    }
	    
	    int count = 0;
    
	    for (int i = 0; i < 1 << (N - 2); i++) {
		int[] candidate = getCoinJamCandidate(i, N);
		String result = new Task(candidate).compute();
		
		if (result != null) {
		    results.add(result);
		    if (++count == J) {
			break;
		    }
		}
	    }
	    
	} catch (NumberFormatException | IOException ex) {
	    ex.printStackTrace();
	    System.exit(-1);
	}
    }
    
    private static int[] getCoinJamCandidate(int num, int N) {
	int[] candidate = new int[N];
	candidate[0] = 1;
	candidate[N - 1] = 1;
	
	int idx = 1;
	
	while (num > 0) {
	    candidate[idx] = num % 2;
	    idx++;
	    num /= 2;
	}
	
	return candidate;
    }

    
    public static void main(String[] args) {
	
	final long startTime = System.currentTimeMillis();
	
	ClassLoader classloader = Thread.currentThread().getContextClassLoader();
	InputStream is = classloader.getClass().getResourceAsStream(DATA_FILE_PATH);
	
	List<Future<String>> computedTasks = new ArrayList<>();
	ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREAD);
	
	parseInputAndProcess(executorService, computedTasks, is);
		
	try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {
	    bw.write("Case #1: \n");
	    for (String result : results) {
		bw.write(result);
	    }	    
	} catch (CancellationException | IOException ex) {
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
