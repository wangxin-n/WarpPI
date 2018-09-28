package it.cavallium.warppi.gui.graphicengine.html;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLImageElement;

import ar.com.hjg.pngj.PngReader;
import it.cavallium.warppi.Engine;
import it.cavallium.warppi.gui.graphicengine.GraphicEngine;
import it.cavallium.warppi.gui.graphicengine.Skin;

public class HtmlSkin implements Skin {

	private String url;

	private int[] skinSize;

	private boolean initd;

	private HTMLImageElement imgEl;

	public HtmlSkin(final String file) throws IOException {
		load(file);
	}

	@Override
	public void use(final GraphicEngine d) {
		if (d instanceof HtmlEngine) {
			if (!initd)
				initialize(d);
			((HtmlEngine) d).getRenderer().currentSkin = this;
		}
	}

	@Override
	public void load(String file) throws IOException {
		if (!file.startsWith("/"))
			file = "/" + file;
		url = Engine.getPlatform().getStorageUtils().getBasePath() + file;
		final InputStream stream = Engine.getPlatform().getStorageUtils().getResourceStream(file);
		final PngReader r = new PngReader(stream);
		skinSize = new int[] { r.imgInfo.cols, r.imgInfo.rows };
		r.close();
	}

	@Override
	public void initialize(final GraphicEngine d) {
		final HTMLDocument doc = Window.current().getDocument();
		imgEl = doc.createElement("img").cast();
		imgEl.setSrc(url);
		imgEl.setClassName("hidden");
		doc.getBody().appendChild(imgEl);
		initd = true;
	}

	@Override
	public boolean isInitialized() {
		return initd;
	}

	@Override
	public int getSkinWidth() {
		return skinSize[0];
	}

	@Override
	public int getSkinHeight() {
		return skinSize[1];
	}

	public final String getUrl() {
		return url;
	}

	public final HTMLImageElement getImgElement() {
		return imgEl;
	}
}