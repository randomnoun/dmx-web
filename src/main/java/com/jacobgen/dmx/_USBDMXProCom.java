/**
 * JacobGen generated file --- do not edit
 *
 * (http://www.sourceforge.net/projects/jacob-project */
package com.jacobgen.dmx;

import com.jacob.com.*;

public class _USBDMXProCom extends Dispatch {

	public static final String componentName = "OpenDMX._USBDMXProCom";

	public _USBDMXProCom() {
		super(componentName);
	}

	/**
	* This constructor is used instead of a case operation to
	* turn a Dispatch object into a wider object - it must exist
	* in every wrapper class whose instances may be returned from
	* method calls wrapped in VT_DISPATCH Variants.
	*/
	public _USBDMXProCom(Dispatch d) {
		// take over the IDispatch pointer
		m_pDispatch = d.m_pDispatch;
		// null out the input's pointer
		d.m_pDispatch = 0;
	}

	public _USBDMXProCom(String compName) {
		super(compName);
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @return the result is of type String
	 */
	public String getDllVersion() {
		return Dispatch.call(this, "getDllVersion").toString();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intDeviceNumber an input-parameter of type int
	 * @return the result is of type String
	 */
	public String getErrorString(int intDeviceNumber) {
		return Dispatch.call(this, "getErrorString", new Variant(intDeviceNumber)).toString();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intDeviceNumber an input-parameter of type int
	 * @return the result is of type boolean
	 */
	public boolean getConnected(int intDeviceNumber) {
		return Dispatch.call(this, "getConnected", new Variant(intDeviceNumber)).changeType(Variant.VariantBoolean).getBoolean();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intDeviceNumber an input-parameter of type int
	 */
	public void init(int intDeviceNumber) {
		Dispatch.call(this, "Init", new Variant(intDeviceNumber));
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intDeviceNumber an input-parameter of type int
	 */
	public void close(int intDeviceNumber) {
		Dispatch.call(this, "close", new Variant(intDeviceNumber));
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intDeviceNumber an input-parameter of type int
	 * @param dmxArray an input-parameter of type SafeArray
	 */
	public void setDMXValues(int intDeviceNumber, SafeArray dmxArray) {
		Dispatch.call(this, "SetDMXValues", new Variant(intDeviceNumber), dmxArray);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param intDeviceNumber is an one-element array which sends the input-parameter
	 *                        to the ActiveX-Component and receives the output-parameter
	 * @param dmxArray is an one-element array which sends the input-parameter
	 *                 to the ActiveX-Component and receives the output-parameter
	 */
	public void setDMXValues(int[] intDeviceNumber, SafeArray[] dmxArray) {
		Variant vnt_intDeviceNumber = new Variant();
		if( intDeviceNumber == null || intDeviceNumber.length == 0 )
			vnt_intDeviceNumber.putNoParam();
		else
			vnt_intDeviceNumber.putIntRef(intDeviceNumber[0]);

		Variant vnt_dmxArray = new Variant();
		if( dmxArray == null || dmxArray.length == 0 )
			vnt_dmxArray.putNoParam();
		else
			vnt_dmxArray.putSafeArrayRef(dmxArray[0]);

		Dispatch.call(this, "SetDMXValues", vnt_intDeviceNumber, vnt_dmxArray);

		if( intDeviceNumber != null && intDeviceNumber.length > 0 )
			intDeviceNumber[0] = vnt_intDeviceNumber.toInt();
		if( dmxArray != null && dmxArray.length > 0 )
			dmxArray[0] = vnt_dmxArray.toSafeArray();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intDeviceNumber an input-parameter of type int
	 * @param channel an input-parameter of type int
	 * @return the result is of type int
	 */
	public int getDMXValue(int intDeviceNumber, int channel) {
		return Dispatch.call(this, "GetDMXValue", new Variant(intDeviceNumber), new Variant(channel)).changeType(Variant.VariantInt).getInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param intDeviceNumber is an one-element array which sends the input-parameter
	 *                        to the ActiveX-Component and receives the output-parameter
	 * @param channel is an one-element array which sends the input-parameter
	 *                to the ActiveX-Component and receives the output-parameter
	 * @return the result is of type int
	 */
	public int getDMXValue(int[] intDeviceNumber, int[] channel) {
		Variant vnt_intDeviceNumber = new Variant();
		if( intDeviceNumber == null || intDeviceNumber.length == 0 )
			vnt_intDeviceNumber.putNoParam();
		else
			vnt_intDeviceNumber.putIntRef(intDeviceNumber[0]);

		Variant vnt_channel = new Variant();
		if( channel == null || channel.length == 0 )
			vnt_channel.putNoParam();
		else
			vnt_channel.putIntRef(channel[0]);

		int result_of_GetDMXValue = Dispatch.call(this, "GetDMXValue", vnt_intDeviceNumber, vnt_channel).changeType(Variant.VariantInt).getInt();

		if( intDeviceNumber != null && intDeviceNumber.length > 0 )
			intDeviceNumber[0] = vnt_intDeviceNumber.toInt();
		if( channel != null && channel.length > 0 )
			channel[0] = vnt_channel.toInt();

		return result_of_GetDMXValue;
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intDeviceNumber an input-parameter of type int
	 */
	public void send(int intDeviceNumber) {
		Dispatch.call(this, "send", new Variant(intDeviceNumber));
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 */
	public void searchPorts() {
		Dispatch.call(this, "SearchPorts");
	}

}
