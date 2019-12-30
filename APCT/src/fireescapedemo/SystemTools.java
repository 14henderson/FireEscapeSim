package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.*;

public class SystemTools {
    public AStarPath pathFinder;
    public SystemTools(Tile startNode, Tile[][] map, ArrayList<Line> walls){
        this.pathFinder = new AStarPath(startNode,map, walls);
    }


    public Queue<Tile> getOpenedNodes(){return this.pathFinder.openedNodes;}

    public class AStarPath {
        private PriorityQueue<Tile> unopenedNodes;
        private PriorityQueue<Tile> openedNodes;
        private ArrayList<Pair<Point2D, Tile>> velocitys;
        private ArrayList<Line> walls;
        private Tile[][] map;
        private Tile startNode, endNode;
        private Tile actorTile;
        boolean foundExit;
        private double endX, endY;
        private double range;

        public AStarPath(Tile startNode, Tile[][] map, ArrayList<Line> walls) {
            this.unopenedNodes = new PriorityQueue<>();
            this.openedNodes = new PriorityQueue<>();
            this.velocitys = new ArrayList<>();
            this.map = map;
            this.startNode = this.map[startNode.getGridX()][startNode.getGridY()];

            this.endNode = findClosestExit(startNode);
            if (this.endNode != null) {
                this.endX = endNode.getActualCords()[0];
                this.endY = endNode.getActualCords()[1];
            } else {
                this.endX = 0;
                this.endY = 0;
            }
            this.range = 0;
            this.walls = walls;

        }

