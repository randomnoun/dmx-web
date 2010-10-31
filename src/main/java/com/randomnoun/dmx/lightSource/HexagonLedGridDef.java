package com.randomnoun.dmx.lightSource;

public class HexagonLedGridDef extends LedGridDef {
	
	/** Defines a hexagon of LEDs.
	 * 
	 * <p>The arrangement is specified as a string, using characters to denote
	 * LED types (R=red, G=green, B=blue). An empty cell in the hexagonal grid
	 * is denoted with an x. The hexagon is defined with a flat side on the top, e.g.:
	 * 
	 * <pre>
	 *     R G B
	 *    R G B R
	 *   R B G R B
	 *    G R B G
	 *     R B G
	 * </pre>
	 * 
	 * <p>Defines a hex-grid of length 3 with 19 LEDs
	 * 
	 * @param ledsPerSide number of LEDs on one length of the hexagon
	 * @param arrangement a String representing the layout of LEDs in the hexagon
	 */
	public HexagonLedGridDef(long ledsPerSide, String arrangement) {
		int ledCount = 0;
		for (int i=0; i<arrangement.length(); i++) {
			if ("RGB".indexOf((int) arrangement.charAt(i))!=-1) {
				ledCount++;
			}
		}
		LedDef[] leds = new LedDef[ledCount];
		// TODO: populate the leddef
	}
}
