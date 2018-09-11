package it.cavallium.warppi.hardware;

import java.io.InputStream;

import it.cavallium.warppi.deps.Platform.PngUtils;

public class HardwarePngUtils implements PngUtils {

	@Override
	public PngReader load(InputStream resourceStream) {
		return new HardwarePngReader(resourceStream);
	}

}