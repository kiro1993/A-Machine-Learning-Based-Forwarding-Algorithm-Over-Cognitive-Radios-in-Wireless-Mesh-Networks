import java.util.ArrayList;
import java.util.HashMap;

public class Mesh {
	private ArrayList<Node> nodes;
	private HashMap<String, Float> ipToipBandwidthMap;

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

	public HashMap<String, Float> getIpToipBandwidthMap() {
		return ipToipBandwidthMap;
	}

	public void setIpToipBandwidthMap(HashMap<String, Float> ipToipBandwidthMap) {
		this.ipToipBandwidthMap = ipToipBandwidthMap;
	}

	public void deliver(Node receiver, Message msg) {
	}

	public void flood(Message msg) {
		for (Node node : nodes) {
			if (msg.getCarrierIP() != node.getIp())
				node.receive(msg, Rayleigh.getNextBandWidth(), 1);
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

		for (Node node : nodes) {
			if (node.getGps().getX() >= minX && node.getGps().getX() <= maxX && node.getGps().getY() >= minY
					&& node.getGps().getY() <= maxY && msg.getCarrierIP() != node.getIp()) {
				node.receive(msg, 2, 1);
				SimLog.print("node ip:" + node.getIp() + " , gps:(x=" + node.getGps().getX() + ",y="
						+ node.getGps().getY() + " was located in flooding region");
			} else {
				SimLog.print("node ip:" + node.getIp() + " , gps:(x=" + node.getGps().getX() + ",y="
						+ node.getGps().getY() + " was not located in flooding region");
			}
		}
	}

	public void generateNodes() {

	}

}
