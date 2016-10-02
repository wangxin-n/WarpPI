package org.warp.picalculator.math.functions;

import static org.warp.picalculator.device.graphicengine.Display.Render.getStringWidth;
import static org.warp.picalculator.device.graphicengine.Display.Render.glDrawStringLeft;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.nevec.rjm.NumeroAvanzato;
import org.warp.picalculator.Error;
import org.warp.picalculator.Errors;
import org.warp.picalculator.Utils;
import org.warp.picalculator.device.PIDisplay;
import org.warp.picalculator.device.graphicengine.Display;
import org.warp.picalculator.math.MathematicalSymbols;
import org.warp.picalculator.math.Variable;
import org.warp.picalculator.math.Variables;

public class Sum extends FunctionTwoValues {

	public Sum(Function value1, Function value2) {
		super(value1, value2);
	}

	@Override
	public String getSymbol() {
		return MathematicalSymbols.SUM;
	}
	
	@Override
	protected boolean isSolvable() throws Error {
		if (variable1 instanceof Number & variable2 instanceof Number) {
			return true;
		}
		return false;
	}

	@Override
	public List<Function> solveOneStep() throws Error {
		if (variable1 == null || variable2 == null) {
			throw new Error(Errors.SYNTAX_ERROR);
		}
		ArrayList<Function> result = new ArrayList<>();
		if (variable1.isSolved() & variable2.isSolved()) {
			if (((Number)variable1).term.isBigInteger(false) & ((Number)variable2).term.isBigInteger(false)) {
				if (((Number)variable1).term.toBigInteger(false).compareTo(new BigInteger("2")) == 0 && ((Number)variable2).term.toBigInteger(false).compareTo(new BigInteger("2")) == 0) {
					NumeroAvanzato na = NumeroAvanzato.ONE;
					Variables iy = na.getVariableY();
					List<Variable> newVariableList = iy.getVariablesList();
					newVariableList.add(new Variable('♓', 1, 1));
					iy = new Variables(newVariableList.toArray(new Variable[newVariableList.size()]));
					na = na.setVariableY(iy);
					result.add(new Number(na));
					return result;
				}
			}
			result.add(((Number)variable1).add((Number)variable2));
		} else {
			List<Function> l1 = new ArrayList<Function>();
			List<Function> l2 = new ArrayList<Function>();
			if (variable1.isSolved()) {
				l1.add(variable1);
			} else {
				l1.addAll(variable1.solveOneStep());
			}
			if (variable2.isSolved()) {
				l2.add(variable2);
			} else {
				l2.addAll(variable2.solveOneStep());
			}

			Function[][] results = Utils.joinFunctionsResults(l1, l2);
			
			for (Function[] f : results) {
				result.add(new Sum((Function)f[0], (Function)f[1]));
			}
		}
		return result;
	}

	@Override
	public void generateGraphics() {
		variable1.setSmall(small);
		variable1.generateGraphics();
		
		variable2.setSmall(small);
		variable2.generateGraphics();
		
		width = calcWidth();
		height = calcHeight();
		line = calcLine();
	}
	
	@Override
	public void draw(int x, int y) {
//		glColor3f(127, 127-50+new Random().nextInt(50), 255);
//		glFillRect(x,y,width,height);
//		glColor3f(0, 0, 0);
		
		int ln = getLine();
		int dx = 0;
		variable1.draw(dx + x, ln - variable1.getLine() + y);
		dx += variable1.getWidth();
		Display.Render.setFont(Utils.getFont(small));
		dx += 1;
		glDrawStringLeft(dx + x, ln - Utils.getFontHeight(small) / 2 + y, getSymbol());
		dx += getStringWidth(getSymbol());
		variable2.draw(dx + x, ln - variable2.getLine() + y);
	}

	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	protected int calcWidth() {
		int dx = 0;
		dx += variable1.getWidth();
		dx += 1;
		dx += getStringWidth(getSymbol());
		return dx += variable2.getWidth();
	}
}