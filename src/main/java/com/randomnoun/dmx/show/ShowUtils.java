package com.randomnoun.dmx.show;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

import com.randomnoun.common.ClassInspector;
import com.randomnoun.common.Text;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureControllerMatrix;

/**
 * @TODO various color model parameter ranges are inconsistent
 * 
 * @see http://www.brucelindbloom.com/index.html?ColorCalculator.html
 * @see http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
 * 
 * @author knoxg
 *
 */

public class ShowUtils {

	static Map colorMap;
	
	static Logger logger = Logger.getLogger(ShowUtils.class);
	
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
	
	
	/** Convert an RGB color to it's representation in the YCbCr 
	 * Luminance / Chrominance color model (used in image and video compression).
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param ycbcr
	 */
	public static void rgb2ycbcr(int r, int g, int b, int[] ycbcr) {
		int y  = (int)( 0.299   * r + 0.587   * g + 0.114   * b);
		int cb = (int)(-0.16874 * r - 0.33126 * g + 0.50000 * b);
		int cr = (int)( 0.50000 * r - 0.41869 * g - 0.08131 * b);
		
		ycbcr[0] = y;
		ycbcr[1] = cb;
		ycbcr[2] = cr;         
	}
	
	/** Convert an RGB color to it's representation in the YUV 
	 * Luminance / Chrominance color model (used in PAL-TV systems).
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param ycbcr
	 */

	public static void rgb2yuv(int r,int g, int b, int[] yuv) {
			int y = (int)(0.299 * r + 0.587 * g + 0.114 * b);
			int u = (int)((b - y) * 0.492f); 
			int v = (int)((r - y) * 0.877f);
		
			yuv[0]= y;
			yuv[1]= u;
			yuv[2]= v;
		}

	/** Convert an RGB color to it's representation in the HSB 
	 * (Hue, Saturation, Brightness) Luminance / Chrominance color model.
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param ycbcr
	 */

	public static void rgb2hsb(int r, int g, int b, int[] hsb) {
			float [] hsbvals = new float[3]; 
			Color.RGBtoHSB(r, g, b, hsbvals);
	}

	 
	/** Convert an RGB color to it's representation in the HMMD 
	 * (Hue, Max, Min, Diff) Luminance / Chrominance color model, as used by
	 * by the MPEG7 standard.
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param ycbcr
	 */

	public static void rgb2hmmd(int r, int g, int b, int[] hmmd) {
		
		float max = (int)Math.max(Math.max(r,g), Math.max(g,b));
		float min = (int)Math.min(Math.min(r,g), Math.min(g,b));
		float diff = (max - min);
		float sum = (float) ((max + min)/2.);
	
		float hue = 0;
		if (diff == 0)
			hue = 0;
		else if (r == max && (g - b) > 0)
			hue = 60*(g-b)/(max-min);
		else if (r == max && (g - b) <= 0)
			hue = 60*(g-b)/(max-min) + 360;
		else if (g == max)
			hue = (float) (60*(2.+(b-r)/(max-min)));
		else if (b == max)
			hue = (float) (60*(4.+(r-g)/(max-min)));
						 
		hmmd[0] = (int)(hue);
		hmmd[1] = (int)(max);
		hmmd[2] = (int)(min);
		hmmd[3] = (int)(diff);
	}

	 
	/** Convert an RGB color to it's representation in the HSL 
	 * (Hue, Saturation, Lightness (also Luminance or Luminosity )) 
	 * Luminance / Chrominance color model.
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param ycbcr
	 */

