import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;



public class Checker {
	
	//Set the language of the checked assignments,
	public static final boolean CHECK_C = true;
	public static final boolean CHECK_JAVA = false;
	
	
	//Set the name of the executable file (which should be the same as the name of the c / java source file)
	public static final String EXEC_FILE_NAME = "assignment3";
	
	//Set the c compiler + flags. command will be run on the terminal.
	public static final String CC = "gcc -o " + EXEC_FILE_NAME;
	
	//Configure JAVA_HOME = java binaries location on the computer. 
	public static final String JAVA_HOME = "C:\\Program Files\\Java\\jdk1.8.0_25\\bin\\";
	public static final String JAVAC = "\"" + JAVA_HOME + "javac.exe\" ";
	public static final String JAVA = "\"" + JAVA_HOME + "java.exe\" ";

	//Configure those 4 paths to point at the directories containing the solution, students code, inputs and location to put logs into.
	public static final String CORRECT_LOCATION = "C:\\Test\\Correct";
	public static final String REPORT_LOCATION = "C:\\Test\\Reports";
	public static final String STUDENT_LOCATION = "C:\\Test\\Students";
	public static final String INPUT_LOCATION = "C:\\Test\\Tests";
	
	
	//Special checks. Use in case you want to disallow loops on the recursion task and so on. Please note that those work based on word counting in the source files. Nothing smart.
	//So for example ALLOW_LOOPS will look into the source file and count all the instances of the words "for" and "while". If this option reports violations make sure to manually check those, as those can show up in comments. 
	public static final boolean ALLOW_LOOPS = true;
	public static final boolean ALLOW_FUNCTIONS = true; //Java only for now.
	public static final boolean ALLOW_ARRAYS = true; //Excluding one for main's signature
	public static final int COUNT_COMMENTS = -1; //Quick hack, may count more comments than there is in the file. But not the other way around. Non positive number disables the check.
	
	public static final int TESTS_WAIGHT = 100;
	public static final int FIXED_POINT_REDUCE = 0; //Reduce fixed amount of points from each grade. good for late submission or other cases.
	public static final boolean DELETE_STUDENT_EXE = false; //Delete the student EXE file when finish the test
	public static final boolean DELET_TEACHER_EXE = false; //Delete the teacher EXE file when finish the test
	public static final boolean USING_EXE_ONLY = true; //When the correct solution is an executable format
	public static final boolean OTHER_WORDS_TO_CHECK = false; //In order to check if any specific words exist in the student code, set this to "true"
	public static final String[] BAD_WORDS_ARRAY = {}; //Add the words for checking here
	
	//Configure debug flags, those show more information on the screen
	public static final boolean DEBUG_PRINT_CORRECT_ANSWERS = false;
	public static final boolean DEBUG_PRINT_STUDENT_ANSWERS = false;
	public static final boolean DEBUG_PRINT_STUDENT_REPORT = true;
	public static final boolean DEBUG_PRINT_CMD_COMMAND = false;
	public static final boolean DEBUG_PRINT_STUDENT_EXE_DELETED = false;
	public static final boolean DEBUG_PRINT_TEACHER_EXE_DELETED = false;

	//Set timeout used to catch infinite loops.
	public static final int PROGRAM_TIMEOUT_IN_SECONDS = 50;

	
	//Set grades for each test in the following format. 
	/**
	 * @return Map that binds test name (filename)to score.
	 */
	private static Map<String, Integer> getHardcodedTestScores(){
		Map<String, Integer> scoresPerTest = new HashMap<String,Integer>();
		hardcodedTestScores(scoresPerTest);
		return scoresPerTest;
	}
	
