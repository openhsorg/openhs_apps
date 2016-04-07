package org.openhs.arduino.binding;

import org.openhs.core.commons.Message;
import org.openhs.core.site.data.ISiteService;

public class Main {
	
    /*
     * Messages.
     */
	Message msg = new Message ();	
	
    /*
     * Basic data structure.
     */
    ISiteService m_siteService = null;	
    
    /*
     * Serial communication.
     */	
	TwoWaySerialComm comm = new TwoWaySerialComm ();
	
	public void activate() {		
		msg.println("org.openhs.arduino.binding: activated");	
	}
	
	public void deactivate () {
		msg.println("org.openhs.arduino.binding: deactivated");
	}
	
    void setService(ISiteService ser) {
    	msg.println("org.openhs.arduino.binding: Set ISiteService");
        m_siteService = ser;       
        comm.m_siteService = ser;
		
        try
        {          
        	comm.listPorts();
        	comm.connect("/dev/ttyS33");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }	        
    }

    void unsetService(ISiteService ser) {
    	msg.println("org.openhs.arduino.binding: UnSet ISiteService");
        if (m_siteService == ser) {
            ser = null;
        }
    }	

}