	public static void rgb2hsl(int r, int g, int b, int hsl[]) {
							
		float var_R = ( r / 255f );                    
		float var_G = ( g / 255f );
		float var_B = ( b / 255f );
		
		float var_Min;    //Min. value of RGB
		float var_Max;    //Max. value of RGB
		float del_Max;    //Delta RGB value
						 
		if (var_R > var_G) 
			{ var_Min = var_G; var_Max = var_R; }
		else 
			{ var_Min = var_R; var_Max = var_G; }

		if (var_B > var_Max) var_Max = var_B;
		if (var_B < var_Min) var_Min = var_B;

		del_Max = var_Max - var_Min; 
								 
		float H = 0, S, L;
		L = ( var_Max + var_Min ) / 2f;
	
		if ( del_Max == 0 ) { H = 0; S = 0; } // gray
		else {                                //Chroma
			if ( L < 0.5 ) 
				S = del_Max / ( var_Max + var_Min );
			else           
				S = del_Max / ( 2 - var_Max - var_Min );
	
			float del_R = ( ( ( var_Max - var_R ) / 6f ) + ( del_Max / 2f ) ) / del_Max;
			float del_G = ( ( ( var_Max - var_G ) / 6f ) + ( del_Max / 2f ) ) / del_Max;
			float del_B = ( ( ( var_Max - var_B ) / 6f ) + ( del_Max / 2f ) ) / del_Max;
	
			if ( var_R == var_Max ) 
				H = del_B - del_G;
			else if ( var_G == var_Max ) 
				H = ( 1 / 3f ) + del_R - del_B;
			else if ( var_B == var_Max ) 
				H = ( 2 / 3f ) + del_G - del_R;
			if ( H < 0 ) H += 1;
			if ( H > 1 ) H -= 1;
		}
		hsl[0] = (int)(360*H);
		hsl[1] = (int)(S*100);
		hsl[2] = (int)(L*100);
	}

	 
	/** Convert an RGB color to it's representation in the HSV 
	 * (Hue, Saturation, Value) Luminance / Chrominance color model.
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param hsv result (h=0..360, s=0..100, v=0..100) 
	 */

	public static void rgb2hsv(int r, int g, int b, int hsv[]) {
		
		int min;    //Min. value of RGB
		int max;    //Max. value of RGB
		int delMax; //Delta RGB value
		
		if (r > g) { min = g; max = r; }
		else { min = r; max = g; }
		if (b > max) max = b;
		if (b < min) min = b;
								
		delMax = max - min;
	 
		float H = 0, S;
		float V = max/255f;
		   
		if ( delMax == 0 ) { H = 0; S = 0; }
		else {                                   
			S = delMax/255f;
			if ( r == max ) 
				H = (      (g - b)/(float)delMax)*60;
			else if ( g == max ) 
				H = ( 2 +  (b - r)/(float)delMax)*60;
			else if ( b == max ) 
				H = ( 4 +  (r - g)/(float)delMax)*60;   
		}
		if (H < 0) { H += 360; }
								 
		hsv[0] = (int)(H);
		hsv[1] = (int)(S*100);
		hsv[2] = (int)(V*100);
	}

	public static void rgb2hsvAlt(int rv, int gv, int bv, int[] hsv) {
        float h;
        float s;
        float v;
        // int[] result = new int[3];
        float r = (float)rv/255;
        float g = (float)gv/255;
        float b = (float)bv/255;
        

        float min, max, delta;
        min = Math.min(r, Math.min(g, b));
        max = Math.max(r, Math.max(g, b));
        v = max;                // v
        delta = max - min;
        if (max != 0) {
            s = delta / max;        // s
        } else {
            // r = g = b = 0            // s = 0, v is undefined
            s = 0;
            h = -1;
            hsv[0]=0; hsv[1]=0; hsv[2]=0; return; 
            // return new float[]{h,s,v};
        }
        if (r == max) {
            h = (g - b) / delta;        // between yellow & magenta
        } else if (g == max) {
            h = 2 + (b - r) / delta;    // between cyan & yellow
        } else {
            h = 4 + (r - g) / delta;    // between magenta & cyan
        }
        h *= 60;                // degrees
        if (h < 0)
            h += 360;

        hsv[0]=(int)h; hsv[1]=(int)(s*100); hsv[2]=(int)(v*100);
        
    }
	 
	/** Convert an RGB color to it's representation in the xyY "Tristimulus Color"
	 * CIE color model
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param ycbcr
	 */

