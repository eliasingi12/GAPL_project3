package GAPL_project3;

import java.io.IOException;
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class Tester {

	public static void main(String[] args) throws IOException, MoveDefinitionException, TransitionDefinitionException{

		String prefix = System.getProperty("user.dir") + "/src/main/java/GAPL_project3/";
		StateMachine state = utils.getGameSM(prefix + "tictactoeXwin.txt");

		MachineState init = state.getInitialState();
		GameTree test = new GameTree(init, null);

		// Move random = RandomGamer.stateMachineSelectMove(100);
		List<List<Move>> jm = state.getLegalJointMoves(init);
		System.out.println(jm);
		List<MachineState> next = state.getNextStates(init);

		for(MachineState x : state.getNextStates(init)){
			System.out.println(x);
		}

		//Move[] arr = jm.toArray(new Move[jm.size()]);
		while(!jm.isEmpty())
		{
			test.addChild(jm.remove(0), next.remove(0));
		}

		System.out.println(test.toString());


	}
}