	//tests and their weight.
	private static void hardcodedTestScores(Map<String, Integer> scoresPerTest){
		
		scoresPerTest.put("1.txt",3);
		scoresPerTest.put("2.txt",3);
		scoresPerTest.put("3.txt",1);
		scoresPerTest.put("4.txt",1);
		scoresPerTest.put("5.txt",1);
		scoresPerTest.put("6.txt",1);
		scoresPerTest.put("7.txt",1);
		scoresPerTest.put("8.txt",1);
		scoresPerTest.put("9.txt",2);
		scoresPerTest.put("10.txt",2);
		scoresPerTest.put("11.txt",2);		
		scoresPerTest.put("12.txt",2);
		scoresPerTest.put("13.txt",2);
		scoresPerTest.put("14.txt",2);		
		scoresPerTest.put("15.txt",3);
		scoresPerTest.put("16.txt",2);
		scoresPerTest.put("17.txt",2);
		scoresPerTest.put("18.txt",3);
		scoresPerTest.put("19.txt",3);
		scoresPerTest.put("20.txt",3);
		scoresPerTest.put("21.txt",2);
		scoresPerTest.put("22.txt",2);
		scoresPerTest.put("23.txt",2);
		scoresPerTest.put("24.txt",2);
		scoresPerTest.put("25.txt",2);
		scoresPerTest.put("26.txt",2);		
		scoresPerTest.put("27.txt",2);
		scoresPerTest.put("28.txt",2);
		scoresPerTest.put("29.txt",2);
		scoresPerTest.put("30.txt",2);
		scoresPerTest.put("31.txt",1);
		scoresPerTest.put("32.txt",2);
		scoresPerTest.put("33.txt",2);
		scoresPerTest.put("34.txt",2);
		scoresPerTest.put("35.txt",2);
		scoresPerTest.put("36.txt",2);
		scoresPerTest.put("37.txt",2);
		scoresPerTest.put("38.txt",2);
		scoresPerTest.put("39.txt",2);
		scoresPerTest.put("40.txt",1);
		scoresPerTest.put("41.txt",2);
		scoresPerTest.put("42.txt",2);
		scoresPerTest.put("43.txt",2);
		scoresPerTest.put("44.txt",2);
		scoresPerTest.put("45.txt",2);
		scoresPerTest.put("46.txt",2);
		scoresPerTest.put("47.txt",2);
		scoresPerTest.put("48.txt",2);
		scoresPerTest.put("49.txt",2);
		scoresPerTest.put("50.txt",4);

	}


	
	public static final ExecutorService executor = Executors.newFixedThreadPool(2);
	
	/**
	 * The program compares the correct answer to students answer with the following code.
	 * @param a String containing an answer
	 * @param b String containing an answer
	 * @return true if both strings are similar enough that the student should get the grade for this test, false otherwise. 
	 */
	private static boolean compareStrings(String a, String b) {
		a = a.trim();
		b = b.trim();
		int i = 0, j = 0;

		while (i < a.length() && j < b.length()) {
			char c1 = a.charAt(i);
			char c2 = b.charAt(j);

			if ((Character.isAlphabetic(c1) && Character.isAlphabetic(c2))) {

				if (!("" + c1).equalsIgnoreCase("" + c2))
					return false;
				i++;
				j++;
			} else if (Character.isDigit(c1) && Character.isDigit(c2)) {
				if (c1 != c2)
					return false;
				i++;
				j++;

			} else {

				if (Character.isWhitespace(c1)) {
					c1 = ' ';
					do {
						i++;
					} while (i < a.length() && Character.isWhitespace(a.charAt(i)));
					i--;
				}

				if (Character.isWhitespace(c2)) {
					c2 = ' ';
					do {
						j++;
					} while (j < b.length() && Character.isWhitespace(b.charAt(j)));
					j--;
				}

				if (c1 != c2)
					return false;
				i++;
				j++;

			}
		}

		if (i >= a.length() && j >= b.length())
			return true;
		else if (i >= a.length() && j < b.length())
			return false;
		else if (i < a.length() && j >= b.length())
			return false;
		else {
			// impossible?
			System.err.println("Both string have free space? weird");
			return true;
		}
	}
	
	
	
	/**
	 * Used to read data from InputStream / BufferedReader with the ability to timeout the reading operation
	 */
	private static class nonBlockingReader implements Callable<String> {

		BufferedReader bufferedreader;

		public nonBlockingReader(BufferedReader buff) {
			bufferedreader = buff;
		}

		public nonBlockingReader(InputStream is) {
			bufferedreader = new BufferedReader(new InputStreamReader(is));
		}

		@Override
		public String call() throws Exception {
			StringBuilder ret = new StringBuilder();
			int out2;
			while (true) {

				out2 = bufferedreader.read();

				if (out2 < 0)
					break;
				ret.append((char) out2);
			}

			return ret.toString();

		}

	}