	public static void rgb2xyY(int R, int G, int B, int []xyy) {
		//http://www.brucelindbloom.com
		
		float rf, gf, bf;
		float r, g, b, X, Y, Z;
		  
		// RGB to XYZ
		r = R/255.f; //R 0..1
		g = G/255.f; //G 0..1
		b = B/255.f; //B 0..1
		 
		if (r <= 0.04045)
			r = r/12;
		else
			r = (float) Math.pow((r+0.055)/1.055,2.4);
			  
		if (g <= 0.04045)
			g = g/12;
		else
			g = (float) Math.pow((g+0.055)/1.055,2.4);
		
		if (b <= 0.04045)
			b = b/12;
		else
			b = (float) Math.pow((b+0.055)/1.055,2.4);
		  
		X =  0.436052025f*r  + 0.385081593f*g  + 0.143087414f *b;
		Y =  0.222491598f*r  + 0.71688606f *g  + 0.060621486f *b;
		Z =  0.013929122f*r  + 0.097097002f*g  + 0.71418547f  *b;
		 
		float x;
		float y;
					  
		float sum = X + Y + Z;
		if (sum != 0) {
		x = X / sum;
		y = Y / sum;
		}
		else {
			float Xr = 0.964221f;  // reference white
			float Yr = 1.0f;
			float Zr = 0.825211f;
								 
		x = Xr / (Xr + Yr + Zr);
		y = Yr / (Xr + Yr + Zr);
		}
		   
		xyy[0] = (int) (255*x + .5);
		xyy[1] = (int) (255*y + .5);
		xyy[2] = (int) (255*Y + .5);
		 
	} 

	 
	/** Convert an RGB color to it's representation in the XYZ "Tristimulus Color" 
	 * CIE color model.
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param ycbcr
	 */

	public static void rgb2xyz(int R, int G, int B, int []xyz) {
		float rf, gf, bf;
		float r, g, b, X, Y, Z;
								 
		r = R/255.f; //R 0..1
		g = G/255.f; //G 0..1
		b = B/255.f; //B 0..1
		 
		if (r <= 0.04045)
			r = r/12;
		else
			r = (float) Math.pow((r+0.055)/1.055,2.4);
		
		if (g <= 0.04045)
			g = g/12;
		else
			g = (float) Math.pow((g+0.055)/1.055,2.4);
		
		if (b <= 0.04045)
			b = b/12;
		else
			b = (float) Math.pow((b+0.055)/1.055,2.4);
		 
		X =  0.436052025f*r     + 0.385081593f*g + 0.143087414f *b;
		Y =  0.222491598f*r     + 0.71688606f *g + 0.060621486f *b;
		Z =  0.013929122f*r     + 0.097097002f*g + 0.71418547f  *b;
							 
		xyz[1] = (int) (255*Y + .5);
		xyz[0] = (int) (255*X + .5); 
		xyz[2] = (int) (255*Z + .5);    
	} 

	 
	/** Convert an RGB color to it's representation in the LAB device-independent
	 * CIE color model (internal color space of Photoshop)
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param ycbcr
	 */

