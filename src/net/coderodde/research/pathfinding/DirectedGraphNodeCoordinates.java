package net.coderodde.research.pathfinding;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public final class DirectedGraphNodeCoordinates {

    private final Map<DirectedGraphNode, Point2D.Double> map = new HashMap<>();
    
    public void put(DirectedGraphNode node, Point2D.Double point) {
        map.put(node, point);
    }
    
    public Point2D.Double get(DirectedGraphNode node) {
        return map.get(node);
    }
}
