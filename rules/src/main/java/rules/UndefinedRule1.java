package rules;
/*
SETTINGS: (please don't move this part)
 PATH=UndefinedRule1
*/

import it.cavallium.warppi.math.Function;
import it.cavallium.warppi.math.FunctionOperator;
import it.cavallium.warppi.math.MathContext;
import it.cavallium.warppi.math.functions.Number;
import it.cavallium.warppi.math.functions.Power;
import it.cavallium.warppi.math.functions.Undefined;
import it.cavallium.warppi.math.rules.Rule;
import it.cavallium.warppi.math.rules.RuleType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Undefined rule
 * 0^0=undefined
 *
 * @author Andrea Cavalli
 *
 */
public class UndefinedRule1 implements Rule {
	// Rule name
	@Override
	public String getRuleName() {
		return "UndefinedRule1";
	}

	// Rule type
	@Override
	public RuleType getRuleType() {
		return RuleType.EXISTENCE;
	}

	/* Rule function
	   Returns:
	     - null if it's not executable on the function "f"
	     - An ObjectArrayList<Function> if it did something
	*/

	@Override
	public ObjectArrayList<Function> execute(final Function f) {
		boolean isExecutable = false;
		if (f instanceof Power) {
			final MathContext root = f.getMathContext();
			final FunctionOperator fnc = (FunctionOperator) f;
			if (fnc.getParameter1().equals(new Number(root, 0)) && fnc.getParameter2().equals(new Number(root, 0)))
				isExecutable = true;
		}

		if (isExecutable) {
			final MathContext root = f.getMathContext();
			final ObjectArrayList<Function> result = new ObjectArrayList<>();
			result.add(new Undefined(root));
			return result;
		} else
			return null;
	}
}
