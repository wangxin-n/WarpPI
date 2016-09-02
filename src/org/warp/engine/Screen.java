package org.warp.engine;

import org.warp.device.Keyboard.Key;
import org.warp.device.PIDisplay;

public abstract class Screen {
	public PIDisplay d;
	public boolean created = false;
	public boolean initialized = false;
	public boolean canBeInHistory = false;

	public Screen() {}

	public void initialize() throws InterruptedException {
		if (!initialized) {
			initialized = true;
			init();
		}
	}

	public void create() throws InterruptedException {
		if (!created) {
			created = true;
			created();
		}
	}

	public abstract void created() throws InterruptedException;

	public abstract void init() throws InterruptedException;

	public abstract void render();

	public abstract void beforeRender(float dt);

	public abstract boolean mustBeRefreshed();

	public abstract boolean keyPressed(Key k);
	
	public abstract boolean keyReleased(Key k);
}
