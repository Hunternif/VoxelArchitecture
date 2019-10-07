package hunternif.voxarch.util;

import java.util.Random;

public class RandomUtil {
	private static final Random rand = new Random();
	
	/** Returns a random option from the given array of weighted choices.
	 * The "probabilities" don't have to add up to 1.*/
	public static <O extends IWeightedOption> O randomWeightedOption(O[] options) {
		double sumTotal = 0; // Just to be sure we count probability correctly.
		for (O opt : options) {
			sumTotal += opt.probability();
		}
		double toss = rand.nextDouble() * sumTotal;
		for (O opt : options) {
			toss -= opt.probability();
			if (toss <= 0) return opt;
		}
		// This should never occur
		return options[0];
	}
}
