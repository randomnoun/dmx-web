package com.example.dmx.show;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

import com.randomnoun.common.Text;
import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.show.Show;
import com.randomnoun.dmx.PropertyDef;

/** Light up a sign in one of two ways:
 * 
 * 1) lighting each letter individually from L to R, then flashing them on & off
 * 2) equalizer-looking thing (horiz fill each letters from a random Y position down)
 * 
 * @author knoxg
 */
public class LetterShow extends Show {

    Logger logger = Logger.getLogger(LetterShow.class);

    int maxX=0, maxY=0;
    FixtureController[][] letters;  // [letterIdx][ledIdx]
    AudioController audioController;
    BufferedImage[] anim;
    int showTypeId = 0;
    
    public LetterShow(long id, Controller controller, Properties properties) {
        super(id, controller, "Letter", Long.MAX_VALUE, properties);
      
        String fixtureTemplate = properties.getProperty("fixtureTemplate");
        String letterGapPositions = properties.getProperty("letterGapPositions");
        String showType = properties.getProperty("showType");
        //String animation = properties.getProperty("animation");
      
        if (fixtureTemplate==null) { fixtureTemplate = "sign-{n}"; }
        if (letterGapPositions==null) { letterGapPositions = "1,2,3"; }
        if (showType==null) { showType = "0"; } // @TODO make this text
        //if (animation==null) { animation  = "ani1.gif"; }
      
        logger.info("Initialising LetterShow with fixtureTemplate='" + fixtureTemplate + "', letterGapPositions='" + letterGapPositions + "'");
      
        letters = getLetterArray(fixtureTemplate, letterGapPositions);
        showTypeId = Integer.parseInt(showType);
        
        // anim = getImages(animation);
      //if (anim!=null) {
      //  logger.info("Read animation with " + anim.length + " frames");
      //} else {
      //  logger.info("Error loading animation");
      //}
      
    }
  
    public List getDefaultProperties() {
      List properties = new ArrayList();
      properties.add(new PropertyDef("fixtureTemplate", "Fixture template", "sign-{n}"));
      properties.add(new PropertyDef("letterGapPositions", "Letter gap locations (Fixture panel X value)", "1,2,3,4"));
      properties.add(new PropertyDef("showType", "Show ID", "0"));
      //properties.add(new PropertyDef("animation", "Animation", "ani1.gif"));
      return properties;
    }  
    
    public FixtureController[][] getLetterArray(String fixtureNameTemplate, String letterGapPositionCsv) {
    	try {
	        String fixtureNameRegex = Text.replaceString(fixtureNameTemplate, "{n}", "(.*)");
	        List<String> letterGapPositionStrings = Text.parseCsv(letterGapPositionCsv);
	        int[] letterGapPosition = new int[letterGapPositionStrings.size()];
	        List<List<FixtureController>> letters = new ArrayList<List<FixtureController>>();
	        for (int i=0; i<letterGapPosition.length; i++) {
	        	letterGapPosition[i] = Integer.parseInt(letterGapPositionStrings.get(i));
	        	letters.add(new ArrayList<FixtureController>());
	        }
	        letters.add(new ArrayList<FixtureController>()); // last letter is after last gap
	        
	        Controller c = getController();
	        Pattern p = Pattern.compile(fixtureNameRegex);
	        int fixCount=0;
	        for (Fixture f : c.getFixtures()) {
	            Matcher m = p.matcher(f.getName());
	            if (m.matches()) {
	            	long fixPanelX = f.getFixPanelPosition()[0];
	            	int letterNum = letterGapPosition.length;
	            	for (int i=0; i<letterGapPosition.length; i++) {
	            		if (fixPanelX<letterGapPosition[i]) {
	            			letterNum = i; break;
	            		}
	            	}
	            	letters.get(letterNum).add(f.getFixtureController());
	            	fixCount++;
	            }
	        }
	        
	        logger.debug("getLetterArray[][]; found letters " + fixtureNameTemplate + " with " + fixCount + " fixtures");
	        
	        FixtureController[][] fc = new FixtureController[letterGapPosition.length+1][];
	        for (int i=0; i<letterGapPosition.length+1; i++) {
	        	fc[i] = new FixtureController[letters.get(i).size()];
	        	for (int j=0; j<letters.get(i).size(); j++) {
	        		fc[i][j]=letters.get(i).get(j);
	        	}
	        }
	        return fc;
	        
    	} catch (Exception e) {
    		logger.error("Error determining letter shapes", e);
    		return new FixtureController[0][0];
    		    		
    	}
        
    }
    
