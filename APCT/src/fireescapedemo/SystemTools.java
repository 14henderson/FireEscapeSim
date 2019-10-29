package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;

import java.net.URISyntaxException;
import java.util.*;

public class SystemTools {
    public AStarPath pathFinder;
    public SystemTools(Tile startNode, Tile endNode, Tile[][] map){
        this.pathFinder = new AStarPath(startNode,endNode,map);
    }

    public Queue<Tile> getOpenedNodes(){return this.pathFinder.openedNodes;}

    public class AStarPath{
        private PriorityQueue<Tile> unopenedNodes;
        private PriorityQueue<Tile> openedNodes;
        private ArrayList<Point2D> velocitys;
        private Tile[][] map;
        private Tile startNode, endNode;
        private int endX,endY;
        public AStarPath(Tile startNode, Tile exitNode, Tile[][] map){
            this.unopenedNodes = new PriorityQueue<>();
            this.openedNodes = new PriorityQueue<>();
            this.velocitys = new ArrayList<>();
            this.startNode = startNode;
            this.endNode = exitNode;
            this.map = map;
            this.endX = endNode.gridX;
            this.endY = endNode.gridY;
        }

        public boolean findPath() {
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

                for (Tile t : getNeighbors(nodeX, nodeY,currentNode)) {
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
            Point2D point;
            System.out.println("Finished");
            if(this.openedNodes.contains(goal)){
                int counter = 1;
                currentNode = goal;
                boolean test = true;
                if (test) {
                    do {
                        int prevX = currentNode.gridX, prevY = currentNode.gridY, proX = currentNode.getPerant().gridX, proY = currentNode.getPerant().gridY,vel =0;
                        System.out.println("x: " + proX + ", y: " + proY);
                        if(prevX== proX){
                            vel = prevY > proY ? 1 : -1;
                            this.velocitys.add(new Point2D(0,vel));
                        }else{
                            vel = prevX > proX ? 1 : -1;
                            this.velocitys.add(new Point2D(vel,0));
                        }
                        System.out.println("Count " + counter);
                        System.out.println("x: " + currentNode.gridX + ", y: " + currentNode.gridY);
                        currentNode.block.setFill(Color.RED);
                        currentNode = currentNode.getPerant();
                        counter++;
                    }while (currentNode != start);
                }
                Collections.reverse(this.velocitys);
                return true;
            }
            return false;
        }


        private int calculateDistance(Tile a, Tile b){
            int maxX = Math.abs(a.gridX - b.gridX);
            int maxY = Math.abs(a.gridY - b.gridY);
            if(maxY < maxX)return 14 * maxY + 10 * (maxX - maxY);
            return 14 * maxX + 10 * (maxY - maxX);

        }

        private ArrayList<Tile> getNeighbors(int nodeX,int nodeY, Tile node){
            ArrayList<Tile> nodesToExplore = new ArrayList<>();
            int i, j, aX, aY, newX, newY, mapLength = this.map.length;
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
                            }/*else if(dirAcess[0] && dirAcess[1]){
                                nodesToExplore.add(this.map[newX][newY]);
                            }*/
                        }
                    }
                }
            }
            return nodesToExplore;
        }

        public PriorityQueue<Tile> getPath(){return this.openedNodes;}
        public ArrayList<Point2D> getVelocities() {return this.velocitys;}
    }

}
