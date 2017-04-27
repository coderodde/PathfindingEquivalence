package net.coderodde.research.pathfinding;

public class HeuristicFunction {

    private final DirectedGraphNodeCoordinates coordinates;
    
    public HeuristicFunction(DirectedGraphNodeCoordinates coordinates) {
        this.coordinates = coordinates;
    }
    
    public double getEstimate(DirectedGraphNode node1, 
                              DirectedGraphNode node2) {
        return coordinates.get(node1).distance(coordinates.get(node2));
    }
}
