import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean playAgain = true;

        System.out.println("=== Welcome to the Number Guessing Game! ===");

        while (playAgain) {
            playGame(scanner);

            // Replay Option
            System.out.print("\nPlay again? (yes/no): ");
            String response = scanner.next().trim().toLowerCase();
            playAgain = response.equals("yes") || response.equals("y");
        }

        System.out.println("Thank you for playing! Goodbye.");
        scanner.close();
    }

    private static void playGame(Scanner scanner) {
        Random random = new Random();
        int minRange = 1;
        int maxRange = 100;
        int secretNumber = random.nextInt((maxRange - minRange) + 1) + minRange;
        int maxAttempts = 10;
        int attemptsUsed = 0;
        boolean hasWon = false;

        System.out.println("\nGuess a number between " + minRange + " and " + maxRange + ".");
        System.out.println("You have " + maxAttempts + " attempts.");

        while (attemptsUsed < maxAttempts) {
            attemptsUsed++;
            int remainingAttempts = maxAttempts - attemptsUsed;

            // 1. Get and validate guess
            int guess = getValidGuess(scanner, attemptsUsed, maxAttempts, minRange, maxRange);

            // 2. Check the guess
            if (guess == secretNumber) {
                System.out.println("Correct! You guessed it in " + attemptsUsed + " attempts.");
                hasWon = true;
                break;
            } else if (guess > secretNumber) {
                System.out.print("Too High! ");
            } else {
                System.out.print("Too Low! ");
            }

            // 3. Display remaining attempts if game isn't over
            if (remainingAttempts > 0) {
                System.out.println(remainingAttempts + " attempts remaining.\n");
            }
        }

        // 4. Handle lose condition
        if (!hasWon) {
            System.out.println("\nGame Over! You've run out of attempts.");
            System.out.println("The secret number was: " + secretNumber);
        }
    }

    // Method to handle input validation (non-integers and out-of-bounds numbers)
    private static int getValidGuess(Scanner scanner, int currentAttempt, int maxAttempts, int min, int max) {
        while (true) {
            System.out.print("Attempt " + currentAttempt + "/" + maxAttempts + " — Enter your guess: ");

            // Check if input is a valid integer
            if (!scanner.hasNextInt()) {
                System.out.println("Error: Invalid input. Letters or symbols are not accepted.");
                scanner.next(); // Clear the invalid input buffer
                continue;
            }

            int guess = scanner.nextInt();

            // Check if input is within the defined range
            if (guess < min || guess > max) {
                System.out.println("Warning: Your guess must be between " + min + " and " + max + ".");
                continue;
            }

            return guess;
        }
    }
}