package fireescapedemo;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class SystemTools {


    public class AStarPath{
        private PriorityQueue<Tile> unopenedNodes;
        private Queue<Tile> openedNodes;
        private Tile[][] map;
        private Tile endNode;
        private int endX,endY;
        public AStarPath(Tile startNode, Tile exitNode, Tile[][] map){
            this.unopenedNodes = new PriorityQueue<>();
            this.unopenedNodes.add(startNode);
            this.openedNodes = new LinkedList<>();
            this.endNode = exitNode;
            this.map = map;
            this.endX = endNode.gridX;
            this.endY = endNode.gridY;
        }

        public Queue<Tile> findPath(){
            int i,j,k,nodeX,nodeY;
            double currentPathScore = 0;
            Tile expandingNode,currentNode;

            ArrayList<Tile> nodesToExplore = new ArrayList<>();
            currentNode = this.unopenedNodes.poll();
            this.openedNodes.add(currentNode);

            while(!isComplete(currentNode)){
                nodeX = currentNode.gridX;
                nodeY = currentNode.gridY;
                if(nodesToExplore.size() == 0){
                    if(currentNode.getAccess(3) && nodeX != 0){
                        expandingNode = map[nodeX-1][nodeY];
                        expandingNode.addGCost(currentPathScore);
                        expandingNode.computeDistance(this.endNode);
                        expandingNode.calculateFCost();
                        nodesToExplore.add(expandingNode);
                    }
                    if(currentNode.getAccess(1) && nodeX != map.length-1){
                        expandingNode = map[nodeX+1][nodeY];
                        expandingNode.addGCost(currentPathScore);
                        expandingNode.computeDistance(this.endNode);
                        expandingNode.calculateFCost();
                        nodesToExplore.add(expandingNode);
                    }
                    if(currentNode.getAccess(0) && nodeY != 0){
                        expandingNode = map[nodeX][nodeY - 1];
                        expandingNode.addGCost(currentPathScore);
                        expandingNode.computeDistance(this.endNode);
                        expandingNode.calculateFCost();
                        nodesToExplore.add(expandingNode);
                    }
                    if(currentNode.getAccess(2) && nodeY != map.length-1){
                        expandingNode = map[nodeX][nodeY+1];
                        expandingNode.addGCost(currentPathScore);
                        expandingNode.computeDistance(this.endNode);
                        expandingNode.calculateFCost();
                        nodesToExplore.add(expandingNode);
                    }

                }
                for(Tile t : nodesToExplore){
                    this.unopenedNodes.add(t);
                }


                currentNode = this.unopenedNodes.poll();
                this.openedNodes.add(currentNode);
                currentPathScore += currentNode.getGCost();

            }
            System.out.println("Finished");
            return this.openedNodes;
        }

        private boolean isComplete(Tile node){return node == this.endNode;}



    }

}
