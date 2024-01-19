package pl.edu.agh.kt;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;

public class StatisticsCollector {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsCollector.class);
    private IOFSwitch sw;

    public class PacketSendingAndDelayMeasurementTask extends TimerTask {

        @Override
        public void run() {
            logger.debug("run() begin");
            synchronized (StatisticsCollector.this) {
                if (sw == null) { // no switch
                    logger.error("run() end (no switch)");
                    return;
                }

                // Send a sample packet
                sendSamplePacket();

                // Measure the delay
               // measureDelay();
            }
            logger.debug("run() end");
        }

        private void sendSamplePacket() {
        	boolean reachable = false;
        	try {
                //test router
                //InetAddress address = InetAddress.getByName("192.168.2.1");
                byte[] b = new byte[] { (byte) 10, (byte) 0, (byte) 0, (byte) 2 };
                InetAddress address = InetAddress.getByAddress(b);

                // Try to reach the specified address within the timeout
                // periode. If during this periode the address cannot be
                // reach then the method returns false.
                reachable = address.isReachable(5000); // 5 seconds
        		 logger.info("Address: ", address.toString());
        		 logger.info("Is reachable: ", reachable);
        		 
            } catch (Exception e) {
                reachable = false;
                e.printStackTrace();
            }
            String ipAddress = "10.0.0.2"; // Replace with the target IP address
            int timeout = 1000; // Timeout in milliseconds

            try {
                long startTime = System.currentTimeMillis();

                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                boolean isReachable = inetAddress.isReachable(timeout);

                long endTime = System.currentTimeMillis();
                long delay = endTime - startTime;

                if (isReachable) {
                    System.out.println("Host " + ipAddress + " is reachable.");
                    System.out.println("Delay: " + delay + " milliseconds");
                } else {
                    System.out.println("Host " + ipAddress + " is not reachable.");
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        
        private void measureDelay() {
            // Record the time before sending the packet
            long startTime = System.currentTimeMillis();

            // Add logic here to wait for the packet to be received (e.g., use a callback or sleep)
            try {
                Thread.sleep(1000); // Sleep for 1 second as an example; replace with appropriate logic
            } catch (InterruptedException e) {
                logger.error("Error while waiting for the packet", e);
            }

            // Measure the time again after receiving the packet
            long endTime = System.currentTimeMillis();

            // Calculate and log the delay
            long delay = endTime - startTime;
            logger.info("Measured delay: {} milliseconds", delay);
        }
    }

    public static final int PACKET_SENDING_AND_DELAY_MEASUREMENT_INTERVAL = 3000; // in ms

    private static StatisticsCollector singleton;

    private StatisticsCollector(IOFSwitch sw) {
        this.sw = sw;
        new Timer().scheduleAtFixedRate(new PacketSendingAndDelayMeasurementTask(), 0,
                PACKET_SENDING_AND_DELAY_MEASUREMENT_INTERVAL);
    }

    public static StatisticsCollector getInstance(IOFSwitch sw) {
        logger.debug("getInstance() begin");
        synchronized (StatisticsCollector.class) {
            if (singleton == null) {
                logger.debug("Creating StatisticsCollector singleton");
                singleton = new StatisticsCollector(sw);
            }
        }
        logger.debug("getInstance() end");
        return singleton;
    }
    
}