	public static void rgb2lab(int R, int G, int B, int []lab) {
		//http://www.brucelindbloom.com
		  
		float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
		float Ls, as, bs;
		float eps = 216.f/24389.f;
		float k = 24389.f/27.f;
		   
		float Xr = 0.964221f;  // reference white D50
		float Yr = 1.0f;
		float Zr = 0.825211f;
		
		// RGB to XYZ
		r = R/255.f; //R 0..1
		g = G/255.f; //G 0..1
		b = B/255.f; //B 0..1
		
		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r/12;
		else
			r = (float) Math.pow((r+0.055)/1.055,2.4);
		
		if (g <= 0.04045)
			g = g/12;
		else
			g = (float) Math.pow((g+0.055)/1.055,2.4);
		
		if (b <= 0.04045)
			b = b/12;
		else
			b = (float) Math.pow((b+0.055)/1.055,2.4);
		
		
		X =  0.436052025f*r     + 0.385081593f*g + 0.143087414f *b;
		Y =  0.222491598f*r     + 0.71688606f *g + 0.060621486f *b;
		Z =  0.013929122f*r     + 0.097097002f*g + 0.71418547f  *b;
		
		// XYZ to Lab
		xr = X/Xr;
		yr = Y/Yr;
		zr = Z/Zr;
				
		if ( xr > eps )
			fx =  (float) Math.pow(xr, 1/3.);
		else
			fx = (float) ((k * xr + 16.) / 116.);
		 
		if ( yr > eps )
			fy =  (float) Math.pow(yr, 1/3.);
		else
		fy = (float) ((k * yr + 16.) / 116.);
		
		if ( zr > eps )
			fz =  (float) Math.pow(zr, 1/3.);
		else
			fz = (float) ((k * zr + 16.) / 116);
		
		Ls = ( 116 * fy ) - 16;
		as = 500*(fx-fy);
		bs = 200*(fy-fz);
		
		lab[0] = (int) (2.55*Ls + .5);
		lab[1] = (int) (as + .5); 
		lab[2] = (int) (bs + .5);       
	} 

	 
	/** Convert an RGB color to it's representation in the LUV CIE color model.
	 * 
	 * <p>Code sourced from http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
	 * 
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 * @param ycbcr
	 */

	public static void rgb2luv(int R, int G, int B, int []luv) {
		//http://www.brucelindbloom.com
		
		float rf, gf, bf;
		float r, g, b, X_, Y_, Z_, X, Y, Z, fx, fy, fz, xr, yr, zr;
		float L;
		float eps = 216.f/24389.f;
		float k = 24389.f/27.f;
		 
		float Xr = 0.964221f;  // reference white D50
		float Yr = 1.0f;
		float Zr = 0.825211f;
		 
		// RGB to XYZ
		 
		r = R/255.f; //R 0..1
		g = G/255.f; //G 0..1
		b = B/255.f; //B 0..1
		   
		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r/12;
		else
			r = (float) Math.pow((r+0.055)/1.055,2.4);
		
		if (g <= 0.04045)
			g = g/12;
		else
			g = (float) Math.pow((g+0.055)/1.055,2.4);
		 
		if (b <= 0.04045)
			b = b/12;
		else
			b = (float) Math.pow((b+0.055)/1.055,2.4);
		
		
		X =  0.436052025f*r     + 0.385081593f*g + 0.143087414f *b;
		Y =  0.222491598f*r     + 0.71688606f *g + 0.060621486f *b;
		Z =  0.013929122f*r     + 0.097097002f*g + 0.71418547f  *b;
		
		// XYZ to Luv
		
		float u, v, u_, v_, ur_, vr_;
						
		u_ = 4*X / (X + 15*Y + 3*Z);
		v_ = 9*Y / (X + 15*Y + 3*Z);
				 
		ur_ = 4*Xr / (Xr + 15*Yr + 3*Zr);
		vr_ = 9*Yr / (Xr + 15*Yr + 3*Zr);
		  
		yr = Y/Yr;
		
		if ( yr > eps )
			L =  (float) (116*Math.pow(yr, 1/3.) - 16);
		else
			L = k * yr;
		 
		u = 13*L*(u_ -ur_);
		v = 13*L*(v_ -vr_);
		
		luv[0] = (int) (2.55*L + .5);
		luv[1] = (int) (u + .5); 
		luv[2] = (int) (v + .5);        
	} 

	/** Convert HSV to RGB
	 * 
	 * <p>Source from http://www.koders.com/java/fid698452C6AA108615D4A611B52D27A9F5819B39F5.aspx
	 * 
	 * @param h
	 * @param s
	 * @param v
	 * @param rgb
	 * 
	 */

