package pl.edu.agh.kt;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.util.FlowModUtils;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdnLabListener implements IFloodlightModule, IOFMessageListener {

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	public static final int TCP_FLAG_SYN = 0x02; // filter for only SYN_FLAG
	public static final int TCP_FLAG_ACK = 0x10; // 00010000 in binary (filter for only ACK flags)
    private List<SynEvent> synEvents = new ArrayList<>(); //buffer
	
	@Override
	public String getName() {
		return SdnLabListener.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
	    logger.info("************* NEW PACKET IN *************");

	    // Check if the message is a PACKET_IN message
	    if (msg.getType() == OFType.PACKET_IN) {
	        // Parse the received packet
	        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

	        // Check if the packet contains an IP payload
	        if (eth.getPayload() instanceof IPv4) {
	            IPv4 ipv4 = (IPv4) eth.getPayload();

	            // Check if the IP packet contains a TCP payload
	            if (ipv4.getPayload() instanceof TCP) {
	                TCP tcp = (TCP) ipv4.getPayload();

	                //SYN_FLAG - 0x02
	                //SYN_ACK_FLAG - 0x12
	                //ACK_FLAG - 0x10
	                // Check if the SYN flag is set and dont care about synACK (remove those with ACK flag)
	                if ((tcp.getFlags() & TCP_FLAG_SYN) != 0 && (tcp.getFlags() & TCP_FLAG_ACK) == 0) {
	                    logger.info("Received TCP packet with SYN flag");
	                    String sourceIp = ipv4.getSourceAddress().toString();
	                    Date now = new Date();
	                    
	                    SynEvent event = new SynEvent(sourceIp, now);
	                    synEvents.add(event);

	                    //log the event
	                    System.out.println("SYN event added: " + event);
	                }
	            }
	        }
	    }
	    System.out.println("Number of SYN events in list: " + synEvents.size());
	    removeOldSynEvents();
	    checkSourceIPsWithHighSynCounts(400,sw);
	    return Command.CONTINUE;
	}
	
	//garbage collector
    public void removeOldSynEvents() {
        long tenSecondsAgo = new Date().getTime() - 10000; // 10 seconds in milliseconds
        synchronized (synEvents) {
            Iterator<SynEvent> iterator = synEvents.iterator();
            while (iterator.hasNext()) {
                SynEvent event = iterator.next();
                if (event.getTimestamp().getTime() < tenSecondsAgo) {
                    iterator.remove();
                    logger.info("Removed SYN event: " + event);
                }
            }
        }
    }

    public void checkSourceIPsWithHighSynCounts(int threshold, IOFSwitch sw) {
        Map<String, Integer> synEventCounts = new HashMap<>();

        synchronized (synEvents) {
            for (SynEvent event : synEvents) {
                String ip = event.getSourceIpAddress();
                Integer count = synEventCounts.containsKey(ip) ? synEventCounts.get(ip) : 0;
                synEventCounts.put(ip, count + 1);
            }

            for (Map.Entry<String, Integer> entry : synEventCounts.entrySet()) {
                if (entry.getValue() > threshold) {
                    logger.info("IP Address " + entry.getKey() + " has exceeded the threshold with " + entry.getValue() + " SYN events. Blocking IP.");
                    blockSourceIp(entry.getKey(), 60, sw); // Block for 60 seconds on each switch
                }
            }
        }
    }

    
    private void blockSourceIp(String sourceIp, int timeoutInSeconds, IOFSwitch sw) {
        OFFactory myFactory = sw.getOFFactory(); // Get the factory from the switch

        Match myMatch = myFactory.buildMatch()
            .setExact(MatchField.ETH_TYPE, EthType.IPv4)
            .setExact(MatchField.IPV4_SRC, IPv4Address.of(sourceIp))
            .build();

        ArrayList<OFAction> actionList = new ArrayList<>();

        OFFlowAdd flowAdd = myFactory.buildFlowAdd()
            .setMatch(myMatch)
            .setActions(actionList) // No action means drop
            .setIdleTimeout(timeoutInSeconds)
            .setHardTimeout(timeoutInSeconds)
            .setPriority(FlowModUtils.PRIORITY_MAX)
            .build();

        sw.write(flowAdd); // Write the flow rule to the switch
    }
        
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(SdnLabListener.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		logger.info("******************* START **************************");
         
	}

}