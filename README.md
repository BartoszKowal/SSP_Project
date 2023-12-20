# SSP_Project
Repozytorium dla projektu z przedmiotu Sieci Sterowane Programowo  

## Temat projektu
### Wykrywanie ataków DDoS na podstawie analizy opóźnień pakietów. 

## Skład zespołu:
* Jakub Sośniak  
* Kamil Pokusa  
* Bartosz Kowal  
* Adam Zielina  


## Topologia

![image](https://github.com/BartoszKowal/SSP_Project/assets/56104920/993ce8a5-73b1-423f-9000-f9bdd046fa0a)

## Oprogramowanie wykorzystywane do przeprowadzania ataków

Do symulowania ataków na serwer HTTP użyty zostanie program Scapy pozwalający na tworzenie i wysyłanie pakietów, takich jak pakiet ARP spoofing, pakiety ICMP flood, czy też inne ataki typowe dla różnych warstw modelu OSI.

## Źródła
1. 'Detection of Control Layer DDoS Attack using Entropy metrics in SDN: An Empirical Investigation'    
Kshira Sagar Sahoo; Bibhudatta Sahoo; Manikanta Vankayala; Ratnakar Dash    
https://ieeexplore-1ieee-1org-1000047zk0169.wbg2.bg.agh.edu.pl/document/8441392  
2. 'Detection of DDoS Attacks in Software Defined Networking Using Entropy'  
Cong Fan: Nitheesh Murugan Kaliyamurthy; Shi Chen; He Jiang; Yiwen Zhou; Carlene Campbell  
https://www.mdpi.com/2076-3417/12/1/370  
3. 'Detection of Control Layer DDoS Attack using Entropy metrics in SDN: An Empirical Investigation'  
Kshira Sagar Sahoo; Bibhudatta Sahoo; Manikanta Vankayala; Ratnakar Dash  
https://ieeexplore-1ieee-1org-1000047zk0169.wbg2.bg.agh.edu.pl/document/8441392  
4. 'DDoS Attack Detection and Mitigation Using SDN: Methods, Practices, and Solutions'  
Narmeen Bawany; Jawwad Shamsi; Khaled Salah  
https://www.researchgate.net/publication/313222794_DDoS_Attack_Detection_and_Mitigation_Using_SDN_Methods_Practices_and_Solutions  
5. 'Detection of Control Layer DDoS Attack using Entropy metrics in SDN: An Empirical Investigation'  
Kshira Sagar Sahoo; Bibhudatta Sahoo; Manikanta Vankayala; Ratnakar Dash  
https://ieeexplore-1ieee-1org-1000047zk0169.wbg2.bg.agh.edu.pl/document/8441392  
7. Strona internetowa ProjectFloodlight  
https://floodlight.atlassian.net/wiki/spaces/floodlightcontroller/overview  
8. Strona internetowa sFlow  
https://sflow.org/developers/specifications.php


## Pseudokod



## Co klasyfikujemy jako DDoS? Jak będziemy wykrywać ataki? Co będziemy mierzyć i gdzie?

1. System zbiera informacje dotyczące opóźnień pakietów w sieci (pomiar czasu podróży (RTT) między różnymi węzłami sieciowymi).  
2. Na podstawie zebranych danych buduje się profil normalnych opóźnień w sieci. Profil ten obejmuje statystyki dotyczące oczekiwanych wartości opóźnień w normalnych warunkach.  
3. System ustala progi dla RTT.  
4. System monitoruje opóźnienia pakietów w czasie rzeczywistym i porównuje je do wcześniej utworzonego profilu normalnego.  
5. Jeśli opóźnienia przekraczają ustalone progi, system podejmuje działania w celu analizy bardziej szczegółowej sytuacji.
6. Profil normalnych opóźnień w sieci będzie dynamicznie aktualizowany, aby uwzględniać zmieniające się warunki ruchu i infrastruktury sieciowej.
7. Reakcja na atak: Jeśli system uzna, że zachodzi atak DDoS (na podstawie przekroczenia progów opóźnień, po zaktualizowaniu), podejmuje odpowiednie kroki w celu zminimalizowania wpływu ataku.




