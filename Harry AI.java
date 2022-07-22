import java.util.*;

import static java.lang.Math.*;

/**
 * the class that stores all the required data and methods common for different classes, has main() that solves the task
 */
class Main {
    static boolean MANUAL_INPUT_FLAG = true;
    static int PERCEPTION_TYPE;
    static final int FILCH_RANGE = 2;
    static final int NORRIS_RANGE = 1;
    static Map map;
    static char enemyZoneSymbol = 9632;
    static char intersectionSymbol = 9635;
    static char harrysZoneSymbol = 9634;
    static char bookSymbol = 9707;
    static char cloakSymbol = 10190;
    static char exitSymbol = 9919;
    static char freeCellSymbol = 9633;
    static char harrysSymbol = 'H';
    static char norrisSymbol = 'N';
    static char filchsSymbol = 'F';

    /**
     * calculates distance between two points
     * @param point1 point to calculate the distance from
     * @param point2 point to calculate the distance to
     * @return a double denoting the distance between point1 and point2
     */
    static double pointToPoint(int[] point1, int[] point2) {
        return sqrt(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2));
    }

    public static void main(String[] args) {
        try {
            if (MANUAL_INPUT_FLAG) {
                //System.out.println("Greetings, ye brave one! It's the time fro ye now to determine young Potter's life once and for all!");
                Scanner scanner = new Scanner(System.in);
                String[] coordStrings = scanner.nextLine().split(" ");
                //System.out.println("Foine, and how far does he see?");
                PERCEPTION_TYPE = scanner.nextInt();
//                System.out.println("Legend:");
//                System.out.println("H, F and N are Harry, Filch and Ms. Norris");
//                System.out.println(freeCellSymbol + " is a free cell");
//                System.out.println(enemyZoneSymbol + " is an enemy zone cell");
//                System.out.println(harrysZoneSymbol + " is Harry's zone cell");
//                System.out.println(intersectionSymbol + " is a cell of Harry's and enemy's zones intersection");
//                System.out.println(enemyZoneSymbol + " is the exit");
//                System.out.println(cloakSymbol + " is the invisibility cloak");
//                System.out.println(bookSymbol + " is the book\n");

                if (PERCEPTION_TYPE != 1 && PERCEPTION_TYPE != 2) throw new Exception();
                int[] coordInts = new int[12];
                int i = 0;
                for (String coord : coordStrings) {
                    char[] chars = coord.toCharArray();
                    if (chars[0] != '[' || chars[4] != ']' || chars[2] != ',') throw new Exception();
                    coordInts[i] = chars[1] - '0';
                    coordInts[i + 1] = chars[3] - '0';
                    i += 2;
                }

                map = new Map(new int[]{coordInts[0], coordInts[1]},
                        new int[]{coordInts[2], coordInts[3]},
                        new int[]{coordInts[4], coordInts[5]},
                        new int[]{coordInts[10], coordInts[11]},
                        new int[]{coordInts[6], coordInts[7]},
                        new int[]{coordInts[8], coordInts[9]},
                        PERCEPTION_TYPE);

            } else {
                Random random = new Random();

                PERCEPTION_TYPE = random.nextInt(2) + 1;
                int[] harrysCoords = {random.nextInt(9), random.nextInt(9)};
                int[] bookCoords = {random.nextInt(9), random.nextInt(9)};
                int[] cloakCoords = {random.nextInt(9), random.nextInt(9)};
                int[] exitCoords = {random.nextInt(9), random.nextInt(9)};
                int[] filchsCoords = new int[]{random.nextInt(9), random.nextInt(9)};
                int[] norrisCoords = new int[]{random.nextInt(9), random.nextInt(9)};

                while (pointToPoint(filchsCoords, bookCoords) < FILCH_RANGE + 1 || pointToPoint(norrisCoords, bookCoords) < NORRIS_RANGE + 1 ||
                        pointToPoint(filchsCoords, cloakCoords) < FILCH_RANGE + 1 || pointToPoint(norrisCoords, cloakCoords) < NORRIS_RANGE + 1 ||
                        pointToPoint(filchsCoords, exitCoords) < FILCH_RANGE + 1 || pointToPoint(norrisCoords, exitCoords) < NORRIS_RANGE + 1) {
                    filchsCoords = new int[]{random.nextInt(9), random.nextInt(9)};
                    norrisCoords = new int[]{random.nextInt(9), random.nextInt(9)};
                }
                map = new Map(harrysCoords, filchsCoords, norrisCoords, exitCoords, bookCoords, cloakCoords, PERCEPTION_TYPE);
            }
            map.show();
            System.out.println("\n\n\n");

            Harry harry = new Harry(map.harrysCoords, map.exitCoords, map);
            long Time = System.nanoTime();
            harry.backtracking();
            Time = System.nanoTime() - Time;
            System.out.println("5. Time taken: " + Time);
            System.out.println("\n\n\n");
            Time = System.nanoTime();
            harry.aStar();
            Time = System.nanoTime() - Time;
            System.out.println("5. Time taken: " + Time);

        } catch (Exception e) {
            System.out.println("try again\n\n\n");
        }

    }
}

