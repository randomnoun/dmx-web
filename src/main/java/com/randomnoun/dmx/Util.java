package com.randomnoun.dmx;

import java.awt.Color;
import java.lang.reflect.Field;

/** Miscellaneous utility class
 * 
 * @author knoxg
 * @version $Id$
 *
 */

public class Util {

	/** Return a Color given it's string name. Only standard Java Color names are
	 * currently recognised.
	 * 
	 * @TODO HTML color names / other color spaces
	 * 
	 * @param colorName the name of the color
	 * 
	 * @return a Color object containing the RGB values of this color.
	 */
	public static Color getColorByName(String colorName) {
	    try {
	        // Find the field and value of colorName
		    Field field = Class.forName("java.awt.Color").getField(colorName.toUpperCase());
		    return (Color)field.get(null);
		} catch (Exception e) {
		    return null;
		}
	}
	
}
