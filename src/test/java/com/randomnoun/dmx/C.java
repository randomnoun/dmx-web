package com.randomnoun.dmx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import jakarta.mail.MessagingException;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.randomnoun.common.log4j.Log4jCliConfiguration;

public class C {

	public static int LEDSIZE=5;
	public static int LEDSPACE=7;

	static public boolean cpl(byte[][] img, int xm, int ym) {
		for (int x=xm-LEDSIZE; x<xm+LEDSIZE; x++) {
			for (int y=ym-LEDSIZE; y<ym+LEDSIZE; y++) {
				if (img[x][y]!=1) { return false; }
			}
		}
		return true;
	}
	
	static int pc = 0;
	static int lxm = -1, lym = -1;
	static public void pl(JdbcTemplate jt, BufferedImage bi, byte[][] img, int xm, int ym) {
		if (lxm!=-1) {
			Graphics2D gd = bi.createGraphics();
			gd.setColor(new Color(128,128,128));
			gd.drawLine(lxm,  lym, xm, ym);
		}
		lxm = xm; lym = ym;
		for (int x=xm-LEDSIZE; x<xm+LEDSIZE; x++) {
			for (int y=ym-LEDSIZE; y<ym+LEDSIZE; y++) {
				bi.setRGB(x, y, 0);
			}
		}
		for (int x=xm-LEDSPACE; x<xm+LEDSPACE; x++) {
			for (int y=ym-LEDSPACE; y<ym+LEDSPACE; y++) {
				img[x][y]=3; // LED
			}
		}
		pc++;
		System.out.println("Placed LED " + pc + " at " + xm + ", " + ym);
		/*
		jt.update(
			"INSERT INTO fixture(fixtureDefId, name, dmxOffset, stageId, fixPanelType, fixPanelX, fixPanelY) " +
			" VALUES(?, ?, ?, ?, ?, ?, ?)",
			new Object[] { 15, "sign-" + pc, pc, 5, "M", xm-LEDSIZE, ym-LEDSIZE },
			new int[] { Types.NUMERIC, Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.NUMERIC,  Types.NUMERIC } );
			   */
	}
	
	
	/**
	 * @param args
	 * @throws MessagingException 
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws MessagingException, IOException, SQLException {
		
		Log4jCliConfiguration lcc = new Log4jCliConfiguration();
		Properties props = new Properties();
		lcc.init("[test]", props);

		System.out.println(File.separator);
		
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyyddMMHHmmssSSS000Z");
    	sdf.setTimeZone(TimeZone.getTimeZone("Australia/Brisbane"));
    	Date now = new Date();
    	System.out.println(sdf.format(now));
    	*/
		
		//BufferedImage bi = new BufferedImage();
		BufferedImage bi = ImageIO.read(new File("c:\\multi\\image\\dmx-web\\upscale-fix-back.png"));
		Graphics2D gd = bi.createGraphics();
		byte[][] pixels = new byte[817][168];
		//System.out.println(bi.getRGB(26,72));
		int x, y;
		for (x=0; x<817; x++) {
			for (y=0; y<168;y++) {
				int c = bi.getRGB(x, y);
				if (c==-1052673) { pixels[x][y] = 0; } // background 
				else if (c==-2304519) { pixels[x][y] = 1; } // inside letter
				else { pixels[x][y] = 2; } // letter border
			}
		}
		
		Connection con = DriverManager.getConnection("jdbc:mysql://filament/comedy_dev?zeroDateTimeBehavior=convertToNull&autoReconnect=true", "comedy_dev", "comedy_dev");
		DataSource ds = new SingleConnectionDataSource(con, true);
		JdbcTemplate jt = new JdbcTemplate(ds);
		
		// fill this thing with LEDs
		for (x=LEDSPACE; x<817-LEDSPACE; x++) {
			for (y=LEDSPACE; y<168-LEDSPACE; y++) {
				if (cpl(pixels,x,y)) { pl(jt, bi,pixels,x,y); };
			}
		}
		
			
		
		ImageIO.write(bi, "PNG", new File("c:\\multi\\image\\dmx-web\\upscale-fix-wireleds.png"));
		
		
		
    	
		// hopefully off the sbl by now
		/*
		EmailWrapper.emailTo("greg_knox@hotmail.com", 
				"emailwrapper-test@randomnoun.com", 
	    		"bnedev03", "test 5", "test 5 body", null, null);
*/
		/*
		EmailWrapper.emailTo("knoxg@randomnoun.com,greg_knox@hotmail.com,randomnoun@gmail.com", 
			"emailwrapper-test@randomnoun.com", 
    		"bnedev03", "test 3", "test 3 body", null, null);
	*/
	
		/*
		EmailWrapper.emailTo("knoxg+a@randomnoun.com", 
				"emailwrapper-test@randomnoun.com", 
	    		"bnedev03", "test 2", "test 2 body", null, null);

		EmailWrapper.emailTo("knoxg+b@randomnoun.com", 
				"emailwrapper-test@randomnoun.com", 
	    		"bnedev03", "test 2", "test 2 body", null, null);

		EmailWrapper.emailTo("knoxg+c@randomnoun.com", 
				"emailwrapper-test@randomnoun.com", 
	    		"bnedev03", "test 2", "test 2 body", null, null);
*/
	}

}
