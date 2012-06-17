package com.randomnoun.dmx.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.randomnoun.common.Struct;

/** Various databasey things */
public class DatabaseUtil {
	
	/** convert a text file of SQL statements into a List of individual
	 * statements. Comments may also be returned in the result.
	 * Comments that occur within a statement are returned before that statement
	 * has finished parsing.
	 * 
	 * <p>NB: Does not handle escape sequences found within double or single quotes
	 * 
	 * @param includeComments include comment strings in result
	 * 
	 * @throws IOException 
	 * @throws ParseException unclosed /*-style comment or single/double-quoted string
	 */
	public static List<String> parseStatements(InputStream is, boolean includeComments) 
		throws IOException, ParseException 
	{
		// @TODO some error handling
		List allSql = new ArrayList<String>();
		
		int state = 0; // command parsing [to states 0, 1, 2, 4, 7]
		// 1 // parsed double quote, in double quotes [to states 0, 1]
		// 2 // parsed - [to states 0, 3]
		// 3 // parsed -- [ to states 0, 3]
		// 4 // parsed /  [ to states 0, 5]
		// 5 // parsed /* [ to states 6, 5]
		// 6 // parsed * from state 5  [ to states 0, 5]
		// 7 // parse single quote, in single quotes [to states 0, 1]

		String s = ""; // current statement
		String c = ""; // current comment
		int intch = is.read();
		while (intch!=-1) {
			char ch = (char) intch;
			
			if (state==0) {
				switch(ch) {
					case '"' : state = 1; s = s + ch; break;
					case '-' : state = 2; break;
					case '/' : state = 4; break;
					case '\'' : state = 7; s = s + ch; break;
					case ';' : allSql.add(s.trim() + ";"); s=""; break;
					default: s = s + ch;
				}
			} else if (state==1) {
				switch(ch) {
					case '"' : state = 0; s = s + ch; break;
					default: s = s + ch;
				}
			} else if (state==2) {
				switch(ch) {
					case '-' : state = 3; break;
					default: state = 0; s = s + "-" + ch;
				}
			} else if (state==3) {
				switch(ch) {
					case '\r' : 
					case '\n' : 
						state = 0; 
						if (includeComments) { allSql.add("-- " + c.trim()); } 
						c=""; break;
					default :
						c = c + ch;
				}
			} else if (state==4) {
				switch(ch) {
					case '*' : state = 5; break;
					default: state = 0; s = s + "/" + ch;
				}
			} else if (state==5) {
				switch(ch) {
					case '*' : state = 6; break;
					default: c = c + ch;
				}
			} else if (state==6) {
				switch(ch) {
					case '/' : 
						state = 0; 
						if (includeComments) { allSql.add("/* " + c.trim() + " */"); } 
						c=""; break;
					default: c = c + "*" + ch;
				}
			} else if (state==7) {
				switch(ch) {
					case '\'' : state = 0; s = s + ch; break;
					default: s = s + ch;
				}
			}
			
			intch = is.read();
		}
		
		if (state==5) {
			// unclosed /*-style comment
			// ignore for the time being
			throw new ParseException("Unclosed /*-style comment before EOF", -1);
		} else if (state==1) {
			// unclosed quoted string
			// ignore for the time being
			throw new ParseException("Unclosed double quoted string before EOF", -1);
		} else if (state==7) {
			// unclosed quoted string
			// ignore for the time being
			throw new ParseException("Unclosed single quoted string before EOF", -1);
		} else if (state==0) {
			if (!s.trim().equals("")) {
				// unterminated statement at end of InputStream; add to list
				allSql.add(s.trim() + ";");
			}
		}
		
		return allSql;
	}
	
	public static void main(String args[]) throws IOException, ParseException {
		String s1 = "statement1; statement2; statement3; /* a comment */ statement /* inside */ 4;";
		System.out.println(Struct.structuredListToString("result",parseStatements(new ByteArrayInputStream(s1.getBytes()),true)));
		System.out.println(Struct.structuredListToString("result",parseStatements(new ByteArrayInputStream(s1.getBytes()),false)));
		
		String s2 = "--things\n" +
			"a multi-line -- more things\n" +
			"statement with -- yet more things\n" +
			"eol comments \"-- not this one /* or this */ of course--\" embedded within -- things?\n" +
			"it;";
		System.out.println(Struct.structuredListToString("result",parseStatements(new ByteArrayInputStream(s2.getBytes()),true)));
		System.out.println(Struct.structuredListToString("result",parseStatements(new ByteArrayInputStream(s2.getBytes()),false)));

		s2 = "--things\n" +
			"a multi-line -- more things\n" +
			"statement with -- yet more things\n" +
			"eol comments '-- not this one /* or this */ of course--' embedded within -- things?\n" +
			"it;";
		System.out.println(Struct.structuredListToString("result",parseStatements(new ByteArrayInputStream(s2.getBytes()),true)));
		System.out.println(Struct.structuredListToString("result",parseStatements(new ByteArrayInputStream(s2.getBytes()),false)));

		String s3 = "a statement; followed by a statement not terminated with a semi-colon";
		System.out.println(Struct.structuredListToString("result",parseStatements(new ByteArrayInputStream(s3.getBytes()),true)));
		
		try {
			String s4 = "a statement; followed by a statement with an unclosed \"quoted string";
			System.out.println(Struct.structuredListToString("result",parseStatements(new ByteArrayInputStream(s4.getBytes()),true)));
		} catch (ParseException pe) {
			// fine
		}

		try {
			String s5 = "a statement; followed by a statement with an unclosed /* comment";
			System.out.println(Struct.structuredListToString("result",parseStatements(new ByteArrayInputStream(s5.getBytes()),true)));
		} catch (ParseException pe) {
			// fine
		}

	}
	

}
