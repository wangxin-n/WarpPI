package org.warp.picalculator;

import static org.warp.engine.Display.Render.getStringWidth;
import static org.warp.engine.Display.Render.glDrawStringLeft;

import org.nevec.rjm.Rational;
import org.warp.device.PIDisplay;
import org.warp.engine.Display;

import com.rits.cloning.Cloner;

public abstract class FunzioneDueValoriBase extends FunzioneBase {
	public FunzioneDueValoriBase(FunzioneBase value1, FunzioneBase value2) {
		setVariable1(value1);
		setVariable2(value2);
	}

	protected FunzioneBase variable1 = new Termine(Rational.ZERO);
	protected int width;
	protected int height;
	protected int line;
	protected boolean small;

	public FunzioneBase getVariable1() {
		return variable1;
	}

	public void setVariable1(FunzioneBase value) {
		variable1 = value;
	}

	protected FunzioneBase variable2 = new Termine(Rational.ZERO);

	public FunzioneBase getVariable2() {
		return variable2;
	}

	public void setVariable2(FunzioneBase value) {
		variable2 = value;
	}

	@Override
	public abstract String simbolo();

	@Override
	public abstract Termine calcola() throws Errore;
	
	@Override
	public void calcolaGrafica() {
		variable1.setSmall(small);
		variable1.calcolaGrafica();
		
		variable2.setSmall(small);
		variable2.calcolaGrafica();
		
		width = calcWidth();
		height = calcHeight();
		line = calcLine();
	}

	@Override
	public void draw(int x, int y) {
		int ln = getLine();
		int dx = 0;
		variable1.draw(dx + x, ln - variable1.getLine() + y);
		dx += 1+variable1.getWidth();
		if (drawSignum()) {
			if (small) {
				Display.Render.setFont(PIDisplay.fonts[1]);
			} else {
				Display.Render.setFont(PIDisplay.fonts[0]);
			}
			glDrawStringLeft(dx + x, ln - Utils.getFontHeight(small) / 2 + y, simbolo());
			dx += getStringWidth(simbolo());
		}
		variable2.draw(dx + x, ln - variable2.getLine() + y);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public String toString() {
		try {
			return calcola().toString();
		} catch (Errore e) {
			return e.id.toString();
		}
	}

	@Override
	public FunzioneDueValoriBase clone() {
		Cloner cloner = new Cloner();
		return cloner.deepClone(this);
	}

	public boolean drawSignum() {
		return true;
	}
	
	@Override
	public void setSmall(boolean small) {
		this.small = small;
	}	

	protected int calcWidth() {
		return variable1.getWidth() + 1 + (drawSignum() ? getStringWidth(simbolo()) : 0) + variable2.getWidth();
	}
	
	protected int calcHeight() {

		FunzioneBase tmin = variable1;
		FunzioneBase tmax = variable1;
		if (tmin == null || variable2.getLine() >= tmin.getLine()) {
			tmin = variable2;
		}
		if (tmax == null || variable2.getHeight() - variable2.getLine() >= tmax.getHeight() - tmax.getLine()) {
			tmax = variable2;
		}
		return tmin.getLine() + tmax.getHeight() - tmax.getLine();
	}
	
	protected int calcLine() {
		FunzioneBase tl = variable1;
		if (tl == null || variable2.getLine() >= tl.getLine()) {
			tl = variable2;
		}
		return tl.getLine();
	}
}
