package org.warp.picalculator;

import static org.warp.engine.Display.Render.glColor3f;
import static org.warp.engine.Display.Render.glDrawLine;

public class ParteSistema extends FunzioneAnteriore {

	public ParteSistema(Equazione equazione) {
		super(equazione);
	}

	@Override
	public String simbolo() {
		return Simboli.SYSTEM;
	}

	@Override
	public Equazione calcola() throws NumberFormatException, Errore {
		// TODO implementare il calcolo dei sistemi
		return (Equazione) variable.calcola();
	}

	@Override
	public void calcolaGrafica() {
		variable.setSmall(false);
		variable.calcolaGrafica();
		
		width = 5 + getVariable().getWidth();
		height = 3 + getVariable().getHeight() + 2;
		line = 3 + getVariable().getLine();
	}
	
	@Override
	public void draw(int x, int y) {
		final int h = this.getHeight() - 1;
		final int paddingTop = 3;
		final int spazioSotto = (h - 3 - 2) / 2 + paddingTop;
		final int spazioSopra = h - spazioSotto;
		variable.draw(x + 5, y + paddingTop);
		glColor3f(0, 0, 0);
		glDrawLine(x + 2, y + 0, x + 3, y + 0);
		glDrawLine(x + 1, y + 1, x + 1, y + spazioSotto / 2);
		glDrawLine(x + 2, y + spazioSotto / 2 + 1, x + 2, y + spazioSotto - 1);
		glDrawLine(x + 0, y + spazioSotto, x + 1, y + spazioSotto);
		glDrawLine(x + 2, y + spazioSotto + 1, x + 2, y + spazioSotto + spazioSopra / 2 - 1);
		glDrawLine(x + 1, y + spazioSotto + spazioSopra / 2, x + 1, y + h - 1);
		glDrawLine(x + 2, y + h, x + 3, y + h);
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
}