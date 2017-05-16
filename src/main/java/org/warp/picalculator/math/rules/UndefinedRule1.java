package org.warp.picalculator.math.rules;

import org.warp.picalculator.Error;
import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.MathContext;
import org.warp.picalculator.math.functions.Number;
import org.warp.picalculator.math.functions.Power;
import org.warp.picalculator.math.functions.Undefined;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Undefined rule<br>
 * <b>0^0=undefined</b>
 * 
 * @author Andrea Cavalli
 *
 */
public class UndefinedRule1 {

	public static boolean compare(Function f) {
		final MathContext root = f.getMathContext();
		final Power fnc = (Power) f;
		if (fnc.getParameter1().equals(new Number(root, 0)) && fnc.getParameter2().equals(new Number(root, 0))) {
			return true;
		}
		return false;
	}

	public static ObjectArrayList<Function> execute(Function f) throws Error {
		final MathContext root = f.getMathContext();
		final ObjectArrayList<Function> result = new ObjectArrayList<>();
		result.add(new Undefined(root));
		return result;
	}

}
