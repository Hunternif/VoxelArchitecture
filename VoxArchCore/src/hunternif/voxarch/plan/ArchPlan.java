package hunternif.voxarch.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/** The architectural plan composed of Nodes and Floors. */
public class ArchPlan {
	private final Config config;
	
	private TreeMap<Floor, List<Node>> map;
	
	public ArchPlan(Config config) {
		this.config = config;
		map = new TreeMap<Floor, List<Node>>();
	}
	
	public Config getConfig() {
		return config;
	}
	
	public Set<Floor> getFloors() {
		return map.keySet();
	}
	public List<Node> getFloorNodes(Floor floor) {
		return map.get(floor);
	}
	public Entry<Floor, List<Node>> getNearestFloorBelow(Floor floor) {
		return map.floorEntry(floor);
	}
	public Entry<Floor, List<Node>> getNearestFloorAbove(Floor floor) {
		return map.ceilingEntry(floor);
	}
	
	protected boolean addNode(Node node) {
		List<Node> nodes = getFloorNodes(node.getFloor());
		if (nodes == null) {
			nodes = new ArrayList<Node>();
			map.put(node.getFloor(), nodes);
		}
		return nodes.add(node);
	}
}
