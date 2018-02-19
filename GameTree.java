package GAPL_project3;

import java.util.HashMap;
import java.util.Map;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;

public class GameTree {

	private GameTree parent;
	private Map<Move[],GameTree> children = new HashMap<Move[],GameTree>();
	//private List<Pair<Move,GameTree>> children = new ArrayList<>();
	private MachineState state;
	private Move[][] legalMoves; // 2d array [no. roles][no. moves for given role]
	private double[][] Q; // 2d array of Q values for each role for each move
	private Map<Role,HashMap<Move,Float>> Qs = new HashMap<Role, HashMap<Move,Float>>();
	private Map<Role,HashMap<Move,Integer>> Ns = new HashMap<Role, HashMap<Move,Integer>>();
	private int N;

	public GameTree(MachineState s, GameTree p) {
		state = s;
		parent = p;
	}

	public GameTree getParent() {
		return parent;
	}

	public MachineState getState() {
		return state;
	}

	public void addChild(Move[] M, GameTree t) {
		children.put(M,t);
		//children.add(new Pair<Move,GameTree>(m,t));
	}

	public GameTree getChild(Move[] M) {
		return children.get(M);
	}

	//public List<Pair<Move,GameTree>> getChildren() {
	//	return children;
	//}

	public double[][] getQScores() {
		return Q;
	}

	public float getQScore(Role r, Move m) {
		return Qs.get(r).get(m).floatValue();
	}

	public void updateQScore(Role r, Move m, Float v) {
		Qs.get(r).put(m,v);
	}

	public int getNoSimulations(Role r, Move m) {
		return Ns.get(r).get(m).intValue();
	}

	public void updateNoSimulations(Role r, Move m, Integer v) {
		Ns.get(r).put(m,v);
	}

	public int getNoSimulation() {
		return N;
	}

	public void incrNoSimulation() {
		N++;
	}
}
