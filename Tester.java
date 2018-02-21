package GAPL_project3;

import java.io.IOException;
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
		GameTree test = new GameTree(init, null, sm);

		List<List<Move>> jm = sm.getLegalJointMoves(init);
		System.out.println("Legal moves: "+jm);
		System.out.println("\nNext possible states:");
		for(MachineState x : sm.getNextStates(init)){
			System.out.println(x);
		}

		//while(!jm.isEmpty())
		//{
		//	test.addChild(jm.remove(0));
		//}

		//List<Move> jm0 = jm.remove(0);
		//test.addChild(jm0);
		//GameTree child = test.getChild(jm0);

		System.out.println(test.toString());
		Role role = sm.getRoles().get(0);
		GameTree t;
		Pair<Move,GameTree> p = utils.MCTS(test, sm, role, 5000000, 50);
		System.out.println(p.getKey());
	}
}
