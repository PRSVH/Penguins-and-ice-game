package comp1110.ass1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class PenguinsPoolParty {

    // The game board
    public Hex[][] board;

    // The given challenge for the game
    public Challenge challenge;

    // The width of the board
    public final static int BOARD_WIDTH = 5;

    // The height of the board
    public final static int BOARD_HEIGHT = 4;

    // An array containing all four ice blocks for the game. Please consult the
    // readme for more details about ice blocks.
    //
    // Please note that positions instantiated for each ice block are not
    // permanent. Each ice block has an `onBoard` field that determines whether
    // they are on the board or not. The positions used here are simply used
    // for your ease in viewing (as opposed to negative coordinates, which are
    // harder on the eyes).
    private final Ice[] iceBlocks = new Ice[]{
            new Ice('A', new int[]{0, 0, 0, -1, -1, -1, -2, -1}, 0), // Ice block A
            new Ice('B', new int[]{0, 0, 0, -1, -1, -1, -2, -2}, 0), // Ice block B
            new Ice('C', new int[]{0, 0, 0, -1, -1, -1, -1, -2}, 0), // Ice block C
            new Ice('D', new int[]{0, 0, 0, -1, 0, -2, -1, -1}, 0) // Ice block D
    };

    /**
     * Instantiates the game according to a given challenge. This defines the
     * challenge number and the initial placement of penguins on the board.
     *
     * @param challenge the challenge according to which to set up the game
     */
    public PenguinsPoolParty(Challenge challenge) {
        this.challenge = challenge;
        this.initialiseBoard(challenge);
    }

    /**
     * Constructs an instance of the Penguins Pool Party using a given initial
     * state and an array of ice block placements. Note that there is no
     * challenge corresponding to this constructor.
     *
     * This constructor is used only for unit tests. You shouldn't find any
     * need to use it for your own implementation.
     *
     * @param initialState  the initial state of the board
     * @param icePlacements the ice block placements made on the board
     */
    public PenguinsPoolParty(String initialState, Ice[] icePlacements) {
        this.challenge = null;
        this.board = new Hex[5][4];
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                HexType type = HexType.fromChar(initialState.charAt(5 * y + x));
                this.board[x][y] = new Hex(x, y, type);
            }
        }
        for (Ice ice : icePlacements) {
            Ice iceBlock = iceBlocks[ice.getId() - 'A'];
            iceBlock.setOnBoard(true);
            iceBlock.setOriginX(ice.getOriginX());
            iceBlock.setOriginY(ice.getOriginY());
            iceBlock.setRotation(ice.getRotation());
            iceBlock.setHexes(ice.getHexes());
        }
    }

    /**
     * Instantiates the game according to a predefined board state, in String
     * form. This constructor is only used for unit tests, you most likely
     * won't need to use it in your implementation.
     *
     * @param initialState the initial state of the board in String form
     */
    public PenguinsPoolParty(String initialState) {
        this.challenge = null;
        this.board = new Hex[5][4];
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                HexType type = HexType.fromChar(initialState.charAt(5 * y + x));
                this.board[x][y] = new Hex(x, y, type);
            }
        }
    }

    /**
     * Instantiates the game with no pre-defined challenge and an empty board.
     */
    public PenguinsPoolParty() {
        this("EEEEEEEEEEEEEEEEEEEE");
    }

    /**
     * Instantiates the board according to the initial state of the given
     * challenge.
     *
     * @param challenge the challenge with which to initialise the board
     */
    private void initialiseBoard(Challenge challenge) {
        this.board = new Hex[5][4];
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                this.board[x][y] = new Hex(x, y, HexType.EMPTY);
            }
        }
        String penguins = challenge.getInitialState();
        for (int i = 0; i < penguins.length(); i += 2) {
            this.setHex(penguins.charAt(i) - '0', penguins.charAt(i + 1) - '0', HexType.PENGUIN);
        }
    }

    /**
     * @return the state of the board, in String form
     */
    public String boardToString() {
        StringBuilder s = new StringBuilder();
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                s.append(this.getHex(x, y).getType().toChar());
            }
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * @param x the x-coordinate of the hex
     * @param y the y-coordinate of the hex
     * @return  the hex at the given coordinates
     */
    public Hex getHex(int x, int y) {
        return this.board[x][y];
    }

    /**
     * Sets the hex at the given coordinates on this game's board to be the
     * given type.
     *
     * @param x    the x-coordinate of the hex to change
     * @param y    the y-coordinate of the hex to change
     * @param type the hex type that this hex will have (out of EMPTY, ICE and
     *             PENGUIN)
     */
    public void setHex(int x, int y, HexType type) {
        this.board[x][y].setType(type);
    }

    /**
     * @return the ice blocks in this game
     */
    public Ice[] getIceBlocks() {
        return this.iceBlocks;
    }

    public Ice applyPlacement(String placement) {
        int idx = placement.charAt(0) - 'A';
        Ice ice = this.getIceBlocks()[idx];
        int x = placement.charAt(1) - '0';
        int y = placement.charAt(2) - '0';
        ice.translate(this.getHex(x, y));
        int r = placement.charAt(3) - '0';
        while (ice.getRotation() != r) ice.rotate60Degrees();
        return ice;
    }

    public String getSolutionString() {
        StringBuilder res = new StringBuilder();
        for (Ice i : this.getIceBlocks()) {
            res.append(i.toString());
        }
        return res.toString();
    }

    /**
     * Returns an array of hexes, representing the neighbours of a given hex on
     * the board. The neighbours are returned in clockwise order, starting from
     * the hex directly on top of the given hex. You might like to think of the
     * first neighbouring hex as the one at 12 o'clock of the given hex. If a
     * potential neighbour is off the board, its place in the resulting array
     * should contain `null`.
     *
     * Hint 1: the locations of the neighbours of a hex depend on the x-coordinate
     * of that hex. You might want to break this method up into sub-problems
     * depending on the x-coordinate of the given hex. Consult the readme for
     * the design of the board, and draw out the board on paper if you need.
     *
     * Hint 2: Make sure you use the `board` field provided to you in this
     * class. This is important for later tasks. Please do not create any new
     * instances of the Hex class in your implementation.
     *
     * @param hex the hex whose neighbours we wish to find
     * @return the neighbours of the given hex, in the order: <north neighbour>,
     *         <north-east neighbour>, <south-east neighbour>, <south neighbour>,
     *         <south-west neighbour>, <north-west neighbour>.
     */
    public Hex[] getNeighbours(Hex hex) {
        Hex[] hexes = new Hex[6];
        int myX = hex.getX();
        int myY = hex.getY();
        //the north neighbour
        if (myY == 0) {
            hexes[0] = null;
        } else {
            hexes[0] = getHex(myX, (myY - 1));
        }
        //the northeast
        if (myX == 4 || (myY == 0 && myX % 2 == 1)) {
            hexes[1] = null;
        } else if (myX % 2 == 1) {
            hexes[1] = getHex(myX + 1, myY - 1);
        } else {
            hexes[1] = getHex(myX + 1, myY);
        }
        //the southeast
        if (myX == 4 || (myY == 3 && myX % 2 == 0)) {
            hexes[2] = null;
        } else if (myX % 2 == 1) {
            hexes[2] = getHex(myX + 1, myY);
        } else {
            hexes[2] = getHex(myX + 1, myY + 1);
        }
        //the south neighbour
        if (myY == 3) {
            hexes[3] = null;
        } else {
            hexes[3] = getHex(myX, myY + 1);
        }
        //the southwest
        if (myX == 0 || (myY == 3 && myX % 2 == 0)) {
            hexes[4] = null;
        } else if (myX % 2 == 1) {
            hexes[4] = getHex(myX - 1, myY);
        } else {
            hexes[4] = getHex(myX - 1, myY + 1);
        }
        //the northwest
        if (myX == 0 || (myY == 0 && myX % 2 == 1)) {
            hexes[5] = null;
        } else if (myX % 2 == 1) {
            hexes[5] = getHex(myX - 1, myY - 1);
        } else {
            hexes[5] = getHex(myX - 1, myY);
        }

        return hexes;
    }

    /**
     * Determine whether the current board represents a solution to the game.
     * Remember, for a board state to be a solution to the game, all four ice
     * blocks must be placed on the board.
     *
     * Hint: can you think of a way to know whether all four ice blocks are on
     * the board without knowing their positions or rotations?
     *
     * @return whether the current board represents a solution to the game
     */
    public boolean isSolution() {
        String t = this.boardToString(); //get the string of types
        char[] types = t.toCharArray();
        int count = 0;
        for (char i : types) {
            if (i == 'I') {
                count++; // count the number of I
            }
        }
        return count == 16;
    }

    /**
     * Determine whether a placement of ice is valid according to this game's
     * board. You can obtain the placement of each hexagon in the ice block by
     * using the `getHexes()` method in the `Ice` class.
     *
     * For an ice block placement to be valid, each hexagon in the ice block
     * must:
     *
     * 1. be on the board, that is, no hexagon can be hanging off the board;
     *    and
     * 2. not overlap with any penguins or other ice blocks that have already
     *    been placed on the board.
     *
     * You do not need to worry about duplicate ice blocks. That is, you can
     * assume that the given ice block is not already on the board.
     *
     * @param ice the ice block to place on the board, at positions according
     *            to the ice block's hexagons
     * @return    whether the placement of the given ice block is valid
     *            according to the game rules
     */
    public boolean isIcePlacementValid(Ice ice) {
        Hex[] iceHex = ice.getHexes();// get the list of icebox hex
        boolean isFlag = true;
        for (Hex hex : iceHex) {
            int x = hex.getX();
            int y = hex.getY();
            if (x < 0 || x > 4 || y < 0 || y > 3) {
                return false;
            } else {

                isFlag = isFlag && (this.board[x][y].getType() == HexType.EMPTY);
            }
        }
        return isFlag;
    }

    /**
     * Place an ice block on the board.
     *
     * Note that, for this method, you must change the board according to the
     * placement of the ice blocks. That is, you must change all hexes on the
     * board to type ICE that share a coordinate with one of the hexes in the
     * ice block.
     *
     * Note that you can assume that the placement of the ice block is valid.
     *
     * Once the ice block is successfully placed on the board, you will need
     * to modify it so that it is on the board. You will need to use the
     * `setOnBoard()` method in the Ice class to do this.
     *
     * Hint: you might find the `setHex()` method useful to solve this task.
     *
     * @param ice the ice block to place on the board
     */
    public void placeIceBlock(Ice ice) {
        Hex[] iceHex = ice.getHexes(); // get the list of icebox hex
        for (Hex hex : iceHex) {
            int x = hex.getX();// get the x and y coordinate
            int y = hex.getY();
            this.setHex(x, y, HexType.ICE);
        }
        ice.setOnBoard(true);
    }

    /**
     * Remove an ice block from the board.
     *
     * Note that, for this method, you must change the board according to the
     * placement of the ice blocks. That is, you must change all hexes on the
     * board to type EMPTY that share a coordinate with one of the hexes in the
     * ice block to be removed.
     *
     * Note that you can assume that the removal of the ice block is valid,
     * that is that the ice block is validly placed and is already on the
     * board.
     *
     * Once the ice block is successfully removed from the board, you will need
     * to modify it so that it is off the board. You will need to use the
     * `setOnBoard()` method in the Ice class to do this.
     *
     * Hint: you might find the `setHex()` method useful to solve this task.
     *
     * @param ice the ice block to remove from the board
     */
    public void removeIceBlock(Ice ice) {
        Hex[] iceHex = ice.getHexes(); // get the list of icebox hex
        for (Hex hex : iceHex) {
            int x = hex.getX();// get the x and y coordinate
            int y = hex.getY();
            this.setHex(x, y, HexType.EMPTY);
        }
        ice.setOnBoard(false);
    }

    /**
     * Get all the valid ice block placements from this game's board state.
     *
     * The ice blocks can be accessed from the `iceBlocks` field. Note that an
     * ice block should not be placed on the board if it is already on the
     * board. You can determine this using the `isOnBoard` field of the `Ice`
     * class.
     *
     * Return the array elements in alphabetical order: that is, first order by
     * ice block ID, then by origin x-coordinate, then by origin y-coordinate,
     * and finally by rotation.
     *
     * Note that this task is particularly difficult, and may require knowledge
     * of concepts beyond what has been taught in the course so far. If you
     * feel stuck on this problem, I would recommend you come back to it later
     * once we have covered the relevant topics in class. Remember, this
     * assignment is redeemable against the exam, so it is not particularly
     * important to complete all tasks in this assignment.
     *
     * @return all valid ice block placements from this game's board state
     */
    public String[] getAllValidPlacements() {
        ArrayList<String> icePlacements = new ArrayList<>(); //use the arraylist so that i can add string to a array before initialise it size
        for (var ice : this.iceBlocks) {
            if (!ice.isOnBoard()) {
                for (int y = 0; y < 4; y++) {
                    for (int x = 0; x < 5; x++) {
                        ice.translate(this.board[x][y]);
                        for (int i = 0; i < 6; i++) {
                            if (isIcePlacementValid(ice)) {
                                icePlacements.add(ice.toString());
                            }
                            ice.rotate60Degrees();}}}}}
        HashSet<String> set = new HashSet<>(icePlacements);// the best way to remove the duplicate element
        icePlacements.clear();
        icePlacements.addAll(set);//return the removed set to origin
        Collections.sort(icePlacements);// order the list
        String[] validIces = new String[icePlacements.size()];//give it to the string array
        for (int j = 0; j < validIces.length; j++) {
            validIces[j] = icePlacements.get(j);
        }
        return validIces;
    }

    /**
     * Find the solution to this game.
     *
     * The solution is a string, represented by:
     *
     * {String representation of ice block A}{String representation of ice block B}
     * {String representation of ice block C}{String representation of ice block D}
     *
     * Please consult the readme for solution and ice block encodings.
     *
     * Note that this task is particularly difficult, and may require knowledge
     * of concepts beyond what has been taught in the course so far. If you
     * feel stuck on this problem, I would recommend you come back to it later
     * once we have covered the relevant topics in class. Remember, this
     * assignment is redeemable against the exam, so it is not particularly
     * important to complete all tasks in this assignment.
     *
     * @return the solution to this game, in String form
     */
    public String findSolution() {
        int i = 0,j ,m ,n ;
        String[] allA, allB, allC, allD;
        allA = this.getAllValidPlacements();
        while (allA[i].charAt(0) == 'A') {
            int AX = (allA[i].charAt(1) - '0');//x coordinate
            int AY = (allA[i].charAt(2) - '0');//y coordinate
            int AR = (allA[i].charAt(3) - '0');//rotation
            iceBlocks[0].translate(board[AX][AY]);//translate the ice A to the board
            while (iceBlocks[0].getRotation() != AR) {
                iceBlocks[0].rotate60Degrees();  // rotate the A ice block
            }
            placeIceBlock(iceBlocks[0]); // place the A to the board
            allB = this.getAllValidPlacements(); // move B
            if (allB.length == 0) {       //if there is no B can place after place A, then place A again
                i++;
                removeIceBlock(iceBlocks[0]);
                if (i >= allA.length) break;
                continue;
            }
            j = 0;
            while (allB[j].charAt(0) == 'B') {
                int BX = (allB[j].charAt(1) - '0');//x coordinate
                int BY = (allB[j].charAt(2) - '0');//y coordinate
                int BR = (allB[j].charAt(3) - '0');//rotation
                iceBlocks[1].translate(board[BX][BY]);//translate the ice B to the board
                while (iceBlocks[1].getRotation() != BR) {
                    iceBlocks[1].rotate60Degrees();  // rotate the B ice block
                }
                placeIceBlock(iceBlocks[1]); // place the B to the board
                allC = this.getAllValidPlacements(); // move C
                if (allC.length == 0) {       //if there is no C can place after place A and B, then place B again
                    j++;
                    removeIceBlock(iceBlocks[1]);
                    if (j >= allB.length) break;
                    continue;
                }
                m = 0;
                while (allC[m].charAt(0) == 'C') {
                    int CX = (allC[m].charAt(1) - '0');//x coordinate
                    int CY = (allC[m].charAt(2) - '0');//y coordinate
                    int CR = (allC[m].charAt(3) - '0');//rotation
                    iceBlocks[2].translate(board[CX][CY]);//translate the ice C to the board
                    while (iceBlocks[2].getRotation() != CR) {
                        iceBlocks[2].rotate60Degrees();  // rotate the C ice block
                    }
                    placeIceBlock(iceBlocks[2]); // place the C to the board
                    allD = this.getAllValidPlacements(); // move D
                    if (allD.length == 0) {       //if there is no D can place after place ABC, then place C again
                        m++;
                        removeIceBlock(iceBlocks[2]);
                        if (m >= allC.length) break;
                        continue;
                    }
                    n = 0;
                    while (allD[n].charAt(0) == 'D') {
                        int DX = (allD[n].charAt(1) - '0');//x coordinate
                        int DY = (allD[n].charAt(2) - '0');//y coordinate
                        int DR = (allD[n].charAt(3) - '0');//rotation
                        iceBlocks[3].translate(board[DX][DY]);//translate the ice A to the board
                        while (iceBlocks[3].getRotation() != DR) {
                            iceBlocks[3].rotate60Degrees();  // rotate the A ice block
                        }
                        if (isIcePlacementValid(iceBlocks[3])) {
                            return iceBlocks[0].toString() + iceBlocks[1].toString()
                                    + iceBlocks[2].toString() + iceBlocks[3].toString();
                        }
                        n++;
                        if (n >= allD.length) break;

                        removeIceBlock(iceBlocks[3]);
                    }
                    m++;
                    if (m >= allC.length) break;
                    removeIceBlock(iceBlocks[2]);
                }
                j++;
                if (j >= allB.length) break;
                removeIceBlock(iceBlocks[1]);
            }
            i++;
            if (i >= allA.length) break;
            removeIceBlock(iceBlocks[0]);
        }
        System.out.println("failed");
        return "";

    }
}

