package fireescapedemo;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;

import java.awt.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class SystemTools {
    public AStarPath pathFinder;
    public SystemTools(Tile startNode, Tile endNode, Tile[][] map){
        this.pathFinder = new AStarPath(startNode,endNode,map);
    }

    public Queue<Tile> getOpenedNodes(){return this.pathFinder.openedNodes;}

    public class AStarPath{
        private PriorityQueue<Tile> unopenedNodes;
        private PriorityQueue<Tile> openedNodes;
        private Tile[][] map;
        private Tile startNode, endNode;
        private int endX,endY;
        public AStarPath(Tile startNode, Tile exitNode, Tile[][] map){
            this.unopenedNodes = new PriorityQueue<>();
            this.unopenedNodes.add(startNode);
            this.openedNodes = new PriorityQueue<>();
            this.startNode = startNode;
            this.endNode = exitNode;
            this.map = map;
            this.endX = endNode.gridX;
            this.endY = endNode.gridY;
        }

        public Queue<Tile> findPath(){
            int i,j,k,nodeX,nodeY;
            double currentPathScore = 0;
            Tile expandingNode = null,currentNode;

            ArrayList<Tile> nodesToExplore = new ArrayList<>();
            currentNode = this.unopenedNodes.poll();
            this.openedNodes.add(currentNode);
            while(!isComplete(currentNode)){
                nodeX = currentNode.gridX;
                nodeY = currentNode.gridY;
                nodesToExplore = getNeighbors(nodesToExplore,currentNode,expandingNode,nodeX,nodeY,currentPathScore);

                for(Tile t : nodesToExplore){
                    t.setGCost(t.getGCost() + currentPathScore);
                    t.calculateFCost();
                    this.unopenedNodes.add(t);
                }

                System.out.println("Size: " + this.openedNodes.size());
                System.out.println("");
                currentNode = this.unopenedNodes.poll();
                this.openedNodes.add(currentNode);
                currentPathScore += currentNode.getGCost();
                nodesToExplore.clear();

            }
            this.openedNodes.add(currentNode);
            System.out.println("Finished");
            int counter = 0, maxCount;
            int absX = Math.abs(this.startNode.gridX - this.endNode.gridX);
            int absY = Math.abs(this.startNode.gridY - this.endNode.gridY);

            maxCount = absX + absY;

            for(Tile t :  this.openedNodes){

                if(t == endNode){break;}
                t.block.setFill(Color.RED);
                counter++;
            }
            return this.openedNodes;
        }

        private boolean isComplete(Tile node){return node == this.endNode;}

        private ArrayList<Tile> getNeighbors(ArrayList<Tile> nodesToExplore, Tile currentNode, Tile expandingNode, int nodeX,int nodeY, double currentPathScore){
            //Horizontal and Vertical Check
            if(currentNode.getAccess(0) && nodeY != 0){
                expandingNode = map[nodeX][nodeY-1];
                if(!this.openedNodes.contains(expandingNode)&& !this.unopenedNodes.contains(expandingNode)) {
                    expandingNode.computeDistance(this.endNode);
                    nodesToExplore.add(expandingNode);
                }
            }
            if(currentNode.getAccess(1) && nodeX != map.length-1){
                expandingNode = map[nodeX+1][nodeY];
                if(!this.openedNodes.contains(expandingNode)&& !this.unopenedNodes.contains(expandingNode)) {
                    expandingNode.computeDistance(this.endNode);
                    nodesToExplore.add(expandingNode);
                }
            }
            if(currentNode.getAccess(2) && nodeY != map.length-1){
                expandingNode = map[nodeX][nodeY + 1];
                if(!this.openedNodes.contains(expandingNode)&& !this.unopenedNodes.contains(expandingNode)) {
                    expandingNode.computeDistance(this.endNode);
                    nodesToExplore.add(expandingNode);
                }
            }
            if(currentNode.getAccess(3) && nodeX != 0){
                expandingNode = map[nodeX-1][nodeY];
                if(!this.openedNodes.contains(expandingNode) && !this.unopenedNodes.contains(expandingNode)) {
                    expandingNode.computeDistance(this.endNode);
                    nodesToExplore.add(expandingNode);
                }
            }

            //Diaganol Checks
            if((currentNode.getAccess(0) && currentNode.getAccess(1)) && (nodeX != this.map.length-1 && nodeY != 0)){
                expandingNode = map[nodeX+1][nodeY-1];
                if(!this.openedNodes.contains(expandingNode)&& !this.unopenedNodes.contains(expandingNode)) {
                    expandingNode.setGCost(2);
                    expandingNode.computeDistance(this.endNode);
                    nodesToExplore.add(expandingNode);
                }
            }
            if((currentNode.getAccess(1) && currentNode.getAccess(2)) && (nodeX != this.map.length-1 && nodeY != this.map.length-1)){
                expandingNode = map[nodeX+1][nodeY+1];
                if(!this.openedNodes.contains(expandingNode)&& !this.unopenedNodes.contains(expandingNode)) {
                    expandingNode.setGCost(2);
                    expandingNode.computeDistance(this.endNode);
                    nodesToExplore.add(expandingNode);
                }
            }
            if((currentNode.getAccess(2) && currentNode.getAccess(3)) && (nodeX != 0 && nodeY != this.map.length-1)){
                expandingNode = map[nodeX-1][nodeY+1];
                if(!this.openedNodes.contains(expandingNode)&& !this.unopenedNodes.contains(expandingNode)) {
                    expandingNode.setGCost(2);
                    expandingNode.computeDistance(this.endNode);
                    nodesToExplore.add(expandingNode);
                }
            }if((currentNode.getAccess(3) && currentNode.getAccess(0)) && (nodeX != 0 && nodeY != 0)){
                expandingNode = map[nodeX-1][nodeY-1];
                if(!this.openedNodes.contains(expandingNode)&& !this.unopenedNodes.contains(expandingNode)) {
                    expandingNode.setGCost(2);
                    expandingNode.computeDistance(this.endNode);
                    nodesToExplore.add(expandingNode);
                }
            }

            return nodesToExplore;
        }

    }

}
