package GAPL_project3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import org.ggp.base.util.game.Game;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import javafx.util.Pair;


public class utils {

	/**
	 * Monte-Carlo Tree Search. Runs until maximum number of iterations are reached
	 * or it catches a TimeoutException in which case it stops and returns a
	 * predicted best move for given role.
	 * @param node - pointer to a GameTree to use
	 * @param machine - state machine
	 * @param role - Role object representing the current player
	 * @param maxIter - maximum number of iterations the search goes through
	 * @param timeLimit - maximum time in milliseconds to spend in MCTS
	 * @param C - exploration/exploitation factor, higher => more exploration,
	 * lower => more exploitation
	 * @return
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws GoalDefinitionException
	 */
	public static Pair<Move,GameTree> MCTS(GameTree node, StateMachine machine, Role role, int maxIter, long timeLimit, double C)
			throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {

		long start = System.currentTimeMillis();
		int roleIndex = node.getRoleIndex(role);
		int iter = 1;

		try {
			while(!timesUp(start,timeLimit) && iter <= maxIter) {

				ArrayList<int[]> takenMoves = new ArrayList<>();
				GameTree currentNode = node;

				/* PHASE 1 - SELECTION */
				Pair<int[],List<Move>> jmoves = selection(currentNode,C);
				int[] jmIndex = jmoves.getKey();
				List<Move> jm = jmoves.getValue();

				takenMoves.add(jmIndex);

				while(currentNode.hasChild(jm)) {
					currentNode = currentNode.getChild(jm);

					if (!currentNode.isTerminal()) {
						jmoves = selection(currentNode.getChild(jm),C);
						jmIndex = jmoves.getKey();
						jm = jmoves.getValue();
						takenMoves.add(jmIndex);
					}

					timesUp(start,timeLimit);
				}

				/* PHASE 2 - EXPANSION */
				GameTree child;
				GameTree rolloutNode = currentNode;
				if (!currentNode.isTerminal()) {
					child = currentNode.getChild(jm);
					rolloutNode = child;
				}

				/* PHASE 3 - PLAYOUT */
				timesUp(start,timeLimit);
				double[] goalValues = rollout(rolloutNode, machine);

				/* PHASE 4 - BACK-PROPAGATION */
				timesUp(start,timeLimit);
				backPropagate(rolloutNode.getParent(), machine, goalValues, takenMoves);

				iter++;
			}
		} catch (TimeoutException e) {
			return new Pair<Move,GameTree>(bestMove(node, roleIndex),node);
		}

		return new Pair<Move,GameTree>(bestMove(node, roleIndex),node);
	}

	/**
	 * Finds the best move for role (represented by roleIndex) by looking at
	 * which child node has been visited most often (and as such its Q score is
	 * likely to be very high)
	 * @param node - the current GameTree which stores next legal moves and the
	 * Q and N values for deciding best move
	 * @param roleIndex - the index of current role, in the order a state machine
	 * would fetch it
	 * @return Move object with the highest visit count
	 */
	public static Move bestMove(GameTree node, int roleIndex) {
		int mostVisits = 0;
		int idx = 0;

		int[] moveCounts = node.getAllNs()[roleIndex];
		Move[] lm = node.getLegalMoves()[roleIndex];

		for(int i = 0; i < moveCounts.length; i++) {
			if (moveCounts[i] > mostVisits) {
				mostVisits = moveCounts[i];
				idx = i;
			}
		}
		return lm[idx];
	}

	/**
	 * Measures the time from start and throws an exception if it exceeds the
	 * time limit.
	 * @param start - start time in milliseconds
	 * @param timeLimit - maximum time in milliseconds
	 * @return false if current time has not exceeded timeLimit
	 * @throws TimeoutException
	 */
	public static boolean timesUp(long start, long timeLimit) throws TimeoutException {
		long stop = System.currentTimeMillis();
		if(stop - start >= timeLimit) {
			System.out.println("Timeout");
			throw new TimeoutException();
		}
		return false;
	}

	/**
	 * Finds the moves with the best computed UCT values for each role.
	 * @param currentNode - the GameTree/state which legal moves we wish to
	 * apply UCT to.
	 * @param C - exploration/exploitation factor.
	 * @return Pair of an int array of indices into the legalMoves of currentNode
	 * which correspond to the selected moves, f.i. i = int[3] is move i for
	 * role 3 as well as the selected joint move represented by a list of Move
	 * objects.
	 */
	public static Pair<int[],List<Move>> selection(GameTree currentNode, double C) {
		int nRoles = currentNode.getNoRoles();
		int[] jmIndex = new int[nRoles];
		for (int i = 0; i < nRoles; i++) {
			jmIndex[i] = UCT(currentNode.getAllQScores()[i], currentNode.getAllNs()[i], currentNode.getNoSimulation(), C);
		}
		List<Move> jm = getJointMove(jmIndex, currentNode.getLegalMoves());
		return new Pair<int[],List<Move>>(jmIndex,jm);
	}

	/**
	 * Returns the index of the next Move action (node to visit) to take/examine
	 * for a given role based on the current node's Q values for its children and
	 * the number of times a child node has been visited. The move-index chosen will
	 * be the one that maximizes the moves UCT value. The Qs[] and Ns[] must
	 * be of the same size.
	 * @param Qs - Q values for all roles' moves from the current state
	 * @param Ns - number of times a role has chosen a specific move
	 * @param N - number of times current state has been visited
	 * @param C - controls exploration (higher C => values less explored are
	 * prioritised) vs. exploitation (lower C => values with better Q score
	 * are prioritised). Typical values for C are in the interval [0,100]
	 * @return next Move-index for a given role.
	 */
	public static int UCT(double[] Qs, int[] Ns, int N, double C) {
		double max = Double.MIN_VALUE;
		int returnIndex = 0;
		double val;
		for(int i = 0; i < Qs.length; i++) {
			if (Ns[i] == 0 && C != 0) return i;
			val = Qs[i] + C*Math.sqrt(Math.log((double) N)/((double) Ns[i]));
			if (val > max) {
				max = val;
				returnIndex = i;
			}
		}
		return returnIndex;
	}

