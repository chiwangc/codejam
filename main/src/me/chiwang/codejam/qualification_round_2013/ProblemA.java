package me.chiwang.codejam.qualification_round_2013;

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

    private static final String DATA_FILE_NAME = "A-large-practice.in";

    private static final String PACKAGE_PATH = "me.chiwang.codejam.qualification_round_2013".replace(".",
	    File.separator);
    private static final String DATA_FILE_PATH = File.separator + PACKAGE_PATH + File.separator + DATA_FILE_NAME;
    private static final String OUTPUT_FILE_PATH = "/home/chiwang/Documents/codejam/"
	    + getOutputFilename(DATA_FILE_NAME);

    private static final int NUM_OF_THREAD = Runtime.getRuntime().availableProcessors();

    private static class Task implements Callable<String> {

	private int caseNum;
	private char[][] board;

	// Statuses
	private boolean hasEmptyCell = false;

	public Task(int caseNum, char[][] board) {
	    this.caseNum = caseNum;
	    this.board = board;
	}

	@Override
	public String call() throws Exception {

	    boolean hasEmpty = false;
	    int xCount, oCount;

	    // Check rows
	    for (int i = 0; i < 4; i++) {
		xCount = 0;
		oCount = 0;
		for (int j = 0; j < 4; j++) {
		    switch (board[i][j]) {
		    case 'X':
			xCount++;
			break;
		    case 'O':
			oCount++;
			break;
		    case 'T':
			xCount++;
			oCount++;
			break;
		    default:
			hasEmpty = true;
		    }
		}

		if (xCount == 4)
		    return fr("X won");
		if (oCount == 4)
		    return fr("O won");
	    }

	    // Check columns
	    for (int i = 0; i < 4; i++) {
		xCount = 0;
		oCount = 0;
		for (int j = 0; j < 4; j++) {
		    switch (board[j][i]) {
		    case 'X':
			xCount++;
			break;
		    case 'O':
			oCount++;
			break;
		    case 'T':
			xCount++;
			oCount++;
			break;
		    default:
			hasEmpty = true;
		    }
		}

		if (xCount == 4)
		    return fr("X won");
		if (oCount == 4)
		    return fr("O won");
	    }

	    // Check diagonal
	    xCount = 0;
	    oCount = 0;

	    for (int i = 0; i < 4; i++) {
		switch (board[i][i]) {
		case 'X':
		    xCount++;
		    break;
		case 'O':
		    oCount++;
		    break;
		case 'T':
		    xCount++;
		    oCount++;
		    break;
		default:
		    hasEmpty = true;
		}
	    }

	    if (xCount == 4)
		return fr("X won");
	    if (oCount == 4)
		return fr("O won");
	    
	    xCount = 0;
	    oCount = 0;

	    for (int i = 0; i < 4; i++) {
		switch (board[i][3 - i]) {
		case 'X':
		    xCount++;
		    break;
		case 'O':
		    oCount++;
		    break;
		case 'T':
		    xCount++;
		    oCount++;
		    break;
		default:
		    hasEmpty = true;
		}
	    }

	    if (xCount == 4)
		return fr("X won");
	    if (oCount == 4)
		return fr("O won");
	    
	    if (hasEmpty)
		return fr("Game has not completed");
	    else 
		return fr("Draw");

	}

	private String fr(String result) {
	    return String.format("Case #%d: %s%n", caseNum, result);
	}
    }

    private static void parseInputAndProcess(ExecutorService executorService, List<Future<String>> computedTasks,
	    InputStream is) {

	try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
	    final int T = Integer.parseInt(br.readLine());

	    for (int caseNum = 1; caseNum <= T; caseNum++) {

		char[][] board = new char[4][4];

		board[0] = br.readLine().toCharArray();
		board[1] = br.readLine().toCharArray();
		board[2] = br.readLine().toCharArray();
		board[3] = br.readLine().toCharArray();

		if (caseNum != T)
		    br.readLine();

		Future<String> futureResult = executorService.submit(new Task(caseNum, board));
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
