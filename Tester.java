package GAPL_project3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import javafx.util.Pair;

public class Tester {

	public static void main(String[] args) throws IOException, MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException{

		String prefix = System.getProperty("user.dir") + "/src/main/java/GAPL_project3/";
		StateMachine sm = utils.getGameSM(prefix + "tictactoeXwin.txt");

		MachineState init = sm.getInitialState();
		GameTree tree = new GameTree(init, null, sm);

		List<List<Move>> jm = sm.getLegalJointMoves(init);
		//System.out.println("Legal moves: "+jm);
		//System.out.println("\nNext possible states:");
		//for(MachineState x : sm.getNextStates(init)){
		//	System.out.println(x);
		//}

		//while(!jm.isEmpty())
		//{
		//	test.addChild(jm.remove(0));
		//}

		/*
		List<Move> jm0 = jm.remove(0);
		test.addChild(jm0);
		GameTree child = test.getChild(jm0);

		int idx = child.getRoleIndex(child.getRoles().get(1));
		System.out.println("Idx: "+idx);
		*/

		//System.out.println(test.toString());
		Role role = sm.getRoles().get(0);

		int xWin = 0;
		int oWin = 0;

		Role xplayer = tree.getRoles().get(0);
		Role oplayer = tree.getRoles().get(1);
		Pair<Move,GameTree> p;

		List<Move> theJM;

		while(!sm.isTerminal(tree.getState())) {
			theJM = new ArrayList<>();

			System.out.println(tree.getState().toString());

			p = utils.MCTS(tree, sm, xplayer, 1000, 2000, 50);
			tree = p.getValue();
			theJM.add(p.getKey());

			System.out.println(xplayer.toString()+" does: "+p.getKey().toString());

			p = utils.MCTS(tree, sm, oplayer, 1000, 2000, 50);
			tree = p.getValue();
			theJM.add(p.getKey());

			System.out.println(oplayer.toString()+" does: "+p.getKey().toString());

			sm.getNextState(tree.getState(), theJM);
			if(!sm.isTerminal(tree.getState())) {
				tree = tree.getChild(theJM);
				tree.setParent(null);
			}
		}

		List<Integer> g = sm.getGoals(tree.getState());

		/*System.out.println(tree.getNs(0, 0));
		System.out.println(tree.getNs(0, 1));
		System.out.println(tree.getNs(0, 2));
		System.out.println(tree.getNs(0, 3));
		System.out.println(tree.getNs(0, 4)+"\n");
		System.out.println(tree.getQScore(0, 0));
		System.out.println(tree.getQScore(0, 1));
		System.out.println(tree.getQScore(0, 2));
		System.out.println(tree.getQScore(0, 3));
		System.out.println(tree.getQScore(0, 4));
		System.out.println(p.getKey());
		System.out.println(sm.getLegalMoves(init, sm.getRoles().get(0)));*/
	}
}