	/**
	 * Runs commands in the terminal
	 * @param command Command to run
	 * @param cwd command is run from this folder
	 * @param inputFile the command will have its standard input redirected and get it from this file (can be null)
	 * @return the output of the command as string or null in case of a timeout or when something went wrong.
	 * @throws IOException
	 */
	private static String runProcess(String command, File cwd, File inputFile) throws IOException {
		if(DEBUG_PRINT_CMD_COMMAND)
			System.out.println(command + (inputFile != null? " < " + inputFile.getName() : ""));
		Process pro = Runtime.getRuntime().exec(command, null, cwd);
		if (inputFile != null) { // send input into the process
			BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(pro.getOutputStream()));
			BufferedReader fileInput = new BufferedReader(new FileReader(inputFile));
			String in = fileInput.readLine();

			while (in != null) {
				bufferedwriter.write(in + '\n');
				bufferedwriter.flush();
				in = fileInput.readLine();
			}
			fileInput.close();
		}

		String ret;
		try { // get output from the process
			Callable<String> readTask = new nonBlockingReader(pro.getInputStream());
			Future<String> future = executor.submit(readTask);
			ret = future.get(PROGRAM_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
		} catch (Exception e) {
			pro.destroy();
			return null;
		}

		try {
			pro.waitFor();
		} catch (InterruptedException e) {
			System.err.println("Interrupted while waiting for child");
			e.printStackTrace();
		}
		return ret;
	}


	public static String compileAndRun(String filename, File cwd, File inputFile) throws IOException {
		if (CHECK_JAVA)
			return compileAndRunJava(filename, cwd, inputFile);
		if(CHECK_C)
			return compileAndRunC(filename, cwd, inputFile);
		return "";
	}
	
	
	
	
	/**
	 * Compiles and runs java program contained inside a single file, using terminal commands
	 * @param filename the name of the java file
	 * @param cwd the directory the file is located in.
	 * @param inputFile file containing the input of the program. the standard input of the program will be redirected to this file (can be null).
	 * @return the output of the java program as string or null in case of a timeout or when something went wrong.
	 * @throws IOException
	 */
	public static String compileAndRunJava(String filename, File cwd, File inputFile) throws IOException {

		String javacCommand = JAVAC + filename + ".java";
		String javaCommand = JAVA + filename;
		runProcess(javacCommand, cwd, null);
		return runProcess(javaCommand, cwd, inputFile);
	}
	
	
	/**
	 * Compiles and runs c program contained inside a single file, using terminal commands
	 * @param filename the name of the java file
	 * @param cwd the directory the file is located in.
	 * @param inputFile file containing the input of the program. the standard input of the program will be redirected to this file (can be null).
	 * @return the output of the java program as string or null in case of a timeout or when something went wrong.
	 * @throws IOException
	 */
	public static String compileAndRunC(String filename, File cwd, File inputFile) throws IOException {
		
		
		String compileCommand = CC + " "+ filename + ".c";
		String runCommand = "\"" + cwd.toString() + "\\" +  filename + ".exe\"";
		
		File exe = new File(cwd, filename + ".exe");
		if (!exe.exists())
			runProcess(compileCommand, cwd, null);
		
		String out = runProcess(runCommand, cwd, inputFile); 
		return  out;
	}
	

	/**
	 * opens the file in the path and look for occurrence of certain words. used to check for functions / loop use. Warning: word occurrence can be inside comments as well. always manually check to make sure this does what you think it does.
	 * @param path path to file
	 * @return false if no suspicious words are used in the file. true if there is a chance something illegal was used.
	 * @throws IOException
	 */
	private static boolean wordCounter(String path) throws IOException {
		File file = new File(path);
		BufferedReader fileInput = new BufferedReader(new FileReader(file));
		StringBuilder buff = new StringBuilder();
		int staticCount = 0;
		int publicCount = 0;
		int privateCount = 0;
		int arrayCount = 0;
		int forCount = 0;
		int whileCount = 0;
		int stringCount = 0;
		int voidCount = 0;
		int commentCount = 0;
		boolean badWords = false;

		int cInt;
		while ((cInt = fileInput.read()) != -1) {
			char c = (char) cInt;
			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
				buff.append(c);
			}		
			
			else {
				if (c == '[') {
					arrayCount++;
				}
				if(c == '/' || c == '*'){
					commentCount++; // Quickfix: will interpret math (1*2/3) as comments as well.
				}
				

				String word = buff.toString();
				buff.setLength(0);

				if (word.equals("static")) {
					staticCount++;
				}
				if (word.equals("public")) {
					publicCount++;
				}
				if (word.equals("private")) {
					privateCount++;
				}
				if (word.equals("for")) {
					forCount++;
				}
				if (word.equals("while")) {
					whileCount++;
				}
				if (word.equals("String")) {
					stringCount++;
				}
				if (word.equals("void")) {
					voidCount++;
				}
				if(OTHER_WORDS_TO_CHECK){
					for(int i=0; i<BAD_WORDS_ARRAY.length; i++){
						if (word.equals(BAD_WORDS_ARRAY[i])) 
							badWords = true;
					}
					
				}
			}
		}
		fileInput.close();

