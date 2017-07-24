package org.warp.picalculator.gui.expression.containers;

import org.warp.picalculator.gui.expression.InputContext;
import org.warp.picalculator.gui.expression.blocks.Block;
import org.warp.picalculator.gui.expression.blocks.BlockChar;
import org.warp.picalculator.gui.expression.blocks.BlockDivision;
import org.warp.picalculator.gui.expression.blocks.BlockParenthesis;
import org.warp.picalculator.gui.expression.blocks.BlockPower;
import org.warp.picalculator.gui.expression.blocks.BlockSine;
import org.warp.picalculator.gui.expression.blocks.BlockSquareRoot;
import org.warp.picalculator.gui.expression.blocks.BlockVariable;
import org.warp.picalculator.math.MathematicalSymbols;

public class NormalInputContainer extends InputContainer {

	private static final long serialVersionUID = 5236564695997222322L;

	@Deprecated()
	/**
	 * Use NormalInputContainer(InputContext) instead
	 */
	public NormalInputContainer() {
		super();
	}
	
	public NormalInputContainer(InputContext ic) {
		super(ic);
	}

	public NormalInputContainer(InputContext ic, boolean small) {
		super(ic, small);
	}

	public NormalInputContainer(InputContext ic, boolean small, int minWidth, int minHeight) {
		super(ic, small, minWidth, minHeight);
	}

	@Override
	public Block parseChar(char c) {
		switch (c) {
			case MathematicalSymbols.DIVISION:
				return new BlockDivision();
			case MathematicalSymbols.SQUARE_ROOT:
				return new BlockSquareRoot();
			case MathematicalSymbols.PARENTHESIS_OPEN:
				return new BlockParenthesis();
			case MathematicalSymbols.PARENTHESIS_CLOSE:
				return null;
			case MathematicalSymbols.POWER:
			case MathematicalSymbols.POWER_OF_TWO:
				return new BlockPower();
			case MathematicalSymbols.MULTIPLICATION:
			case MathematicalSymbols.SUM:
			case MathematicalSymbols.SUM_SUBTRACTION:
			case MathematicalSymbols.SUBTRACTION:
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				return new BlockChar(c);
			case MathematicalSymbols.SINE:
				return new BlockSine();
			case MathematicalSymbols.PI:
				return new BlockVariable(inputContext, c, true);
			default:
				for (char v : MathematicalSymbols.variables) {
					if (c == v) {
						return new BlockVariable(inputContext, c);
					}
				}
				return new BlockChar(c);
		}
	}
	
	@Override
	public void typeChar(char c) {
		super.typeChar(c);
		if (c == MathematicalSymbols.PARENTHESIS_CLOSE) {
			this.moveRight();
		}
	}
}
