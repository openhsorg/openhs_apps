package org.openhs.arduino.binding;

import org.openhs.core.commons.Message;
import org.openhs.core.site.data.ISiteService;
import org.openhs.core.io.serial.ITwoWaySerialComm;

public class Main {
	
    /*
     * Messages.
     */
	Message msg = new Message ();	
	
    /*
     * Basic data structure.
     */
	ITwoWaySerialComm m_command = null;	

	public void activate() {		
		msg.println("org.openhs.arduino.binding: activated");	
	}
	
	public void deactivate () {
		msg.println("org.openhs.arduino.binding: deactivated");
	}
	
    void setService(ITwoWaySerialComm com) {
    	msg.println("org.openhs.arduino.binding: Set ISiteService");
        m_command = com;  
        
                		
        try
        {
        	m_command.connect("/dev/ttyS33");
        	
        	//comm.listPorts();
        	//comm.connect("/dev/ttyS33");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }	        
    }	

    void unsetService(ITwoWaySerialComm com) {
    	msg.println("org.openhs.arduino.binding: UnSet ISiteService");
        if (m_command == com) {
            com = null;
        }
    }	

}
