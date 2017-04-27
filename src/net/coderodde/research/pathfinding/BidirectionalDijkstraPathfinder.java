package net.coderodde.research.pathfinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import net.coderodde.research.pathfinding.DijkstraPathfinder.NodeHeapWrapper;

public final class BidirectionalDijkstraPathfinder {

    public static List<DirectedGraphNode> 
            search(DirectedGraphNode source,
                   DirectedGraphNode target,
                   DirectedGraphWeightFunction weightFunction) {
        Queue<NodeHeapWrapper> openForward = new PriorityQueue<>();
        Queue<NodeHeapWrapper> openBackward = new PriorityQueue<>();
        
        Set<DirectedGraphNode> closedForward = new HashSet<>();
        Set<DirectedGraphNode> closedBackward = new HashSet<>();
        
        Map<DirectedGraphNode, Double> distanceForward = new HashMap<>();
        Map<DirectedGraphNode, Double> distanceBackward = new HashMap<>();
        
        Map<DirectedGraphNode, DirectedGraphNode> parentsForward;
        Map<DirectedGraphNode, DirectedGraphNode> parentsBackward;
        
        parentsForward = new HashMap<>();
        parentsBackward = new HashMap<>();
        
        Double bestPathLength = Double.POSITIVE_INFINITY;
        DirectedGraphNode meetingNode = null;
        
        openForward.add(new NodeHeapWrapper(source, 0.0));
        openBackward.add(new NodeHeapWrapper(target, 0.0));
        
        parentsForward.put(source, null);
        parentsBackward.put(target, null);
        
        distanceForward.put(source, 0.0);
        distanceBackward.put(target, 0.0);
        
        while (!openForward.isEmpty() && !openBackward.isEmpty()) {
            double tmpDistance = distanceForward.get(openForward.peek().node) +
                                 distanceBackward.get(openBackward.peek().node);
            
            if (tmpDistance >= bestPathLength) {
                return DijkstraPathfinder.tracebackPath(meetingNode,
                                                        parentsForward,
                                                        parentsBackward);
            }
            
            if (openForward.size() + closedForward.size() <
                    openBackward.size() + closedBackward.size()) {
                DirectedGraphNode current = openForward.remove().node;
                closedForward.add(current);
                
                for (DirectedGraphNode child : current.getChildren()) {
                    if (!closedForward.contains(child)) {
                        double tentativeDistance = 
                                distanceForward.get(current) +
                                weightFunction.get(current, child);
                        
                        if (!distanceForward.containsKey(child) ||
                             distanceForward.get(child) > tentativeDistance) {
                            distanceForward.put(child, tentativeDistance);
                            parentsForward.put(child, current);
                            openForward.add(
                                    new NodeHeapWrapper(child, 
                                                        tentativeDistance));
                            
                            if (closedBackward.contains(child)) {
                                double pathLength = 
                                        distanceBackward.get(child) +
                                        tentativeDistance;
                                
                                if (bestPathLength > pathLength) {
                                    bestPathLength = pathLength;
                                    meetingNode = child;
                                }
                            }
                        }
                    }
                }
            } else {
                DirectedGraphNode current = openBackward.remove().node;
                closedBackward.add(current);
                
                for (DirectedGraphNode parent : current.getParents()) {
                    if (!closedBackward.contains(parent)) {
                        double tentativeDistance = 
                                distanceBackward.get(current) + 
                                weightFunction.get(parent, current);
                        
                        if (!distanceBackward.containsKey(parent) ||
                             distanceBackward.get(parent) > tentativeDistance) {
                            distanceBackward.put(parent, tentativeDistance);
                            parentsBackward.put(parent, current);
                            openBackward.add(
                                    new NodeHeapWrapper(parent, 
                                                        tentativeDistance));
                            
                            if (closedForward.contains(parent)) {
                                double pathLength = 
                                        distanceForward.get(parent) + 
                                        tentativeDistance;
                                
                                if (bestPathLength > pathLength) {
                                    bestPathLength = pathLength;
                                    meetingNode = parent;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        throw new IllegalStateException("no path");
    }
}
