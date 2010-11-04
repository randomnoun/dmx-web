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
	 * @param n an input-parameter of type int
	 * @return the result is of type String
	 */
	public String getErrorString(int n) {
		return Dispatch.call(this, "getErrorString", new Variant(n)).toString();
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
	 * @param intPortNumber an input-parameter of type int
	 */
	public void init(int intPortNumber) {
		Dispatch.call(this, "Init", new Variant(intPortNumber));
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intPortNumber an input-parameter of type int
	 */
	public void done(int intPortNumber) {
		Dispatch.call(this, "Done", new Variant(intPortNumber));
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intPortNumber an input-parameter of type int
	 * @param dmxArray an input-parameter of type SafeArray
	 */
	public void setDMXValues(int intPortNumber, SafeArray dmxArray) {
		Dispatch.call(this, "SetDMXValues", new Variant(intPortNumber), dmxArray);
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param intPortNumber is an one-element array which sends the input-parameter
	 *                      to the ActiveX-Component and receives the output-parameter
	 * @param dmxArray is an one-element array which sends the input-parameter
	 *                 to the ActiveX-Component and receives the output-parameter
	 */
	public void setDMXValues(int[] intPortNumber, SafeArray[] dmxArray) {
		Variant vnt_intPortNumber = new Variant();
		if( intPortNumber == null || intPortNumber.length == 0 )
			vnt_intPortNumber.putNoParam();
		else
			vnt_intPortNumber.putIntRef(intPortNumber[0]);

		Variant vnt_dmxArray = new Variant();
		if( dmxArray == null || dmxArray.length == 0 )
			vnt_dmxArray.putNoParam();
		else
			vnt_dmxArray.putSafeArrayRef(dmxArray[0]);

		Dispatch.call(this, "SetDMXValues", vnt_intPortNumber, vnt_dmxArray);

		if( intPortNumber != null && intPortNumber.length > 0 )
			intPortNumber[0] = vnt_intPortNumber.toInt();
		if( dmxArray != null && dmxArray.length > 0 )
			dmxArray[0] = vnt_dmxArray.toSafeArray();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param n an input-parameter of type int
	 * @param channel an input-parameter of type int
	 * @return the result is of type int
	 */
	public int getDMXValue(int n, int channel) {
		return Dispatch.call(this, "GetDMXValue", new Variant(n), new Variant(channel)).changeType(Variant.VariantInt).getInt();
	}

	/**
	 * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).
	 * @param n is an one-element array which sends the input-parameter
	 *          to the ActiveX-Component and receives the output-parameter
	 * @param channel is an one-element array which sends the input-parameter
	 *                to the ActiveX-Component and receives the output-parameter
	 * @return the result is of type int
	 */
	public int getDMXValue(int[] n, int[] channel) {
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

		int result_of_GetDMXValue = Dispatch.call(this, "GetDMXValue", vnt_n, vnt_channel).changeType(Variant.VariantInt).getInt();

		if( n != null && n.length > 0 )
			n[0] = vnt_n.toInt();
		if( channel != null && channel.length > 0 )
			channel[0] = vnt_channel.toInt();

		return result_of_GetDMXValue;
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intPortNumber an input-parameter of type int
	 */
	public void send(int intPortNumber) {
		Dispatch.call(this, "send", new Variant(intPortNumber));
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param binStr an input-parameter of type String
	 * @return the result is of type String
	 */
	public String hexStr(String binStr) {
		return Dispatch.call(this, "hexStr", binStr).toString();
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 */
	public void searchPorts() {
		Dispatch.call(this, "SearchPorts");
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intPortNumber an input-parameter of type int
	 */
	public void send_CFG_Request_Packet(int intPortNumber) {
		Dispatch.call(this, "Send_CFG_Request_Packet", new Variant(intPortNumber));
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intPortNumber an input-parameter of type int
	 */
	public void send_Serial_Request_Packet(int intPortNumber) {
		Dispatch.call(this, "Send_Serial_Request_Packet", new Variant(intPortNumber));
	}

	/**
	 * Wrapper for calling the ActiveX-Method with input-parameter(s).
	 * @param intPortNumber an input-parameter of type int
	 * @param buf an input-parameter of type String
	 * @return the result is of type long
	 */
	public long receivePacket(int intPortNumber, String buf) {
		Variant v = Dispatch.call(this, "ReceivePacket", new Variant(intPortNumber), buf);
		return v.getLong();
	}

}
