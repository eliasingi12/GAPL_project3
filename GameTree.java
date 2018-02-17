package GAPL_project3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;

import javafx.util.Pair;

public class GameTree {

	private GameTree parent;
	private List<Pair<Move,GameTree>> children = new ArrayList<>();
	private MachineState state;
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

	public void addChild(Move m, GameTree t) {
		children.add(new Pair<Move,GameTree>(m,t));
	}

	public List<Pair<Move,GameTree>> getChildren() {
		return children;
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
