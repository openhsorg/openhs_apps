package org.openhs.arduino.binding;

import org.openhs.core.commons.Message;

public class Main {
	
    /*
     * Messages.
     */
	Message msg = new Message ();	
	
	TwoWaySerialComm comm = new TwoWaySerialComm ();
	
	public void activate() {		
		msg.println("org.openhs.arduino.binding: activated");
		
        try
        {
          //  comm.connect("COM3"); ///dev/ttyACM0
        	comm.connectPort();
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }		
	}
	
	public void deactivate () {
		msg.println("org.openhs.arduino.binding: deactivated");
	}

}
