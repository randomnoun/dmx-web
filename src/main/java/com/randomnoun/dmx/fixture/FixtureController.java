package com.randomnoun.dmx.fixture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.randomnoun.common.ExceptionUtils;
import com.randomnoun.dmx.channel.BitResolution;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channel.dimmer.BlueDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.GreenDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.MasterDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.RedDimmerChannelDef;
import com.randomnoun.dmx.channel.rotation.PanPositionChannelDef;
import com.randomnoun.dmx.channel.rotation.TiltPositionChannelDef;

/** An object which can be used to control a fixture using
 * relatively simple commands
 * 
 * @return
 */
public abstract class FixtureController {

	protected Fixture fixture;
	protected List<CustomControl> customControls = null;
	
	public FixtureController(Fixture fixture) {
		this.fixture = fixture;
	}
	
	/** Sets the color of this fixture.
	 * 
	 * By default, this sets the red, green and blue dimmers to the values for this color,
	 * and if a master dimmer is available for the fixture, it is set to 100%. 
	 * 
	 * @param color The color to set the light to.
	 */
	public void setColor(Color color) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		DimmerChannelDef redDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(RedDimmerChannelDef.class);
		DimmerChannelDef greenDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(GreenDimmerChannelDef.class);
		DimmerChannelDef blueDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(BlueDimmerChannelDef.class);
		DimmerChannelDef masterDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(MasterDimmerChannelDef.class);
		
		if (redDimmerChannelDef==null || 
			greenDimmerChannelDef==null ||
			blueDimmerChannelDef==null) 
		{
			throw new UnsupportedOperationException("Default setColor implementation requires red, green and blue dimmers for this fixture");
		}
		fixture.setDmxChannelValue(redDimmerChannelDef.getOffset(), color.getRed());
		fixture.setDmxChannelValue(greenDimmerChannelDef.getOffset(), color.getGreen());
		fixture.setDmxChannelValue(blueDimmerChannelDef.getOffset(), color.getBlue());
		
