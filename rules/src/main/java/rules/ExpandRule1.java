package rules;
/*
SETTINGS: (please don't move this part)
 PATH=ExpandRule1
*/

import it.cavallium.warppi.math.Function;
import it.cavallium.warppi.math.FunctionOperator;
import it.cavallium.warppi.math.MathContext;
import it.cavallium.warppi.math.functions.Multiplication;
import it.cavallium.warppi.math.functions.Number;
import it.cavallium.warppi.math.functions.Subtraction;
import it.cavallium.warppi.math.functions.Sum;
import it.cavallium.warppi.math.functions.SumSubtraction;
import it.cavallium.warppi.math.rules.Rule;
import it.cavallium.warppi.math.rules.RuleType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Expand rule
 * -(+a+b) = -a-b
 * -(+a-b) = -a+b
 *
 * @author Andrea Cavalli
 *
 */
public class ExpandRule1 implements Rule {
	// Rule name
	@Override
	public String getRuleName() {
		return "ExpandRule1";
	}

	// Rule type
	@Override
	public RuleType getRuleType() {
		return RuleType.EXPANSION;
	}

	/* Rule function
	   Returns:
	     - null if it's not executable on the function "f"
	     - An ObjectArrayList<Function> if it did something
	*/
	@Override
	public ObjectArrayList<Function> execute(final Function f) {
		boolean isExecutable = false;
		if (f instanceof Multiplication) {
			final Multiplication fnc = (Multiplication) f;
			if (fnc.getParameter1().equals(new Number(fnc.getMathContext(), -1))) {
				final Function expr = fnc.getParameter2();
				if (expr instanceof Sum)
					isExecutable = true;
				else if (expr instanceof Subtraction)
					isExecutable = true;
				else if (expr instanceof SumSubtraction)
					isExecutable = true;
			}
		} else if (f instanceof Subtraction || f instanceof SumSubtraction) {
			final FunctionOperator fnc = (FunctionOperator) f;
			final Function expr = fnc.getParameter2();
			if (expr instanceof Sum)
				isExecutable = true;
			else if (expr instanceof Subtraction)
				isExecutable = true;
			else if (expr instanceof SumSubtraction)
				isExecutable = true;
		}
		if (isExecutable) {
			final ObjectArrayList<Function> result = new ObjectArrayList<>();
			final MathContext root = f.getMathContext();

			Function expr = null;
			int fromSubtraction = 0;
			FunctionOperator subtraction = null;
			if (f instanceof Multiplication)
				expr = ((Multiplication) f).getParameter2();
			else if (f instanceof Subtraction || f instanceof SumSubtraction) {
				expr = ((Multiplication) f).getParameter2();
				if (f instanceof Subtraction)
					fromSubtraction = 1;
				else
					fromSubtraction = 2;
			}

			if (f instanceof SumSubtraction) {

			}

			final Function fnc = expr;
			if (fnc instanceof Sum) {
				final Function a = ((Sum) fnc).getParameter1();
				final Function b = ((Sum) fnc).getParameter2();
				final Function fnc2 = new Subtraction(root, new Multiplication(root, new Number(root, -1), a), b);
				if (fromSubtraction > 0) {
					subtraction = new Subtraction(root, ((FunctionOperator) f).getParameter1(), fnc2);
					result.add(subtraction);
				} else
					result.add(fnc2);
			} else if (fnc instanceof Subtraction) {
				final Function a = ((Subtraction) fnc).getParameter1();
				final Function b = ((Subtraction) fnc).getParameter2();
				final Function fnc2 = new Sum(root, new Multiplication(root, new Number(root, -1), a), b);
				if (fromSubtraction > 0) {
					subtraction = new Subtraction(root, ((FunctionOperator) f).getParameter1(), fnc2);
					result.add(subtraction);
				} else
					result.add(fnc2);
			} else if (fnc instanceof SumSubtraction) {
				final Function a = ((SumSubtraction) fnc).getParameter1();
				final Function b = ((SumSubtraction) fnc).getParameter2();
				final Function fnc2 = new Sum(root, new Multiplication(root, new Number(root, -1), a), b);
				final Function fnc3 = new Subtraction(root, new Multiplication(root, new Number(root, -1), a), b);
				if (fromSubtraction > 0) {
					subtraction = new SumSubtraction(root, ((FunctionOperator) f).getParameter1(), fnc2);
					result.add(subtraction);
					subtraction = new SumSubtraction(root, ((FunctionOperator) f).getParameter1(), fnc3);
					result.add(subtraction);
					result.add(subtraction);
				} else {
					result.add(fnc2);
					result.add(fnc2);
				}
			}
			return result;
		} else
			return null;

	}
}