/**
 * the class that is used to store the data and methods for locations
 * also used for Harry's map that stores only data he knows
 */
class Map {
    int[] harrysCoords;
    int[] filchsCoords;
    int[] norrisCoords;
    int[] exitCoords;
    int[] bookCoords;
    int[] cloakCoords;
    static int perceptionType;

    /**
     * Map() constructor used to create the location's object to store and operate all the whereabouts in the game
     * @param harrysCoords Harry's coordinates
     * @param filchsCoords Filch's coordinates
     * @param norrisCoords Norris' coordinates
     * @param exitCoords exit's coordinates
     * @param bookCoords book's coordinates
     * @param cloakCoords cloak's coordinates
     * @param perceptionType the type of perception
     */
    public Map(int[] harrysCoords,
               int[] filchsCoords,
               int[] norrisCoords,
               int[] exitCoords,
               int[] bookCoords,
               int[] cloakCoords,
               int perceptionType) {
        this.harrysCoords = harrysCoords;
        this.filchsCoords = filchsCoords;
        this.norrisCoords = norrisCoords;
        this.exitCoords = exitCoords;
        this.bookCoords = bookCoords;
        this.cloakCoords = cloakCoords;
        Map.perceptionType = perceptionType;
    }

    /**
     * Map() constructor used to store and operate only the data harry knows
     * @param harrysCoords Harry's coordinates
     * @param exitCoords exit's coordinates
     */
    public Map(int[] harrysCoords,
               int[] exitCoords) {
        this.harrysCoords = harrysCoords;
        this.filchsCoords = null;
        this.norrisCoords = null;
        this.exitCoords = exitCoords;
        this.bookCoords = null;
        this.cloakCoords = null;
    }

    /**
     * show() reveals the location to the user via console
     */
    public void show() {
        for (int y = 8; y > -1; y--) {
            for (int x = 0; x < 9; x++) {
                int[] current = {x, y};
                char symbol = ' ';
                //distances from current coords to the Norris', Filch's and Harry's correspondingly
                double norrisDist = sqrt(Math.pow(norrisCoords[0] - current[0], 2) + Math.pow(norrisCoords[1] - current[1], 2));
                double filchsDist = sqrt(Math.pow(filchsCoords[0] - current[0], 2) + Math.pow(filchsCoords[1] - current[1], 2));
                double harrysDist = sqrt(Math.pow(harrysCoords[0] - current[0], 2) + Math.pow(harrysCoords[1] - current[1], 2));
                if (norrisDist < Main.NORRIS_RANGE + 1 || filchsDist < Main.FILCH_RANGE + 1) {
                    symbol = Main.enemyZoneSymbol;
                }               //if the cell is within Filch's or Norris' zone, put the enemy zone cell sign ■
                //if the cell is in Harry's zone, be it one perception type or another
                if (perceptionType <= harrysDist && harrysDist < perceptionType + 1 && harrysDist < 2 * sqrt(2)) {
                    if (symbol == Main.enemyZoneSymbol)
                        symbol = Main.intersectionSymbol;                   //if it's enemy's zone, put the intersection sign ▣
                    else
                        symbol = Main.harrysZoneSymbol;                                         //put Harry's zone sign ▢
                }
                if (current[0] == bookCoords[0] && current[1] == bookCoords[1])
                    symbol = Main.bookSymbol;                          //if it's book's coordinates, puts the ◫ sign
                if (current[0] == cloakCoords[0] && current[1] == cloakCoords[1])
                    symbol = Main.cloakSymbol;                       //if it's cloak's coordinates, puts the ⟎ sign
                if (current[0] == exitCoords[0] && current[1] == exitCoords[1])
                    symbol = Main.exitSymbol;                          //if it's exit's coordinates, puts the ⚿ sign
                if (current[0] == norrisCoords[0] && current[1] == norrisCoords[1])
                    symbol = Main.norrisSymbol;                       //if it's Norris' coordinates, puts the N
                if (current[0] == filchsCoords[0] && current[1] == filchsCoords[1])
                    symbol = Main.filchsSymbol;                       //if it's Filch's coordinates, puts the F
                if (current[0] == harrysCoords[0] && current[1] == harrysCoords[1])
                    symbol = Main.harrysSymbol;                       //if it's Harry's coordinates, puts the H
                if (symbol == ' ')
                    symbol = Main.freeCellSymbol;                               //put an empty cell sign □
                System.out.print(symbol + "  ");
            }
            System.out.print("\n");
        }
    }
}

