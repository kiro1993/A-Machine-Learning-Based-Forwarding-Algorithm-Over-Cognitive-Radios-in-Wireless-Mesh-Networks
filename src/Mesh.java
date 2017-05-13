import java.util.ArrayList;
import java.util.HashMap;

public class Mesh {
	private ArrayList<Node> nodes;
	private HashMap<String, Rayleigh> ipToipBandwidthMap;

	public Mesh() {
		nodes = new ArrayList<>();
		ipToipBandwidthMap = new HashMap<>();
	}

	public void addNode(Node node) {
		nodes.add(node);
		node.addMesh(this);
	}

	public void sayHelllo() {
		for (Node node : nodes) {
			node.sendHello();
		}
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}

	public void deliver(Node receiver, Message msg) {
	}

	public void flood(Message msg) {
		for (Node node : nodes) {
			if (msg.getCarrierIP() != node.getIp()) {
				Rayleigh ray = ipToipBandwidthMap.get(msg.getCarrierIP() + node.getIp());
				if (ray == null) {
					ray = new Rayleigh((float) (Math.random() * 10) + 10);
					ipToipBandwidthMap.put(msg.getCarrierIP() + node.getIp(), ray);
					ipToipBandwidthMap.put(node.getIp() + msg.getCarrierIP(), ray);
				}
				float bandwidth = ray.getNextBandWidth();
				node.receive(msg, bandwidth, msg.getCarrierTime() + Message.MSG_PAYLOAD_SIZE / bandwidth);
			}
		}
	}
	public void floodWithGPS(Message msg) {
		SimLog.print("");

		SimLog.print("node ip:" + msg.getCarrierIP() + " , gps:(x=" + msg.getCarrierGPS().getX() + ",y="
				+ msg.getCarrierGPS().getY() + ") flooded the mesh to reach destination node ip:"
				+ msg.getDestinationIP() + " , gps:(x=" + msg.getDestinationGPS().getX() + ",y="
				+ msg.getDestinationGPS().getY() + ")");
		double minX = Math.min(msg.getCarrierGPS().getX(), msg.getDestinationGPS().getX());
		double maxX = Math.max(msg.getCarrierGPS().getX(), msg.getDestinationGPS().getX());
		double minY = Math.min(msg.getCarrierGPS().getY(), msg.getDestinationGPS().getY());
		double maxY = Math.max(msg.getCarrierGPS().getY(), msg.getDestinationGPS().getY());

		SimLog.print(
				"flooding region is reduced. region from x " + minX + " : " + maxX + " and y " + minY + " : " + maxY);


		float maxBandwidth = -1;
		Node maxNode = null;

		for (Node node : nodes) {
			if (node.getGps().getX() >= minX && node.getGps().getX() <= maxX && node.getGps().getY() >= minY
					&& node.getGps().getY() <= maxY && msg.getCarrierIP() != node.getIp()) {
				
				BandwidthState x = node.getIpStateMap().get(msg.getCarrierIP());
				if (x == null) {
					x = new BandwidthState();
					node.getIpStateMap().put(msg.getCarrierIP(), new BandwidthState());
				}
				float expected = x.getExpectedBandwidth(msg.getCarrierTime());
				SimLog.print("\nel expected el rege3ly:"+expected);
				if (expected==-5){		//means there is no enough previous cases to predict the next bandwidth 
					Rayleigh ray = ipToipBandwidthMap.get(msg.getCarrierIP() + node.getIp());
					if (ray == null) {
						ray = new Rayleigh((float) (Math.random() * 10) + 10);
						ipToipBandwidthMap.put(msg.getCarrierIP() + node.getIp(), ray);
						ipToipBandwidthMap.put(node.getIp() + msg.getCarrierIP(), ray);
					}
					float bandwidth = ray.getNextBandWidth();
					node.receive(msg, bandwidth, msg.getCarrierTime() + Message.MSG_PAYLOAD_SIZE / bandwidth);
					SimLog.print("node ip:" + node.getIp() + " , gps:(x=" + node.getGps().getX() + ",y="
							+ node.getGps().getY() + " was located in flooding region ");

				}
				else{
					if (expected > maxBandwidth){
						maxBandwidth = expected;
						maxNode = node;
					}
				}

				
				
			} else {
				SimLog.print("node ip:" + node.getIp() + " , gps:(x=" + node.getGps().getX() + ",y="
						+ node.getGps().getY() + " was not located in flooding region");
			}
		}
		if(maxNode != null){
			Rayleigh ray = ipToipBandwidthMap.get(msg.getCarrierIP() + maxNode.getIp());
			if (ray == null) {
				ray = new Rayleigh((float) (Math.random() * 100) + 10);
				ipToipBandwidthMap.put(msg.getCarrierIP() + maxNode.getIp(), ray);
				ipToipBandwidthMap.put(maxNode.getIp() + msg.getCarrierIP(), ray);
			}
			float bandwidth = ray.getNextBandWidth();
			maxNode.receive(msg, bandwidth, msg.getCarrierTime() + Message.MSG_PAYLOAD_SIZE / bandwidth);
			SimLog.print("*************************************node ip:" + maxNode.getIp() + " , gps:(x=" + maxNode.getGps().getX() + ",y="
					+ maxNode.getGps().getY() + " was located in flooding region ... Expected bandwidth:" +maxBandwidth +" ,Actual Bandwidth:"+bandwidth);
		
			}
		

	}

	public void generateNodes() {

	}

}
