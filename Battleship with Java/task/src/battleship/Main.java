package battleship;

import java.io.IOException;
import java.util.*;

public class Main {
    private static int x1, y1, x2, y2, currentLength;
    private static boolean turn = true;
    private static int lengthOfShip;
    private static boolean shipIsHorizontal;
    private final static  String[][] player1Field = new String[12][12];
    private final static  String[][] player1FieldFog = new String[12][12];
    private final static String[][] player2Field = new String[12][12];
    private final static String[][] player2FieldFog = new String[12][12];
    private final static String player_1 = "Player 1";
    private final static String player_2 = "Player 2";
    protected static Map<ShipType, List<ShipCoordinates>> player_1FleetCoordinates = new HashMap<>();
    protected static Map<ShipType, List<ShipCoordinates>> player_2FleetCoordinates = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Player 1, place your ships on the game field\n");
        initializeGameField(scanner, player1Field, player1FieldFog, player_1FleetCoordinates);

        pressEnterToContinue();

        System.out.println("Player 2, place your ships to the game field\n");
        initializeGameField(scanner, player2Field, player2FieldFog, player_2FleetCoordinates);

        pressEnterToContinue();

        while (gameGoesOn(player1Field) && gameGoesOn(player2Field)) {
            if (turn) {
                printBattlefield(player1Field, player2FieldFog);
                startGame(scanner, player2Field, player2FieldFog, player_1, player_2FleetCoordinates);
            } else {
                printBattlefield(player2Field, player1FieldFog);
                startGame(scanner, player1Field, player1FieldFog, player_2, player_1FleetCoordinates);
            }
        }

