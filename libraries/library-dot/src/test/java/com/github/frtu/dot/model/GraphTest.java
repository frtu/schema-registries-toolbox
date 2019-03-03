package com.github.frtu.dot.model;

import com.github.frtu.dot.SuperGraph;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GraphTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphTest.class);

    public static final String GRAPH_ID = "GraphID";

    @Test
    public void createBaseGraph() {
        Graph graph = new Graph(GRAPH_ID);
        Assert.assertEquals(GRAPH_ID, graph.getId());
        Assert.assertNotNull("List should not be null", graph.getAllNodes());
        Assert.assertNull("Not initialized", graph.getRootNode());
        Assert.assertNull("Not initialized", graph.getCurrentParentNode());
        Assert.assertTrue("Not initialized", graph.getAllNodes().isEmpty());
    }

    @Test
    public void testGraph() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        Graph graph = new Graph(GRAPH_ID);
        final GraphNode expectedRootNode = buildGraph(graph);

        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        final GraphNode actualRootNode = graph.getRootNode();
        Assert.assertEquals(expectedRootNode, actualRootNode);
        Assert.assertEquals(3, actualRootNode.children.size());
        Assert.assertTrue(actualRootNode.children.stream().allMatch(node -> node.label.startsWith("label")));

        // ORDERED ADD
        final List<GraphNode> node3Children = graph.getNode("id3").children;
        Assert.assertEquals(2, node3Children.size());
        Assert.assertTrue(node3Children.stream().allMatch(node -> node.label.startsWith("childLabel3-")));

        // UNORDERED ADD
        final List<GraphNode> node2Children = graph.getNode("id2").children;
        Assert.assertEquals(2, node2Children.size());
        Assert.assertTrue(node2Children.stream().allMatch(node -> node.label.startsWith("childLabel2-")));

        final List<GraphNode> children32 = graph.getNode("child3_2").children;
        Assert.assertTrue(children32.stream().allMatch(node -> node.label.startsWith("subchildLabel3-2-")));
    }

    public static GraphNode buildGraph(Graph graph) {
        //--------------------------------------
        // 2. Run tests
        //--------------------------------------
        // First node is always the root & latest known current
        final String rootId = "root";
        final String rootLabel = "rootLabel";
        final GraphNode expectedRootNode = graph.addNode(rootId, rootLabel, PolygonShapeDotEnum.POLYGON);

        // Adding child node happen on the latest known
        graph.addNode("id1", "label1", PolygonShapeDotEnum.BOX);
        final GraphNode node2 = graph.addNode("id2", "label2", PolygonShapeDotEnum.BOX);

        // Adding child node and set it as the current -> create level 2
        graph.addNodeMoveToChild("id3", "label3", PolygonShapeDotEnum.BOX);
        graph.addNode("child3_1", "childLabel3-1", PolygonShapeDotEnum.CIRCLE);

        // Adding child node and set it as the current -> create level 3
        graph.addNodeMoveToChild("child3_2", "childLabel3-2", PolygonShapeDotEnum.CIRCLE);
        graph.addNode("subchild3_2_1", "subchildLabel3-2-1", PolygonShapeDotEnum.CIRCLE);

        // Adding child node to a previously known parent
        graph.addNodeMoveParent("child2_2", "childLabel2-2", PolygonShapeDotEnum.ELLIPSE, node2);
        graph.addNode("child2_3", "childLabel2-3", PolygonShapeDotEnum.CIRCLE);


        if (LOGGER.isDebugEnabled()) {
            // Graph::toString is a long method
            LOGGER.debug(graph.toString());
        }
        return expectedRootNode;
    }

    @Test
    public void testGraphWithEdge() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        Graph graph = new Graph(GRAPH_ID);
        final GraphNode expectedRootNode = buildGraph(graph);
        graph.addEdge("child2_2", "subchild3_2_1");

        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        final List<GraphEdge> allEdges = graph.getAllEdges();
        Assert.assertEquals(1, allEdges.size());

        final GraphEdge graphEdge = allEdges.get(0);
        Assert.assertEquals("child2_2", graphEdge.getSourceId());
        Assert.assertEquals("subchild3_2_1", graphEdge.getTargetId());
    }

    @Test
    public void testSuperGraph() {
        SuperGraph superGraph = buildSuperGraph();

        if (LOGGER.isDebugEnabled()) {
            // Graph::toString is a long method
            LOGGER.debug(superGraph.toString());
        }
        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        Assert.assertEquals(2, superGraph.getSubgraphs().size());
        // TODO Add more tests
    }

    public static SuperGraph buildSuperGraph() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        final Graph cluster_0 = new Graph("cluster_0");

        cluster_0.newGraphAttributes()
                .setStyle("filled")
                .setColor("lightgrey");

        cluster_0.newNodeAttributes()
                .setStyle("filled")
                .setColor("white");

        cluster_0.addEdge("a0", "a1", "a2", "a3");


        final Graph cluster_1 = new Graph("cluster_1");

        cluster_1.newNodeAttributes().setStyle("filled");
        cluster_1.newEdgeAttributes().setColor("red");

        cluster_1.addEdge("b0", "b1", "b2", "b3");
        //--------------------------------------
        // 2. Run tests
        //--------------------------------------
        SuperGraph superGraph = new SuperGraph("G");
        superGraph.setRankdir("LR");

        superGraph.addSubgraph(cluster_0);
        superGraph.addSubgraph(cluster_1);

        final GraphNode start = superGraph.addNode("start", PolygonShapeDotEnum.MDIAMOND);
        final GraphNode end = superGraph.addNode("end", PolygonShapeDotEnum.MDIAMOND);

        superGraph.addEdge(start, "a0");
        superGraph.addEdge(start, "b0");
        superGraph.addEdge("a1", "b3");
        superGraph.addEdge("b2", "a3");
        superGraph.addEdge("a3", "a0");
        superGraph.addEdge("a3", end);
        superGraph.addEdge("b3", end);
        return superGraph;
    }
}