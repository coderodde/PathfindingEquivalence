package net.coderodde.research.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class DijkstraPathfinder {

    public static List<DirectedGraphNode> 
        search(DirectedGraphNode source,
               DirectedGraphNode target,
               DirectedGraphWeightFunction weightFunction) {
       Queue<NodeHeapWrapper> open = new PriorityQueue<>();
       Set<DirectedGraphNode> closed = new HashSet<>();
       Map<DirectedGraphNode, Double> distance = new HashMap<>();
       Map<DirectedGraphNode, DirectedGraphNode> parents = new HashMap<>();
       
       open.add(new NodeHeapWrapper(source, 0.0));
       distance.put(source, 0.0);
       parents.put(source, null);
       
       while (!open.isEmpty()) {
           DirectedGraphNode currentNode = open.remove().node;
           
           if (currentNode.equals(target)) {
               return tracebackPath(target, parents);
           }
           
           if (closed.contains(currentNode)) {
               continue;
           }
           
           closed.add(currentNode); 
           
           for (DirectedGraphNode childNode : currentNode.getChildren()) {
               if (closed.contains(childNode)) {
                   continue;
               }
               
               double tentativeDistance = distance.get(currentNode) + 
                       weightFunction.get(currentNode, childNode);
               
               if (!distance.containsKey(childNode)
                       || distance.get(childNode) > tentativeDistance) {
                   distance.put(childNode, tentativeDistance);
                   parents.put(childNode, currentNode);
                   open.add(new NodeHeapWrapper(childNode, tentativeDistance));
               }
           }
       }
       
       throw new IllegalStateException("no path");
    }
        
    static final class NodeHeapWrapper implements Comparable<NodeHeapWrapper> {
        NodeHeapWrapper(DirectedGraphNode node, double cost) {
            this.node = node;
            this.cost = cost;
        }
        
        DirectedGraphNode node;
        double cost;

        @Override
        public int compareTo(NodeHeapWrapper o) {
            return Double.compare(cost, o.cost);
        }
    }
    
    static List<DirectedGraphNode> tracebackPath(
              DirectedGraphNode meetingNode,
              Map<DirectedGraphNode, DirectedGraphNode> forwardSearchParents,
              Map<DirectedGraphNode, DirectedGraphNode> backwardSearchParents) {
        List<DirectedGraphNode> path = new ArrayList<>();
        DirectedGraphNode currentNode = meetingNode;
        
        while (currentNode != null) {
            path.add(currentNode);
            currentNode = forwardSearchParents.get(currentNode);
        }
        
        Collections.reverse(path);
        
        if (backwardSearchParents != null) {
            currentNode = backwardSearchParents.get(meetingNode);

            while (currentNode != null) {
                path.add(currentNode);
                currentNode = backwardSearchParents.get(currentNode);
            }
        }
        
        return path;
    }
    
    static List<DirectedGraphNode> tracebackPath(
            DirectedGraphNode targetNode,
            Map<DirectedGraphNode, DirectedGraphNode> parents) {
        return tracebackPath(targetNode, parents, null);
    }
}
