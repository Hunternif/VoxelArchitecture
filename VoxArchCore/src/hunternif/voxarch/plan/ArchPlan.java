package hunternif.voxarch.plan;

import hunternif.voxarch.plan.style.Geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeMap;

/**
 * The architectural plan composed of Nodes and Floors.
 * @author Hunternif
 */
public class ArchPlan {
	private final Geometry geometry;
	
	private TreeMap<Floor, List<Node>> map;
	
	public ArchPlan(Geometry config) {
		this.geometry = config;
		map = new TreeMap<Floor, List<Node>>();
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public NavigableSet<Floor> getFloors() {
		return map.navigableKeySet();
	}
	public List<Node> getFloorNodes(Floor floor) {
		return map.get(floor);
	}
	public Entry<Floor, List<Node>> getLowerFloor(Floor floor) {
		return map.lowerEntry(floor);
	}
	public Entry<Floor, List<Node>> getHigherFloor(Floor floor) {
		return map.higherEntry(floor);
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
