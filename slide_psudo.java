package GAPL_project3;


public class slide_psudo {



	/* (returns the “best” move for role r in state s)
	mc_search(Role r, MachineState s) // s = getCurrentState() when we call mc_search;
	{
		Q(a) = 0; // for all a
		N(a) = 0; // for all a

		// while there is time left
  		while(time > 0)
  		{
  	  		// randomly select a joint move jm from the legal moves in s
  	  		Move selection = getRandomJointMove(s); // Returns a random joint move from among all the possible joint moves in the given state.

  			a = jm(r) // is the action of role r
  			s′ = update(jm, s)
  			score := run simulation(r,s′)
  			Q(a) := (Q(a) ∗ N(a) + score)/(N(a) + 1)
  			N(a):=N(a)+1
  		}

		return a with Q(a) = maxa′ Q(a′)
	}

	// 	(returns the score for role r if the game is in state s and randomly played to the end)
	run_simulation(role r, state s)
	{
		if terminal(s)
		{
  			return goal(r, s);
  		}
		else
		{
  			randomly select a joint move jm from the legal moves in s
  			List<Move> jm = s.getLegalJointMoves(getCurrentState());
        	Move selection = (jm.get(ThreadLocalRandom.current().nextInt(jm.size())));

  			s′ := update(jm, s)
  			return run simulation(r,s′)
  		}
  	}
	*/
}
