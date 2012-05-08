package com.randomnoun.dmx.show;

import java.awt.Color;
import java.util.Map;

import com.randomnoun.common.ClassInspector;

public class ShowUtils {

	static Map colorMap;
	
	/** @deprecated use {@link #getColorByName(String)} instead */
	public static Color toColor(String colorName) {
		return getColorByName(colorName);
	}

	/** Return a Color given it's string name. If the name does not match a standard Java
	 * Color name, then it is evaluated as a hexadecimal RGB value (with an optional leading '#'
	 * character). 
	 * 
	 * @TODO HTML colour names / other colourspace names
	 * 
	 * @param colorName the name of the colour
	 * 
	 * @return a Color object containing the RGB values of this colour.
	 */
	public static Color getColorByName(String colorName) {
		colorName = colorName.toUpperCase().trim();
		Color color = (Color) colorMap.get(colorName);
		if (color!=null) { return color; }
		// try decoding as hex
		// @TODO: interpret 3-digit colors as per HTML spec 
		if (colorName.startsWith("#")) { colorName = colorName.substring(1); }
		try {
			long hexValue = Long.parseLong(colorName, 16);
			return new Color((int) hexValue);
		} catch (NumberFormatException nfe) {
			// intentional fallthrough
		}
		
		throw new IllegalArgumentException("Could not parse color '" + colorName + "'");
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
