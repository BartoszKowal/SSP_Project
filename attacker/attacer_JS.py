from scapy.all import Ether, IP, ICMP, sendp 
import time


# Size of the payload in bytes
payload_size_bytes = 6553 # Adjust as needed

# Destination IP address (replace with your destination IP)
destination_ip = "10.0.0.2"

# Generate a large payload
large_payload = b"A" * payload_size_bytes

# Get the duration of the loop from the user
loop_duration = int(input("Enter the duration of the loop in seconds: "))

# Calculate the time to stop the loop
end_time = time.time() + loop_duration

# Loop until the specified duration is reached
while time.time() < end_time:
    # Craft an Ethernet frame with an ICMP packet and the large payload
    packet = Ether() / IP(dst=destination_ip) / ICMP() / large_payload
    # Display the packet details
    print("Packet details for large payload:")
    print(packet.show2())
    # Send the packet
    sendp(packet, iface="lo") # Replace 'eth0' with the appropriate network interface
    # Optionally, pause for a short duration between packets