		boolean badFunc = !ALLOW_FUNCTIONS && (staticCount > 2 || publicCount > 3 || privateCount > 0 || voidCount > 1);  
		boolean badArray = !ALLOW_ARRAYS && (arrayCount > 1);
		boolean badLoops = !ALLOW_LOOPS && (forCount > 0 || whileCount > 0);
		boolean badComments = COUNT_COMMENTS > 0 && commentCount < 2*COUNT_COMMENTS;// "//" and "/*" consists of two characters. 


		return badFunc || badArray || badLoops || badComments || badWords;
		

	}

	/**
	 * Saves the report on the disk
	 * @param content the content of the report
	 * @param path path where it'll be saved
	 * @param filename the name of the file that will be created in path. will be overwritten if exists.
	 */
	private static void saveReport(String content, String path, String filename) {
		File file = new File(path + "\\" + filename);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			BufferedWriter fileOutput = new BufferedWriter(new FileWriter(file));
			fileOutput.write(content);
			fileOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * make sure that the map contains logical scores. checks that the score adds up to 100
	 * @param score maps file names to score
	 * @return true if everything is ok, false otherwise
	 */
	private static boolean checkScores(Map<String,Integer> score) {
		int sum = 0;
		for (int a : score.values()) {
			sum += a;
		}
		if (sum != TESTS_WAIGHT) {
			System.err.println("score doesn't add up to " + TESTS_WAIGHT);
			return false;

		}
		return true;

	}

	/**
	 * get all files inside a folder
	 * @param path path to folder
	 * @return array of files
	 */
	private static File[] getSubFiles(String path) {
		File root = null;

		try {
			root = new File(path);
			if (!root.exists()) {
				System.err.println("Can't open Input dir");
				return null;

			}
		} catch (Exception e) {
			System.err.println("Can't open Input dir");
			e.printStackTrace();
			return null;
		}
		return root.listFiles();

	}

	/**
	 * get all tests / input files for the program
	 * @return array of files
	 */
	private static File[] getInputFiles() {
		return getSubFiles(INPUT_LOCATION);

	}

	/**
	 * get all projects written by the students
	 * @return array of folders, each containing a project
	 */
	private static File[] getStudentProjectDirs() {
		return getSubFiles(STUDENT_LOCATION);
	}


	private static String getCorrectAnswer(String execPath, File cwd, File input) throws IOException {
		String correctOutput = compileAndRun(execPath, cwd, input);
		if (correctOutput == null) {
			System.err.println("Error: Correct program timeout");
		}
		if (correctOutput == "") {
			System.err.println("Warrning: Correct program output is empty string");
		}
		if (DEBUG_PRINT_CORRECT_ANSWERS) {
			System.out.println("Correct: " + correctOutput);
		}
		return correctOutput;
	}

	private static List<String> getCorrectAnswers(String correctLocation, File[] inputs) {

		List<String> correct = new LinkedList<String>();
		try {
			File cwd = new File(correctLocation);

			if (!cwd.exists()) {
				System.err.println("Can't open CORRECT_LOCATION");
				return null;
			}
			
			String extRun = CHECK_JAVA? ".class" : CHECK_C? ".exe" : "";
			File exe = new File(cwd, EXEC_FILE_NAME + extRun);
			if (!USING_EXE_ONLY && exe.exists()){
				if (DEBUG_PRINT_TEACHER_EXE_DELETED){
					System.out.println("Deleting old file: Teacher's " + EXEC_FILE_NAME + extRun);
					System.out.println("Press any key to continue");
					System.in.read();
				}
				exe.delete();
			}

			if (inputs != null && inputs.length > 0) {

				for (File input : inputs) {
					if (input.isDirectory())
						continue;
					correct.add(getCorrectAnswer(EXEC_FILE_NAME, cwd, input));
				}

			} else {
				correct.add(getCorrectAnswer(EXEC_FILE_NAME, cwd, null));
			}			
			
			if (DELET_TEACHER_EXE &&exe.exists()){
				exe.delete();
			}

		} catch (IOException e) {
			System.err.println("Problem with the original, terminating.");
			e.printStackTrace();
			return null;
		}

		return correct;
	}

	

	private static boolean checkAnswer(File projectRoot, File input, String correctAnswer) {
		try {
			String studentAnswer = compileAndRun(EXEC_FILE_NAME, projectRoot, input);

			boolean isCorrect = compareStrings(correctAnswer, studentAnswer);
			if (DEBUG_PRINT_STUDENT_ANSWERS) {
				System.out.println(projectRoot.getName() + ": ");
				System.out.println(studentAnswer);
			}
			return isCorrect;
		} catch (Exception e) {
			return false;
		}
	}
	
	private static class CheckResult{
		public enum Status {
			USED_ILEAGAL, MISSING_FILE,MISSING_SCORE_FOR_TEST, SIZE_MISMATCH, CORRECT_ANSWER_NOT_FOUND, OK
		}
		
		
		public Status status;
		public Integer score;
		public String note;
		
		
		CheckResult(Status status, Integer score, String note){
			this.status = status;
			this.score = score;
			this.note = note;
		}
		
	}

	private static CheckResult checkProject(File projectRoot, File[] tests, Map<String,Integer> scorePerTest, List<String> correctAnswers) {
		if (tests != null && tests.length > 0) {
			Iterator<String> correctIterator = correctAnswers.iterator();
			int totalScore = 0;
			StringBuilder failedChecks = new StringBuilder();
			String extCode = CHECK_JAVA? ".java" : CHECK_C? ".c" : "";
			String extRun = CHECK_JAVA? ".class" : CHECK_C? ".exe" : "";
			String path = projectRoot.getAbsolutePath() + '\\' + EXEC_FILE_NAME + extCode;
			CheckResult.Status status = CheckResult.Status.OK;

			try {
				boolean usedIleagal = wordCounter(path);
				if (usedIleagal) {
					status = CheckResult.Status.USED_ILEAGAL;
				}

			} catch (IOException e) {
				status = CheckResult.Status.MISSING_FILE;

			}
			
			File exe = new File(projectRoot, EXEC_FILE_NAME + extRun);
			if (exe.exists()){
				if (DEBUG_PRINT_STUDENT_EXE_DELETED)
					System.out.println("Deleting old file: Student's "+ EXEC_FILE_NAME + extRun + " (" + projectRoot.getName() + ")");
				exe.delete();
			}

			for (File input : tests) {
				if (!correctIterator.hasNext()) {
					System.err.println("Number of tests and correct answers missmatch");
					status = CheckResult.Status.SIZE_MISMATCH;
					break;
				}
				String correctAnswer = correctIterator.next();
				Integer score = scorePerTest.get(input.getName());
				if (score == null){
					status = CheckResult.Status.MISSING_SCORE_FOR_TEST;
					break;
				}

				boolean isCorrect = checkAnswer(projectRoot, input, correctAnswer);
				if (isCorrect) {
					totalScore += score;
				}else{
					failedChecks.append(' ');
					failedChecks.append(input.getName());
				}
			}
			
			if (DELETE_STUDENT_EXE && exe.exists()){
				exe.delete();
			}
			
			String note = failedChecks.length() != 0? "Failed:" + failedChecks.toString():"";
			return new CheckResult(status, Math.max(totalScore-FIXED_POINT_REDUCE, 0), note);
		} else {
			if (correctAnswers.size() == 0) {
				return new CheckResult(CheckResult.Status.CORRECT_ANSWER_NOT_FOUND, null, null);
			}
			String correctAnswer = correctAnswers.get(0);

			boolean isCorrect = checkAnswer(projectRoot, null, correctAnswer);
			if (isCorrect) {
				return new CheckResult(CheckResult.Status.OK, Math.max(100-FIXED_POINT_REDUCE, 0), "");
			} else {
				return new CheckResult(CheckResult.Status.OK, 0, "Failed Test");
			}
		}

	}

	public static void check() {

		int numberOfStudents = 0;
		int numberOfFails = 0;
		int sumOfGrades = 0;

		StringBuilder fullReport = new StringBuilder();

		fullReport.append("groupName,id,projectScore,zeroForTechnicalReasons 0=true 1=false,finalScore,note\n");
		StringBuilder submissionSystemReport = new StringBuilder();

		//List<Integer> scoresPerTest = hardcodedScoresPerTest;
		Map<String,Integer> scoresPerTest = getHardcodedTestScores();
		checkScores(scoresPerTest);

		File[] inputs = getInputFiles();
		List<String> correct = getCorrectAnswers(CORRECT_LOCATION, inputs);
		if (correct == null) {
			return;
		}

		if (correct.size() != 1 && scoresPerTest.size() != correct.size()) {
			System.err.println("The number of scores doesn't match the number of Tests! (Continuing to run)");
		}

		File[] projectDirs = getStudentProjectDirs();

		for (File projectDir : projectDirs) {
			if (!projectDir.isDirectory())
				continue;
			CheckResult res = checkProject(projectDir, inputs, scoresPerTest, correct);
			String[] idAndName = getGroupName(projectDir.getName());
			String groupID = idAndName[0];
			String groupName = idAndName[1];

			int zeroForTechnicalReasons = 1;
			switch (res.status) {
				case USED_ILEAGAL:
					res.note = "Used Ilegal Expression " + res.note;
					zeroForTechnicalReasons = 0;
				break;//break the switch - continue to check this student

				case MISSING_FILE:
					System.err.printf("Can't Open %s in project %s, id %s%n", EXEC_FILE_NAME, groupID, groupName);
					res.note = "Can't locate " + EXEC_FILE_NAME;
					zeroForTechnicalReasons = 0;

				break;//break the switch - continue to check this student
				case SIZE_MISMATCH:
					System.err.printf("Size Missmatch: Something went terribly wrong in project %s, id %s%n", groupID, groupName);

					continue;// loop to the next student

				case CORRECT_ANSWER_NOT_FOUND:
					System.err.printf("Correct Answer is missing: Something went terribly wrong in project %s, id %s%n", groupID, groupName);
					continue;// loop to the next student
					
				case MISSING_SCORE_FOR_TEST:
					
					System.err.println("Missing score for a Test");
					
					continue;// loop to the next student

				case OK:

				break;//break the switch - continue to check this student
			}

			int finalScore = zeroForTechnicalReasons == 0 ? 0 : res.score;

			numberOfStudents++;
			if (finalScore < 56) {
				numberOfFails++;
			}
			sumOfGrades += finalScore;
			
			String fullReportLine = groupName + ',' + groupID + ',' + res.score + ',' + zeroForTechnicalReasons + ',' + finalScore + ',' + res.note + "\n";
			
			fullReport.append(fullReportLine);

			String submissionSystemReportLine = groupID + ',' + res.score + ',' + res.note + "\n";
			submissionSystemReport.append(submissionSystemReportLine);
			if (DEBUG_PRINT_STUDENT_REPORT)
				System.out.print(fullReportLine);

		}
		executor.shutdown();
		String conclusion = String.format("%nNumber of Submitions: %d%nNumber of fails: %d%nPercentage of fails %.2f%%%nGrade Average: %.2f%n", numberOfStudents, numberOfFails, (((double) numberOfFails) / numberOfStudents) * 100, (((double) sumOfGrades) / numberOfStudents));
		System.out.println(conclusion);

		fullReport.append("\n\n\n");
		fullReport.append(conclusion);

		saveReport(fullReport.toString(), REPORT_LOCATION, "reportCSV.csv");
		saveReport(submissionSystemReport.toString(), REPORT_LOCATION, "reportSubmissionSystem.csv");

	}

	private static String[] getGroupName(String fileName) {
		int length = fileName.length();
		int i;
		for (i = 0; i < length; i++) {
			char c = fileName.charAt(i);
			if (c < '0' || c > '9') {
				break;
			}
		}

		String id = fileName.substring(0, i);
		String name = fileName.substring(i + 1);

		return new String[] { id, name };
	}


	public static void main(String[] args) {
		check();
	}

}
