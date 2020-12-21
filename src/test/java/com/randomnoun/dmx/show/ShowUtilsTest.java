package com.randomnoun.dmx.show;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.common.Struct;
import com.randomnoun.common.jexl.ExpressionUtil;
import com.randomnoun.common.jexl.ast.TopLevelExpression;
import com.randomnoun.common.jexl.eval.EvalContext;
import com.randomnoun.common.jexl.eval.EvalException;
import com.randomnoun.common.jexl.eval.EvalFunction;
import com.randomnoun.common.jexl.eval.Evaluator;
import com.randomnoun.common.jexl.parser.ExpressionParser;
import com.randomnoun.common.jexl.parser.ParseException;
import com.randomnoun.common.jexl.parser.TokenMgrError;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ShowUtilsTest extends TestCase {

	public Logger logger = Logger.getLogger(ShowUtilsTest.class);
	
	Map functions;
	public void setUp() {
		functions = new HashMap();
		functions.put("floor", new EvalFunction(){
			public Object evaluate(String functionName, EvalContext context, List arguments)
				throws EvalException {
				if (arguments.size() != 1) { throw new EvalException(functionName + "() must contain one parameter"); }
	            if (arguments.get(0) == null) { throw new EvalException(functionName + "() parameter cannot be null"); }
	            if (!(arguments.get(0) instanceof Number)) {
	                throw new EvalException(functionName + "() parameter must be a numeric type");
	            }
	            return Math.floor( ((Number)arguments.get(0)).doubleValue() );				
	        }});
		functions.put("iif", new EvalFunction(){
			public Object evaluate(String functionName, EvalContext context, List arguments)
				throws EvalException {
				if (arguments.size() != 3) { throw new EvalException(functionName + "() must contain three parameters"); }
	            if (arguments.get(0) == null) { throw new EvalException(functionName + "() parameter 1 cannot be null"); }
	            if (!(arguments.get(0) instanceof Boolean)) {
	                throw new EvalException(functionName + "() parameter must be a boolean type");
	            }
	            boolean b = ((Boolean)arguments.get(0)).booleanValue();
	            return b ? arguments.get(1) : arguments.get(2);
	        }});
		functions.put("fillDmxOffset", new EvalFunction(){
			public Object evaluate(String functionName, EvalContext context, List arguments)
				throws EvalException {
				if (arguments.size() != 4) { throw new EvalException(functionName + "() must contain four parameters"); }
				for (int i=0; i<3; i++) { if (arguments.get(i) == null) { throw new EvalException(functionName + "() parameter " + (i+1) + " cannot be null"); } }
				for (int i=0; i<3; i++) { if (!(arguments.get(i) instanceof Number)) { throw new EvalException(functionName + "() parameter " + (i+1) + " must be numeric"); } }
				int startUniverse = ((Number) arguments.get(0)).intValue();
				int startOffset = ((Number) arguments.get(1)).intValue();
				int numChannels = ((Number) arguments.get(2)).intValue();
				int n = ((Number) arguments.get(3)).intValue();
	            return ShowUtils.fillDmxOffset(startUniverse, startOffset, numChannels, n);
	        }});
		functions.put("fillDmxUniverse", new EvalFunction(){
			public Object evaluate(String functionName, EvalContext context, List arguments)
				throws EvalException {
				if (arguments.size() != 4) { throw new EvalException(functionName + "() must contain four parameters"); }
				for (int i=0; i<3; i++) { if (arguments.get(i) == null) { throw new EvalException(functionName + "() parameter " + (i+1) + " cannot be null"); } }
				for (int i=0; i<3; i++) { if (!(arguments.get(i) instanceof Number)) { throw new EvalException(functionName + "() parameter " + (i+1) + " must be numeric"); } }
				int startUniverse = ((Number) arguments.get(0)).intValue();
				int startOffset = ((Number) arguments.get(1)).intValue();
				int numChannels = ((Number) arguments.get(2)).intValue();
				int n = ((Number) arguments.get(3)).intValue();
	            return ShowUtils.fillDmxUniverse(startUniverse, startOffset, numChannels, n);
	        }});
	}

	// because I just think it's going to be easier if people can see these numbers change
	
	/** Return a Map of function names to function definitions for a grid
	 * of the specified size. The keys in the
	 * returned Map are:
	 *
	 *  <ul>
	  <li>loop-rd: Loop alternating left-to-right, then right-to-left, downwards
	  <li>loop-ld: Loop alternating right-to-left, then left-to-right, downwards<br/>
	  <li>loop-ru: Loop alternating left-to-right, then right-to-left, upwards<br/>
	  <li>loop-ld: Loop alternating right-to-left, then left-to-right, upwards<br/>
	  
	  <li>grid-lr-tb: Grid in rows, left to right, top to bottom
	  <li>grid-lr-bt: Grid in rows, left to right, bottom to top
	  <li>grid-rl-tb: Grid in rows, right to left, top to bottom
	  <li>grid-rl-bt: Grid in rows, right to left, bottom to top
	  </ul>
	  
		 */	
	public Map<String, String> getAllocations(int countX, int countY) {
		Map<String, String> allocations = new HashMap<String, String>();
		allocations.put("loop-rd", "y * " + countX + " + iif(y % 2 == 0, x, " + countX + " - 1 - x)");
		allocations.put("loop-ld", "y * " + countX + " + iif(y % 2 == 1, x, " + countX + " - 1 - x)");
		allocations.put("loop-ru", "(" + countY + " - 1 - y) * " + countX + " + iif(y % 2 == (" + countY + "-1) % 2, x, " + countX + " - 1 - x)");
		allocations.put("loop-lu", "(" + countY + " - 1 - y) * " + countX + " + iif(y % 2 == 1 - ((" + countY + "-1) % 2), x, " + countX + " - 1 - x)");
		
		allocations.put("grid-lr-tb", "y * " + countX + " + x");
		allocations.put("grid-lr-bt", "(" + countY + " - 1 - y) * " + countX + " + x");
		allocations.put("grid-rl-tb", "y * " + countX + " + " + countX + " - 1 - x");
		allocations.put("grid-rl-bt", "(" + countY + " - 1 - y) * " + countX + " + " + countX + " - 1 - x");
		
		return allocations;
	}

    private TopLevelExpression parseExpression(String expr) throws java.text.ParseException {
    	StringReader reader = new StringReader(expr);
        ExpressionParser parser = new ExpressionParser(reader);
        try {
        	TopLevelExpression expr2 = parser.TopLevelExpression();;
        	return expr2;
        } catch (ParseException pe) {
            throw new java.text.ParseException(pe.getMessage(), -1);
        } catch (TokenMgrError tme) {
            throw new java.text.ParseException(tme.getMessage(), -1);
        }
    }

    
	public int[][] getDmxOffsets(int startUniverse, int startOffset, int numChannels, int countX, int countY, String allocation) throws java.text.ParseException {
		String func = "fillDmxOffset(" + startUniverse + "," + startOffset + "," + numChannels + "," + allocation + ")";
		TopLevelExpression dmxOffsetExpr = parseExpression(func);
		int[][] result = new int[countX][countY];
		int n = 0;
		for (int x = 0; x<countX; x++) {
			for (int y = 0; y<countY; y++) {
				result[x][y] = evalLong(dmxOffsetExpr, functions, x, y, n).intValue();
				//System.out.println("x=" + x + "y=" + y + ", r=" + result[x][y]);
				n++;
			}
		}
		return result;
	}
	
	public void testFillDmxOffset3x4() throws java.text.ParseException {
		// 3 x 4 grid in each of the above
		Map<String, String> allocations = getAllocations(4, 3);
		System.out.println(Struct.structuredMapToString("allocations", allocations));
		int[][] t1 = getDmxOffsets(0, 0, 1, 4, 3, allocations.get("loop-rd"));
		assertEquals("[0,1,2,3][7,6,5,4][8,9,10,11]", testString(t1));
		
		int[][] t2 = getDmxOffsets(0, 0, 1, 4, 3, allocations.get("loop-ld"));
		assertEquals("[3,2,1,0][4,5,6,7][11,10,9,8]", testString(t2));

		int[][] t3 = getDmxOffsets(0, 0, 1, 4, 3, allocations.get("loop-ru"));
		assertEquals("[8,9,10,11][7,6,5,4][0,1,2,3]", testString(t3));

		int[][] t4 = getDmxOffsets(0, 0, 1, 4, 3, allocations.get("loop-lu"));
		assertEquals("[11,10,9,8][4,5,6,7][3,2,1,0]", testString(t4));

		int[][] t5 = getDmxOffsets(0, 0, 1, 4, 3, allocations.get("grid-lr-tb"));
		assertEquals("[0,1,2,3][4,5,6,7][8,9,10,11]", testString(t5));

		int[][] t6 = getDmxOffsets(0, 0, 1, 4, 3, allocations.get("grid-lr-bt"));
		assertEquals("[8,9,10,11][4,5,6,7][0,1,2,3]", testString(t6));
		
		int[][] t7 = getDmxOffsets(0, 0, 1, 4, 3, allocations.get("grid-rl-tb"));
		assertEquals("[3,2,1,0][7,6,5,4][11,10,9,8]", testString(t7));

		int[][] t8 = getDmxOffsets(0, 0, 1, 4, 3, allocations.get("grid-rl-bt"));
		assertEquals("[11,10,9,8][7,6,5,4][3,2,1,0]", testString(t8));
		
		
		//fail("Not yet implemented");
	}
	
	public void testFillDmxOffset4x4() throws java.text.ParseException {
		// 3 x 4 grid in each of the above
		Map<String, String> allocations = getAllocations(4, 4);
		System.out.println(Struct.structuredMapToString("allocations", allocations));
		int[][] t1 = getDmxOffsets(0, 50, 50, 4, 4, allocations.get("loop-rd"));
		assertEquals("[50,100,150,200][400,350,300,250][450,451,1,51][251,201,151,101]", testString(t1));
		
		int[][] t2 = getDmxOffsets(0, 50, 50, 4, 4, allocations.get("loop-ld"));
		assertEquals("[200,150,100,50][250,300,350,400][51,1,451,450][101,151,201,251]", testString(t2));

		int[][] t3 = getDmxOffsets(0, 50, 50, 4, 4, allocations.get("loop-ru"));
		assertEquals("[251,201,151,101][450,451,1,51][400,350,300,250][50,100,150,200]", testString(t3));

		int[][] t4 = getDmxOffsets(0, 50, 50, 4, 4, allocations.get("loop-lu"));
		assertEquals("[101,151,201,251][51,1,451,450][250,300,350,400][200,150,100,50]", testString(t4));

		int[][] t5 = getDmxOffsets(0, 50, 50, 4, 4, allocations.get("grid-lr-tb"));
		assertEquals("[50,100,150,200][250,300,350,400][450,451,1,51][101,151,201,251]", testString(t5));

		int[][] t6 = getDmxOffsets(0, 50, 50, 4, 4, allocations.get("grid-lr-bt"));
		assertEquals("[101,151,201,251][450,451,1,51][250,300,350,400][50,100,150,200]", testString(t6));
		
		int[][] t7 = getDmxOffsets(0, 50, 50, 4, 4, allocations.get("grid-rl-tb"));
		assertEquals("[200,150,100,50][400,350,300,250][51,1,451,450][251,201,151,101]", testString(t7));

		int[][] t8 = getDmxOffsets(0, 50, 50, 4, 4, allocations.get("grid-rl-bt"));
		assertEquals("[251,201,151,101][51,1,451,450][400,350,300,250][200,150,100,50]", testString(t8));
		
		
		//fail("Not yet implemented");
	}
	
	
	
	public String testString(int[][] r) {
		String s = "";
		for (int y=0; y<r[0].length; y++) {
			s+="[";
			for (int x=0; x<r.length; x++) {
				s+=r[x][y] + (x<r.length-1 ? "," : "");
			}
			s+="]";
		}
		return s;
	}
	
	public String toString(int[][] r) {
		String s = "";
		for (int y=0; y<r[0].length; y++) {
			s+="[";
			for (int x=0; x<r.length; x++) {
				s+=r[x][y] + (x<r.length-1 ? "," : "");
			}
			s+="]\n";
		}
		return s;
	}

	public void testFillDmxUniverse() {
		//fail("Not yet implemented");
	}
	
	
	private Long evalLong(TopLevelExpression expr, Map functions, long x, long y, long n) {
    	String exprString = null;
    	try {
    		
    		exprString = ExpressionUtil.expressionToString(expr);
    		logger.info("Evaluating '" + exprString + "' with x=" + x + ", y=" + y + ", n=" + n);
	        Evaluator evaluator = new Evaluator();
	        EvalContext evalContext = new EvalContext();
			evalContext.setVariable("x", new Long(x));
	        evalContext.setVariable("y", new Long(y));
	        evalContext.setVariable("n", new Long(n));
	        evalContext.setFunctions(functions);
	        
	        Object result = evaluator.visit(expr, evalContext);
	        logger.info("Evaluating '" + exprString + "' as " + result);
	        return ((Number)result).longValue();
    	} catch (Exception e) {
    		// TODO log or return an error string
    		logger.error("Exception evaluating '" + exprString + "'", e);
    		return null;
    	}
    }
	
	public static void testHsv2rgb() {
		int rgb[] = new int[3];
		ShowUtils.hsv2rgb(0.0f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 255, 0, 0 });
		ShowUtils.hsv2rgb(0.1f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 255, 153, 0 });
		ShowUtils.hsv2rgb(0.2f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 203, 255, 0 });
		ShowUtils.hsv2rgb(0.3f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 50, 255, 0 });
		ShowUtils.hsv2rgb(0.4f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 0, 255, 102 });
		ShowUtils.hsv2rgb(0.5f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 0, 255, 255 });
		ShowUtils.hsv2rgb(0.6f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 0, 101, 255 });
		ShowUtils.hsv2rgb(0.7f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 50, 0, 255 });
		ShowUtils.hsv2rgb(0.8f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 204, 0, 255 });
		ShowUtils.hsv2rgb(0.9f, 1.0f, 1.0f, rgb); assertArrayEquals(rgb, new int[] { 255, 0, 153 });
	}
	
	public static void testRgb2hsv() {
		int hsv[] = new int[3];
		ShowUtils.rgb2hsv(255, 0, 0, hsv); assertArrayEquals(hsv, new int[] { 0, 100, 100 }); 
		ShowUtils.rgb2hsv(225, 153, 0, hsv); assertArrayEquals(hsv, new int[] { 40, 88, 88 });
		ShowUtils.rgb2hsv(203, 255, 0, hsv); assertArrayEquals(hsv, new int[] { 72, 100, 100 });
		ShowUtils.rgb2hsv(50, 255, 0, hsv); assertArrayEquals(hsv, new int[] { 108, 100, 100 });
		ShowUtils.rgb2hsv(0, 255, 102, hsv); assertArrayEquals(hsv, new int[] { 144, 100, 100 });
		ShowUtils.rgb2hsv(0, 255, 255, hsv); assertArrayEquals(hsv, new int[] { 180, 100, 100 });
		ShowUtils.rgb2hsv(0, 101, 255, hsv); assertArrayEquals(hsv, new int[] { 216, 100, 100 });
		ShowUtils.rgb2hsv(50, 0, 255, hsv); assertArrayEquals(hsv, new int[] { 251, 100, 100 });
		ShowUtils.rgb2hsv(204, 0, 153, hsv); assertArrayEquals(hsv, new int[] { 315, 80, 80 });
		ShowUtils.rgb2hsv(0, 0, 100, hsv); assertArrayEquals(hsv, new int[] { 240, 39, 39 });
	}
	
	// is in junit 4
	public static void assertArrayEquals(int[] result, int[] expected) {
		if (expected.length!=result.length) { fail("arrays wrong length (expected " + expected.length + "; found " + result.length + ")"); }
		for (int i=0; i<expected.length; i++) {
			if (result[i]!=expected[i]) { 
				fail("array mismatch index " + i + " (expected " + expected[i] + "; found " + result[i] + ")"); 
			}
		}
	}
	
	
}
