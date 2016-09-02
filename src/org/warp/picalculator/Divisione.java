package org.warp.picalculator;

import static org.warp.engine.Display.Render.getStringWidth;
import static org.warp.engine.Display.Render.glColor3f;
import static org.warp.engine.Display.Render.glDrawStringLeft;
import static org.warp.engine.Display.Render.glFillRect;

import org.nevec.rjm.NumeroAvanzatoVec;
import org.warp.device.PIDisplay;
import org.warp.engine.Display;

public class Divisione extends FunzioneDueValoriBase {

	public Divisione(FunzioneBase value1, FunzioneBase value2) {
		super(value1, value2);
	}

	@Override
	public String simbolo() {
		return Simboli.DIVISION;
	}

	@Override
	public Termine calcola() throws Errore {
		if (variable2 == null || variable1 == null) {
			return new Termine("0");
		}
		if (variable2.calcola().getTerm().compareTo(NumeroAvanzatoVec.ZERO) == 0) {
			throw new Errore(Errori.DIVISION_BY_ZERO);
		}
		return variable1.calcola().divide(variable2.calcola());
	}

	public boolean hasMinus() {
		String numerator = variable1.toString();
		if (numerator.startsWith("-")) {
			return true;
		}
		return false;
	}

	public void draw(int x, int y, boolean small, boolean drawMinus) {
		boolean beforedrawminus = this.drawMinus;
		this.drawMinus = drawMinus;
		draw(x, y);
		this.drawMinus = beforedrawminus;
	}

	private boolean drawMinus = true;

	@Override
	public void calcolaGrafica() {
		variable1.setSmall(true);
		variable1.calcolaGrafica();
		
		variable2.setSmall(true);
		variable2.calcolaGrafica();
		
		width = calcWidth();
		height = calcHeight();
		line = variable1.getHeight() + 1;
	}
	
	@Override
	public void draw(int x, int y) {
//		glColor3f(255, 127-50+new Random().nextInt(50), 0);
//		glFillRect(x,y,width,height);
//		glColor3f(0, 0, 0);
		
		Object var1 = variable1;
		Object var2 = variable2;
		boolean minus = false;
		int minusw = 0;
		int minush = 0;
		String numerator = ((Funzione) var1).toString();
		if (numerator.startsWith("-") && ((Funzione) var1) instanceof Termine && ((Termine) var1).term.isBigInteger(true)) {
			minus = true;
			numerator = numerator.substring(1);
		}
		int w1 = 0;
		int h1 = 0;
		if (minus) {
			w1 = getStringWidth(numerator);
			h1 = Utils.getFontHeight(small);
		} else {
			w1 = ((Funzione) var1).getWidth();
			h1 = ((Funzione) var1).getHeight();
		}
		int w2 = ((Funzione) var2).getWidth();
		int maxw;
		if (w1 > w2) {
			maxw = 1 + w1;
		} else {
			maxw = 1 + w2;
		}
		if (minus && drawMinus) {
			minusw = getStringWidth("-") + 1;
			minush = Utils.getFontHeight(small);
			if (small) {
				Display.Render.setFont(PIDisplay.fonts[1]);
			} else {
				Display.Render.setFont(PIDisplay.fonts[0]);
			}
			glDrawStringLeft(x+1, y + h1 + 1 + 1 - (minush / 2), "-");
			glDrawStringLeft((int) (x+1 + minusw + 1 + (maxw - w1) / 2d), y, numerator);
		} else {
			((Funzione) var1).draw((int) (x+1 + minusw + (maxw - w1) / 2d), y);
		}
		((Funzione) var2).draw((int) (x+1 + minusw + (maxw - w2) / 2d), y + h1 + 1 + 1 + 1);
		glColor3f(0, 0, 0);
		glFillRect(x+1+ minusw, y + h1 + 1, maxw, 1);
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	protected int calcHeight() {

		boolean minus = false;
		String numerator = variable1.toString();
		if (numerator.startsWith("-") && variable1 instanceof Termine && ((Termine) variable1).term.isBigInteger(true)) {
			minus = true;
			numerator = numerator.substring(1);
		}
		int h1 = 0;
		if (minus) {
			h1 = Utils.getFontHeight(small);
		} else {
			h1 = variable1.getHeight();
		}
		int h2 = variable2.getHeight();
		return h1 + 3 + h2;
	}

	@Override
	public int getLine() {
		return line;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	protected int calcWidth() {
		boolean minus = false;
		String numerator = variable1.toString();
		if (numerator.startsWith("-") && variable1 instanceof Termine && ((Termine) variable1).term.isBigInteger(true)) {
			minus = true;
			numerator = numerator.substring(1);
		}
		int w1 = 0;
		if (minus) {
			w1 = getStringWidth(numerator);
		} else {
			w1 = variable1.getWidth();
		}
		int w2 = variable2.getWidth();
		int maxw = 0;
		if (w1 > w2) {
			maxw = w1+1;
		} else {
			maxw = w2+1;
		}
		if (minus && drawMinus) {
			return 1 + getStringWidth("-") + 1 + maxw;
		} else {
			return 1 + maxw;
		}
	}
}