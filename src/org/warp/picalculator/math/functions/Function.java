package org.warp.picalculator.math.functions;

import java.util.List;

import org.warp.picalculator.Error;

public interface Function {
	public String getSymbol();

	public List<Function> solveOneStep() throws Error;

	public boolean isSolved() throws Error;

	public void generateGraphics();

	public void draw(int x, int y);

	public int getWidth();

	public int getHeight();

	public int getLine();
	
	public void setSmall(boolean small);
	
	@Override
	public int hashCode();
	
	@Override
	public boolean equals(Object o);
}
