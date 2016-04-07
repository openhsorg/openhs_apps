package org.openhs.arduino.binding;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Enumeration;

import org.openhs.core.commons.Message;

public class TwoWaySerialComm {
		
    /*
     * Messages.
     */
	Message msg = new Message ();	
	
    SerialPort serialPort = null;

    private static final String PORT_NAMES[] = { 
        "/dev/tty.usbmodem", // Mac OS X
//        "/dev/usbdev", // Linux
//        "/dev/tty", // Linux
//        "/dev/serial", // Linux
//        "COM3", // Windows
    };
    
    private String appName;
    private BufferedReader input;
    private OutputStream output;
    
    private static final int TIME_OUT = 1000; // Port open timeout
    private static final int DATA_RATE = 9600; // Arduino serial port
	
	public TwoWaySerialComm()
    {
        super();
    }	
	
	public boolean connectPort () {
		
	      try {
	            CommPortIdentifier portId = null;
	            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

	            // Enumerate system ports and try connecting to Arduino over each
	            //
	            System.out.println( "Trying:");
	            while (portId == null && portEnum.hasMoreElements()) {
	                // Iterate through your host computer's serial port IDs
	                //
	                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
	                System.out.println( "   port" + currPortId.getName() );
	                for (String portName : PORT_NAMES) {
	                    if ( currPortId.getName().equals(portName) 
	                      || currPortId.getName().startsWith(portName)) {

	                        // Try to connect to the Arduino on this port
	                        //
	                        // Open serial port
	                        serialPort = (SerialPort)currPortId.open(appName, TIME_OUT);
	                        portId = currPortId;
	                        System.out.println( "Connected on port" + currPortId.getName() );
	                        break;
	                    }
	                }
	            }
	        
	            if (portId == null || serialPort == null) {
	                System.out.println("Oops... Could not connect to Arduino");
	                return false;
	            }
	            
	            System.out.println("OKOK, detected this port: " + serialPort.getName());
	        /*
	            // set port parameters
	            serialPort.setSerialPortParams(DATA_RATE,
	                            SerialPort.DATABITS_8,
	                            SerialPort.STOPBITS_1,
	                            SerialPort.PARITY_NONE);

	            // add event listeners
	            serialPort.addEventListener();
	            serialPort.notifyOnDataAvailable(true);

	            // Give the Arduino some time
	            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
	            */
	            return true;
	        }
	        catch ( Exception e ) { 
	            e.printStackTrace();
	        }
	        return false;
		
	}
    
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                
                (new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out))).start();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    /** */
    public static class SerialReader implements Runnable 
    {
        InputStream in;
        
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    System.out.print(new String(buffer,0,len));
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }

    /** */
    public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {                
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
}
