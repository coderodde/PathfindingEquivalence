package net.coderodde.research.pathfinding;

import java.util.HashMap;
import java.util.Map;

public class DirectedGraphWeightFunction {

    private final Map<DirectedGraphNode, Map<DirectedGraphNode, Double>> map = 
            new HashMap<>();
    
    public void put(DirectedGraphNode tail, 
                    DirectedGraphNode head,
                    Double weight) {
        if (!map.containsKey(tail)) {
            map.put(tail, new HashMap<>());
        }
        
        map.get(tail).put(head, weight);
    }
    
    public Double get(DirectedGraphNode tail, DirectedGraphNode head) {
        return map.get(tail).get(head);
    }
}
