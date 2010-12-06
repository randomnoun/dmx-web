package com.randomnoun.dmx.show;

import java.awt.Color;
import java.util.Map;

import com.randomnoun.common.ClassInspector;

public class ShowUtils {

	static Map colorMap;
	
	static Color toColor(String colorString) {
		Color color = (Color) colorMap.get(colorString);
		if (color!=null) { return color; }
		
		// @TODO: parse (r,g,b) or #rrggbb values
		throw new IllegalArgumentException("Could not parse color '" + colorString + "'");
	}
	
	static {
		colorMap = ClassInspector.getConstantsMap(Color.class, "");
	}
}