        public boolean findPath(boolean avoiding) {
            if (this.endNode != null) {
                int nodeX, nodeY;
                double newDis;
                Tile currentNode = null, start = this.map[this.startNode.getGridX()][this.startNode.getGridY()],
                        goal = this.map[this.endNode.getGridX()][this.endNode.getGridY()];
                this.unopenedNodes.add(start);
                this.actorTile = start;
                System.out.println("Map length [x]: " + this.map.length + ", [y]: " + this.map[0].length);
                while (this.unopenedNodes.size() != 0) {

                    System.out.println("Size: " + this.openedNodes.size());
                    System.out.println("");
                    currentNode = this.unopenedNodes.poll();
                    this.openedNodes.add(currentNode);
                    if (currentNode == goal) {
                        break;
                    }
                    nodeX = currentNode.getGridX();
                    nodeY = currentNode.getGridY();
                    ;

                    for (Tile t : getNeighbors(nodeX, nodeY, currentNode, avoiding)) {
                        newDis = currentNode.getGCost() + calculateDistance(currentNode, t);
                        if (this.openedNodes.contains(t)) {
                            continue;
                        }
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
                if (this.openedNodes.contains(goal)) {
                    int counter = 1;
                    currentNode = goal;
                    boolean test = true;
                    if (test) {
                        Random rand = new Random();
                        this.range = 1;//Math.round((rand.nextDouble() + 0.5) * 100.0) / 100.0;
                        double vel = 0;
                        int skip = 0, skipMax = 1;
                        //this.nonPolygonalFunnelAlgorithm(currentNode);

                        do {
                            if (/*currentNode == startNode ||*/ currentNode == endNode) {
                                skip = skipMax;
                            }
                            if (skip == skipMax) {
                                double prevX = currentNode.getActualCords()[0], prevY = currentNode.getActualCords()[1], proX =
                                        currentNode.getParent().getActualCords()[0], proY = currentNode.getParent().getActualCords()[1];
                                System.out.println("x: " + proX + ", y: " + proY);
                                if (prevX == proX) {
                                    vel = prevY > proY ? range : -range;
                                    this.velocitys.add(new Pair(new Point2D(0, vel), currentNode));
                                } else {
                                    vel = prevX > proX ? range : -range;
                                    this.velocitys.add(new Pair(new Point2D(0, vel), currentNode));
                                }
                                System.out.println("Count " + counter);
                                System.out.println("x: " + currentNode.getActualCords()[0] + ", y: " + currentNode.getActualCords()[1]);
                                //currentNode.getFxRef().setFill(Color.LIGHTBLUE);
                                //currentNode.setType(Tile.BlockType.Path);
                                counter++;
                                skip = 0;
                            }
                            currentNode = currentNode.getParent();
                            skip++;
                        } while (currentNode != start);
                    }
                    Collections.reverse(this.velocitys);
                    System.out.println("\n\nsize of array: " + this.velocitys.size() + "\n\n");

                    this.velocitys = this.refinePath(this.velocitys);
                    //modification for testing purposes
                    /*
                    for(int n=this.velocitys.size()-1; n>=0; n--){
                        Pair<Point2D, Tile> p = this.velocitys.get(n);
                        System.out.println("Velocity: "+p.getKey().toString()+" | "+p.getValue().getGridX()+", "+p.getValue().getGridY());
                        if(n%2 == 0){
                            this.velocitys.remove(n);
                        }
                    }
                    */



                    return true;
                }
            }
            return false;
        }

        private boolean checkRoute(Tile start, Tile end){
            int gridWidth = Math.abs(start.getGridX()-end.getGridX())+1;
            int gridHeight = Math.abs(start.getGridY()-end.getGridY())+1;

            System.out.println("Starting Tile X: "+start.getGridX());
            System.out.println("Final Tile X: "+end.getGridX());
            System.out.println("Grid Width & Height: "+gridWidth+", "+gridHeight);


            double actualWidth = end.getActualX()-start.getActualX();
            double actualHeight = end.getActualY()-start.getActualY();
            int checkCount = (gridHeight*gridWidth)-1;

            System.out.println("actual width and height: "+actualWidth+", "+actualHeight);
            System.out.println("check count: "+checkCount);

            double gradient = actualHeight/actualWidth;
            double euclideanDistance = Math.sqrt(Math.pow(actualHeight, 2)+Math.pow(actualWidth, 2));
            double checkGap = euclideanDistance/checkCount;

            boolean endFlag = false;

            double xcha;
            double ycha;
            double currentGap = checkGap;

            double[] currentCheckingCoordinate = new double[2];
            int[] gridCheckingCoordinate = new int[2];
            ArrayList<Tile> tilesInPath = new ArrayList<>();
            Tile prevTileInPath = null;
            while(currentGap<=euclideanDistance) {
                xcha = Math.sqrt(Math.pow(currentGap, 2) / (Math.pow(gradient, 2) + 1));
                ycha = Math.sqrt(Math.pow(currentGap, 2) / (Math.pow(gradient, -2) + 1));

                currentCheckingCoordinate[0] = start.getActualX()+xcha+(start.getWidth()/2);
                currentCheckingCoordinate[1] = start.getActualY()+ycha+(start.getHeight()/2);

                System.out.println("Currently checking: "+currentCheckingCoordinate[0]+", "+currentCheckingCoordinate[1]);

                gridCheckingCoordinate[0] = (int)(currentCheckingCoordinate[0]/start.getWidth());
                gridCheckingCoordinate[1] = (int)(currentCheckingCoordinate[1]/start.getHeight());

                System.out.println("Currently grid checking: "+gridCheckingCoordinate[0]+", "+gridCheckingCoordinate[1]);

                if(this.map[gridCheckingCoordinate[0]][gridCheckingCoordinate[1]] != prevTileInPath) {
                    tilesInPath.add(this.map[gridCheckingCoordinate[0]][gridCheckingCoordinate[1]]);
                    prevTileInPath = this.map[gridCheckingCoordinate[0]][gridCheckingCoordinate[1]];
                }

                currentGap += checkGap;
                System.out.println("----------------------");
            }

            System.out.println("Worked out which tiles are in path. Now checking them...");
            System.out.println("Amount of tiles in path: "+tilesInPath.size());
            for(int n=0; n<tilesInPath.size()-1; n++){
                System.out.println("Checking tile num: "+n);
                if(tilesInPath.get(n).checkAccess(tilesInPath.get(n+1)) == false){
                    System.out.println("CANNOT ACCESS THIS PATH.");
                    return false;
                }
            }
            return true;
        }





        private ArrayList<Pair<Point2D, Tile>> refinePath(ArrayList<Pair<Point2D, Tile>> p){
            ArrayList<Pair<Point2D, Tile>> waypoints = new ArrayList<>();
            Tile currentWaypoint = this.actorTile;
            int currentWaypointIndex = -1;
            waypoints.add(new Pair(1, this.actorTile));

            boolean reachedEnd = false;
            while(!reachedEnd) {
                for (int n = p.size() - 1; n > currentWaypointIndex; n--) {
                    System.out.println("Current waypoint: "+currentWaypointIndex);
                    System.out.println("Currently checking against: "+n);
                    ///if(p.get(n).getValue() == c)
                    if (checkRoute(currentWaypoint, p.get(n).getValue()) == true) {
                        currentWaypoint = p.get(n).getValue();
                        currentWaypointIndex = n;
                        waypoints.add(new Pair(p.get(currentWaypointIndex).getKey(), currentWaypoint));
                        if(currentWaypointIndex == p.size()-1) {
                            reachedEnd = true;
                        }
                        break;
                    }
                }
            }
            return waypoints;
        }


        private int calculateDistance(Tile a, Tile b){
            int maxX = (int)Math.abs(a.getActualCords()[0] - b.getActualCords()[0]);
            int maxY = (int)Math.abs(a.getActualCords()[1] - b.getActualCords()[1]);
            if(maxY < maxX)return 14 * maxY + 10 * (maxX - maxY);
            else return 14 * maxX + 10 * (maxY - maxX);

        }

        private ArrayList<Tile> getNeighbors(int nodeX, int nodeY, Tile node, boolean avoiding) {
            ArrayList<Tile> nodesToExplore = new ArrayList<>();
            int i, j, aX, aY, newX, newY, mapLength = this.map.length;
            boolean add = true;
            boolean[] dirAcess = new boolean[2];
            for (i = -1; i < 2; i++) {
                for (j = -1; j < 2; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    } else {
                        aY = i == -1 ? 3 : 1;
                        aX = j == -1 ? 0 : 2;
                        dirAcess[0] = node.getAccess(aX);
                        dirAcess[1] = node.getAccess(aY);
                        newX = i + nodeX;
                        newY = j + nodeY;

                        if (newX < mapLength && newX > -1 && newY < mapLength && newY > -1) {
                            if ((i == 0 && dirAcess[0])) {
                                if (avoiding && this.map[newX][newY].type.equals(Tile.BlockType.Employee)) {
                                    add = false;
                                }
                                if (add) {
                                    nodesToExplore.add(this.map[newX][newY]);
                                }
                            } else if (j == 0 && dirAcess[1]) {
                                if (avoiding && this.map[newX][newY].type.equals(Tile.BlockType.Employee)) {
                                    add = false;
                                }
                                if (add) {
                                    nodesToExplore.add(this.map[newX][newY]);
                                }
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

        public Tile findClosestExit(Tile startNode) {
            //  for iteration       the amount of blocks in grid                         total amount of blocks to check before lockout
            int lockoutCounter = 0, lockoutLocal = this.map.length * this.map[0].length, lockoutMax = 182;
            boolean exitCondition = false, success = false;
            ArrayList<Tile> neighbors = new ArrayList<>();
            Queue<Tile> unopenList = new LinkedList<>();
            Queue<Tile> openList = new LinkedList<>();
            unopenList.add(startNode);
            Tile currentTile, exitTile = null;
            while (!exitCondition) {
                System.out.println("lockoutCounter: " + lockoutCounter);
                if (lockoutCounter < lockoutLocal && lockoutCounter < lockoutMax) {
                    currentTile = unopenList.poll();
                    lockoutCounter++;
                    openList.add(currentTile);
                    //currentTile.setColour(Color.LIGHTBLUE);
                    neighbors = getNeighbors(currentTile.getGridX(), currentTile.getGridY(), currentTile, false);
                    for (Tile neighbor : neighbors) {
                        if (!openList.contains(neighbor) && !unopenList.contains(neighbor)) {
                            if (neighbor.type.equals(Tile.BlockType.Exit)) {
                                exitTile = neighbor;
                                exitCondition = true;
                                success = true;
                                break;
                            } else {
                                unopenList.add(neighbor);
                            }
                        }

                    }
                    System.out.println("cur cords: x: " + currentTile.getGridX() + ", y: " + currentTile.getGridY());
                    neighbors.clear();
                    //if exit not found
                } else {
                    this.endNode = null;
                    exitCondition = true;
                    success = false;
                }
            }
            System.out.println("Finished, success: " + success);
            this.foundExit = success;
            return exitTile;
        }

        public PriorityQueue<Tile> getPath() {
            return this.openedNodes;
        }

        public double getRange() {
            return this.range;
        }

        public boolean exitFound() {
            return this.foundExit;
        }

        public ArrayList<Pair<Point2D, Tile>> getVelocities() {
            return this.velocitys;
        }

        public boolean intersectsWalls(Line path){
            int i, len = this.walls.size();
            for(i = 0; i < len; i++){
                if(path.intersects(this.walls.get(i).getBoundsInLocal())){return true;}
            }
            return false;
        }
        public void nonPolygonalFunnelAlgorithm(Tile currentNode) {
            double vel;
            double curX = currentNode.actualCords[0], curY = currentNode.actualCords[1];
            Line prevPath, postPath;

            do {
                double prevX = currentNode.getActualCords()[0], prevY = currentNode.getActualCords()[1], proX =
                        currentNode.getParent().getActualCords()[0], proY = currentNode.getParent().getActualCords()[1];
                System.out.println("x: " + proX + ", y: " + proY);
                prevPath = new Line(curX,curY, prevX, prevY);
                postPath = new Line(curX,curY,proX,proY);
                if(this.intersectsWalls(postPath)){
                    System.out.println("Was true");
                    if (prevX == proX) {
                        vel = prevY > proY ? range : -range;
                        this.velocitys.add(new Pair(new Point2D(0, vel), currentNode));
                    } else {
                        vel = prevX > proX ? range : -range;
                        this.velocitys.add(new Pair(new Point2D(0, vel), currentNode));
                    }
                    System.out.println("x: " + currentNode.getActualCords()[0] + ", y: " + currentNode.getActualCords()[1]);
                    //currentNode.getFxRef().setFill(Color.LIGHTBLUE);
                    currentNode.setType(Tile.BlockType.Path);
                    curX = prevPath.getStartX();
                    curY = prevPath.getStartY();
                }
                 currentNode = currentNode.getParent();
            } while (currentNode != this.startNode);

        }

    }

}
