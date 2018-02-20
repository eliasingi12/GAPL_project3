package GAPL_project3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.ggp.base.util.game.Game;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;


public class utils {

	public static StateMachine getGameSM(String gamedesc) throws IOException{

		FileInputStream fstream = new FileInputStream(gamedesc);
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

	public static Move UCT(Move[] moves, double[] Qs, int[] Ns, int N, double C) {
		double max = Double.MIN_VALUE;
		Move bestMove = null;
		double val;
		for(int i = 0; i < moves.length; i++) {
			if (Ns[i] == 0) return moves[i];
			val = Qs[i] + C*Math.sqrt(Math.log((double) N)/((double) Ns[i]));
			if (val > max) {
				max = val;
				bestMove = moves[i];
			}
		}
		return bestMove;
	}
}
