from mininet.net import Mininet
from mininet.node import Controller, OVSSwitch, RemoteController
from mininet.cli import CLI
import sys

def create_sdn_network(controller_ip='127.0.0.1', controller_port=6633):
    # Create Mininet object
    net = Mininet(controller=Controller, switch=OVSSwitch)

    # Add external controller if specified
    if controller_ip != '127.0.0.1':
        c0 = net.addController('c0', controller=RemoteController, ip=controller_ip, port=controller_port)
    else:
        c0 = net.addController('c0')

    # Add OpenFlow switch
    s1 = net.addSwitch('s1')

    # Add four hosts
    h1 = net.addHost('h1')
    h2 = net.addHost('h2')
    h3 = net.addHost('h3')
    h4 = net.addHost('h4')

    # Connect hosts to the switch
    net.addLink(h1, s1)
    net.addLink(h2, s1)
    net.addLink(h3, s1)
    net.addLink(h4, s1)

    # Start the network
    net.build()
    c0.start()
    s1.start([c0])

    # Configure host interfaces
    h1.cmd('ifconfig h1-eth0 10.0.0.1 netmask 255.255.255.0')
    h2.cmd('ifconfig h2-eth0 10.0.0.2 netmask 255.255.255.0')
    h3.cmd('ifconfig h3-eth0 10.0.0.3 netmask 255.255.255.0')
    h4.cmd('ifconfig h4-eth0 10.0.0.4 netmask 255.255.255.0')

    # Start HTTP server on h1 and h2
    h1.cmd('python -m SimpleHTTPServer 80 &')
    h2.cmd('python -m SimpleHTTPServer 80 &')

    # Start Mininet CLI for interactive control
    CLI(net)

    # Stop the network after completion
    net.stop()

if __name__ == '__main__':
    # Check if controller IP and port are provided as command-line arguments
    if len(sys.argv) == 3:
        controller_ip = sys.argv[1]
        controller_port = int(sys.argv[2])
        create_sdn_network(controller_ip, controller_port)
    else:
        create_sdn_network()