	public static void hsv2rgb(float h, float s, float v, int[] rgb) {
	    float r = 0;
	    float g = 0;
	    float b = 0;

	    if (s == 0) {
	      // this color in on the black white center line <=> h = UNDEFINED
	      if (Float.isNaN(h)) {
	        // Achromatic color, there is no hue
	        r = v; g = v; b = v;
	      } else {
	        //throw new IllegalArgumentException("When s=0, h must be NaN");
	    	  r = v; g = v; b = v;
	      }
	    } else {
	       	
	      if (h == 1) {
	        // 1 is equiv to 0
	        h = 0;
	      }

	      // h should be now in [0,6)
	       h = h * 6;

	      int i = (int) Math.floor(h);
	      float f = h - i; //f is fractional part of h
	      float p = v * (1 - s);
	      float q = v * (1 - (s * f));
	      float t = v * (1 - (s * (1 - f)));

	      switch (i) {
	        case 0:
	          r = v;
	          g = t;
	          b = p;

	          break;

	        case 1:
	          r = q;
	          g = v;
	          b = p;

	          break;

	        case 2:
	          r = p;
	          g = v;
	          b = t;

	          break;

	        case 3:
	          r = p;
	          g = q;
	          b = v;

	          break;

	        case 4:
	          r = t;
	          g = p;
	          b = v;

	          break;

	        case 5:
	          r = v;
	          g = p;
	          b = q;

	          break;
	      }
	    }

	    // now assign everything....
	    rgb[0] = (int) (r * 255);
	    rgb[1] = (int) (g * 255);
	    rgb[2] = (int) (b * 255);
	}
	
	// same units as rgb2hsv(int int int)
	public static void hsv2rgb(int h, int s, int v, int[] rgb) {
		hsv2rgb(((float) h)/360, ((float) s)/100, ((float) v)/100, rgb);
	}
	
	/** Returns the dmx offset of a fixture within a set of fixtures with a given
	 * starting universe number and offset, assuming that fixtures
	 * do not span universes and start at channel 1 after the initial universe.
	 * 
	 * @see #fillDmxUniverse(int, int, int, int)
	 * 
	 * @param startUniverse universe number of the first fixture in the set
	 * @param startOffset dmx offset of the first fixture in the set
	 * @param numChannels number of dmx channels between each fixture
	 * @param n the (0-based) fixture number we are interested in returning the DMX offset for
	 */
	public static int fillDmxOffset(int startUniverse, int startOffset, int numChannels, int n) {
		int startc = (512-startOffset) / numChannels; // number of fixtures in 1st universe
		int nextc = 512 / numChannels;  // number of fixtures in subsequent universes
		if (n < startc) { return startOffset + n*numChannels; }
		return (n % nextc) * numChannels + 1;
	}

	/** Returns the dmx universe of a fixture within a set of fixtures with a given
	 * starting universe number and offset, assuming that fixtures
	 * do not span universes and start at channel 1 after the initial universe.
	 * 
	 * @see #fillDmxOffset(int, int, int, int)
	 * 
	 * @param startUniverse universe number of the first fixture in the set
	 * @param startOffset dmx offset of the first fixture in the set
	 * @param numChannels number of dmx channels between each fixture
	 * @param n the (0-based) fixture number we are interested in returning the DMX offset for
	 */
	public static int fillDmxUniverse(int startUniverse, int startOffset, int numChannels, int n) {
		int startc = (512-startOffset) / numChannels; // number of fixtures in 1st universe
		int nextc = 512 / numChannels;  // number of fixtures in subsequent universes
		if (n < startc) { return startUniverse; }
		return startUniverse + (n - startc) / nextc;
	}

	
	public static FixtureControllerMatrix getFixtureControllerMatrix(Controller c, String fixtureNameTemplate) {
        // @TODO: detect {y} before {x}
		FixtureControllerMatrix fcm = new FixtureControllerMatrix();
        String fixtureNameRegex = Text.replaceString(fixtureNameTemplate, "{x}", "(.*)");
        fixtureNameRegex = Text.replaceString(fixtureNameRegex, "{y}", "(.*)");
        //Controller c = getController();
        Pattern p = Pattern.compile(fixtureNameRegex);
        int maxX=0, maxY=0, minX=Integer.MAX_VALUE, minY=Integer.MAX_VALUE;
        for (Fixture f : c.getFixtures()) {
            Matcher m = p.matcher(f.getName());
            if (m.matches()) {
                maxX = Math.max(maxX, Integer.parseInt(m.group(1)));
                maxY = Math.max(maxY, Integer.parseInt(m.group(2)));
                minX = Math.min(minX, Integer.parseInt(m.group(1)));
                minY = Math.min(minY, Integer.parseInt(m.group(2)));
            }
        }
        logger.debug("getFixtureControllerMatrix(): found matrix " + fixtureNameTemplate + " with minX=" + minX + ", minY=" + minY + ", maxX=" + maxX + ", maxY=" + maxY);
        FixtureController[][] fcs = new FixtureController[maxX-minX+1][maxY-minY+1];
        for (int x=minX; x<=maxX; x++) {
            for (int y=minY; y<=maxY; y++) {
                //logger.debug("finding fixture at x=" + x + ", y=" + y);
                String name = Text.replaceString(fixtureNameTemplate, "{x}", String.valueOf(x));
                name = Text.replaceString(name, "{y}", String.valueOf(y));
                fcs[x-minX][y-minY] = c.getFixtureControllerByNameNoEx(name);
                if (fcs[x-minX][y-minY]==null) {
                    logger.warn("getFixtureControllerMatrix(): missing fixture '" + name + "' in matrix");
                }
            }
        }
        fcm.setMinX(minX); fcm.setMinY(minY);
        fcm.setMaxX(maxX); fcm.setMaxY(maxY);
        fcm.setFixtureControllers(fcs);
        return fcm;
    }
	