	/**
	 * Simulates a random game from the state of node/GameTree t.
	 * @param t - Current node/GameTree
	 * @param machine - Initalized StateMachine with the GDL rules of the game
	 * @return double array with GoalValues from that random game for all roles
	 * in the order machine fetches them
	 * @throws GoalDefinitionException
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 */
	public static double[] rollout(GameTree t, StateMachine machine)
			throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		MachineState ms = t.getState();
		if (machine.isTerminal(ms)) { // Base case
			List<Role> roles = machine.getRoles();
			double[] goalValues = new double[roles.size()];
			for(int i = 0; i < roles.size(); i++) {
				goalValues[i] = (double) machine.getGoal(ms, roles.get(i));
			}
			return goalValues;
		}else{ // Recursion
			Pair<List<Move>,Integer[]> randMove = getRandJointMoves(t.getLegalMoves());
			GameTree randChild = t.getChild(randMove.getKey());
			return rollout(randChild, machine);
		}
	}

	/**
	 * After rollout/simulation from a chosen node/GameTree/state all ancestor
	 * nodes' Q values for the chosen node to simulate a random game from must
	 * be updated.
	 * @param t - Parent node of the node that rollout took place from
	 * @param machine - initalized StateMachine with the relevant game description
	 * @param goalVal - array of double goal values found from the random
	 * simulation; values must be in the same order as the StateMachine would
	 * fetch the roles
	 * @param moveList - Stack (as an ArrayList) of move indices taken for every
	 * role that were taken to reach the current state. Required to update correct
	 * indices in the Q arrays of each node. For instance if there were 3 roles,
	 * moveList could look like this:
	 * [4,1,0] -> [2,0,2] -> [0,2,1]
	 * The first int array represents the moves (as indices) each role took in
	 * the oldest ancestor node. The last int array are the most recent moves
	 * taken. This assumes the order of roles is the same as the StateMachine
	 * would fetch them in.
	 */
	public static void backPropagate(GameTree t, StateMachine machine, double[] goalVal, ArrayList<int[]> moveList) {
		int[] theMoves = moveList.remove(moveList.size()-1);
		t.incrNoSimulation();
		for(int i = 0; i < theMoves.length; i++) {
			t.updateQScore(i, theMoves[i], goalVal[i]);
			t.incrNs(i, theMoves[i]);
		}
		if (t.getParent() != null) { // Base case is parent == null, in that case we don't do anything else
			backPropagate(t.getParent(), machine, goalVal, moveList);
		}
	}

	/**
	 * Creates an array of randomly chosen Move action for every role, in
	 * conjunction these represent a joint Move.
	 * @param legalMoves - Move[][] array, rows are Move[] arrays which contain
	 * the legal moves for the role represented by that row
	 * @return Array of Move objects for every role. This assumes roles are
	 * indexed in the same order as a StateMachine would fetch them.
	 */
	public static Pair<List<Move>,Integer[]> getRandJointMoves(Move[][] legalMoves) {
		List<Move> jointMove = new ArrayList<>();
		Integer[] jointMoveIdx = new Integer[legalMoves.length];
		int noLegalMoves;
		int randint;
		for(int i = 0; i < legalMoves.length; i++) {
			noLegalMoves = legalMoves[i].length;
			randint = (new Random()).nextInt(noLegalMoves);
			jointMoveIdx[i] = (Integer) randint;
			jointMove.add(legalMoves[i][randint]);
		}
		return new Pair<List<Move>,Integer[]>(jointMove,jointMoveIdx);
	}

	/**
	 * Creates a List of Moves to represent a joint move.
	 * @param idx - index array for moves, f.i. i = idx[3] is move i for role 3.
	 * @param legalMoves - legal moves for an implied current state.
	 * @return joint move as a list of Move objects.
	 */
	public static List<Move> getJointMove(int[] idx, Move[][] legalMoves) {
		List<Move> jm = new ArrayList<>();
		for (int i = 0; i < idx.length; i++) {
			jm.add(legalMoves[i][idx[i]]);
		}
		return jm;
	}

	/**
	 * Takes an absolute path to a GDL file and returns a StateMachine,
     * intialized with the game description from gdlFile.
	 * @param gdlFile - Absolute path to a GDL file
	 * @return initalized StateMachine
	 * @throws IOException
	 */
	public static StateMachine getGameSM(String gdlFile) throws IOException{
		FileInputStream fstream = new FileInputStream(gdlFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine = br.readLine();
		StringBuilder sb = new StringBuilder();

		while (strLine != null) {
			if(!strLine.startsWith(";")) {
				sb.append(strLine).append("\n");
			}
			strLine = br.readLine();
		}
		br.close();
		String gd = sb.toString();
		gd = gd.replaceAll("[\n]+","\n");

		gd = Game.preprocessRulesheet(gd);
		Game g = Game.createEphemeralGame(gd);
		List<Gdl> rules = g.getRules();
		StateMachine s = new ProverStateMachine();
		s.initialize(rules);

		return s;
	}
}
