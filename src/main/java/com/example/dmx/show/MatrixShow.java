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
import com.randomnoun.dmx.show.PropertyDef;
import com.randomnoun.dmx.show.Show;

/** Generate a matrix animation from an animated GIF
 * 
 * @author knoxg
 */
public class MatrixShow extends Show {

    Logger logger = Logger.getLogger(MatrixShow.class);

    int maxX=0, maxY=0;
    FixtureController[][] matrix;
    AudioController audioController;
    BufferedImage[] anim;
    
    public MatrixShow(long id, Controller controller, Properties properties) {
        super(id, controller, "Matrix", Long.MAX_VALUE, properties);
      
        String fixtureTemplate = properties.getProperty("fixtureTemplate");
        String animation = properties.getProperty("animation");
      
      if (fixtureTemplate==null) { fixtureTemplate = "matrix{x}-{y}"; }
      if (animation==null) { animation  = "ani1.gif"; }
      
              logger.info("Initialising MatrixShow with fixtureTemplate='" + fixtureTemplate + "', animation='" + animation + "'");
      
        matrix = getMatrixArray(fixtureTemplate);
        maxX = matrix.length;
        maxY = matrix[0].length;
        
        anim = getImages(animation);
      if (anim!=null) {
        logger.info("Read animation with " + anim.length + " frames");
      } else {
        logger.info("Error loading animation");
      }
      
    }
  
    public List getDefaultProperties() {
      List properties = new ArrayList();
      properties.add(new PropertyDef("fixtureTemplate", "Fixture template", "matrix{x}-{y}"));
      properties.add(new PropertyDef("animation", "Animation", "ani1.gif"));
      return properties;
    }  
    
    public FixtureController[][] getMatrixArray(String fixtureNameTemplate) {
        // @TODO: detect {y} before {x}
        String fixtureNameRegex = Text.replaceString(fixtureNameTemplate, "{x}", "(.*)");
        fixtureNameRegex = Text.replaceString(fixtureNameRegex, "{y}", "(.*)");
        Controller c = getController();
        Pattern p = Pattern.compile(fixtureNameRegex);
        int maxX=0, maxY=0;
        for (Fixture f : c.getFixtures()) {
            Matcher m = p.matcher(f.getName());
            if (m.matches()) {
                maxX = Math.max(maxX, Integer.parseInt(m.group(1)));
                maxY = Math.max(maxY, Integer.parseInt(m.group(2)));
            }
        }
        logger.debug("getMatrixArray[][]; found matrix " + fixtureNameTemplate + " with maxX=" + maxX + ", maxY=" + maxY);
        FixtureController[][] fc = new FixtureController[maxX][maxY];
        for (int x=0; x<maxX; x++) {
            for (int y=0; y<maxY; y++) {
                String name = Text.replaceString(fixtureNameTemplate, "{x}", String.valueOf(x+1));
                name = Text.replaceString(name, "{y}", String.valueOf(y+1));
                fc[x][y] = c.getFixtureControllerByNameNoEx(name);
                if (fc[x][y]==null) { 
                    logger.warn("getMatrixArray[][]; missing fixture '" + name + "' in matrix");
                }
            }
        }
        return fc;
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
        for (int x=0; x<maxX; x++) {
            for (int y=0; y<maxY; y++) {
                matrix[x][y].blackOut();
            }
        }
    }
    
    public void play() {
        logger.debug("play()");
        reset();
        int frameNum = 0;
        while (!isCancelled()) {
            BufferedImage bi = anim[frameNum];
            for (int x=0; x<maxX; x++) {
                for (int y=0; y<maxY; y++) {
                    Color c = new Color(bi.getRGB(x, y));
                    matrix[x][y].setColor(c);
                }
            }
            frameNum = (frameNum+1) % anim.length;
            waitFor(100);
        }
        logger.debug("play() completed");
    }

}    
