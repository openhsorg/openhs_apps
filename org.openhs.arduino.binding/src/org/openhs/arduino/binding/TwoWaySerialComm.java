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
import java.util.regex.Matcher;
import java.util.regex.*;
//import org.apache.commons.io.IOUtils;
import java.util.Scanner;

import org.openhs.core.commons.Message;
import org.openhs.core.commons.Temperature;
import org.openhs.core.site.data.ISiteService;

public class TwoWaySerialComm {
		
    /*
     * Messages.
     */
	Message msg = new Message ();	
	
    /*
     * Basic data structure.
     */
    ISiteService m_siteService = null;		
	
    SerialPort serialPort = null;

    private static final String PORT_NAMES[] = { 
    //    "/dev/tty.usbmodem", // Mac OS X
//        "/dev/usbdev", // Linux
        "/dev/ttyS33", // Linux
//        "/dev/serial", // Linux
//        "COM3", // Windows
    };
    
    private String appName;
    private BufferedReader input;
    private OutputStream output;
    
    private static final int TIME_OUT = 1000; // Port open timeout
    private static final int DATA_RATE = 9600; // Arduino serial port

	
    static void listPorts()
    {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ) 
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
        }        
    }
    
    static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
    
    public void connect ( String portName ) throws Exception
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
                serialPort.setSerialPortParams(DATA_RATE,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                
                (new Thread(new SerialReader(in, this.m_siteService))).start();
                (new Thread(new SerialWriter(out, this.m_siteService))).start();

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
    	ISiteService m_siteService = null;	
    	
        InputStream in;
        
        public SerialReader ( InputStream in, ISiteService ss)
        {
            this.in = in;
            this.m_siteService = ss;
        }
        
        public void run ()
        {
        	String s = "";
        	
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {            	            	
                while ( ( len = this.in.read(buffer)) > -1 )
                {                	
                    //System.out.print(new String(buffer,0,len));   
                    
                    s = s + new String(buffer,0,len);
                    
                    if (s.contains("\n"))
                    {                    	
                    	decode (s);   	
                    	s = "";
                    }     
                    
                    try
                    {
                    	Thread.sleep (500);
                    }
                    catch (Exception ex)
                    {
                    	
                    }
                }                                
                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }        
          
        }
        
        void decode (String msg)
        {        	
			String pattern1;
			String pattern2;
			Pattern p;
			Matcher m;			
			
        	if (msg.contains("Sensor:"))
        	{
    			pattern1 = "Sensor:";
    			pattern2 = ";";
    			
    			p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
    			m = p.matcher(msg);    	
    			
    			String sensorName = "";
    			
    			while (m.find()) {    
    			
    				sensorName = m.group(1);
    			}
        		
        		if (msg.contains("temp:"))
        		{
        			//System.out.println("decode: -->");
        			
        			pattern1 = "temp:";
        			pattern2 = ";";
        			
        			p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
        			m = p.matcher(msg);
        			
        			while (m.find()) {        			 
        			  
        			  double f = Double.parseDouble(m.group(1));
        			  
        			  System.out.println("Sensor: "+ sensorName + " Temp: " + String.format("%.2f", f));
        			  
        			  Temperature temp = new Temperature ();
        			  
        			  temp.set(f);        			  
        			          			  
        			  if (!this.m_siteService.setSensorTemperature("Room1", sensorName, temp)) {
        				  System.out.println("Cannot write temp :(");
        			  }  
        			  
        			}
        		}        		
        	}        	
        }        
    }

    /** */
    public static class SerialWriter implements Runnable 
    {
    	ISiteService m_siteService = null;	
    	
        OutputStream out;
        
        public SerialWriter ( OutputStream out, ISiteService ss )
        {
            this.out = out;
            this.m_siteService = ss;
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
