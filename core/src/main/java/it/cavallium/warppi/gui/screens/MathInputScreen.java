package it.cavallium.warppi.gui.screens;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang3.SerializationUtils;

import it.cavallium.warppi.Engine;
import it.cavallium.warppi.Platform.ConsoleUtils;
import it.cavallium.warppi.device.Keyboard;
import it.cavallium.warppi.event.Key;
import it.cavallium.warppi.event.KeyPressedEvent;
import it.cavallium.warppi.event.KeyReleasedEvent;
import it.cavallium.warppi.gui.HistoryBehavior;
import it.cavallium.warppi.gui.expression.InputContext;
import it.cavallium.warppi.gui.expression.blocks.Block;
import it.cavallium.warppi.gui.expression.blocks.BlockContainer;
import it.cavallium.warppi.gui.expression.containers.InputContainer;
import it.cavallium.warppi.gui.expression.containers.NormalInputContainer;
import it.cavallium.warppi.gui.expression.containers.NormalOutputContainer;
import it.cavallium.warppi.gui.expression.containers.OutputContainer;
import it.cavallium.warppi.gui.graphicengine.BinaryFont;
import it.cavallium.warppi.gui.graphicengine.Renderer;
import it.cavallium.warppi.math.AngleMode;
import it.cavallium.warppi.math.Function;
import it.cavallium.warppi.math.FunctionDynamic;
import it.cavallium.warppi.math.FunctionOperator;
import it.cavallium.warppi.math.FunctionSingle;
import it.cavallium.warppi.math.MathContext;
import it.cavallium.warppi.math.MathematicalSymbols;
import it.cavallium.warppi.math.functions.Expression;
import it.cavallium.warppi.math.functions.Number;
import it.cavallium.warppi.math.functions.Variable;
import it.cavallium.warppi.math.functions.Variable.V_TYPE;
import it.cavallium.warppi.math.functions.Variable.VariableValue;
import it.cavallium.warppi.math.parser.MathParser;
import it.cavallium.warppi.math.solver.MathSolver;
import it.cavallium.warppi.util.Error;
import it.cavallium.warppi.util.Errors;
import it.cavallium.warppi.util.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class MathInputScreen extends Screen {

	private static final BinaryFont fontBig = Utils.getFont(false);

	public MathContext calc;
	public InputContext ic;
	public InputContainer userInput;
	public OutputContainer result;
	public int errorLevel = 0; // 0 = nessuno, 1 = risultato, 2 = tutto
	private boolean computingResult = false;
	private Thread computingThread;
	private int computingAnimationIndex = 0;
	private double computingAnimationElapsedTime = 0;
	private double computingElapsedTime = 0;
	private boolean computingBreakTipVisible = false;
	boolean mustRefresh = true;
	@SuppressWarnings("unused")
	private int currentStep = 0;

	public MathInputScreen() {
		super();
		historyBehavior = HistoryBehavior.NORMAL;
	}

	/**
	 * Create a copy of this element
	 * @param mathInputScreen
	 */
	private MathInputScreen(MathInputScreen old) {
		this.calc = new MathContext(old.calc);
		this.historyBehavior = old.historyBehavior;
		this.created = old.created;
		this.currentStep = old.currentStep;
		this.d = old.d;
		this.errorLevel = old.errorLevel;
		this.ic = new InputContext(old.ic);
		this.initialized = old.initialized;
		this.mustRefresh = old.mustRefresh;
		this.result = new NormalOutputContainer(old.result);
		this.userInput = new NormalInputContainer(old.userInput, this.ic);
	}

	@Override
	public void created() throws InterruptedException {
		ic = new InputContext();
		calc = new MathContext();

		try {
			BlockContainer.initializeFonts(Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine.loadFont("norm"), Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine.loadFont("smal"));
		} catch (final IOException e) {
			e.printStackTrace();
			Engine.getPlatform().exit(1);
		}

		userInput = new NormalInputContainer(ic);
		result = new NormalOutputContainer();

		calc.init();
	}

	@Override
	public void initialized() throws InterruptedException {
		/* Fine caricamento */
	}

	@Override
	public void graphicInitialized() throws InterruptedException {
		/* Fine caricamento */
	}

	@Override
	public void beforeRender(final float dt) {
		if (Engine.INSTANCE.getHardwareDevice().getDisplayManager().error == null) {
			Engine.INSTANCE.getHardwareDevice().getDisplayManager().renderer.glClearColor(0xFFc5c2af);
		} else {
			Engine.INSTANCE.getHardwareDevice().getDisplayManager().renderer.glClearColor(0xFFDC3C32);
		}
		if (userInput.beforeRender(dt)) {
			mustRefresh = true;
		}
		if (computingResult) {
			computingElapsedTime += dt;
			computingAnimationElapsedTime += dt;
			if (computingAnimationElapsedTime > 0.1) {
				computingAnimationElapsedTime -= 0.1;
				computingAnimationIndex = (computingAnimationIndex + 1) % 16;
				mustRefresh = true;
			}
			if (computingElapsedTime > 5) {
				computingBreakTipVisible = true;
			}
		} else {
			computingElapsedTime = 0;
			computingAnimationElapsedTime = 0;
			computingAnimationIndex = 0;
			computingBreakTipVisible = false;
		}
	}

	@Override
	public void render() {
		final Renderer renderer = Engine.INSTANCE.getHardwareDevice().getDisplayManager().renderer;
		MathInputScreen.fontBig.use(Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine);
		final int textColor = 0xFF000000;
		final int padding = 4;
		renderer.glColor(textColor);

		userInput.draw(Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine, renderer, padding, padding + 20);

		if (computingResult) {
			renderer.glColor3f(1, 1, 1);
			final int leftX = 208;
			final int leftY = 16;
			final int size = 32;
			final int posY = computingAnimationIndex % 2;
			final int posX = (computingAnimationIndex - posY) / 2;
			renderer.glFillRect(Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine.getWidth() - size - 4, Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine.getHeight() - size - 4, size, size, leftX + size * posX, leftY + size * posY, size, size);
			if (computingBreakTipVisible) {
				Utils.getFont(false).use(Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine);
				renderer.glColor3f(0.75f, 0, 0);
				renderer.glDrawStringRight(Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine.getWidth() - 4 - size - 4, Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine.getHeight() - size / 2 - renderer.getCurrentFont().getCharacterHeight() / 2 - 4, "Press (=) to stop");
			}
		} else if (!result.isContentEmpty()) {
			result.draw(Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine, renderer, Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine.getWidth() - result.getWidth() - 2, Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine.getHeight() - result.getHeight() - 2);
		}
	}

	@Override
	public void renderTopmost() {
		final Renderer renderer = Engine.INSTANCE.getHardwareDevice().getDisplayManager().renderer;
		renderer.glColor3f(1, 1, 1);
		final int pos = 2;
		final int spacersNumb = 1;
		int skinN = 0;
		if (calc.exactMode) {
			skinN = 22;
		} else {
			skinN = 21;
		}
		Engine.INSTANCE.getHardwareDevice().getDisplayManager().guiSkin.use(Engine.INSTANCE.getHardwareDevice().getDisplayManager().engine);
		renderer.glFillRect(2 + 18 * pos + 2 * spacersNumb, 2, 16, 16, 16 * skinN, 16 * 0, 16, 16);
	}

	@Override
	public boolean mustBeRefreshed() {
		if (mustRefresh) {
			mustRefresh = false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onKeyPressed(final KeyPressedEvent k) {
		Engine.getPlatform().getConsoleUtils().out().println(ConsoleUtils.OUTPUTLEVEL_DEBUG_MIN, "MathInputScreen", "Pressed key " + k.getKey().toString());
		try {
			switch (k.getKey()) {
				case OK:
					userInput.toggleExtra();
					mustRefresh = true;
					return true;
				case HISTORY_BACK:
					if (userInput.isExtraOpened()) {
						userInput.closeExtra();
						currentStep = 0;
						mustRefresh = true;
						return true;
					}
				case BACK:
					if (userInput.isExtraOpened()) {
						userInput.closeExtra();
						mustRefresh = true;
						return true;
					}
				default:
					if (userInput.isExtraOpened() && userInput.getExtraKeyboardEventListener().onKeyPressed(k)) {
						currentStep = 0;
						return true;
					} else {
						final boolean step = k.getKey() == Key.STEP;
						switch (k.getKey()) {

							case STEP:
								currentStep++;
								return simplify(true, false);
							case SIMPLIFY:
								return simplify(false, true);
							case NUM0:
								typeChar('0');
								return true;
							case NUM1:
								typeChar('1');
								return true;
							case NUM2:
								typeChar('2');
								return true;
							case NUM3:
								typeChar('3');
								return true;
							case NUM4:
								typeChar('4');
								return true;
							case NUM5:
								typeChar('5');
								return true;
							case NUM6:
								typeChar('6');
								return true;
							case NUM7:
								typeChar('7');
								return true;
							case NUM8:
								typeChar('8');
								return true;
							case NUM9:
								typeChar('9');
								return true;
							case PLUS:
								typeChar('+');
								return true;
							case MINUS:
								typeChar('-');
								return true;
							case PLUS_MINUS:
								typeChar('±');
								return true;
							case MULTIPLY:
								typeChar('*');
								return true;
							case DIVIDE:
								typeChar('/');
								return true;
							case PARENTHESIS_OPEN:
								typeChar('(');
								return true;
							case PARENTHESIS_CLOSE:
								typeChar(')');
								return true;
							case DOT:
								typeChar('.');
								return true;
							case EQUAL:
								typeChar('=');
								return true;
							case SQRT:
								typeChar('Ⓐ');
								return true;
							case ROOT:
								typeChar('√');
								return true;
							case POWER_OF_2:
								typeChar(MathematicalSymbols.POWER_OF_TWO);
								return true;
							case POWER_OF_x:
								typeChar(MathematicalSymbols.POWER);
								return true;
							case PI:
								typeChar(MathematicalSymbols.PI);
								return true;
							case EULER_NUMBER:
								typeChar(MathematicalSymbols.EULER_NUMBER);
								return true;
							case LETTER_X:
								typeChar(MathematicalSymbols.variables[23]);
								return true;
							case LETTER_Y:
								typeChar(MathematicalSymbols.variables[24]);
								return true;
							case SINE:
								typeChar(MathematicalSymbols.SINE);
								return true;
							case COSINE:
								typeChar(MathematicalSymbols.COSINE);
								return true;
							case TANGENT:
								typeChar(MathematicalSymbols.TANGENT);
								return true;
							case ARCSINE:
								typeChar(MathematicalSymbols.ARC_SINE);
								return true;
							case ARCCOSINE:
								typeChar(MathematicalSymbols.ARC_COSINE);
								return true;
							case ARCTANGENT:
								typeChar(MathematicalSymbols.ARC_TANGENT);
								return true;
							case LOGARITHM:
								typeChar(MathematicalSymbols.LOGARITHM);
								return true;
							case DELETE:
								userInput.del();
								currentStep = 0;
								mustRefresh = true;
								return true;
							case LEFT:
								userInput.moveLeft();
								mustRefresh = true;
								return true;
							case RIGHT:
								userInput.moveRight();
								mustRefresh = true;
								return true;
							case RESET:
								userInput.clear();
								result.clear();
								currentStep = 0;
								if (Engine.INSTANCE.getHardwareDevice().getDisplayManager().error != null) {
									Engine.getPlatform().getConsoleUtils().out().println(1, "Resetting after error...");
									Engine.INSTANCE.getHardwareDevice().getDisplayManager().error = null;
								}
								return true;
							case SURD_MODE:
								calc.exactMode = !calc.exactMode;
								if (!result.isContentEmpty()) {
									result.clear();
									currentStep = 0;
									simplify(false, false);
								}
								return true;
							case debug1:
								Engine.INSTANCE.getHardwareDevice().getDisplayManager().setScreen(new EmptyScreen());
								return true;
							case HISTORY_BACK:
								//					if (Engine.INSTANCE.getHardwareDevice().getDisplayManager().canGoBack()) {
								//						if (currentExpression != null && currentExpression.length() > 0 & Engine.INSTANCE.getHardwareDevice().getDisplayManager().sessions[Engine.INSTANCE.getHardwareDevice().getDisplayManager().currentSession + 1] instanceof MathInputScreen) {
								//							newExpression = currentExpression;
								//							try {
								//								interpreta(true);
								//							} catch (final Error e) {}
								//						}
								//					}
								return false;
							case HISTORY_FORWARD:
								//					if (Engine.INSTANCE.getHardwareDevice().getDisplayManager().canGoForward()) {
								//						if (currentExpression != null && currentExpression.length() > 0 & Engine.INSTANCE.getHardwareDevice().getDisplayManager().sessions[Engine.INSTANCE.getHardwareDevice().getDisplayManager().currentSession - 1] instanceof MathInputScreen) {
								//							newExpression = currentExpression;
								//							try {
								//								interpreta(true);
								//							} catch (final Error e) {}
								//						}
								//					}
								return false;
							case debug_DEG:
								if (calc.angleMode.equals(AngleMode.DEG) == false) {
									calc.angleMode = AngleMode.DEG;
									currentStep = 0;
									return true;
								}
								return false;
							case debug_RAD:
								if (calc.angleMode.equals(AngleMode.RAD) == false) {
									calc.angleMode = AngleMode.RAD;
									currentStep = 0;
									return true;
								}
								return false;
							case debug_GRA:
								if (calc.angleMode.equals(AngleMode.GRA) == false) {
									calc.angleMode = AngleMode.GRA;
									currentStep = 0;
									return true;
								}
								return false;
							case DRG_CYCLE:
								if (calc.angleMode.equals(AngleMode.DEG) == true) {
									calc.angleMode = AngleMode.RAD;
								} else if (calc.angleMode.equals(AngleMode.RAD) == true) {
									calc.angleMode = AngleMode.GRA;
								} else {
									calc.angleMode = AngleMode.DEG;
								}
								currentStep = 0;
								return true;
							default:
								return false;
						}
					}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	private void swapInputScreen() {
		MathInputScreen mis = new MathInputScreen(this);
		Engine.INSTANCE.getHardwareDevice().getDisplayManager().setScreen(mis);
	}

	@SuppressWarnings("unused")
	@Deprecated
	private ObjectArrayList<Function> solveExpression(final ObjectArrayList<Function> f22) {
		return null;
		/*
		try {
			try {
				return calc.solveExpression(f22);
			} catch (final Exception ex) {
				if (Utils.debugOn) {
					ex.printStackTrace();
				}
				throw new Error(Errors.SYNTAX_ERROR);
			}
		} catch (final Error e) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			d.errorStackTrace = sw.toString().toUpperCase().replace("\t", "    ").replace("\r", "").split("\n");
			Engine.INSTANCE.getHardwareDevice().getDisplayManager().error = e.id.toString();
			System.err.println(e.id);
		}
		return null;
		*/
	}

	@Deprecated
	protected void step() {
		/*
		try {
			try {
				showVariablesDialog();
				ObjectArrayList<Function> results = new ObjectArrayList<>();
				final ObjectArrayList<Function> partialResults = new ObjectArrayList<>();
				for (final Function f : calc.f2) {
					if (f instanceof Equation) {
						Engine.INSTANCE.getHardwareDevice().getDisplayManager().setScreen(new SolveEquationScreen(this));
					} else {
						results.add(f);
						for (final Function itm : results) {
							if (itm.isSimplified() == false) {
								final List<Function> dt = itm.simplify();
								partialResults.addAll(dt);
							} else {
								partialResults.add(itm);
							}
						}
						results = new ObjectArrayList<>(partialResults);
						partialResults.clear();
					}
				}
		
				if (results.size() == 0) {
					calc.resultsCount = 0;
				} else {
					calc.resultsCount = results.size();
					Collections.reverse(results);
					// add elements to al, including duplicates
					final Set<Function> hs = new LinkedHashSet<>();
					hs.addAll(results);
					results.clear();
					results.addAll(hs);
					calc.f2 = results;
				}
				Utils.out.println(1, calc.f2.toString());
			} catch (final Exception ex) {
				if (Utils.debugOn) {
					ex.printStackTrace();
				}
				throw new Error(Errors.SYNTAX_ERROR);
			}
		} catch (final Error e) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			d.errorStackTrace = sw.toString().toUpperCase().replace("\t", "    ").replace("\r", "").split("\n");
			Engine.INSTANCE.getHardwareDevice().getDisplayManager().error = e.id.toString();
			System.err.println(e.id);
		}
		*/
	}

	protected boolean simplify(final boolean step, final boolean swapScreen) {
		if (!step) {
			currentStep = 0;
		}
		if (Engine.INSTANCE.getHardwareDevice().getDisplayManager().error != null) {
			//TODO: make the error management a global API rather than being relegated to this screen.
			Engine.getPlatform().getConsoleUtils().out().println(1, "Resetting after error...");
			Engine.INSTANCE.getHardwareDevice().getDisplayManager().error = null;
			calc.f = null;
			calc.f2 = null;
			calc.resultsCount = 0;
			return true;
		} else if (!computingResult) {
			computingResult = true;
			computingThread = new Thread(() -> {
				try {
					try {
						if (!userInput.isAlreadyParsed() && !userInput.isEmpty()) {
							final Expression expr = MathParser.parseInput(calc, userInput);
							if (calc.f == null | calc.f2 == null) {
								calc.f = new ObjectArrayList<>();
								calc.f2 = new ObjectArrayList<>();
							} else {
								calc.f.clear();
								calc.f2.clear();
							}
							calc.f.add(expr);
							Engine.getPlatform().getConsoleUtils().out().println(2, "INPUT: " + expr);
							final MathSolver ms = new MathSolver(expr);
							final ObjectArrayList<ObjectArrayList<Function>> resultSteps = ms.solveAllSteps();
							resultSteps.add(0, Utils.newArrayList(expr));
							final ObjectArrayList<Function> resultExpressions = resultSteps.get(resultSteps.size() - 1);
							for (final Function rr : resultExpressions) {
								Engine.getPlatform().getConsoleUtils().out().println(0, "RESULT: " + rr.toString());
							}
							final ObjectArrayList<ObjectArrayList<Block>> resultBlocks = MathParser.parseOutput(calc, resultExpressions);
							result.setContentAsMultipleGroups(resultBlocks);
							//									showVariablesDialog(() -> {
							//										currentExpression = newExpression;
							//										simplify();
							//									});
							if (swapScreen == true) {
								this.swapInputScreen();
							}
						}
					} catch (final InterruptedException ex) {
						Engine.getPlatform().getConsoleUtils().out().println(ConsoleUtils.OUTPUTLEVEL_DEBUG_MIN, "Computing thread stopped.");
					} catch (final Exception ex) {
						if (Engine.getPlatform().getSettings().isDebugEnabled()) {
							ex.printStackTrace();
						}
						throw new Error(Errors.SYNTAX_ERROR);
					}
				} catch (final Error e) {
					d.errorStackTrace = Engine.getPlatform().stacktraceToString(e);
					Engine.INSTANCE.getHardwareDevice().getDisplayManager().error = e.id.toString();
					System.err.println(e.id);
				}
				computingResult = false;
			});
			Engine.getPlatform().setThreadName(computingThread, "Computing Thread");
			Engine.getPlatform().setThreadDaemon(computingThread);
			computingThread.setPriority(Thread.NORM_PRIORITY + 3);
			computingThread.start();
			return true;
		} else {
			if (computingThread != null) {
				computingThread.interrupt();
				computingResult = false;
				return true;
			}
			return false;
		}
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void changeEquationScreen() {
		throw new UnsupportedOperationException();
//
//		if (!userInput.isEmpty()) {
//			final MathInputScreen cloned = clone();
//			cloned.userInput.setCaretPosition(cloned.userInput.getCaretMaxPosition()-1);
//			Engine.INSTANCE.getHardwareDevice().getDisplayManager().replaceScreen(cloned);
//			initialized = false;
//			Engine.INSTANCE.getHardwareDevice().getDisplayManager().setScreen(this);
//
//		}
	}

	public void typeChar(final char chr) {
		userInput.typeChar(chr);
		mustRefresh = true;
	}

	@Override
	public boolean onKeyReleased(final KeyReleasedEvent k) {
		if (k.getKey() == Key.OK) {
			return true;
		} else if (userInput.isExtraOpened() && userInput.getExtraKeyboardEventListener().onKeyReleased(k)) {
			return true;
		} else {
			switch (k.getKey()) {
				default:
					return false;
			}
		}
	}

	public void showVariablesDialog() {
		showVariablesDialog(null);
	}

	public void showVariablesDialog(final Runnable runnable) {
		final Thread ct = new Thread(() -> {
			final ObjectArrayList<Function> knownVarsInFunctions = getKnownVariables(calc.f.toArray(new Function[calc.f.size()]));
			for (final VariableValue f : calc.variablesValues) {
				if (knownVarsInFunctions.contains(f.v)) {
					knownVarsInFunctions.remove(f.v);
				}
			}

			boolean cancelled = false;
			for (final Function f : knownVarsInFunctions) {
				final ChooseVariableValueScreen cvs = new ChooseVariableValueScreen(this, new VariableValue((Variable) f, new Number(calc, 0)));
				Engine.INSTANCE.getHardwareDevice().getDisplayManager().setScreen(cvs);
				try {
					Engine.INSTANCE.getHardwareDevice().getDisplayManager().screenChange.acquire();
				} catch (final InterruptedException e) {}
				if (cvs.resultNumberValue == null) {
					cancelled = true;
					break;
				} else {
					final int is = calc.variablesValues.size();
					for (int i = 0; i < is; i++) {
						if (calc.variablesValues.get(i).v == f) {
							calc.variablesValues.remove(i);
						}
					}
					calc.variablesValues.add(new VariableValue((Variable) f, (Number) cvs.resultNumberValue));
				}
			}
			if (!cancelled) {
				if (runnable != null) {
					runnable.run();
				}
			}
		});
		Engine.getPlatform().setThreadName(ct, "Variables user-input queue thread");
		ct.setPriority(Thread.MIN_PRIORITY);
		Engine.getPlatform().setThreadDaemon(ct);
		ct.start();
	}

	private ObjectArrayList<Function> getKnownVariables(final Function[] fncs) {
		final ObjectArrayList<Function> res = new ObjectArrayList<>();
		for (final Function f : fncs) {
			if (f instanceof FunctionOperator) {
				res.addAll(getKnownVariables(new Function[] { ((FunctionOperator) f).getParameter1(), ((FunctionOperator) f).getParameter2() }));
			} else if (f instanceof FunctionDynamic) {
				res.addAll(getKnownVariables(((FunctionDynamic) f).getParameters()));
			} else if (f instanceof FunctionSingle) {
				res.addAll(getKnownVariables(new Function[] { ((FunctionSingle) f).getParameter() }));
			} else if (f instanceof Variable) {
				if (((Variable) f).getType() == Variable.V_TYPE.CONSTANT) {
					if (!res.contains(f)) {
						res.add(f);
					}
				}
			}
		}
		return res;
	}

	@Override
	public String getSessionTitle() {
		return "Calculator";
	}

}
