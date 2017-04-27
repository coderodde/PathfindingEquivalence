package net.coderodde.research.pathfinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import net.coderodde.research.pathfinding.DijkstraPathfinder.NodeHeapWrapper;
import static net.coderodde.research.pathfinding.DijkstraPathfinder.tracebackPath;

public class AStarPathfinder {

    public static List<DirectedGraphNode> 
        search(DirectedGraphNode source,
               DirectedGraphNode target,
               DirectedGraphWeightFunction weightFunction,
               HeuristicFunction heuristicFunction) {
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
                   open.add(new NodeHeapWrapper(childNode, 
                           heuristicFunction.getEstimate(childNode, target) + 
                                   tentativeDistance));
               }
           }
       }
       
       throw new IllegalStateException("no path");
    }
}
