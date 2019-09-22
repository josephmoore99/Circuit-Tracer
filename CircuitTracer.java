import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail
 */
public class CircuitTracer {

	/** launch the program
	 * @param args three required arguments:
	 *  first arg: -s for stack or -q for queue
	 *  second arg: -c for console output or -g for GUI output
	 *  third arg: input file name 
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
			System.out.println("Did not get 3 arguments");
			System.exit(1);
		}
		try {
			new CircuitTracer(args); //create this with args
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/** Print instructions for running CircuitTracer from the command line. */
	private static void printUsage() {
		System.out.println("Usage: $java CircuitTracer [-s|-q] [-c|-g] [filename]"); // print out usage instructions when there are errors with command line arguments
	}
	
	/** 
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * 
	 * @param args command line arguments passed through from main()
	 */
	private CircuitTracer(String[] args) {

		CircuitBoard board = null;
		Storage<TraceState> stateStore = null;
		ArrayList<TraceState> bestPaths = new ArrayList<TraceState>();
		
		switch(args[0]) { //check first argument
		case "-s":
			stateStore = Storage.getStackInstance();
			break;
		case "-q":
			stateStore = Storage.getQueueInstance();
			break;
		default:
			printUsage();
			System.exit(1);
		}
		
		switch(args[1]) { //check second argument
		case "-c":
			break;
		case "-g":
			throw new UnsupportedOperationException(); //This class currently doesn't support the gui
		default:
			printUsage();
			System.exit(1);
		}
		
		try {
			board = new CircuitBoard(args[2]); // Assemble circuit board from specified file
		} catch(NullPointerException e) { // Couldn't find the file
			printUsage();
			System.exit(1);
		} catch(InvalidFileFormatException e) { // Invalid file format
			throw new InvalidFileFormatException(args[2]);
		} catch(Exception e) { // Some other error
			System.out.println(e);
			System.exit(1);
		}
		
		for(int i=-1; i<2; i+=2) { // initialize the first TraceStates around the start position
			if(board.isOpen(board.getStartingPoint().x+i, board.getStartingPoint().y)) {
				stateStore.store(new TraceState(board, board.getStartingPoint().x+i, board.getStartingPoint().y));
			}
			if(board.isOpen(board.getStartingPoint().x, board.getStartingPoint().y+i)) {
				stateStore.store(new TraceState(board, board.getStartingPoint().x, board.getStartingPoint().y+i));
			}
		}
		
		TraceState temp;
		
		while(!stateStore.isEmpty()) {
			temp = stateStore.retrieve();
			if(temp.isComplete()) { // check if the TraceState has completed
				if(bestPaths.size()!=0) { // Check to see if bestPaths is empty first
					if(temp.pathLength()==bestPaths.get(0).pathLength()) { // if TraceState path is the same size as those in bestPaths
						bestPaths.add(temp);
					} else if(temp.pathLength()<=bestPaths.get(0).pathLength()) { // if TraceState path is smaller than those in bestPaths
						bestPaths = new ArrayList<TraceState>();
						bestPaths.add(temp);
					}
				} else { // if bestPaths is empty, then just add the TraceState
					bestPaths.add(temp);
				}
			} else { // if the TraceState hasn't yet completed, continue to build it
				for(int i=-1; i<2; i+=2) { // find the next possible slots
					if(temp.isOpen(temp.getRow()+i, temp.getCol())) {
						stateStore.store(new TraceState(temp, temp.getRow()+i, temp.getCol()));
					}
					if(temp.isOpen(temp.getRow(), temp.getCol()+i)) {
						stateStore.store(new TraceState(temp, temp.getRow(), temp.getCol()+i));
					}
				}
			}
		}
		
		for(TraceState stuff : bestPaths) { // print out solutions from bestPaths
			System.out.print(stuff.toString());
		}
		

	}
	
} // class CircuitTracer
