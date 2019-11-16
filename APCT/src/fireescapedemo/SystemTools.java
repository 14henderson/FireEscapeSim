package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.*;

public class SystemTools {
    public AStarPath pathFinder;
    public SystemTools(Tile startNode, Tile[][] map){
        this.pathFinder = new AStarPath(startNode,map);
    }


    public Queue<Tile> getOpenedNodes(){return this.pathFinder.openedNodes;}

    public class AStarPath{
        private PriorityQueue<Tile> unopenedNodes;
        private PriorityQueue<Tile> openedNodes;
        private ArrayList<Point2D> velocitys;
        private Tile[][] map;
        private Tile startNode, endNode;
        boolean foundExit;
        private int endX,endY;
        private double range;

        public AStarPath(Tile startNode, Tile[][] map){
            this.unopenedNodes = new PriorityQueue<>();
            this.openedNodes = new PriorityQueue<>();
            this.velocitys = new ArrayList<>();
            this.map = map;
            this.startNode = this.map[startNode.getGridX()][startNode.getGridY()];
            this.endNode = findClosestExit(startNode);
            if(this.endNode != null){
                this.endX = endNode.getActualCords()[0];
                this.endY = endNode.getActualCords()[1];
            }else{
                this.endX = 0;
                this.endY = 0;
            }
            this.range = 0;
        }

        public boolean findPath(boolean avoiding) {
            if(this.endNode != null){
                int nodeX, nodeY;
                double newDis;
                Tile currentNode = null, start = this.map[this.startNode.getGridX()][this.startNode.getGridY()],
                        goal = this.map[this.endNode.getGridX()][this.endNode.getGridY()];
                this.unopenedNodes.add(start);
                System.out.println("Map length [x]: " + this.map.length + ", [y]: " + this.map[0].length);
                while (this.unopenedNodes.size() != 0) {

                    System.out.println("Size: " + this.openedNodes.size());
                    System.out.println("");
                    currentNode = this.unopenedNodes.poll();
                    this.openedNodes.add(currentNode);
                    if(currentNode == goal){break;}
                    nodeX = currentNode.getGridX();
                    nodeY = currentNode.getGridY();;

                    for (Tile t : getNeighbors(nodeX, nodeY,currentNode,avoiding)) {
                        newDis = currentNode.getGCost() + calculateDistance(currentNode, t);
                        if(this.openedNodes.contains(t)){continue;}
                        if (newDis < t.getGCost() || !this.unopenedNodes.contains(t)) {
                            t.setGCost(newDis);
                            t.setHCost(calculateDistance(t, goal));
                            t.calculateFCost();
                            t.setParent(currentNode);
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
                        Random rand = new Random();
                        this.range = Math.round((rand.nextDouble()+0.5) * 100.0) / 100.0;
                        double vel = 0;
                        do {
                            int prevX = currentNode.getActualCords()[0], prevY = currentNode.getActualCords()[1], proX =
                                    currentNode.getParent().getActualCords()[0], proY = currentNode.getParent().getActualCords()[1];
                            System.out.println("x: " + proX + ", y: " + proY);
                            if(prevX== proX){

                                vel = prevY > proY ? range : -range;
                                this.velocitys.add(new Point2D(0,vel));
                            }else{
                                vel = prevX > proX ? range : -range;
                                this.velocitys.add(new Point2D(vel,0));
                            }
                            System.out.println("Count " + counter);
                            System.out.println("x: " + currentNode.getActualCords()[0] + ", y: " + currentNode.getActualCords()[1]);
                            //currentNode.block.setFill(Color.RED);
                            //currentNode.setType(Tile.BlockType.Path);
                            currentNode = currentNode.getParent();
                            counter++;
                        }while (currentNode != start);
                    }
                    Collections.reverse(this.velocitys);
                    return true;
                }
            }
            return false;
        }


        private int calculateDistance(Tile a, Tile b){
            int maxX = Math.abs(a.getActualCords()[0] - b.getActualCords()[0]);
            int maxY = Math.abs(a.getActualCords()[1] - b.getActualCords()[1]);
            if(maxY < maxX)return 14 * maxY + 10 * (maxX - maxY);
            return 14 * maxX + 10 * (maxY - maxX);

        }

        private ArrayList<Tile> getNeighbors(int nodeX,int nodeY, Tile node, boolean avoiding){
            ArrayList<Tile> nodesToExplore = new ArrayList<>();
            int i, j, aX, aY, newX, newY, mapLength = this.map.length;
            boolean add = true;
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
                            if((i == 0 && dirAcess[0])){
                                if(avoiding && this.map[newX][newY].type.equals(Tile.BlockType.Employee)){
                                    add = false;
                                }
                                if(add){nodesToExplore.add(this.map[newX][newY]);}
                            }else if(j == 0 && dirAcess[1]){
                                if(avoiding && this.map[newX][newY].type.equals(Tile.BlockType.Employee)){
                                    add = false;
                                }
                                if(add){nodesToExplore.add(this.map[newX][newY]);}
                            }/*else if(dirAcess[0] && dirAcess[1]){
                                nodesToExplore.add(this.map[newX][newY]);
                            }*/
                        }
                        add = true;
                    }
                }
            }
            return nodesToExplore;
        }

        public Tile findClosestExit(Tile startNode){
            //  for iteration       the amount of blocks in grid                         total amount of blocks to check before lockout
            int lockoutCounter = 0, lockoutLocal = this.map.length * this.map[0].length, lockoutMax = 182;
            boolean exitCondition = false, success = false;
            ArrayList<Tile> neighbors = new ArrayList<>();
            Queue<Tile> unopenList = new LinkedList<>();
            Queue<Tile> openList = new LinkedList<>();
            unopenList.add(startNode);
            Tile currentTile, exitTile = null;
            while(!exitCondition){
                System.out.println("lockoutCounter: " + lockoutCounter);
                if(lockoutCounter < lockoutLocal && lockoutCounter < lockoutMax){
                    currentTile = unopenList.poll();
                    lockoutCounter++;
                    openList.add(currentTile);
                    //currentTile.setColour(Color.LIGHTBLUE);
                    neighbors = getNeighbors(currentTile.getGridX(),currentTile.getGridY(),currentTile,false);
                    for (Tile neighbor : neighbors){
                        if(!openList.contains(neighbor) && !unopenList.contains(neighbor)){
                            if(neighbor.type.equals(Tile.BlockType.Exit)){
                                exitTile = neighbor;
                                exitCondition = true;
                                success = true;
                                break;
                            }else{
                                unopenList.add(neighbor);
                            }
                        }

                    }
                    System.out.println("cur cords: x: " + currentTile.getGridX() + ", y: " + currentTile.getGridY());
                    neighbors.clear();
                    //if exit not found
                }else{this.endNode = null; exitCondition = true; success = false;}
            }
            System.out.println("Finished, success: " + success);
            this.foundExit = success;
            return exitTile;
        }

        public PriorityQueue<Tile> getPath(){return this.openedNodes;}
        public double getRange(){return this.range;}
        public boolean exitFound(){return this.foundExit;}
        public ArrayList<Point2D> getVelocities() {return this.velocitys;}
    }

}
