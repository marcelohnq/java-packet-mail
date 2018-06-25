import java.util.ArrayList;
import java.util.List;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

public class Monitor {
	public static void main(String[] args) {
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r != Pcap.OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}

		PcapIf device = alldevs.get(REDELOCAL_DEVICE); // Get first device in list

		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: " + errbuf.toString());
			return;
		}

		PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

			public void nextPacket(PcapPacket packet, String user) {
				Tcp tcp = new Tcp();
				Ip4 ip = new Ip4();
				byte[] sIP = new byte[4];
				byte[] dIP = new byte[4];
				String sourceIP = "";
				String destIP = "";

				if (packet.hasHeader(ip) && packet.hasHeader(tcp)) {
					sIP = packet.getHeader(ip).source();
					sourceIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
					dIP = packet.getHeader(ip).destination();
					destIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);

					if (tcp.destination() == 22 || tcp.destination() == 631 || tcp.destination() == 8084) {
						String s = "Source IP: " + sourceIP;
						String d = "Destination IP: " + destIP;

						s += " [" + tcp.source() + "]";
						d += " [" + tcp.destination() + "]";

						System.out.println(s);
						System.out.println(d);

						if (!destIP.equals(IP_SERVIDOR_SEGURO)) {
							Monitor.alerta(destIP);
						} else {
							Monitor.alerta(tcp.destination()+"");
						}												
					}
				}
			}
		};

		System.out.println("Lendo pacotes...");
		pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "jNetPcap");
		pcap.close();
	}

	PRECISA SER TESTADO:public static void alerta(int alerta) {

		String subject = "[Alerta] Acesso a porta " + alerta;
		String text = "";

		switch (alerta) {
		case 22:
			text = "Verificou-se acesso a porta 80 [requisição HTTP errônea], há um grande possibilidade de tentativa de ataque ao servidor";
			break;
		case 631:
			text = "Verificou-se acesso a porta 3306 [conexão ao banco de dados MySQL errônea], há um grande possibilidade de tentativa de ataque ao servidor";
			break;	
				
		default: //Caso não seja o ip seguro
			subject = "[Alerta] Máquina NÃO segura [IP: " + alerta + "]";
			text = "Verificou-se acesso de uma máquina NÃO segura, há um grande possibilidade de tentativa de ataque ao servidor";
			break;			
		}

		new Mail().send(subject, text);
	}
}
