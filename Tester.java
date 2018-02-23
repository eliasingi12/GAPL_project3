package GAPL_project3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

		int fullTime = 5000;
		int noop = 100;
		int playTime;

		GameTree treeX = new GameTree(init, null, sm);
		GameTree treeO = new GameTree(init, null, sm);

		Role xplayer = treeX.getRoles().get(0);
		Role oplayer = treeX.getRoles().get(1);
		Pair<Move,GameTree> p;

		int maxIter = Integer.MAX_VALUE;

		boolean xPlaying = true;

		Move xmove;
		Move omove;

		List<Move> theJM;

		while(!sm.isTerminal(treeX.getState())) {
			theJM = new ArrayList<>();

			if(xPlaying) {
				playTime = fullTime;
			} else {
				playTime = noop;
			}

			System.out.println(treeX.getState().toString());

			p = utils.MCTS(treeX, sm, xplayer, maxIter, playTime, 100);
			treeX = p.getValue();
			theJM.add(p.getKey());
			System.out.println("Legal moves: "+Arrays.toString(treeX.getLegalMoves()[0]));
			System.out.println("Q scores: "+Arrays.toString(treeX.getAllQScores()[0]));
			System.out.println("N scores: "+Arrays.toString(treeX.getAllNs()[0]));

			System.out.println(xplayer.toString()+" does: "+p.getKey().toString());

			if(xPlaying) {
				playTime = noop;
			} else {
				playTime = fullTime;
			}

			p = utils.MCTS(treeO, sm, oplayer, maxIter, playTime, 1000);
			treeO = p.getValue();
			theJM.add(p.getKey());
			System.out.println("Legal moves: "+Arrays.toString(treeX.getLegalMoves()[1]));
			System.out.println("Q scores: "+Arrays.toString(treeX.getAllQScores()[1]));
			System.out.println("N scores: "+Arrays.toString(treeX.getAllNs()[1]));

			System.out.println(oplayer.toString()+" does: "+p.getKey().toString());

			xPlaying = !xPlaying;

			sm.getNextState(treeX.getState(), theJM);
			if(!sm.isTerminal(treeX.getState())) {
				treeX = treeX.getChild(theJM);
				treeX.setParent(null);
				treeO = treeO.getChild(theJM);
				treeO.setParent(null);
			}
		}

		List<Integer> g = sm.getGoals(treeX.getState());
		System.out.println(g.toString());

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
