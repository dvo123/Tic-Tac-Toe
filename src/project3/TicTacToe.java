package project3;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class TicTacToe {
	static final int SIZE = 8; // Size = 8x8 board
	static final int CONNECT = 4; // connect = 4 in a line to win
	static final int maxdepth = 9;
	static int[][] b = new int[SIZE][SIZE];
	static int maxTime = 0;
	static long startTime;

	public static void main(String[] args) {
		// Setup Board
		Setup();

		// Get Max Time from user
		maxTime = GetMaxTime();

		// Ask user who moves first
		boolean computerMovesFirst = AIMovesFirst();

		if (computerMovesFirst)
			Makemove(); // Computer makes first move
		Printboard(); // Show board

		// Enter Game Loop
		while (true) {
			GetMove();
			if (CheckGameOver())
				break;

			startTime = System.currentTimeMillis();
			Makemove();
			if (CheckGameOver())
				break;

			System.out.println();
		}

		System.out.println("Thank you for playing!\nExiting...");
		System.exit(0); // Exit
	}

	static boolean AIMovesFirst() {
		char keyPressed;
		Scanner scanner = new Scanner(System.in);
		do {
			System.out.print("Do you want to move first [Y/N]? ");
			keyPressed = Character.toUpperCase(scanner.next().charAt(0));
			if (keyPressed != 'Y' && keyPressed != 'N') {
				System.out.println("Invalid Input: Please enter 'Y' or 'N'.");
			}
		} while (keyPressed != 'Y' && keyPressed != 'N');
		return (keyPressed == 'N');
	}

	static int GetMaxTime() {
		int time = 0;
		boolean invalidTime = true;
		Scanner scanner = new Scanner(System.in);
		while (invalidTime) {
			System.out.print("Max amount of time allowed for AI [1-30] in seconds: ");
			if (scanner.hasNextInt()) {
				time = scanner.nextInt();
				if (time >= 1 && time <= 30) {
					invalidTime = false;
				} else {
					System.out.println("Invalid Time: Please enter a number between 1 and 30.");
				}
			} else {
				System.out.println("Invalid Input: Please enter a valid number.");
				scanner.next();
			}
		}
		return time;
	}

	static void Printboard() { // Show Game Board
		System.out.print("  ");
		for (int i = 1; i <= SIZE; i++)
			System.out.print(i + " ");
		System.out.println();

		char c = 'A';
		int cVal = (int) 'A';

		for (int j = 0; j < SIZE; j++) {
			System.out.print(c + " ");
			for (int i = 0; i < SIZE; i++) {
				if (b[j][i] == 2)
					System.out.print("O ");
				else if (b[j][i] == 1)
					System.out.print("X ");
				else
					System.out.print("- ");
			}
			System.out.println();
			cVal++;
			c = (char) cVal;
		}
	}

	static void Setup() { // Initialize Board to Zeros
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++) {
				b[j][i] = 0;
			}
	}

	static void GetMove() {
		int i, j;
		Scanner scanner = new Scanner(System.in);
		do {
			i = -1;
			j = -1;
			System.out.print("Enter your move: ");
			String line = scanner.nextLine().toUpperCase();

			// Check if the input is a valid column letter
			char keyPressed = line.charAt(0);
			if (keyPressed >= 'A' && keyPressed <= 'A' + SIZE - 1) {
				j = (int) keyPressed - (int) 'A';
			}

			// Convert the string to an integer (row number)
			try {
				i = Integer.parseInt(line.substring(1));
				i--; // Convert to 0-based index
			} catch (NumberFormatException e) {
				// Invalid input
			}

			// Check if coordinates input are in range and the cell is empty.
			if (j > -1 && j < SIZE && i > -1 && i < SIZE && b[j][i] == 0) {
				break; // Valid move
			} else {
				System.out.println("Illegal Move! Please enter a valid move in the format 'A1', 'B3', etc.");
			}
		} while (true);

		b[j][i] = 2; // Adjusted i to be 0-indexed
	}

	static int Evaluate() {
		int CompCount;
		int PlayerCount;
		int blanks = 0;
		int sum = 0;
		for (int j = 0; j < SIZE; j++) {
			for (int i = 0; i < SIZE; i++) {

				// Checks Board Horizontally For Patterns
				PlayerCount = 0; // Number of O's
				CompCount = 0; // Number of X's
				blanks = 0; // Number of Blanks

				// Checks a chunk of the board
				for (int x = i; x < CONNECT + i && x < SIZE; x++) {
					if (b[j][x] == 2)
						PlayerCount++;

					if (b[j][x] == 1)
						CompCount++;

					if (b[j][x] == 0)
						blanks++;
				}

				if (PlayerCount > CompCount) // Tends to make defending a higher priority
					sum += (int) Math.pow(10, PlayerCount + CompCount);
				else if ((CompCount + blanks) == CONNECT) // Looks for optimal positions
					sum += (int) Math.pow(10, CompCount);
				else if ((PlayerCount + blanks) == CONNECT) // Looks for disadvantageous positions
					sum -= ((int) Math.pow(10, PlayerCount) + 10 * PlayerCount);

				PlayerCount = 0;
				CompCount = 0;
				blanks = 0;
				for (int y = i; y < CONNECT + i && y < SIZE; y++) {
					if (b[y][j] == 2)
						PlayerCount++;

					if (b[y][j] == 1)
						CompCount++;

					if (b[y][j] == 0)
						blanks++;
				}
				if (PlayerCount > CompCount)
					sum += (int) Math.pow(10, PlayerCount + CompCount);
				else if ((CompCount + blanks) == CONNECT)
					sum += (int) Math.pow(10, CompCount);
				else if ((PlayerCount + blanks) == CONNECT)
					sum -= ((int) Math.pow(10, PlayerCount) + (10 * PlayerCount));
			}
		}
		return sum;
	}

	static void Makemove() { // AI
		int best = Integer.MIN_VALUE;
		int depth = 1; // Start with a depth of 1
		int score, mi = 0, mj = 0;

		// Perform iterative deepening until we reach the maximum depth or run out of
		while (depth <= maxdepth && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) < maxTime) {
			for (int j = 0; j < SIZE; j++) {
				for (int i = 0; i < SIZE; i++) {
					if (b[j][i] == 0) {
						b[j][i] = 1; // Make move
						score = Min(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
						if (score > best) {
							mi = i;
							mj = j;
							best = score;
						}
						b[j][i] = 0;
					}
				}
			}
			depth++;
		}

		int cVal = 'A' + mj;
		System.out.println("My move is " + (char) cVal + " " + (mi + 1));
		b[mj][mi] = 1;
	}

	static int Min(int depth, int alpha, int beta) {
		int v = Integer.MAX_VALUE;
		int result = Check4winner();
		if (result != 0) // Check for Terminal
			return result;
		if (depth == 0 || TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) >= maxTime)
			return Evaluate();
		for (int j = 0; j < SIZE; j++) {
			for (int i = 0; i < SIZE; i++) {
				if (b[j][i] == 0) {
					b[j][i] = 2; // Make move on board
					v = Math.min(v, Max(depth - 1, alpha, beta));
					if (v <= alpha) {
						b[j][i] = 0; // Undo move
						return v;
					}
					beta = Math.min(beta, v);
					b[j][i] = 0; // Undo move
				}
			}
		}
		return v;
	}

	static int Max(int depth, int alpha, int beta) {
		int v = Integer.MIN_VALUE;
		int result = Check4winner();
		if (result != 0)
			return result;
		if (depth == 0 || TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) >= maxTime)
			return Evaluate();
		for (int j = 0; j < SIZE; j++) {
			for (int i = 0; i < SIZE; i++) {
				if (b[j][i] == 0) {
					b[j][i] = 1; // Make move on board
					v = Math.max(v, Min(depth - 1, alpha, beta));
					if (v >= beta) {
						b[j][i] = 0; // Undo move
						return v;
					}
					alpha = Math.max(alpha, v);
					b[j][i] = 0;
				}
			}
		}
		return v;
	}

	static int Check4winner() {
		int emptySpacesCount = 0;
		for (int j = 0; j < SIZE; j++) {
			int XcomputerCount = 0;
			int XplayerCount = 0;

			int YcomputerCount = 0;
			int YplayerCount = 0;
			for (int i = 0; i < SIZE; i++) {
				// Check Horizontal
				if (b[j][i] == 1)
					XcomputerCount++;
				else
					XcomputerCount = 0;

				if (b[j][i] == 2)
					XplayerCount++;
				else
					XplayerCount = 0;

				if (XcomputerCount == CONNECT)
					return Integer.MAX_VALUE; // Computer Wins
				if (XplayerCount == CONNECT)
					return Integer.MIN_VALUE; // Player Wins

				// Check Vertical
				if (b[i][j] == 1)
					YcomputerCount++;
				else
					YcomputerCount = 0;

				if (b[i][j] == 2)
					YplayerCount++;
				else
					YplayerCount = 0;

				if (YcomputerCount == CONNECT)
					return Integer.MAX_VALUE; // Computer Wins
				if (YplayerCount == CONNECT)
					return Integer.MIN_VALUE; // Player Wins

				if (b[i][j] == 0)
					emptySpacesCount++;
			}
		}
		if (emptySpacesCount == 0)
			return 1; // Draw
		return 0;
	}

	static boolean CheckGameOver() {
		Printboard();
		String s = "";
		boolean gameOver = false;
		int winner = Check4winner();
		if (winner != 0) {
			gameOver = true;
			switch (winner) {
			case Integer.MIN_VALUE:
				s = "You win";
				break;
			case Integer.MAX_VALUE:
				s = "Opponent win";
				break;
			case 1:
				s = "Draw";
				break;
			}
			System.out.println(s);
		}
		return gameOver;
	}
}
