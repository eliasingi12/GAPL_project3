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
	private StateMachine machine;
	private MachineState state;
	private Move[][] legalMoves = null; // 2d array of legal moves for every role [no. roles][no. legal moves for given role]

	/* Role variables */
	private List<Role> roles;
	private Map<Role,Integer> roleIndex = new HashMap<Role,Integer>();
	private int nRoles;

	/* Children */
	private Map<List<Move>,GameTree> children = new HashMap<List<Move>,GameTree>();

	/* Q scores, child and self visit counter*/
	private double[][] Qs = null; // 2d array of Q values for each role for each move
	private int[][] Ns = null; // Counts how many times each child state has been visited
	private int N = 0;

	public GameTree(MachineState state, GameTree parent, StateMachine sm) throws MoveDefinitionException {
		this.state = state;
		this.parent = parent;
		machine = sm;
		initalize();
	}

	private void initalize() throws MoveDefinitionException {
		// Init legal moves, roles, and Qs and Ns to 0
		roles = machine.getRoles();
		nRoles = roles.size();
		for(int i = 0; i < nRoles; i++) {
			roleIndex.put(roles.get(i), (Integer) i);
		}
		legalMoves = new Move[nRoles][];
		Qs = new double[nRoles][];
		Ns = new int[nRoles][];
		Move[] movesArr;
		for(int i = 0; i < roles.size(); i++) {
			if(!machine.isTerminal(this.getState())) {
				List<Move> moves = machine.getLegalMoves(state, roles.get(i));
				movesArr = moves.toArray(new Move[moves.size()]);
				legalMoves[i] = movesArr;
				Qs[i] = new double[movesArr.length];
				Ns[i] = new int[movesArr.length];
			}
		}
	}

	public GameTree getParent() {
		return parent;
	}

	public void setParent(GameTree p) {
		parent = p;
	}

	public MachineState getState() {
		return state;
	}

	public boolean isTerminal() {
		return machine.isTerminal(state);
	}

	public Move[][] getLegalMoves() {
		return legalMoves;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public int getNoRoles() {
		return nRoles;
	}

	public int getRoleIndex(Role role) {
		return roleIndex.get(role).intValue();
	}


	public void addChild(List<Move> M) throws MoveDefinitionException, TransitionDefinitionException {
		MachineState childState = machine.getNextState(state, M);
		children.put(M, new GameTree(childState,this,machine));
	}

	public GameTree getChild(List<Move> M) throws MoveDefinitionException, TransitionDefinitionException {
		if (children.get(M) == null) {
			addChild(M);
		}
		return children.get(M);
	}

	public boolean hasChild(List<Move> M) {
		return (children.get(M) != null);
	}

	public GameTree[] getChildren() {
		List<GameTree> arr = new ArrayList<GameTree>();
		for (List<Move> key : children.keySet()) {
		    arr.add(children.get(key));
		}
		return arr.toArray(new GameTree[arr.size()]);
	}

	public double[][] getAllQScores() {
		return Qs;
	}

	public double getQScore(int role, int move) {
		return Qs[role][move];
	}

	public void updateQScore(int role, int move, double val) {
		Qs[role][move] += (val - Qs[role][move])/((double) Ns[role][move] + 1);
	}

	public int[][] getAllNs() {
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