    public BufferedImage[] getImages(String resourceName) {
        // InputStream is = getController().getClass().getResourceAsStream("matrix-animations/" + resourceName);
        try {

            File f = new File("C:/data/tomcat/eclipse-embedded/dmx-web/matrix-animations/" + resourceName);
            InputStream is = new FileInputStream("C:/data/tomcat/eclipse-embedded/dmx-web/matrix-animations/" + resourceName);
            if (is==null) {
                logger.error("No resource found at 'C:/data/tomcat/eclipse-embedded/dmx-web/matrix-animations/matrix-animations/" + resourceName + "'");
                return null;
            }

            ImageInputStream stream = ImageIO.createImageInputStream(f);
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
                
                bi[i] = resizedImage;
                //System.out.println("image[" + i + "] = " + image);
            }
            is.close();
            return bi;
        } catch (Exception e) {
            logger.error("Could not read resource 'C:/data/tomcat/eclipse-embedded/dmx-web/matrix-animations/matrix-animations/" + resourceName + "'", e);
            return null;
        }
    }
    
    public void pause() {}
    public void stop() {}
    
    protected void reset() {
        super.reset();
        logger.debug("reset()");
        for (int c=0; c<letters.length; c++) {
            for (int f=0; f<letters[c].length; f++) {
                letters[c][f].blackOut();
            }
        }
    }
    
    public void play() {
        logger.debug("play()");
        reset();
        int frameNum = 0;

		long minY = Long.MAX_VALUE, maxY = 0;
		for (int c=0; c<letters.length; c++) {
			for (int f=0; f<letters[c].length; f++) {
				Long[] pos = letters[c][f].getFixture().getFixPanelPosition();
				maxY = Math.max(maxY, pos[1]);
				minY = Math.min(minY, pos[1]);
			}
		}

        
        while (!isCancelled()) {
        	if (showTypeId==0) {
	        	// light them up one by one
	        	for (int c=0; c<letters.length; c++) {
	        		for (int f=0; f<letters[c].length; f++) {
	        			letters[c][f].setColor(Color.WHITE);
	                }
	        		waitFor(500);
	        		if (isCancelled()) { return; }
	            }
	        	// flash them off and on a couple of times
	        	for (int i=0; i<4; i++) {
	            	for (int c=0; c<letters.length; c++) {
	            		for (int f=0; f<letters[c].length; f++) {
	            			letters[c][f].setColor(i%2==0 ? Color.BLACK : Color.WHITE);
	                    }
	                }
	        		waitFor(500);
	        		if (isCancelled()) { return; }
	        	}
	        	// turn them all off
	        	for (int c=0; c<letters.length; c++) {
		    		for (int f=0; f<letters[c].length; f++) {
		    			letters[c][f].setColor(Color.BLACK);
		            }
	        	}
	    		waitFor(500);
        	
        	
        	} else if (showTypeId==1) {
        		// @TODO use audioSource data for this
        		 for (int c=0; c<letters.length; c++) {
                     double r = Math.random();
                     long fillY = (long) (r*(maxY-minY)+minY);
                     for (int f=0; f<letters[c].length; f++) {
                       long fixY = letters[c][f].getFixture().getFixPanelPosition()[1];
                       double v = (fixY-minY) / (double) maxY;
                       Color color = (v < 0.2 ? Color.RED :
                       (v < 0.4 ? Color.YELLOW :
                       Color.GREEN ));                
                       letters[c][f].setColor(fixY>fillY ? color : Color.BLACK);
                         }
                     if (isCancelled()) { return; }
                     }
                   waitFor(100);
        	
        	}
        	
        }
        logger.debug("play() completed");
    }

}    
