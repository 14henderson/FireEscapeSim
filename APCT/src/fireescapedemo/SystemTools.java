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
            this.openedNodes = new PriorityQueue<>();
            this.startNode = startNode;
            this.endNode = exitNode;
            this.map = map;
            this.endX = endNode.gridX;
            this.endY = endNode.gridY;
        }

        public Queue<Tile> findPath() {
            int nodeX, nodeY;
            double newDis;
            Tile currentNode = null, start = this.map[this.startNode.gridX][this.startNode.gridY],
                    goal = this.map[this.endNode.gridX][this.endNode.gridY];
            this.unopenedNodes.add(start);

            while (this.unopenedNodes.size() != 0) {

                System.out.println("Size: " + this.openedNodes.size());
                System.out.println("");
                currentNode = this.unopenedNodes.poll();
                this.openedNodes.add(currentNode);
                if(currentNode == goal){break;}
                nodeX = currentNode.gridX;
                nodeY = currentNode.gridY;

                for (Tile t : getNeighbors(nodeX, nodeY)) {
                    newDis = currentNode.getGCost() + calculateDistance(currentNode, t);
                    if(this.openedNodes.contains(t)){continue;}
                    if (newDis < t.getGCost() || !this.unopenedNodes.contains(t)) {
                        t.setGCost(newDis);
                        t.setHCost(calculateDistance(t, goal));
                        t.calculateFCost();
                        t.setPerant(currentNode);
                        if (!this.unopenedNodes.contains(t)) {
                            this.unopenedNodes.add(t);
                        }
                    }
                }


            }

            System.out.println("Finished");
            int counter = 1;
            currentNode = goal;
            boolean test = true;
            if (test) {
                while (currentNode != start) {
                    System.out.println("Count " + counter);
                    System.out.println("x: " + currentNode.gridX + ", y: " + currentNode.gridY);
                    currentNode.block.setFill(Color.RED);
                    currentNode = currentNode.getPerant();
                    counter++;
                }
            }
            else {
                for (Tile t : this.openedNodes) {
                    System.out.println("Count " + counter);
                    System.out.println("x: " + currentNode.gridX + ", y: " + currentNode.gridY);
                    t.block.setFill(Color.RED);
                }
            }

            return this.openedNodes;
        }


        private int calculateDistance(Tile a, Tile b){
            int maxX = Math.abs(a.gridX - b.gridX);
            int maxY = Math.abs(a.gridY - b.gridY);
            if(maxY < maxX)return 14 * maxY + 10 * (maxX - maxY);
            return 14 * maxX + 10 * (maxY - maxX);

        }

        private ArrayList<Tile> getNeighbors(int nodeX,int nodeY){
            ArrayList<Tile> nodesToExplore = new ArrayList<>();
            int i, j, aX, aY, newX, newY, mapLength = this.map.length;
            Tile node = this.map[nodeX][nodeY];
            boolean[] dirAcess = new boolean[2];
            for(i = -1; i < 2; i++){
                for(j = -1; j < 2; j++){
                    if(i == 0 && j == 0){
                        continue;
                    }
                    else{
                        aY = i == -1 ? 3 : 1;
                        aX = j == -1 ? 0 : 2;
                        dirAcess[0] = node.getAccess(aX);
                        dirAcess[1] = node.getAccess(aY);
                        newX = i + nodeX;
                        newY = j + nodeY;

                        if(newX < mapLength && newX > -1 && newY < mapLength && newY > -1 ){
                            if(i == 0 && dirAcess[0]){
                                nodesToExplore.add(this.map[newX][newY]);
                            }else if(j == 0 && dirAcess[1]){
                                nodesToExplore.add(this.map[newX][newY]);
                            }else if(dirAcess[0] && dirAcess[1]){
                                nodesToExplore.add(this.map[newX][newY]);
                            }
                        }
                    }
                }
            }
            return nodesToExplore;
        }

    }

}
