package it.cavallium.warppi.math.functions;

import it.cavallium.warppi.gui.expression.blocks.Block;
import it.cavallium.warppi.gui.expression.blocks.BlockContainer;
import it.cavallium.warppi.gui.expression.blocks.BlockDivision;
import it.cavallium.warppi.math.Function;
import it.cavallium.warppi.math.FunctionOperator;
import it.cavallium.warppi.math.MathContext;
import it.cavallium.warppi.util.Error;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Division extends FunctionOperator {

	public Division(final MathContext root, final Function value1, final Function value2) {
		super(root, value1, value2);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Division) {
			final FunctionOperator f = (FunctionOperator) o;
			return getParameter1().equals(f.getParameter1()) && getParameter2().equals(f.getParameter2());
		}
		return false;
	}

	@Override
	public FunctionOperator clone() {
		return new Division(getMathContext(), getParameter1() == null ? null : getParameter1().clone(), getParameter2() == null ? null : getParameter2().clone());
	}

	@Override
	public FunctionOperator clone(MathContext c) {
		return new Division(c, getParameter1() == null ? null : getParameter1().clone(c), getParameter2() == null ? null : getParameter2().clone(c));
	}

	@Override
	public String toString() {
		return "(" + getParameter1() + ")/(" + getParameter2() + ")";
	}

	@Override
	public ObjectArrayList<Block> toBlock(final MathContext context) throws Error {
		final ObjectArrayList<Block> result = new ObjectArrayList<>();
		final ObjectArrayList<Block> sub1 = getParameter1().toBlock(context);
		final ObjectArrayList<Block> sub2 = getParameter2().toBlock(context);
		final BlockDivision bd = new BlockDivision();
		final BlockContainer uc = bd.getUpperContainer();
		final BlockContainer lc = bd.getLowerContainer();
		for (final Block b : sub1) {
			uc.appendBlockUnsafe(b);
		}
		for (final Block b : sub2) {
			lc.appendBlockUnsafe(b);
		}
		uc.recomputeDimensions();
		lc.recomputeDimensions();
		bd.recomputeDimensions();
		result.add(bd);
		return result;
	}
}