        scanner.close();
    }

    public static void initializeGameField(Scanner scanner, String[][] gameField, String[][] gameFieldFog, Map<ShipType, List<ShipCoordinates>> fleetCoordinates) {
        gameField[0][0] = " ";
        for (int i = 1; i <= 10; i++) {
            gameField[0][i] = " " + i;
        }
        for (int i = 1; i <= 10; i++) {
            for (int j = 0; j < 11; j++) {
                gameField[i][j] = " ~";
            }
            gameField[i][0] = String.valueOf((char) ('A' + i - 1));
        }

        for (int i = 0; i < gameField.length; i++) {
            gameFieldFog[i] = Arrays.copyOf(gameField[i], gameField[i].length);
        }

        printField(gameField);

        for (ShipType shipType : ShipType.values()) {
            List<ShipCoordinates> battleshipCoordinates = new ArrayList<>();

            currentLength = shipType.getLength();
            System.out.printf("\nEnter the coordinates of the %s (%d cells):\n\n", shipType.getName(), currentLength);

            placeShip(scanner, shipType, gameField, fleetCoordinates, battleshipCoordinates);
        }
    }
    public static void printField(String[][] gameField) {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                System.out.print(gameField[i][j]);
            }
            System.out.println();
        }
    }

    public static void printBattlefield(String[][] gameField, String[][] gameFieldFog) {
        printField(gameFieldFog);
        System.out.println("---------------------");
        printField(gameField);
    }

    public static void placeShip(Scanner scanner, ShipType shipType, String[][] gameField, Map<ShipType, List<ShipCoordinates>> fleetCoordinates, List<ShipCoordinates> battleshipCoordinates) {

        do {
            String beginningOfShip = scanner.next();
            String endOfShip = scanner.next();

            x1 = beginningOfShip.charAt(0) - 64;
            y1 = Integer.parseInt(beginningOfShip.substring(1));

            x2 = endOfShip.charAt(0) - 64;
            y2 = Integer.parseInt(endOfShip.substring(1));

            if (x1 == x2) {
                lengthOfShip = Math.abs(y1 - y2) + 1;
                shipIsHorizontal = true;
            }
            else {
                lengthOfShip = Math.abs(x1 - x2) + 1;
                shipIsHorizontal = false;
            }

            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            if (y1 > y2) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }

        } while (checkCoordinates(gameField));

        if (shipIsHorizontal) {
            for (int i = 0; i < lengthOfShip; i++) {
                gameField[x1][y1 + i] = " O";
                battleshipCoordinates.add(new ShipCoordinates(x1, y1 + i));
            }
        } else {
            for (int i = 0; i < lengthOfShip; i++) {
                gameField[x1 + i][y1] = " O";
                battleshipCoordinates.add(new ShipCoordinates(x1 + i, y1));
            }
        }

        fleetCoordinates.put(shipType, battleshipCoordinates);

        printField(gameField);
    }

    public static boolean checkCoordinates(String[][] gameField) {
        if ((x1 == x2 && y1 == y2) || (x1 < 1 || x1 > 10 || y1 < 1 || y1 > 10 || x2 < 1 || x2 > 10 || y2 < 1 || y2 >10) ) {
            System.out.println("\nError! You entered the wrong coordinates! Try again:\n");
            return true;
        } else if (x1 != x2 && y1 != y2) {
            System.out.println("\nError! Wrong ship location! Try again:\n");
            return true;
        }
        if (lengthOfShip != currentLength) {
            System.out.println("\nError! Wrong length of the Submarine! Try again:\n");
            return true;
        }
        if (shipIsHorizontal) {
            for (int i = x1 - 1; i <= x1 + 1; i++) {
                for (int j = y1 - 1; j <= y2 + 1; j++) {
                    try {
                        if (gameField[i][j].equals(" O")) {
                            System.out.println("\nError! You placed it too close to another one. Try again:\n");
                            return true;
                        }
                    } catch (NullPointerException ignored) {

                    }
                }
            }
        } else {
            for (int i = x1 - 1; i <= x2 + 1; i++) {
                for (int j = y1 - 1; j <= y1 + 1; j++) {
                    try {
                        if (gameField[i][j].equals(" O")) {
                            System.out.println("\nError! You placed it too close to another one. Try again:\n");
                            return true;
                        }
                    } catch (NullPointerException ignored) {

                    }
                }
            }
        }
        return false;
    }

    public static boolean checkCoordinates(int x, int y) {
        if (x < 1 || x > 10 || y < 1 || y > 10) {
            System.out.println("\nError! You entered the wrong coordinates! Try again:\n");
            return true;
        }

        return false;
    }

    public static void startGame(Scanner scanner, String[][] gameField, String[][] gameFieldFog, String player, Map<ShipType, List<ShipCoordinates>> fleetCoordinates) {

        System.out.println();
        System.out.println(player + ", it's your turn:\n");

        int shotX;
        int shotY;

        do {
            String shotCoordinates = scanner.next();

            shotX = shotCoordinates.charAt(0) - 64;
            shotY = Integer.parseInt(shotCoordinates.substring(1));
        } while (checkCoordinates(shotX, shotY));

        if (gameField[shotX][shotY].equals(" O") || gameField[shotX][shotY].equals(" X")) {
            removePartOfShip(shotX, shotY, fleetCoordinates);
            pressEnterToContinue();

            gameField[shotX][shotY] = " X";
            gameFieldFog[shotX][shotY] = " X";

        } else {
            System.out.println("\nYou missed!");
            pressEnterToContinue();

            gameField[shotX][shotY] = " M";
            gameFieldFog[shotX][shotY] = " M";

        }

        turn = !turn;
    }

    public static boolean gameGoesOn(String[][] gameField) {
        for (String[] row : gameField) {
            for (String element : row) {
                try {
                    if (element.equals(" O")) {
                        return true;
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }
        return false;
    }

    public static void removePartOfShip(int x, int y, Map<ShipType, List<ShipCoordinates>> fleetCoordinates) {
        ShipCoordinates shipCoordinates = new ShipCoordinates(x, y);

        for (Map.Entry<ShipType, List<ShipCoordinates>> entry : fleetCoordinates.entrySet()) {
            ShipType shipType = entry.getKey();
            List<ShipCoordinates> coordinatesList =  fleetCoordinates.get(shipType);

            coordinatesList.removeIf(coordinates -> coordinates.equals(shipCoordinates));

            if (coordinatesList.isEmpty()) {
                fleetCoordinates.remove(shipType);
                if (fleetCoordinates.isEmpty()) {
                    System.out.println("\nYou sank the last ship. You won. Congratulations!\n");
                    System.exit(0);
                }
                System.out.println("\nYou sank a ship!");
                break;
            }

            System.out.println("\nYou hit a ship!");
        }
    }

    public static void pressEnterToContinue() {
        System.out.println("\nPress Enter and pass the move to another player");
        System.out.println("...");
        while (true) {
            try {
                if (System.in.read() == '\n') break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public enum ShipType {
        AIRCRAFT_CARRIER("Aircraft Carrier", 5),
        BATTLESHIP("Battleship", 4),
        SUBMARINE("Submarine", 3),
        CRUISER("Cruiser", 3),
        DESTROYER("Destroyer", 2);

        private final String name;
        private final int length;

        ShipType(String name, int length) {
            this.name = name;
            this.length = length;
        }

        public String getName() {
            return this.name;
        }

        public int getLength() {
            return this.length;
        }
    }

    public record ShipCoordinates(int x, int y) {
    }
 }