		if (masterDimmerChannelDef==null) {
			// look for a master dimmer in a macro channel definition
			// hmm... going to put this into a FixtureController subclass rather than 
			// typing the Macros within a MacroChannelDef
			/*
			for (ChannelDef channelDef : fixtureDef.getChannelDefs()) {
				if (channelDef instanceof MacroChannelDef) {
					MacroChannelDef mcd = (MacroChannelDef) channelDef;
					for (Macro macro : mcd.getMacros()) {
						if (macro.)
					}
				}
			}
			*/
		} else {
			fixture.setDmxChannelValue(masterDimmerChannelDef.getOffset(), 255);
		}
	}

	/** Sets the pan position of this fixture.
	 * 
	 * By default, this sets the major pan position to the 
	 * closest setting to the desired angle. 
	 * 
	 * @param pan The pan position, in degrees.
	 */
	public void panTo(double panPosition) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		PanPositionChannelDef pcdHigh=null, pcdLow=null;
		for (ChannelDef cd : fixtureDef.getChannelDefs()) {
			if (cd instanceof PanPositionChannelDef) {
				BitResolution br = ((PanPositionChannelDef)cd).getBitResolution(); 
				if (br==BitResolution.BYTE || br==BitResolution.WORDHIGH) {
					pcdHigh = (PanPositionChannelDef)cd;
				} else if (br==BitResolution.WORDLOW){
					pcdLow = (PanPositionChannelDef)cd;
				}
				if (pcdHigh!=null && pcdLow!=null) { break; }
			}
		}
		// @TODO sanity checks
		if (pcdHigh==null) {
			throw new UnsupportedOperationException("Default setPan implementation requires pan position channel definition for this fixture");
		}
		if (panPosition < pcdHigh.getMinAngle()) { throw new IllegalArgumentException("panPosition must be equal or greater than " + pcdHigh.getMinAngle()); }
		if (panPosition > pcdHigh.getMaxAngle()) { throw new IllegalArgumentException("panPosition must be equal or less than " + pcdHigh.getMaxAngle()); }
		if (pcdLow!=null) {
			int value = (int) ((panPosition - pcdHigh.getMinAngle()) *
					(65535) / (pcdHigh.getMaxAngle() - pcdHigh.getMinAngle()));
			fixture.setDmxChannelValue(pcdHigh.getOffset(), value >> 8);
			fixture.setDmxChannelValue(pcdLow.getOffset(), value & 255);
		} else {
			fixture.setDmxChannelValue(pcdHigh.getOffset(),
				pcdHigh.getLowDmxValue() + 
				(int) ((panPosition - pcdHigh.getMinAngle()) *
				(pcdHigh.getHighDmxValue() - pcdHigh.getLowDmxValue()) /
				(pcdHigh.getMaxAngle() - pcdHigh.getMinAngle())));
		}
	}
	

	
	/** Sets the tilt position of this fixture.
	 * 
	 * By default, this sets the major tilt position to the 
	 * closest setting to the desired angle. 
	 * 
	 * @param tiltPosition The tilt position, in degrees.
	 */
	public void tiltTo(double tiltPosition) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		TiltPositionChannelDef pcdHigh=null, pcdLow=null;
		for (ChannelDef cd : fixtureDef.getChannelDefs()) {
			if (cd instanceof TiltPositionChannelDef) {
				BitResolution br = ((TiltPositionChannelDef)cd).getBitResolution(); 
				if (br==BitResolution.BYTE || br==BitResolution.WORDHIGH) {
					pcdHigh = (TiltPositionChannelDef)cd;
				} else if (br==BitResolution.WORDLOW){
					pcdLow = (TiltPositionChannelDef)cd;
				}
				if (pcdHigh!=null && pcdLow!=null) { break; }
			}
		}
		// @TODO sanity checks
		if (pcdHigh==null) {
			throw new UnsupportedOperationException("Default setTilt implementation requires tilt position channel definition for this fixture");
		}
		if (tiltPosition < pcdHigh.getMinAngle()) { throw new IllegalArgumentException("tiltPosition must be equal or greater than " + pcdHigh.getMinAngle()); }
		if (tiltPosition > pcdHigh.getMaxAngle()) { throw new IllegalArgumentException("tiltPosition must be equal or less than " + pcdHigh.getMaxAngle()); }
		if (pcdLow!=null) {
			int value = (int) ((tiltPosition - pcdHigh.getMinAngle()) *
					(65535) / (pcdHigh.getMaxAngle() - pcdHigh.getMinAngle()));
			fixture.setDmxChannelValue(pcdHigh.getOffset(), value >> 8);
			fixture.setDmxChannelValue(pcdLow.getOffset(), value & 255);
		} else {
			fixture.setDmxChannelValue(pcdHigh.getOffset(),
				pcdHigh.getLowDmxValue() + 
				(int) ((tiltPosition - pcdHigh.getMinAngle()) *
				(pcdHigh.getHighDmxValue() - pcdHigh.getLowDmxValue()) /
				(pcdHigh.getMaxAngle() - pcdHigh.getMinAngle())));
		}
	}
	
	/** Use this fixture's pan and tilt controls to aim it at a specific point.
	 * For this to work, the fixture's x, y, z, (initial) lookingAtX, lookingAtY, lookingAtZ,
	 * and upX, upY and upZ must be set.
	 * 
	 * @param pointAtX X co-ordinate to point at
	 * @param pointAtY Y co-ordinate to point at
	 * @param pointAtZ Z co-ordinate to point at
	 */
	public void pointAt(float pointAtX, float pointAtY, float pointAtZ) {
		// lets assume that the normal vector has length 1 and the distance from 
		// fixture position to fixture lookingAt has length of one.
		// @TODO do this.
		
		// a 2D plane with the points (x,y,z) with normal vector upX, upY, upZ
		//
		// upX*(x-initialX) + upY*(y-initialY) + upZ*(z-initialZ) = 0
		//
		// (lookingAtX, lookingAtY, lookingAtZ) are validated to lie on this plane
		// in the MaintainFixtureAction
		
		// from the gear at http://www.math.umn.edu/~nykamp/m2374/readings/planedist/#thecvt
		// which I have long since forgotten,
		
		// distance d to plane from pointAt is
		//   Math.abs(upX*(pointAtX-initialX) + upY*(pointAtY-initialY) + upZ*(pointAtZ-initialZ)) 
		// / Math.sqrt(upX^2 + upY^2 + upZ^2)
		
		FixtureDef fixtureDef = fixture.getFixtureDef();		
		double d = (fixture.upX*(pointAtX-fixture.x) + fixture.upY*(pointAtY-fixture.y) + fixture.upZ*(pointAtZ-fixture.z))
		 / Math.sqrt(Math.pow(fixture.upX,2) + Math.pow(fixture.upY, 2) + Math.pow(fixture.upZ, 2));
		double projectX = pointAtX + d*fixture.upX;
		double projectY = pointAtY + d*fixture.upY;
		double projectZ = pointAtZ - d*fixture.upZ;  // hmm.... maybe negate fixture.upZ above ?
		
		
		// pan is the angle between 
		//     (initialX,initialY,initialZ)-(lookingAtX,lookingAtY,lookingAtZ)
		// and (initialX,initialY,initialZ)-(projectX,projectY,projectZ)
		// as determined on this 2D plane
		
		// for the benefit of my brain which has been out of highschool for too long:
		// for two vectors A and B, the dot product A.B is:
		//   A.B = a[x]*b[x] + a[y]*b[y] + a[z]*c[z];
		// for vector A, magnitude of vector is:
		//   ||A||  = sqrt(a[x]^2 + a[y]^2 + a[z]^2 )
		// for points A & B, distance between two points are:
		//   |AB| = sqrt((b[x] - a[x])^2  + (b[y] - a[y])^2  + (b[z] - a[z])^2 )
		// and the angle between two vectors are
		//       A.B = ||A||.||B||.cos(t)
		//   ==> cos(t) = A.B / ||A||.||B||
		
		// so to determine pan, need to project pointAt onto the plane via the normal:
		
		//   a[x]=(lookingAtX-initialX), a[y]=(lookingAtY-initialY), a[z]=(lookingAtZ-initialZ)
		//   b[x]=(projectX-initialX), b[y]=(projectY-initialY), b[z]=(projectZ-initialZ)
		double cosT = ((fixture.lookingAtX-fixture.x) * (projectX-fixture.x) +
			(fixture.lookingAtY-fixture.y) * (projectY-fixture.y) +
			(fixture.lookingAtZ-fixture.z) * (projectZ-fixture.z)) 
			/
			(Math.sqrt(Math.pow(fixture.lookingAtX-fixture.x, 2) + Math.pow(fixture.lookingAtY-fixture.y, 2) + Math.pow(fixture.lookingAtZ-fixture.z, 2)) *
			 Math.sqrt(Math.pow(projectX-fixture.x, 2) + Math.pow(projectY-fixture.y, 2) + Math.pow(projectZ-fixture.z, 2)) );
		double pan = Math.acos(cosT); // will be in range 0-pi
		pan = pan * 180 / Math.PI;    // 0-180  @TODO full 360 at some point
		
		// to determine whether pan should be negative we should take a plane
		// defined by 3 points:
		//   (x1,y1,z1)==(initialX, initialY, initialZ), 
		//   (x2,y2,z2)==(initialX + upX, initialY + upY, initialZ + upZ), and
		//   (x3,y3,z3)==(lookingAtX, lookingAtY, lookingAtZ)
		// and see which side of this plane the pointAt point is.
		// Note this plane never changes; we could precompute it for each fixture.
		// from http://local.wasp.uwa.edu.au/~pbourke/geometry/planeeq/
		// this plane has the equation Ax + By + Cz + D = 0
		// with the values
		//   A = y1 (z2 - z3) + y2 (z3 - z1) + y3 (z1 - z2)
		//   B = z1 (x2 - x3) + z2 (x3 - x1) + z3 (x1 - x2)
		//   C = x1 (y2 - y3) + x2 (y3 - y1) + x3 (y1 - y2)
		// - D = x1 (y2 z3 - y3 z2) + x2 (y3 z1 - y1 z3) + x3 (y1 z2 - y2 z1)
		//
		// The sign of s = Ax + By + Cz + D determines which side the point (x,y,z) 
		// lies with respect to the plane. If s > 0 then the point lies on the same 
		// side as the normal (A,B,C).
		
		double x2 = (fixture.x + fixture.upX); 
		double y2 = (fixture.y + fixture.upY);
		double z2 = (fixture.z + fixture.upZ);
		double A = fixture.y * (z2 - fixture.lookingAtZ) + y2 * (fixture.lookingAtZ - fixture.z) + fixture.lookingAtY * (fixture.z - z2);
		double B = fixture.z * (x2 - fixture.lookingAtX) + z2 * (fixture.lookingAtX - fixture.x) + fixture.lookingAtZ * (fixture.x - x2);
		double C = fixture.x * (y2 - fixture.lookingAtY) + x2 * (fixture.lookingAtY - fixture.y) + fixture.lookingAtX * (fixture.y - y2);
		double D = -(fixture.x  * ((y2 * fixture.lookingAtZ) - (fixture.lookingAtY * z2)) +
		             x2         * ((fixture.lookingAtY * fixture.z) - (fixture.y * fixture.lookingAtZ)) +
		             fixture.lookingAtX * ((fixture.y * z2) - (y2 * fixture.z)));
		double s = A * pointAtX + B * pointAtY + C * pointAtZ + D;
		// in this co-ordinate system, +ve pan == clockwise looking from the fixture UP the up vector 
		// (could configure this in the fixtureDef)
		if (s > 0) { pan = -pan; }
		if (fixtureDef.invertPan) { pan = -pan; }
		if (pan < 0) { pan += 360; }
		if (fixtureDef.pointAtPanStart!=0 || fixtureDef.pointAtPanEnd!=0) {
			if (pan > fixtureDef.pointAtPanEnd) { pan=pan-360; }
			if (pan < fixtureDef.pointAtPanStart) { pan=pan+360; }
		}
		// @TODO for fixtures with pan capability > 360degrees, then could choose a 
		// 'preferred' range for pointAt operations
		
		
		// and tilt should be angle between
		//     (initialX,initialY,initialZ)-(pointAtX,pointAtY,pointAtZ)
		// and (initialX,initialY,initialZ)-(projectX,projectY,projectZ)
		// as determined on this 2D plane
		cosT = ((pointAtX-fixture.x) * (projectX-fixture.x) +
			(pointAtY-fixture.y) * (projectY-fixture.y) +
			(pointAtZ-fixture.z) * (projectZ-fixture.z)) 
			/
			(Math.sqrt(Math.pow(pointAtX-fixture.x, 2) + Math.pow(pointAtY-fixture.y, 2) + Math.pow(pointAtZ-fixture.z, 2)) *
			 Math.sqrt(Math.pow(projectX-fixture.x, 2) + Math.pow(projectY-fixture.y, 2) + Math.pow(projectZ-fixture.z, 2)) );
		double tilt = Math.acos(cosT);
		tilt = tilt * 180 / Math.PI;    // 0-180  @TODO full 360 at some point
		if (fixtureDef.invertTilt) { tilt = -tilt; }
		if (tilt < 0) { tilt = tilt + 360; }
		if (fixtureDef.pointAtTiltStart!=0 || fixtureDef.pointAtTiltEnd!=0) {
			if (pan > fixtureDef.pointAtTiltEnd) { tilt=tilt-360; }
			if (pan < fixtureDef.pointAtTiltStart) { tilt=tilt+360; }
		}

		// should probably do the same calculations as above for tilt
		// using the ground plane as a reference, but I Just Don't Care at the moment.
		// (I'm assuming we're always tilting up)
		
		// TODO (perhaps): if target pan or tilt exceed fixture ranges, then just go to the 
		// limits of the fixture rather than throwing an Exception
		
		// the chances of this working are zero.
		panTo(pan);
		tiltTo(tilt);
		
		// @TODO check for pointAt==fixture.(x,y,z); since this'll probably cause divide by zeros
	}

	
	/** Sets the strobe for this fixture to the fastest setting available.
	 * If the fixture does not support strobe, an UnsupportedOperationException is thrown. 
	 */
	public void strobe() {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		StrobeChannelDef strobeChannelDef = (StrobeChannelDef) fixtureDef.getChannelDefByClass(StrobeChannelDef.class);
		if (strobeChannelDef==null) {
			throw new UnsupportedOperationException("Default strobe implementation requires strobe channel for this fixture");
		}
		fixture.setDmxChannelValue(strobeChannelDef.getOffset(), strobeChannelDef.getMaximumStrobeValue());
	}
	
	/** Sets the master dimmer value of this fixture to the specified value
	 * 
	 * @param value
	 */
	public void setMasterDimmer(int value) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		DimmerChannelDef masterDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(MasterDimmerChannelDef.class);		
		// 
		if (masterDimmerChannelDef==null) {
			throw new UnsupportedOperationException("Default setMasterDimmer implementation requires a master dimmer channel for this fixture");
		}
		fixture.setDmxChannelValue(masterDimmerChannelDef.getOffset(), value);
	}
	
	/** Enables this fixture's strobe setting, where value is normalised from
	 * 0 (slowest strobe setting) to 255 (highest strobe setting).
	 * 
	 * <p>Use unsetStrobe() to disable strobe.
	 * 
	 * @param value
	 */
	public void setStrobe(int value) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		StrobeChannelDef strobeChannelDef = (StrobeChannelDef) fixtureDef.getChannelDefByClass(StrobeChannelDef.class);
		if (strobeChannelDef==null) {
			throw new UnsupportedOperationException("Default setStrobe implementation requires a strobe channel for this fixture");
		}
		fixture.setDmxChannelValue(strobeChannelDef.getOffset(),
			strobeChannelDef.getLowDmxValue() + 
			(strobeChannelDef.getHighDmxValue()-strobeChannelDef.getLowDmxValue()) * value / 255); 	
	}
	
	/** Disable's this fixture's strobe setting */
	public void unsetStrobe() {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		StrobeChannelDef strobeChannelDef = (StrobeChannelDef) fixtureDef.getChannelDefByClass(StrobeChannelDef.class);
		if (strobeChannelDef==null) {
			throw new UnsupportedOperationException("Default setStrobe implementation requires a strobe channel for this fixture");
		}
		fixture.setDmxChannelValue(strobeChannelDef.getOffset(), strobeChannelDef.getDisableStrobeValue());
	}
	
	
	
	/** Sets all channels for this fixture to 0, sets all custom control
	 * values to 0 (without triggering callbacks) */
	public void blackOut() {
		// which is different to setting the color to black
		// this.setColor(Color.BLACK);
		FixtureDef fixtureDef = fixture.getFixtureDef();
		for (int i = 0; i < fixtureDef.getNumDmxChannels(); i++) {
			fixture.setDmxChannelValue(i, 0);
		}
		if (this.customControls!=null) {
			for (int i = 0; i < customControls.size(); i++) {
				customControls.get(i).setValue(0); 
			}
		}
	}
	
	public Fixture getFixture() {
		return fixture;
	}
	
	public FixtureDef getFixtureDef() {
		return fixture.getFixtureDef();
	}
	
	public void setCustomControls(List customControls) {
		if (this.customControls==null) { 
			this.customControls = (List<CustomControl>) customControls;
		} else {
			throw new IllegalStateException("Cannot define customControls on a FixtureController more than once");
		}
	}
	
	public List<CustomControl> getCustomControls() {
		return customControls;
	}
	

}
