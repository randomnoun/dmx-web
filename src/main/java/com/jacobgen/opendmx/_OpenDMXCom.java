/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package com.jacobgen.opendmx;

import com.jacob.com.*;

public class _OpenDMXCom extends Dispatch {

	public static final String componentName = "OpenDMX._OpenDMXCom";

	public _OpenDMXCom() {
		super(componentName);
	}

	/**
	* This constructor is used instead of a case operation to
	* turn a Dispatch object into a wider object - it must exist
	* in every wrapper class whose instances may be returned from
	* method calls wrapped in VT_DISPATCH Variants.
	*/
	public _OpenDMXCom(Dispatch d) {
		// take over the IDispatch pointer
		m_pDispatch = d.m_pDispatch;
		// null out the input's pointer
		d.m_pDispatch = 0;
	}

	public _OpenDMXCom(String compName) {
		super(compName);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @return the result is of type String
	 */
	public String getErrorString(int n) {
		return Dispatch.call(this, "getErrorString", new Variant(n)).toString();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @return the result is of type int
	 */
	public int getJumperId(int n) {
		return Dispatch.call(this, "getJumperId", new Variant(n)).changeType(Variant.VariantInt).getInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @return the result is of type boolean
	 */
	public boolean getConnected(int n) {
		return Dispatch.call(this, "getConnected", new Variant(n)).changeType(Variant.VariantBoolean).getBoolean();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @return the result is of type int
	 */
	public int getStartCode(int n) {
		return Dispatch.call(this, "getStartCode", new Variant(n)).changeType(Variant.VariantInt).getInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @return the result is of type boolean
	 */
	public boolean getThreadStarted(int n) {
		return Dispatch.call(this, "getThreadStarted", new Variant(n)).changeType(Variant.VariantBoolean).getBoolean();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 */
	public void initialize() {
		Dispatch.call(this, "Initialize");
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @return the result is of type int
	 */
	public int init(int n) {
		return Dispatch.call(this, "Init", new Variant(n)).changeType(Variant.VariantInt).getInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param n is an one-element array which sends the input-parameter
	 *          to the ActiveX-Component and receives the output-parameter
	 * @return the result is of type int
	 */
	public int init(int[] n) {
		Variant vnt_n = new Variant();
		if( n == null || n.length == 0 )
			vnt_n.putNoParam();
		else
			vnt_n.putIntRef(n[0]);

		int result_of_Init = Dispatch.call(this, "Init", vnt_n).changeType(Variant.VariantInt).getInt();

		if( n != null && n.length > 0 )
			n[0] = vnt_n.toInt();

		return result_of_Init;
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @return the result is of type int
	 */
	public int init_All() {
		return Dispatch.call(this, "Init_All").changeType(Variant.VariantInt).getInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @return the result is of type int
	 */
	public int getID(int n) {
		return Dispatch.call(this, "GetID", new Variant(n)).changeType(Variant.VariantInt).getInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param n is an one-element array which sends the input-parameter
	 *          to the ActiveX-Component and receives the output-parameter
	 * @return the result is of type int
	 */
	public int getID(int[] n) {
		Variant vnt_n = new Variant();
		if( n == null || n.length == 0 )
			vnt_n.putNoParam();
		else
			vnt_n.putIntRef(n[0]);

		int result_of_GetID = Dispatch.call(this, "GetID", vnt_n).changeType(Variant.VariantInt).getInt();

		if( n != null && n.length > 0 )
			n[0] = vnt_n.toInt();

		return result_of_GetID;
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @return the result is of type int
	 */
	public int numberOfOpenDevices() {
		return Dispatch.call(this, "NumberOfOpenDevices").changeType(Variant.VariantInt).getInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @param dmxArray an input-parameter of type SafeArray
	 */
	public void set_DMX(int n, SafeArray dmxArray) {
		Dispatch.call(this, "Set_DMX", new Variant(n), dmxArray);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param n is an one-element array which sends the input-parameter
	 *          to the ActiveX-Component and receives the output-parameter
	 * @param dmxArray is an one-element array which sends the input-parameter
	 *                 to the ActiveX-Component and receives the output-parameter
	 */
	public void set_DMX(int[] n, SafeArray[] dmxArray) {
		Variant vnt_n = new Variant();
		if( n == null || n.length == 0 )
			vnt_n.putNoParam();
		else
			vnt_n.putIntRef(n[0]);

		Variant vnt_dmxArray = new Variant();
		if( dmxArray == null || dmxArray.length == 0 )
			vnt_dmxArray.putNoParam();
		else
			vnt_dmxArray.putSafeArrayRef(dmxArray[0]);

		Dispatch.call(this, "Set_DMX", vnt_n, vnt_dmxArray);

		if( n != null && n.length > 0 )
			n[0] = vnt_n.toInt();
		if( dmxArray != null && dmxArray.length > 0 )
			dmxArray[0] = vnt_dmxArray.toSafeArray();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @param channel an input-parameter of type int
	 * @return the result is of type int
	 */
	public int get_DMX(int n, int channel) {
		return Dispatch.call(this, "Get_DMX", new Variant(n), new Variant(channel)).changeType(Variant.VariantInt).getInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param n is an one-element array which sends the input-parameter
	 *          to the ActiveX-Component and receives the output-parameter
	 * @param channel is an one-element array which sends the input-parameter
	 *                to the ActiveX-Component and receives the output-parameter
	 * @return the result is of type int
	 */
	public int get_DMX(int[] n, int[] channel) {
		Variant vnt_n = new Variant();
		if( n == null || n.length == 0 )
			vnt_n.putNoParam();
		else
			vnt_n.putIntRef(n[0]);

		Variant vnt_channel = new Variant();
		if( channel == null || channel.length == 0 )
			vnt_channel.putNoParam();
		else
			vnt_channel.putIntRef(channel[0]);

		int result_of_Get_DMX = Dispatch.call(this, "Get_DMX", vnt_n, vnt_channel).changeType(Variant.VariantInt).getInt();

		if( n != null && n.length > 0 )
			n[0] = vnt_n.toInt();
		if( channel != null && channel.length > 0 )
			channel[0] = vnt_channel.toInt();

		return result_of_Get_DMX;
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 */
	public void stop_Threads() {
		Dispatch.call(this, "Stop_Threads");
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 */
	public void done(int n) {
		Dispatch.call(this, "Done", new Variant(n));
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param n is an one-element array which sends the input-parameter
	 *          to the ActiveX-Component and receives the output-parameter
	 */
	public void done(int[] n) {
		Variant vnt_n = new Variant();
		if( n == null || n.length == 0 )
			vnt_n.putNoParam();
		else
			vnt_n.putIntRef(n[0]);

		Dispatch.call(this, "Done", vnt_n);

		if( n != null && n.length > 0 )
			n[0] = vnt_n.toInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 */
	public void done_All() {
		Dispatch.call(this, "Done_All");
	}

}
