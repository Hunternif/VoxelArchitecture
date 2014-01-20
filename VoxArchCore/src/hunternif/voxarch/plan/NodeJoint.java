package hunternif.voxarch.plan;

import java.util.ArrayList;
import java.util.List;

/**
 * A node to which other nodes can connect, i.e. a room to which corridors are
 * connected.
 * @author Hunternif
 */
public abstract class NodeJoint extends Node {

	/** Nodes connected to a side of this NodeJoint. */
	private final List<Node> nodesEast = new ArrayList<Node>(),
							 nodesSouth = new ArrayList<Node>(),
							 nodesWest = new ArrayList<Node>(),
							 nodesNorth = new ArrayList<Node>();
	
	/** Only one node connection can vertically. */
	private Node nodeUp, nodeDown;
	
	public NodeJoint(ArchPlan plan, Floor floor) {
		super(plan, floor);
	}
	
	public Node getNodeUp() {
		return nodeUp;
	}

	public void setNodeUp(Node nodeUp) {
		this.nodeUp = nodeUp;
	}

	public Node getNodeDown() {
		return nodeDown;
	}

	public void setNodeDown(Node nodeDown) {
		this.nodeDown = nodeDown;
	}

	public List<Node> getNodesEast() {
		return nodesEast;
	}

	public List<Node> getNodesSouth() {
		return nodesSouth;
	}

	public List<Node> getNodesWest() {
		return nodesWest;
	}

	public List<Node> getNodesNorth() {
		return nodesNorth;
	}

}