    public static BufferedImage[] getImages(Controller controller, String resourceName) {
        // InputStream is = getController().getClass().getResourceAsStream("matrix-animations/" + resourceName);
        logger.info("Reading images from '" + resourceName + "'");
        try {
            InputStream is = controller.getResource(resourceName);
            ImageInputStream stream = ImageIO.createImageInputStream(is);
            Iterator readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                logger.error("no image reader found");
                return null;
            }
            ImageReader reader = (ImageReader) readers.next();
            reader.setInput(stream); // don't omit this line!
            int numImages = reader.getNumImages(true); // don't use false!
            logger.info("numImages = " + numImages);
            BufferedImage[] bi = new BufferedImage[numImages];
            for (int i = 0; i < numImages; i++) {
                BufferedImage frame = reader.read(i);
                /*
                BufferedImage resizedImage = new BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = resizedImage.createGraphics();
                g.setComposite(AlphaComposite.Src);
                // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                // g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.drawImage(frame, 0, 0, maxX, maxY, null);
                g.dispose();
                */
                bi[i] = frame;
                //logger.info("image[" + i + "] = " + image);
            }
            is.close();
            return bi;
        } catch (Exception e) {
            logger.error("Could not read resource '" + resourceName + "'", e);
            return null;
        }
    }
    
    public static BufferedImage[] getResizedImages(Controller controller, String resourceName, int width, int height) {
        // InputStream is = getController().getClass().getResourceAsStream("matrix-animations/" + resourceName);
        logger.info("Reading images from '" + resourceName + "'");
        try {
            InputStream is = controller.getResource(resourceName);
            ImageInputStream stream = ImageIO.createImageInputStream(is);
            Iterator readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                logger.error("no image reader found");
                return null;
            }
            ImageReader reader = (ImageReader) readers.next();
            reader.setInput(stream); // don't omit this line!
            int numImages = reader.getNumImages(true); // don't use false!
            logger.info("numImages = " + numImages);
            BufferedImage[] bi = new BufferedImage[numImages];
            for (int i = 0; i < numImages; i++) {
                BufferedImage frame = reader.read(i);
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = resizedImage.createGraphics();
                g.setComposite(AlphaComposite.Src);
                // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                // g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.drawImage(frame, 0, 0, width, height, null);
                g.dispose();
                
                bi[i] = resizedImage;
                //logger.info("image[" + i + "] = " + image);
            }
            is.close();
            return bi;
        } catch (Exception e) {
            logger.error("Could not read resource '" + resourceName + "'", e);
            return null;
        }
    }
    

	
	static {
		colorMap = ClassInspector.getConstantsMap(Color.class, "");
	}
	
	
}
