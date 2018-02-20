package GAPL_project3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class GameTree {

	private GameTree parent;
	private Map<Move[],GameTree> children = new HashMap<Move[],GameTree>();
	private MachineState state;
	private StateMachine machine;
	private Move[][] legalMoves; // 2d array [no. roles][no. legal moves for given role]
	private List<Role> roles;
	private double[][] Qs; // 2d array of Q values for each role for each move
	private int[][] Ns;
	private int N = 0;
	private int noChildren = 0;

	public GameTree(MachineState s, GameTree p, StateMachine sm) throws MoveDefinitionException {
		state = s;
		parent = p;
		machine = sm;
		initalize();
	}

	private void initalize() throws MoveDefinitionException {
		// Init legal moves and Qs and Ns to 0
		roles = machine.getRoles();
		for(int i = 0; i < roles.size(); i++) {
			List<Move> moves = machine.getLegalMoves(state, roles.get(i));
			Move[] movesArr = moves.toArray(new Move[moves.size()]);
			legalMoves[i] = movesArr;
			Qs[i] = new double[movesArr.length];
			Ns[i] = new int[movesArr.length];
		}
	}

	public GameTree getParent() {
		return parent;
	}

	public MachineState getState() {
		return state;
	}

	public Move[][] getLegalMoves() {
		return legalMoves;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public int getNoRoles() {
		return roles.size();
	}

	// addChild is called with jointMoves
	public void addChild(List<Move> M) throws MoveDefinitionException, TransitionDefinitionException {
		MachineState childState = machine.getNextState(state, M);
		Move[] moves = M.toArray(new Move[M.size()]);
		children.put(moves, new GameTree(childState,this,machine));
		noChildren += 1;
	}

	public GameTree getChild(List<Move> M) throws MoveDefinitionException, TransitionDefinitionException {
		Move[] moves = M.toArray(new Move[M.size()]);
		if (children.get(moves) == null) {
			addChild(M);
		}
		return children.get(moves);
	}

	public GameTree[] getChildren() {
		List<GameTree> arr = new ArrayList<GameTree>();
		for (Move[] key : children.keySet()) {
		    arr.add(children.get(key));
		}
		return arr.toArray(new GameTree[arr.size()]);
	}

	public double[][] getQScores() {
		return Qs;
	}

	public double getQScore(int role, int move) {
		return Qs[role][move];
	}

	public void updateQScore(int role, int move, double val) {
		Qs[role][move] += (val - Qs[role][move])/((double) Ns[role][move] + 1);
	}

	public int[][] getNs() {
		return Ns;
	}

	public int getNs(int role, int move) {
		return Ns[role][move];
	}

	public void incrNs(int role, int move) {
		Ns[role][move] += 1;
	}

	public int getNoSimulation() {
		return N;
	}

	public void incrNoSimulation() {
		N++;
	}

	@Override
	public String toString()
	{
		String s = "\nCurrent state:\n";
		s += state.toString() + "\n\nChildren:\n";
		for(GameTree c : this.getChildren())
		{
			s += c.getState().toString() + "\n";
		}
		return s;
	}
}
