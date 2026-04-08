import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class NumberGuessingGame {

    private String difficultyString;
    private final String[] difficulties;
    private boolean game_state;
    private int chances;
    private int attempts;
    private final int number_guess;

    public NumberGuessingGame() {
        difficulties = new String[3];
        difficulties[0] = "Easy";
        difficulties[1] = "Medium";
        difficulties[2] = "Hard";
        game_state = false;
        number_guess = (int) (Math.random()*100) + 1;
    }

    public void setGame_state(boolean game_state) {
        this.game_state = game_state;
    }

    public void setDifficultyString(int difficulty) {
        this.difficultyString = difficulties[difficulty - 1];
    }

    public void setChances(int chances) {
        this.chances = chances;
    }

    public String getDifficultyString() {
        return difficultyString;
    }

    public int getChances() {
        return chances;
    }

    private static void welcomeMessage() {
        System.out.println("Welcome to the Number Guessing Game!" +
                "\nI'm thinking of a number between 1 and 100." +
                "\nYou have 5 chances to guess the correct number.");
    }

    public void difficultyMessage() {
        int max_chances = 10;
        System.out.println("\nPlease select the difficulty level: ");
        Scanner sc = new Scanner(System.in);
        printDifficulties();
        int option = Integer.parseInt(sc.nextLine());
        setDifficultyString(option);
        setChances(max_chances/option);

        System.out.println("\nGreat! You have selected the " + getDifficultyString() +
                " difficulty level.");
        System.out.println("You have " + getChances() + " chances.");
        System.out.println("Let's start the game!\n");
    }

    private void printDifficulties() {
        for (int i = 0; i < difficulties.length; i++) {
            System.out.println(i+1 + ". " + difficulties[i] + " (" + 10/(i+1) + " chances)");
        }
        System.out.println();
        System.out.print("Enter your choice: ");
    }

    public void initGame() {
        while (chances > 0) {
            System.out.print("Enter your choice: ");
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            if(verifyAnswer(choice)){
                break;
            }
        }
    }

    private boolean verifyAnswer(int choice) {
        attempts++;
        if(choice == number_guess) {
            System.out.println("Congratulations! You guessed the correct number in " + attempts + " attempts.");
            return true;
        } else {
            chances--;
            if (choice > number_guess) {
                System.out.println("Incorrect! The number is less than " + choice);
            } else {
                System.out.println("Incorrect! The number is greater than " + choice);
            }

            return false;
        }
    }

    public static void main(String[] args) {
        NumberGuessingGame game = new NumberGuessingGame();
        System.out.println();
        welcomeMessage();
        game.difficultyMessage();
        game.initGame();
    }
}