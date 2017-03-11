import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

public class ChasmEscape {
	
	// Tile types
	private static final char WALK = (char) 1;
	private static final char UNWALK = ' ';
	private static final char FIRE = '~';
	private static final char MONSTER = '@';
	private static final char JUMP = 'J';
	private static final char POINT = 'P';
	private static final char HEAL = '+';
	private static final char WIN = '*';
	// Add bonus hp? Shields? Poison?
	
	public static void main(String[] args) {
		game();
	}
	
	public static void game() {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Welcome. Choose map dimensions:");
		int n =Integer.parseInt(sc.nextLine()); // Map dimensions
		//~ Integer.parseInt(sc.nextLine());
		
		char[][] map = createMap(n, n); // Create n*n map
		
		boolean end = false;
		int x = 0; // Starting x
		int y = 1; // Starting y
		int hp = 20; // Starting HP
		String move = "";
		String message = "";
		int m = 1; // Movement range modifier
		boolean hasJ = false; // Jump ability
		boolean j = false; // Jump toggle
		int pts = 0; // Points
		
		String help = "\nInput 'quit' or 'exit' to stop playing.\nInput WASD to move one block.\nInput 'hp' to see remaining health points.\nInput 'pts' to see your points.\nInput 'j' to make next move a jump.\nInput 'help' to see this message.\n";
		System.out.println(help);
		sc.nextLine();
		
		while (!end) {
			System.out.println(drawMap(map, x, y));
			
			System.out.println(message); // Generic message holder
			message = "";
			
			move = sc.nextLine();
			if (move.equals("w") && y - m >= 0 && map[y - m][x] != UNWALK) // Move up
				y-=m;
			else if (move.equals("s") && y + m < map.length && map[y + m][x] != UNWALK) // Move down
				y+=m;
			else if (move.equals("a") && x - m >= 0 && map[y][x - m] != UNWALK) // Move left
				x-=m;
			else if (move.equals("d") && x + m < map[0].length && map[y][x + m] != UNWALK) // Move right
				x+=m;
			else if (move.equals("j")) // Jump
				j = true;
			else if (move.equals("hp")) // View HP
				message += hp;
			else if (move.equals("pts")) // View points
				message += pts;
			else if (move.equals("help"))
				message += help;
			else if (move.equals("quit") || move.equals("exit")) // Stop game
				return;
			else if (move.equals("")); // Do nothing and refresh map
			else message += "Invalid move... "; // Invalid move
			
			clearScreen();
			
			/* 
			 * Events
			 */
			
			// Win
			if (map[y][x] == WIN) {
				int score = hp + pts;
				System.out.println("You won!\nScore: " + score + " points.\nInput 'again' to play again. ");
				if (sc.nextLine().equals("again")) {
					System.out.println("Welcome. Choose map dimensions:");
					n =Integer.parseInt(sc.nextLine());
					//~ Integer.parseInt(sc.nextLine());
					map = createMap(n, n);
					x = 0;
					y = 1;
					hp = 20;
					m = 1;
					hasJ = false;
					j = false;
					pts = 0;
					System.out.println("Input 'quit' or 'exit' to stop playing.\nInput WASD to move one block.\nInput 'hp' to see remaining health points.\nInput 'pts' to see your points.\nInput 'j' to make next move a jump.");
					sc.nextLine();
				}
				else end = true;
			}
			
			// Death
			if (hp == 0) {
				System.out.println("You lost. ");
				end = true;
			}
			
			// Fire
			if (map[y][x] == FIRE){
				message += "You take 1 damage.\nYou have " + --hp + " hp left. ";
			}
			
			// Monster
			if (map[y][x] == MONSTER){
				message += "You take 1 damage and kill the monster. You gain a point.\nYou have " + --hp + " hp left and " + ++pts + " points. ";
				map[y][x] = genRandTile();
			}
			
			// Jump ability
			m = 1;
			if (hasJ && j) { m++; j = false; }
			else if (map[y][x] == JUMP) { hasJ = true; map[y][x] = genRandTile(); message += "You have gained the ability to jump. "; }
			else if (j) { message += "You do not have the jump ability. "; j = false; }
			
			// Heal
			if (map[y][x] == HEAL) {
				message += "Healed. You now have " + ++hp + " left. ";
				map[y][x] = genRandTile();
			}
			
			// Point
			if (map[y][x] == POINT) {
				message += "You have gained a point! You now have " + ++pts + " points. ";
				map[y][x] = genRandTile();
			}
			
			// Floor collapse
			if (Math.random() < 0.05) { // Decrease to decrease possibility
				message += "The floor beneath you collapses. ";
				map[y][x] = UNWALK;
			}
			
		}
	}
	
	public static char[][] createMap(int x, int y) {
		char[][] map = new char[y][x];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				char tile;
				double rand = Math.random();
				
				if (rand < 0.3) tile = UNWALK; // Increase to increase unwalkable terrain
				else if (rand > 0.9) tile = FIRE; // Increase to decrease fires
				else tile = WALK; // Normal tiles
				
				map[i][j] = tile;
			}
		}
		
		// First means lower priority
		for (int i=0;i<5;i++) map[(int) (Math.random() * map.length)][(int) (Math.random() * map[0].length)] = MONSTER; // 5
		for (int i=0;i<5;i++) map[(int) (Math.random() * map.length)][(int) (Math.random() * map[0].length)] = JUMP; // 5
		for (int i=0;i<5;i++) map[(int) (Math.random() * map.length)][(int) (Math.random() * map[0].length)] = POINT; // 5
		for (int i=0;i<5;i++) map[(int) (Math.random() * map.length)][(int) (Math.random() * map[0].length)] = HEAL; // 5
		map[(int) (Math.random() * map.length)][(int) (Math.random() * map[0].length)] = WIN;
		
		return map;
	}
	
	public static String drawMap(char[][] map, int x, int y) {
		StringBuilder mapStr = new StringBuilder();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (i == y && j == x) mapStr.append("X ");
				else mapStr.append(map[i][j] + " ");
			}
			mapStr.append("\n");
		}
		return mapStr.toString();
	}
	
	public static void clearScreen() {
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			final String os = System.getProperty("os.name");
			if (os.contains("Windows"))
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			else
				Runtime.getRuntime().exec("clear");
		} catch (Exception e) {}
	}
	
	// Generate random tile
	// Only walkable and unwalkable for now
	public static char genRandTile() {
		if (Math.random() < 0.5) return UNWALK;
		else return WALK;
	}
	
}