/**
 * deadException is used to interrupt the code from sub-methods via exception in case of Lose state and gets caught in the super-method.
 * Swiftly finishes the algorithm without shrinking the recursion
 */
class deadException extends Exception {}

/**
 * wonException is used to interrupt the code from sub-methods via exception in case of Win state and gets caught in the super-method.
 * Swiftly finishes the algorithm without shrinking the recursion
 */
class wonException extends Exception {}

/**
 * Harry class objects store all the data, methods and objects Harry obtains, knows, operates or interacts with
 */
class Harry {
    int perceptionType;
    boolean cloakFlag = false;
    boolean bookFlag = false;
    int[] filchsCoords = new int[]{-1, -1};
    int[] norrisCoords = new int[]{-1, -1};
    ArrayList<List<Integer>> freeCells = new ArrayList<>();
    Set<List<Integer>> dangerZone = new HashSet<>();
    ArrayList<List<Integer>> path = new ArrayList<>();
    Map map;
    Map location;
    deadException dead = new deadException();
    wonException won = new wonException();
    int[] initialHarrysCoords;
    String goal = "Book";
    String state;
    ArrayList<ArrayList<Double>> heuristic = new ArrayList<>();
    List<Integer> aStarGoal;


    /**
     *  creates new Harry, binds the corresponding values and sets free cells to the initial state of whole the matrix
     * @param harrysCoords Harry's coordinates
     * @param exitCoords exit's coordinates
     * @param location the Map object for the whole location
     */
    public Harry(int[] harrysCoords,
                 int[] exitCoords, Map location) {
        this.map = new Map(harrysCoords, exitCoords);
        this.perceptionType = Map.perceptionType;
        this.location = location;
        this.initialHarrysCoords = harrysCoords;

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                freeCells.add(new ArrayList<>(Arrays.asList(x, y)));
            }
        }
    }

    /**
     * tells whether a given point is dangerous
     * @param point a point with coordinates we'd like to check whether the dangerousness of
     * @return a boolean telling whether a given point is within a danger zone
     */
    boolean inDangerZone(int[] point) {
        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                if (point[0] == location.norrisCoords[0] + x && point[1] == location.norrisCoords[1] + y && !(x==0 && y==0))
                    return true;
            }
        }
        for (int y = -2; y < 3; y++) {
            for (int x = -2; x < 3; x++) {
                if (point[0] == location.filchsCoords[0] + x && point[1] == location.filchsCoords[1] + y && !(x==0 && y==0))
                    return true;
            }
        }
        return false;
    }

    /**
     * shows the path and Filch with Norris as sights
     */
    void showPath() {
        for (int y = 8; y > -1; y--) {
            for (int x = 0; x < 9; x++) {
                if (path.contains(new ArrayList<>(Arrays.asList(x, y)))) System.out.print(Main.enemyZoneSymbol + "  ");
                else if (location.norrisCoords[0] == x && location.norrisCoords[1] == y) System.out.print(Main.norrisSymbol + "  ");
                else if (location.filchsCoords[0] == x && location.filchsCoords[1] == y) System.out.print(Main.filchsSymbol + "  ");
                else if (location.exitCoords[0] == x && location.exitCoords[1] == y) System.out.print(Main.exitSymbol + "  ");
                else if (location.cloakCoords[0] == x && location.cloakCoords[1] == y) System.out.print(Main.cloakSymbol + "  ");
                else System.out.print(Main.freeCellSymbol + "  ");
            }
            System.out.print("\n");
        }
    }

    /**
     * calls the actual backtracking method, resets all the data to initial values catches the state exceptions and delegates the output
     */
    void backtracking(){
        try{backtrackingBeforeTheBook();}
        catch(deadException e){
            System.out.println("1. Backtracking");
            System.out.println("2. Lose");
            System.out.println("3. " + path.size());
            System.out.println("4. " + path.toString());
            showPath();
            path.clear();
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    freeCells.add(new ArrayList<>(Arrays.asList(x, y)));
                }
            }
            dangerZone.clear();
            map.harrysCoords = initialHarrysCoords;
            location.harrysCoords = initialHarrysCoords;
            bookFlag = false;
            cloakFlag = false;
            state  = "Lose";

        } catch (wonException e) {
            System.out.println("1. Backtracking");
            System.out.println("2. Win");
            System.out.println("3. " + path.size());
            System.out.println("4. " + path.toString());
            showPath();
            path.clear();
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    freeCells.add(new ArrayList<>(Arrays.asList(x, y)));
                }
            }
            dangerZone.clear();
            map.harrysCoords = initialHarrysCoords;
            location.harrysCoords = initialHarrysCoords;
            bookFlag = false;
            cloakFlag = false;
            state = "Win";
        }
        if (state == null) {
            System.out.println("1. Backtracking");
            System.out.println("2. Lose");
            System.out.println("3. " + path.size());
            System.out.println("4. " + path.toString());
            showPath();
            path.clear();
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    freeCells.add(new ArrayList<>(Arrays.asList(x, y)));
                }
            }
            dangerZone.clear();
            map.harrysCoords = initialHarrysCoords;
            location.harrysCoords = initialHarrysCoords;
            bookFlag = false;
            cloakFlag = false;
            state = "Lose";
        }
    }

    /**
     * a DFS based backtracking algorithm that is used after the book coordinates were obtained. For each iteration iterates
     * cells from the closest to the target ones to the least. The distance is calculated by vector rule D = sqrt((dX)^2 + (dY)^2)
     * @param goal is the point to aim to, can be either Book or Exit
     * @throws deadException to interrupt the workflow of the method to stop the recursion's shrinking and fasten the program as soon as Harry gets caught
     * @throws wonException to interrupt the workflow of the method to stop the recursion's shrinking and fasten the program as soon as Harry escapes
     */
    void backtrackingToTheBook(String goal) throws deadException, wonException {

        freeCells.remove(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));
        path.add(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));

        if (Arrays.equals(map.harrysCoords, map.bookCoords)) {
            goal = "Exit";
            bookFlag = true;
            freeCells.clear();
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if(!dangerZone.contains(new ArrayList<>(Arrays.asList(x, y))) || cloakFlag)
                        freeCells.add(new ArrayList<>(Arrays.asList(x, y)));
                }
            }
            freeCells.remove(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));
            if (norrisCoords != null) freeCells.remove(new ArrayList<>(Arrays.asList(location.norrisCoords[0], location.norrisCoords[1])));
            if (filchsCoords != null) freeCells.remove(new ArrayList<>(Arrays.asList(location.filchsCoords[0], location.filchsCoords[1])));
        }

        if (Arrays.equals(map.harrysCoords, map.cloakCoords)) {
            location.cloakCoords = null;
            cloakFlag = true;
            freeCells.addAll(dangerZone);
        }

        if (inDangerZone(map.harrysCoords) && !cloakFlag || Arrays.equals(map.harrysCoords, location.norrisCoords) ||
                Arrays.equals(map.harrysCoords, location.filchsCoords)) {
            Main.harrysSymbol = 9949;                                           //dead Harry's symbol
            throw dead;
        }

        if (Arrays.equals(map.harrysCoords, map.exitCoords) && bookFlag) {
            throw won;
        }

        for (int y = -2; y < 3; y++) {
            for (int x = -2; x < 3; x++) {
                int[] option = new int[]{map.harrysCoords[0] + x, map.harrysCoords[1] + y};

                if ((perceptionType == 1 && Main.pointToPoint(option, map.harrysCoords) >= 2) || (perceptionType == 2 && (Main.pointToPoint(option, map.harrysCoords) < 2 ||
                        abs(x) + abs(y) == 4)))
                    continue;

                if (Arrays.equals(option, location.filchsCoords)) {
                    freeCells.remove(new ArrayList<>(Arrays.asList(option[0], option[1])));
                    filchsCoords = option;
                    continue;
                }

                if (Arrays.equals(option, location.norrisCoords)) {
                    freeCells.remove(new ArrayList<>(Arrays.asList(option[0], option[1])));
                    norrisCoords = option;
                    continue;
                }

                if (inDangerZone(option) && !cloakFlag) {
                    freeCells.remove(new ArrayList<>(Arrays.asList(option[0], option[1])));
                    dangerZone.add(new ArrayList<>(Arrays.asList(option[0], option[1])));
                }
            }
        }

        ArrayList<double[]> neighbours = new ArrayList<>();
        int[] goalCoords;
        if (goal.equals("Book")) goalCoords = location.bookCoords;
        else if (goal.equals("Exit")) goalCoords = location.exitCoords;
        else goalCoords = new int[] {};
        for (int y = -1; y < 2; y++)
            for (int x = -1; x < 2; x++)
                neighbours.add(new double[] {map.harrysCoords[0] + x, map.harrysCoords[1] + y,
                        Main.pointToPoint(new int[] {map.harrysCoords[0] + x, map.harrysCoords[1] + y}, goalCoords)});

        for (int i = 0; i < neighbours.size(); i++) {
            for (int j = i+1; j < neighbours.size(); j++) {
                if(neighbours.get(i)[2] > neighbours.get(j)[2])  {
                    double[] t = neighbours.get(i);
                    neighbours.set(i, neighbours.get(j));
                    neighbours.set(j, t);
                }
            }
        }

        for (double[] neighbour: neighbours) {
            int[] option = new int[]{(int) neighbour[0], (int) neighbour[1]};
            if (freeCells.contains(new ArrayList<>(Arrays.asList(option[0], option[1])))) {
                int[] prevCoords = map.harrysCoords;
                map.harrysCoords = option;
                location.harrysCoords = option;
                backtrackingToTheBook(goal);
                map.harrysCoords = prevCoords;
                location.harrysCoords = prevCoords;

            }
        }
    }


    /**
     * a simple DFS based backtracking algorithm the only goal of which is to determine either the book's location
     * or that Harry can't win for some reason. If he gets to the book, the method calls backtrackingToTheBook(Book) to
     * this time more effectively get to the book again and only then head to the exit
     * @throws deadException in case Harry gets caught
     * @throws wonException in case the inner recursion of backtrackingToTheBook(Book) throws the same exception
     */
    void backtrackingBeforeTheBook() throws deadException, wonException {

        freeCells.remove(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));
        path.add(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));

        if (Arrays.equals(map.harrysCoords, location.bookCoords)) {
            freeCells.clear();
            path.clear();
            map.bookCoords = map.harrysCoords;
            cloakFlag = false;
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if(!dangerZone.contains(new ArrayList<>(Arrays.asList(x, y))))
                        freeCells.add(new ArrayList<>(Arrays.asList(x, y)));
                }
            }
            if (norrisCoords != null) freeCells.remove(new ArrayList<>(Arrays.asList(location.norrisCoords[0], location.norrisCoords[1])));
            if (filchsCoords != null) freeCells.remove(new ArrayList<>(Arrays.asList(location.filchsCoords[0], location.filchsCoords[1])));
            map.harrysCoords = initialHarrysCoords;
            backtrackingToTheBook(goal);
        }
        if (Arrays.equals(map.harrysCoords, location.cloakCoords)) {
            cloakFlag = true;
            freeCells.addAll(dangerZone);
            map.cloakCoords = map.harrysCoords;
        }
        if (inDangerZone(map.harrysCoords) && !cloakFlag || Arrays.equals(map.harrysCoords, location.norrisCoords) ||
                Arrays.equals(map.harrysCoords, location.filchsCoords)) {
            Main.harrysSymbol = 9949;                                           //dead Harry's symbol
            throw dead;
        }

        for (int y = -2; y < 3; y++) {
           for (int x = -2; x < 3; x++) {
               int[] option = new int[]{map.harrysCoords[0] + x, map.harrysCoords[1] + y};

               if ((perceptionType == 1 && Main.pointToPoint(option, map.harrysCoords) >= 2) || (perceptionType == 2 && (Main.pointToPoint(option, map.harrysCoords) < 2 ||
                       abs(x) + abs(y) == 4)))
                   continue;

               if (Arrays.equals(option, location.filchsCoords)) {
                    freeCells.remove(new ArrayList<>(Arrays.asList(option[0], option[1])));
                    filchsCoords = option;
                    continue;
                }

               if (Arrays.equals(option, location.norrisCoords)) {
                    freeCells.remove(new ArrayList<>(Arrays.asList(option[0], option[1])));
                    norrisCoords = option;
                    continue;
                }

               if (inDangerZone(option) && !cloakFlag) {
                    freeCells.remove(new ArrayList<>(Arrays.asList(option[0], option[1])));
                    dangerZone.add(new ArrayList<>(Arrays.asList(option[0], option[1])));
                    continue;
                }
               if (inDangerZone(option) && cloakFlag) dangerZone.add(new ArrayList<>(Arrays.asList(option[0], option[1])));
           }
        }
        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                int[] option = new int[]{map.harrysCoords[0] + x, map.harrysCoords[1] + y};
                if (freeCells.contains(new ArrayList<>(Arrays.asList(option[0], option[1])))) {
                    map.harrysCoords = option;
                    location.harrysCoords = option;
                    backtrackingBeforeTheBook();
                    map.harrysCoords = new int[] {option[0]-x, option[1]-y};
                    location.harrysCoords = new int[] {option[0]-x, option[1]-y};
                }
            }
        }
    }

    /**
     * update heuristic pivoting around new goal
     */
    void updateHeuristic () {
        heuristic=new ArrayList<>();
        for (int x = 0; x < 9; x++) {
            heuristic.add(new ArrayList<>());
            for (int y = 0; y < 9; y++) {
                ArrayList<Double> row = heuristic.get(x);
                row.add(Main.pointToPoint(new int[] {x, y},  new int[] {aStarGoal.get(0), aStarGoal.get(1)}));
               heuristic.set(x, row);
            }
        }
    }

    /**
     * calculates the path using cost matrix obtained
     * @param cameFrom the beginning of the path
     * @param headedTo the end of the path
     * @param costMatrix cost matrix to base path countdown on
     * @return the path
     */
    ArrayList<List<Integer>> pathCal(int[] cameFrom, int[] headedTo, Double[][] costMatrix) {
        ArrayList<List<Integer>> localPath = new ArrayList<>();
        int[] currentCoord = headedTo;
        while (!Arrays.equals(currentCoord, cameFrom)) {
            int[] minCostCoords = currentCoord;
            Double minCost = (double)Integer.MAX_VALUE;
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    try {
                        if (costMatrix[currentCoord[0] + i][currentCoord[1] + j] < minCost && !(i == 0 && j == 0)) {
                            minCost = costMatrix[currentCoord[0] + i][currentCoord[1] + j];
                            minCostCoords = new int[]{currentCoord[0] + i, currentCoord[1] + j};
                        }
                    }
                    catch (IndexOutOfBoundsException ignored){}
                }
            }
            currentCoord = minCostCoords;
            localPath.add(new ArrayList<>(Arrays.asList(currentCoord[0], currentCoord[1])));
        }
        if(!localPath.contains(new ArrayList<>(Arrays.asList(location.cloakCoords[0], location.cloakCoords[1])))) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if(inDangerZone(new int[] {i, j})) costMatrix[i][j] += 100;
                }
            }
            localPath = new ArrayList<>();
            currentCoord = headedTo;
            while (!Arrays.equals(currentCoord, cameFrom)) {
                int[] minCostCoords = currentCoord;
                Double minCost = (double)Integer.MAX_VALUE;
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        try {
                            if (costMatrix[currentCoord[0] + i][currentCoord[1] + j] < minCost && !(i == 0 && j == 0)) {
                                minCost = costMatrix[currentCoord[0] + i][currentCoord[1] + j];
                                minCostCoords = new int[]{currentCoord[0] + i, currentCoord[1] + j};
                            }
                        }
                        catch (IndexOutOfBoundsException ignored){}
                    }
                }
                currentCoord = minCostCoords;
                localPath.add(new ArrayList<>(Arrays.asList(currentCoord[0], currentCoord[1])));
            }
        }

        Collections.reverse(localPath);
        return localPath;
    }

    /**
     * A* algorithm implementation using the general BFS implementation and vector based heuristic function h = sqrt((dX)^2+(dY)^2)
     */
    void aStar() {
        state = null;
        boolean randGoal = true;
        PriorityQueue<ArrayList<Double>> priorityQueue = new PriorityQueue<>(81, new comparator());
        Double[][] costMatrix = new Double[9][9];
        freeCells.clear();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                freeCells.add(new ArrayList<>(Arrays.asList(i, j)));
            }
        }

        Random random = new Random();
        aStarGoal = freeCells.get(random.nextInt(freeCells.size()));
        updateHeuristic();

        ArrayList<ArrayList<Integer>> checked = new ArrayList<>();

        freeCells.remove(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));
        checked.add(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));
        priorityQueue.add(new ArrayList<>(Arrays.asList((double) initialHarrysCoords[0], (double) initialHarrysCoords[1], heuristic.get(initialHarrysCoords[0]).get(initialHarrysCoords[1]))));

        while(!priorityQueue.isEmpty()) {
            ArrayList<Double> currentPivot = priorityQueue.poll();
            map.harrysCoords = new int[] {(int)(double)currentPivot.get(0), (int)(double)currentPivot.get(1)};
            if((inDangerZone(new int[] {(int) (double) currentPivot.get(0), (int) (double)currentPivot.get(1)}) && !cloakFlag) ||
                    Arrays.equals(new int[]{(int) (double)currentPivot.get(0), (int) (double)currentPivot.get(1)}, location.filchsCoords) ||
                    Arrays.equals(new int[]{(int) (double)currentPivot.get(0), (int) (double)currentPivot.get(1)}, location.norrisCoords)) {
                state  = "Lose";
                break;
            }
            if (Arrays.equals(map.harrysCoords, location.cloakCoords)) {
                cloakFlag = true;
                freeCells.addAll(dangerZone);
                map.cloakCoords = map.harrysCoords;
                for (ArrayList<Double> cell: priorityQueue)
                    if (inDangerZone(new int[]{(int) (double)cell.get(0), (int) (double)cell.get(1)}))  {
                        costMatrix[(int) (double)cell.get(0)][(int) (double)cell.get(1)] = cell.get(2)-100;
                        cell.set(2, cell.get(2) - 100);
                    }


            }
            if (Arrays.equals(map.harrysCoords, location.bookCoords) && randGoal) {
                freeCells.clear();
                randGoal = false;
                path.clear();
                map.bookCoords = map.harrysCoords;
                cloakFlag = false;
                for (int y = 0; y < 9; y++) {
                    for (int x = 0; x < 9; x++) {
                        if(!dangerZone.contains(new ArrayList<>(Arrays.asList(x, y))))
                            freeCells.add(new ArrayList<>(Arrays.asList(x, y)));
                    }
                }
                if (norrisCoords != null) freeCells.remove(new ArrayList<>(Arrays.asList(location.norrisCoords[0], location.norrisCoords[1])));
                if (filchsCoords != null) freeCells.remove(new ArrayList<>(Arrays.asList(location.filchsCoords[0], location.filchsCoords[1])));
                map.harrysCoords = initialHarrysCoords;
                checked.clear();
                priorityQueue.clear();
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        costMatrix[i][j] = (double) Integer.MAX_VALUE;
                    }
                }
                aStarGoal = new ArrayList<>(Arrays.asList((int) (double)map.harrysCoords[0], (int) (double)map.harrysCoords[1]));
                updateHeuristic();
                costMatrix[initialHarrysCoords[0]][initialHarrysCoords[1]] = heuristic.get(initialHarrysCoords[0]).get(initialHarrysCoords[1]);
                freeCells.remove(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));
                checked.add(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));
                priorityQueue.add(new ArrayList<>(Arrays.asList((double) initialHarrysCoords[0], (double) initialHarrysCoords[1], heuristic.get(initialHarrysCoords[0]).get(initialHarrysCoords[1]))));
                continue;
            }
            if (Arrays.equals(map.harrysCoords, location.bookCoords) && !randGoal && !aStarGoal.equals(new ArrayList<>(Arrays.asList(location.exitCoords[0], location.exitCoords[1])))) {
                freeCells.clear();
                path.addAll(pathCal(initialHarrysCoords, location.bookCoords, costMatrix));
                aStarGoal = new ArrayList<>(Arrays.asList((int) (double)map.exitCoords[0], (int) (double)map.exitCoords[1]));
                bookFlag = true;
                updateHeuristic();
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        costMatrix[i][j] = (double)Integer.MAX_VALUE;
                    }
                }
                costMatrix[location.bookCoords[0]][location.bookCoords[1]] = heuristic.get(location.bookCoords[0]).get(location.bookCoords[1]);
                for (int y = 0; y < 9; y++) {
                    for (int x = 0; x < 9; x++) {
                        if(!dangerZone.contains(new ArrayList<>(Arrays.asList(x, y))) || cloakFlag)
                            freeCells.add(new ArrayList<>(Arrays.asList(x, y)));
                    }
                }
                if (norrisCoords != null) freeCells.remove(new ArrayList<>(Arrays.asList(location.norrisCoords[0], location.norrisCoords[1])));
                if (filchsCoords != null) freeCells.remove(new ArrayList<>(Arrays.asList(location.filchsCoords[0], location.filchsCoords[1])));
                checked.clear();
                priorityQueue.clear();

                freeCells.remove(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));
                checked.add(new ArrayList<>(Arrays.asList(map.harrysCoords[0], map.harrysCoords[1])));
                priorityQueue.add(new ArrayList<>(Arrays.asList((double) location.bookCoords[0], (double) location.bookCoords[1], heuristic.get(location.bookCoords[0]).get(location.bookCoords[1]))));
                continue;
            }
            if (bookFlag && Arrays.equals(map.harrysCoords, map.exitCoords)) {
                path.addAll(pathCal(location.bookCoords, location.exitCoords, costMatrix));
                path.add(new ArrayList<>(Arrays.asList(location.exitCoords[0], location.exitCoords[1])));
                state = "Win";
                break;
            }
            for (int y = -2; y < 3; y++) {
                for (int x = -2; x < 3; x++) {
                    int[] option = new int[]{map.harrysCoords[0] + x, map.harrysCoords[1] + y};

                    if ((perceptionType == 1 && Main.pointToPoint(option, map.harrysCoords) >= 2) || (perceptionType == 2 && (Main.pointToPoint(option, map.harrysCoords) < 2 ||
                            abs(x) + abs(y) == 4)))
                        continue;

                    if (Arrays.equals(option, location.filchsCoords)) {
                        freeCells.remove(new ArrayList<>(Arrays.asList(option[0], option[1])));
                        filchsCoords = option;
                        continue;
                    }

                    if (Arrays.equals(option, location.norrisCoords)) {
                        freeCells.remove(new ArrayList<>(Arrays.asList(option[0], option[1])));
                        norrisCoords = option;
                        continue;
                    }

                    if (inDangerZone(option) && !cloakFlag) {
                        freeCells.remove(new ArrayList<>(Arrays.asList(option[0], option[1])));
                        dangerZone.add(new ArrayList<>(Arrays.asList(option[0], option[1])));
                        continue;
                    }
                    if (inDangerZone(option) && cloakFlag) dangerZone.add(new ArrayList<>(Arrays.asList(option[0], option[1])));
                }
            }

            for (int y = -1; y < 2; y++) {
                for (int x = -1; x < 2; x++) {
                    try {
                        ArrayList<Double> option = new ArrayList<>(Arrays.asList((double) map.harrysCoords[0] + x, (double) map.harrysCoords[1] + y,
                                1.0 + heuristic.get(map.harrysCoords[0] + x).get(map.harrysCoords[1] + y) + currentPivot.get(2)));
                        if (!cloakFlag && inDangerZone(new int[]{map.harrysCoords[0] + x, map.harrysCoords[1] + y})) option.set(2, option.get(2)+100);
                        if(freeCells.contains(new ArrayList<>(Arrays.asList(map.harrysCoords[0] + x, map.harrysCoords[1] + y))) && !checked.contains(option) &&
                                map.harrysCoords[0] + x>=0 && map.harrysCoords[0] + x<9 && map.harrysCoords[1] + y>=0 && map.harrysCoords[1] + y<9) {
                            priorityQueue.add(option);
                            if(!randGoal) costMatrix[map.harrysCoords[0] + x][map.harrysCoords[1] + y] = option.get(2);
                            checked.add(new ArrayList<>(Arrays.asList((int) (double)option.get(0), (int) (double)option.get(1))));
                            freeCells.remove(new ArrayList<>(Arrays.asList((int) (double)option.get(0), (int) (double)option.get(1))));
                        }

                    }
                    catch(Exception ignored){}


                }
            }

        }
        if(state == null || state.equals(" Lose")) {
            System.out.println("1. A*");
            System.out.println("2. Lose");
            System.out.println("3. " + path.size());
            System.out.println("4. " + path.toString());
            showPath();
        }
        else if(state.equals("Win")) {
            System.out.println("1. A*");
            System.out.println("2. Win");
            System.out.println("3. " + path.size());
            System.out.println("4. " + path.toString());
            showPath();
            state = "Win";
        }

    }
}

class comparator implements Comparator<ArrayList<Double>> {
    @Override
    public int compare(ArrayList<Double> point1, ArrayList<Double> point2) {
        return point1.get(2).compareTo(point2.get(2));
    }
}
