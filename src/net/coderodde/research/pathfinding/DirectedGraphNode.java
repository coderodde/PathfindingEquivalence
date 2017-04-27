package net.coderodde.research.pathfinding;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class DirectedGraphNode {

    private final Set<DirectedGraphNode> children = new HashSet<>();
    private final Set<DirectedGraphNode> parents = new HashSet<>();
    
    public void addChild(DirectedGraphNode child) {
        children.add(child);
        child.parents.add(this);
    }
    
    public Set<DirectedGraphNode> getChildren() {
        return Collections.<DirectedGraphNode>unmodifiableSet(children);
    }
    
    public Set<DirectedGraphNode> getParents() {
        return Collections.<DirectedGraphNode>unmodifiableSet(parents);
    }
}
