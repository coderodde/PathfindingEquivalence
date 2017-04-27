package net.coderodde.research.pathfinding;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Demo {

    private static final double ARC_LENGTH_FACTOR = 1.2;
    private static final double AREA_WIDTH = 1000.0;
    private static final double AREA_HEIGHT = 1000.0;
    private static final int NODES = 50_000;
    private static final int ARCS = 250_000;
    private static final int WARMUP_ITERATIONS = 100;
    
    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        GraphData graphData = getGraphData(random);
        DirectedGraphWeightFunction weightFunction = graphData.weightFunction;
        DirectedGraphNodeCoordinates coordinates = graphData.coordinates;
        HeuristicFunction heuristicFunction = 
                new HeuristicFunction(coordinates);
        List<DirectedGraphNode> nodeList = graphData.nodeList;
        
        warmup(nodeList, weightFunction, heuristicFunction, random);
        
        DirectedGraphNode source = choose(nodeList, random);
        DirectedGraphNode target = choose(nodeList, random);
        
        System.out.println("Seed = " + seed);
        
        long start = System.currentTimeMillis();
        
        List<DirectedGraphNode> dijkstraPath = 
                DijkstraPathfinder.search(source, target, weightFunction);
    
        long end = System.currentTimeMillis();
        
        System.out.println("DijkstraPathfinder in " + (end - start) + " ms.");
        
        start = System.currentTimeMillis();
        
        List<DirectedGraphNode> astarPath = 
                AStarPathfinder.search(source, 
                                       target, 
                                       weightFunction,
                                       heuristicFunction);
    
        end = System.currentTimeMillis();
        
        System.out.println("AStarPathfinder in " + (end - start) + " ms.");
        
        DirectedGraphWeightFunction modifiedWeightFunction = 
                modifyWeightFunction(nodeList,
                                     weightFunction, 
                                     heuristicFunction,
                                     target);
        
        start = System.currentTimeMillis();
        
        List<DirectedGraphNode> dijkstra2Path = 
                DijkstraPathfinder.search(source, 
                                          target, 
                                          modifiedWeightFunction);
    
        end = System.currentTimeMillis();
        
        System.out.println("DijkstraPathfinder with modified lengths in " + 
                (end - start) + " ms.");
        
        start = System.currentTimeMillis();
        
        List<DirectedGraphNode> bidirDijkstraPath = 
                BidirectionalDijkstraPathfinder.search(source, 
                                          target, 
                                          modifiedWeightFunction);
    
        end = System.currentTimeMillis();
        
        System.out.println("BidirectionalDijkstraPathfinder in " + 
                (end - start) + " ms.");
        
        
        System.out.println("Agreed: " + 
                (dijkstraPath.equals(astarPath) &&
                        dijkstraPath.equals(dijkstra2Path) &&
                        bidirDijkstraPath.equals(astarPath)));
    }
    
    static void warmup(List<DirectedGraphNode> nodeList,
                       DirectedGraphWeightFunction weightFunction,
                       HeuristicFunction heuristicFunction,
                       Random random) {
        System.out.println("Warming up...");
        
        for (int i = 0; i < WARMUP_ITERATIONS; ++i) {
            DirectedGraphNode source = choose(nodeList, random);
            DirectedGraphNode target = choose(nodeList, random);
            
            try {
                DijkstraPathfinder.search(source, target, weightFunction);
                AStarPathfinder.search(source, 
                                       target, 
                                       weightFunction, 
                                       heuristicFunction);
                BidirectionalDijkstraPathfinder.search(source,
                                                       target, 
                                                       weightFunction);
            } catch (IllegalStateException ex) {
                
            }
        }
        
        System.out.println("Warming up done!");
    }
    
    static DirectedGraphWeightFunction 
        modifyWeightFunction(List<DirectedGraphNode> nodeList,
                             DirectedGraphWeightFunction weightFunction,
                             HeuristicFunction heuristicFunction,
                             DirectedGraphNode target) {
        DirectedGraphWeightFunction modifiedWeightFunction = 
                new DirectedGraphWeightFunction();
        
        for (DirectedGraphNode node : nodeList) {
            for (DirectedGraphNode child : node.getChildren()) {
                double length = weightFunction.get(node, child);
                length += heuristicFunction.getEstimate(child, target);
                length -= heuristicFunction.getEstimate(node, target);
                modifiedWeightFunction.put(node, child, length);
            }
        }
        
        return modifiedWeightFunction;
    }
    
    static GraphData getGraphData(Random random) {
        return getGraphData(ARC_LENGTH_FACTOR,
                            AREA_WIDTH,
                            AREA_HEIGHT,
                            NODES,
                            ARCS,
                            random);
    }
    
    static GraphData getGraphData(double arcLengthFactor,
                                  double areaWidth,
                                  double areaHeight,
                                  int nodes,
                                  int arcs,
                                  Random random) {
        GraphData graphData = new GraphData();
        
        List<DirectedGraphNode> nodeList = new ArrayList<>(nodes);
        DirectedGraphNodeCoordinates coordinates = 
                new DirectedGraphNodeCoordinates();
        DirectedGraphWeightFunction weightFunction = 
                new DirectedGraphWeightFunction();
        
        for (int i = 0; i < nodes; ++i) {
            DirectedGraphNode node = new DirectedGraphNode();
            coordinates.put(node, getRandomPoint(areaWidth, 
                                                 areaHeight,
                                                 random));
            nodeList.add(node);
        }
        
        for (int i = 0; i < arcs; ++i) {
            DirectedGraphNode tail = choose(nodeList, random);
            DirectedGraphNode head = choose(nodeList, random);
            tail.addChild(head);
            double distance = coordinates.get(tail)
                                         .distance(coordinates.get(head));
            weightFunction.put(tail, head, arcLengthFactor * distance);
        }
        
        graphData.nodeList = nodeList;
        graphData.weightFunction = weightFunction;
        graphData.coordinates = coordinates;
        return graphData;
    }
    
    static Point2D.Double getRandomPoint(double areaWidth,
                                         double areaHeight,
                                         Random random) {
        return new Point2D.Double(areaWidth * random.nextDouble(), 
                                  areaHeight * random.nextDouble());
    }
    
    static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
    
    static class GraphData {
        List<DirectedGraphNode> nodeList;
        DirectedGraphWeightFunction weightFunction;
        DirectedGraphNodeCoordinates coordinates;
    }
}
