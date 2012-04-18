package com.randomnoun.dmx.show;

import java.awt.Color;
import java.util.Map;

import com.randomnoun.common.ClassInspector;

public class ShowUtils {

	static Map colorMap;
	
	public static Color toColor(String colorString) {
		Color color = (Color) colorMap.get(colorString);
		if (color!=null) { return color; }
		throw new IllegalArgumentException("Could not parse color '" + colorString + "'");
	}

	/** Return a Color given it's string name. Only standard Java Color names are
	 * currently recognised.
	 * 
	 * @TODO HTML color names / other color spaces
	 * 
	 * @param colorName the name of the color
	 * 
	 * @return a Color object containing the RGB values of this color.
	 */
	public static Color getColorByName(String colorString) {
		Color color = (Color) colorMap.get(colorString);
		if (color!=null) { return color; }
		throw new IllegalArgumentException("Could not parse color '" + colorString + "'");
	}
	
	/** Return a color that is a directly-proportional fade from one color to another,
	 * based on the RGB values of that color.
	 * 
	 * @param from source color
	 * @param to target color
	 * @param fade a double value representing how far between each color to fade; 
	 *   a value of 0=100% source color, 1=100% target color. 
	 * 
	 * @return a color somewhere between the given source and target colors
	 */
	public static Color fadeRGBColor(Color from, Color to, double fade) {
		// could ensure 0>=fade>=1
		int fr = from.getRed(), fg = from.getGreen(), fb = from.getBlue(); // from rgb
		int tr = to.getRed(), tg = to.getGreen(), tb = to.getBlue(); // to rgb
		int rr, rg, rb; // result rgb
		rr = (int)(fr + (tr-fr)*fade);
		rg = (int)(fg + (tg-fg)*fade);
		rb = (int)(fb + (tb-fb)*fade);
		return new Color(rr, rg, rb);
	}
	
	
	static {
		colorMap = ClassInspector.getConstantsMap(Color.class, "");
	}
}
