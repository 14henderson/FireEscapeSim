package fireescapedemo;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
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
        private ArrayList<Tile> path;
        private ArrayList<Line> walls;
        private Tile[][] map;
        private Tile startNode, endNode;
        private Tile actorTile;
        private Tile goalTile;
        boolean foundExit;
        private double endX, endY;
        private double range;

        public AStarPath(Tile startNode, Tile[][] map, ArrayList<Line> walls) {
            this.unopenedNodes = new PriorityQueue<>();
            this.openedNodes = new PriorityQueue<>();
            this.path = new ArrayList<>();
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
                this.goalTile = goal;
                System.out.println("Map length [x]: " + this.map.length + ", [y]: " + this.map[0].length);
                while (this.unopenedNodes.size() != 0) {

                    //System.out.println("Size: " + this.openedNodes.size());
                    //System.out.println("");
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
                                this.path.add(currentNode);

                                //System.out.println("Count " + counter);
                                //System.out.println("x: " + currentNode.getActualCords()[0] + ", y: " + currentNode.getActualCords()[1]);
                                //currentNode.getFxRef().setFill(Color.LIGHTBLUE);
                                //currentNode.setType(Tile.BlockType.Path);
                                counter++;
                                skip = 0;
                            }
                            currentNode = currentNode.getParent();
                            skip++;
                        } while (currentNode != start);
                    }

                    Collections.reverse(this.path);
                    System.out.println("\n\nsize of array: " + this.path.size() + "\n\n");

                    this.path = this.refinePath(this.path);
                    //modification for testing purposes
                    for(int n=this.path.size()-1; n>=0; n--){
                        Tile t  = this.path.get(n);
                       // System.out.println("Velocity: "+p.getKey().toString()+" | "+p.getValue().getGridX()+", "+p.getValue().getGridY());
                     //   Circle c = new Circle(p.getValue().getActualX()+25, p.getValue().getActualY()+25, 10, Color.RED);
                      //  p.getValue().mainBuilding.windowContainer.getChildren().add(c);
                    //    if(n%2 == 0){
                      //      this.velocitys.remove(n);
                        }
                 //   }


                    Collections.reverse(this.path);
                    //System.out.println("\n\nsize of array: " + this.path.size() + "\n\n");

                    return true;
                }
            }
            return false;
        }

        private boolean checkRoute(Tile start, Tile end){
            int gridWidth = Math.abs(start.getGridX()-end.getGridX())+1;    //grid width of path boundary
            int gridHeight = Math.abs(start.getGridY()-end.getGridY())+1;   //grid height of path boundary

            double actualWidth = end.getActualX()-start.getActualX();       //actual width of path boundary
            double actualHeight = end.getActualY()-start.getActualY();      //actual height of path boundary
            int checkCount = (gridHeight*gridWidth)-1;                      //how many times is the path going to be cross-checked with access points.

            double gradient = actualHeight/actualWidth;                     //gradient of path
            double inverseGradient = -Math.pow(gradient, -1);               //inverse gradient (used for finding starting coordinates)
            double euclideanDistance = Math.sqrt(Math.pow(actualHeight, 2)+Math.pow(actualWidth, 2));   //length of path (in pixels)
            double checkGap = euclideanDistance/checkCount;                 //calculated gap between cross-checked access points

            double xcha, ycha;
            double currentGap;                                             //current length along path to check

            double[] currentCheckingCoordinate = new double[2];             //
            int[] gridCheckingCoordinate = new int[2];                      //
            ArrayList<Tile> tilesInPath = new ArrayList<>();                //A list of tiles that the path crosses over

            currentCheckingCoordinate[0] = start.getActualX()+(start.getWidth()/2);
            currentCheckingCoordinate[1] = start.getActualY()+(start.getHeight()/2);

        //    System.out.println("inverse gradient: "+inverseGradient);
       //     System.out.println("Origin point coords: "+currentCheckingCoordinate[0]+", "+currentCheckingCoordinate[1]);
            double[] startingCoords1 = new double[2];
            startingCoords1[0] = currentCheckingCoordinate[0] + Math.sqrt(Math.pow(start.mainBuilding.getActorSize()/2, 2) / (Math.pow(inverseGradient, 2) + 1))*Math.signum(-actualHeight);
            startingCoords1[1] = currentCheckingCoordinate[1] + Math.sqrt(Math.pow(start.mainBuilding.getActorSize()/2, 2) / (Math.pow(inverseGradient, -2) + 1))*Math.signum(actualWidth);;

       //     System.out.println("Starting p1 coords: "+startingCoords1[0]+", "+startingCoords1[1]);
        //    Circle s1 = new Circle(startingCoords1[0], startingCoords1[1], 3, Color.BLUE);
        //    start.mainBuilding.windowContainer.getChildren().add(s1);

            double[] startingCoords2 = new double[2];
            startingCoords2[0] = currentCheckingCoordinate[0] + Math.sqrt(Math.pow(start.mainBuilding.getActorSize()/2, 2) / (Math.pow(inverseGradient, 2) + 1))*Math.signum(actualHeight);
            startingCoords2[1] = currentCheckingCoordinate[1] + Math.sqrt(Math.pow(start.mainBuilding.getActorSize()/2, 2) / (Math.pow(inverseGradient, -2) + 1))*Math.signum(-actualWidth);;

          //  System.out.println("Starting p2 coords: "+startingCoords2[0]+", "+startingCoords2[1]);
         //   Circle s2 = new Circle(startingCoords2[0], startingCoords2[1], 3, Color.BLUE);
          //  start.mainBuilding.windowContainer.getChildren().add(s2);

            double[] actualStartingCordsTMP = new double[2];
            actualStartingCordsTMP[0] = start.getActualX()+(start.getWidth()/2);
            actualStartingCordsTMP[1] = start.getActualY()+(start.getHeight()/2);

            ArrayList<double[]> linesToCheck = new ArrayList<>();
            linesToCheck.add(startingCoords1);
            //linesToCheck.add(actualStartingCordsTMP);
            linesToCheck.add(startingCoords2);

            //for every parallel line to the mid-point that needs to be checked.
            for(double[] startpoint : linesToCheck) {
                tilesInPath.clear();
                currentGap = checkGap;
                currentCheckingCoordinate[0] = startpoint[0];
                currentCheckingCoordinate[1] = startpoint[1];


                //for each calculated point on the path
                for (int n = 0; n <= checkCount; n++) {
                    //Circle tmp = new Circle(currentCheckingCoordinate[0], currentCheckingCoordinate[1], 5, Color.RED);
                    //start.mainBuilding.windowContainer.getChildren().add(tmp);

                    gridCheckingCoordinate[0] = (int) (currentCheckingCoordinate[0] / start.getWidth());                   //convert actual coordinate to grid coordinate
                    gridCheckingCoordinate[1] = (int) (currentCheckingCoordinate[1] / start.getHeight());

                    tilesInPath.add(this.map[gridCheckingCoordinate[0]][gridCheckingCoordinate[1]]);                        //add the corresponding tile in path to list
                    xcha = Math.sqrt(Math.pow(currentGap, 2) / (Math.pow(gradient, 2) + 1)) * Math.signum(actualWidth);       //change in x from last checked actual coordinate
                    ycha = Math.sqrt(Math.pow(currentGap, 2) / (Math.pow(gradient, -2) + 1)) * Math.signum(actualHeight);     //change in y from last checked actual coorodinate

                    currentCheckingCoordinate[0] = startpoint[0] + xcha;                            //recalculating actual x coordinate
                    currentCheckingCoordinate[1] = startpoint[1] + ycha;                           //recalculating actual x coordinate




                    //checking pivot points. Don't question this - it just works
                    if (currentCheckingCoordinate[0] % 50 == 0 && currentCheckingCoordinate[1] % 50 == 0) {
                        gridCheckingCoordinate[0] = (int) (currentCheckingCoordinate[0] / start.getWidth());
                        gridCheckingCoordinate[1] = (int) (currentCheckingCoordinate[1] / start.getHeight());
                        if (this.map[gridCheckingCoordinate[0] - 1][gridCheckingCoordinate[1] - 1].checkAccess(this.map[gridCheckingCoordinate[0]][gridCheckingCoordinate[1] - 1])
                                && this.map[gridCheckingCoordinate[0]][gridCheckingCoordinate[1]].checkAccess(this.map[gridCheckingCoordinate[0] - 1][gridCheckingCoordinate[1]])
                                && this.map[gridCheckingCoordinate[0]][gridCheckingCoordinate[1]].checkAccess(this.map[gridCheckingCoordinate[0]][gridCheckingCoordinate[1] - 1])
                                && this.map[gridCheckingCoordinate[0]][gridCheckingCoordinate[1]].checkAccess(this.map[gridCheckingCoordinate[0] - 1][gridCheckingCoordinate[1]])) {
                            currentGap += checkGap;
                            continue;
                        } else {
                            System.out.println("This path does not work. Start node: "+start.getGridX()+", "+start.getGridY()+" End node: "+end.getGridX()+", "+end.getGridY());
                            return false;
                        }}
                    currentGap += checkGap;
                }
                for (int n = 0; n < tilesInPath.size() - 1; n++) {
                    if (!tilesInPath.get(n).checkAccess(tilesInPath.get(n + 1))) {        //if a wall blocks the path, return false.
                        System.out.println("This path does not work. Start node: "+start.getGridX()+", "+start.getGridY()+" End node: "+end.getGridX()+", "+end.getGridY());
                        return false;
                    }
                }
            }
            return true;
        }





        private ArrayList<Tile> refinePath(ArrayList<Tile> p){
            ArrayList<Tile> waypoints = new ArrayList<>();
            Tile currentWaypoint = this.actorTile;
            int currentWaypointIndex = -1;
            waypoints.add(this.actorTile);
            p.add(this.goalTile);
           // System.out.println("Goal cords: "+this.goalTile.getGridX()+", "+this.goalTile.getGridY());

            boolean reachedEnd = false;
            while(!reachedEnd) {
                for (int n = p.size() - 1; n > currentWaypointIndex; n--) {
              //      System.out.println("Current waypoint coords: "+currentWaypoint.getGridX()+", "+currentWaypoint.getGridY());
               //     System.out.println("Currently checking against coords: "+p.get(n).getValue().getGridX()+", "+p.get(n).getValue().getGridY());

                    ///if(p.get(n).getValue() == c)
                    if (checkRoute(currentWaypoint, p.get(n)) == true) {
                        currentWaypoint = p.get(n);
                        currentWaypointIndex = n;
                        waypoints.add(currentWaypoint);
                        if(currentWaypointIndex == p.size()-1) {
                            reachedEnd = true;
                        }
                        break;
                    }
                }
            }
            //System.out.println(waypoints.size()+" Waypoints found for actor at position: "+this.actorTile.getGridX()+", "+this.actorTile.getGridY());
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

            int lockoutCounter = 0, lockoutLocal = this.map.length * this.map[0].length;
            boolean exitCondition = false, success = false;
            ArrayList<Tile> neighbors = new ArrayList<>();
            Queue<Tile> unopenList = new LinkedList<>();
            Queue<Tile> openList = new LinkedList<>();
            unopenList.add(startNode);
            Tile currentTile, exitTile = null;
            Tile.BlockType target = Tile.BlockType.Exit;
            while (!exitCondition) {
                //System.out.println("lockoutCounter: " + lockoutCounter);
                if (lockoutCounter < lockoutLocal) {
                    currentTile = unopenList.poll();
                    lockoutCounter++;
                    openList.add(currentTile);
                    //currentTile.setColour(Color.LIGHTBLUE);
                    neighbors = getNeighbors(currentTile.getGridX(), currentTile.getGridY(), currentTile, false);
                    for (Tile neighbor : neighbors) {
                        if (!openList.contains(neighbor) && !unopenList.contains(neighbor)) {
                            if (neighbor.type.equals(target)) {
                                exitTile = neighbor;
                                exitCondition = true;
                                success = true;
                                break;
                            } else {
                                unopenList.add(neighbor);
                            }
                        }

                    }
                    //System.out.println("cur cords: x: " + currentTile.getGridX() + ", y: " + currentTile.getGridY());
                    //System.out.println("List size: " + unopenList.size());
                    neighbors.clear();
                    //if exit not found
                } else {
                    if(target.equals(Tile.BlockType.Exit)){
                        lockoutCounter = 0;
                        unopenList.clear();
                        openList.clear();
                        unopenList.add(startNode);
                        target = Tile.BlockType.Stairs;
                    }else{
                        this.endNode = null;
                        exitCondition = true;
                        success = false;
                    }
                }
            }
            //System.out.println("Finished, success: " + success);
            this.foundExit = success;
            return exitTile;
        }


        public double getRange() {
            return this.range;
        }

        public boolean exitFound() {
            return this.foundExit;
        }

        public ArrayList<Tile> getPath() {
            return this.path;
        }

        public boolean intersectsWalls(Line path){
            int i, len = this.walls.size();
            for(i = 0; i < len; i++){
                if(path.intersects(this.walls.get(i).getBoundsInLocal())){return true;}
            }
            return false;
        }


    }

}

