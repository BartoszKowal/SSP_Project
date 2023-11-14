"""Custom topology example
Two directly connected switches plus a host for each switch:
 host --- switch --- switch --- host
Adding the 'topos' dict with a key/value pair to generate our newly defined
topology enables one to pass in '--topo=mytopo' from the command line.
"""
from mininet.topo import Topo
class MyTopo( Topo ):
    "Simple topology example."
     def init( self ):
        "Create custom topo."
        # Initialize topology
        Topo.init( self )
        # Add hosts and switches
        Host = self.addHost( 'h1' )
        serv1 = self.addHost( 'serv1' )
        serv2 = self.addHost( 'serv2' )
        attacerHost1 = self.addHost( 'a1' )

        Switch = self.addSwitch( 's1' )


        # Add links
        self.addLink( serv1, Switch)
        self.addLink( serv2, Switch)
        self.addLink( Host, Switch)
        self.addLink( attacerHost1, Switch)

topos = { 'mytopo': ( lambda: MyTopo() ) }