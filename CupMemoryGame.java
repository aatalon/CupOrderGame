import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class CupMemoryGame extends JFrame {

    // All the cup colors we can use in the game
    private final String[] colors = {"Red", "Blue", "Purple", "Orange", "Pink", "Green"};
    
    // The correct order of cups the player needs to guess
    private List<String> correctOrder;
    
    // What the player has guessed so far
    private List<String> playerGuess = new ArrayList<>();
    
    // Labels to show game info to the player
    private JLabel attemptsLabel;
    private JLabel correctLabel;
    private JLabel timerLabel;
    private JLabel highScoreLabel;
    
    // Keep track of the best time score
    private int bestTime = Integer.MAX_VALUE;
    
    // Count how many times the player has tried
    private int attempts = 0;
    
    // Store cup pictures for each color
    private final Map<String, ImageIcon> cupIcons = new HashMap<>();
    
    // Different areas of the game window
    private JPanel guessPanel;      // Shows player's guess
    private JPanel correctPanel;    // Shows the shuffled cups
    private JPanel cupButtonsPanel; // Shows buttons to click
    private JPanel mainCenterPanel;
    
    // Timer to count seconds
    private Timer timer;
    private int elapsedTime = 0;
    
    // Whether the player can click buttons or not
    private boolean allowInput = false;
    
    // All the cup buttons the player can click
    private final List<JButton> cupButtons = new ArrayList<>();
    
    // The mixed up order of cups
    private List<String> shuffledOrder;

    // File to save the best score
    private final File highScoreFile = new File("highscore.txt");

    // Start the game when we create it
    public CupMemoryGame() {
        loadHighScore(); // Get the saved best score
        initializeGame(); // Set up the game window
        initComponents(); // Create all the buttons and labels
        startTimer(); // Start counting time
        showCorrectOrderTemporarily(); // Show the right order briefly
        setVisible(true); // Make the window visible
    }

    // Read the best score from a file
    private void loadHighScore() {
        if (highScoreFile.exists()) {
            try (Scanner sc = new Scanner(highScoreFile)) {
                if (sc.hasNextInt()) {
                    bestTime = sc.nextInt();
                }
            } catch (IOException e) {
                System.out.println("Error loading high score.");
            }
        }
    }

    // Save the best score to a file
    private void saveHighScore() {
        try (PrintWriter pw = new PrintWriter(highScoreFile)) {
            pw.println(bestTime);
        } catch (IOException e) {
            System.out.println("Error saving high score.");
        }
    }

    // Set up the main game window
    private void initializeGame() {
        setTitle("Cup Memory Game");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the window
        setLayout(new BorderLayout());
        generateCupOrder();           // Create a random order of cups
    }

    // Mix up the cups in a random order
    private void generateCupOrder() {
        List<String> cups = new ArrayList<>(Arrays.asList(colors));
        Collections.shuffle(cups);     // Mix them up randomly
        correctOrder = new ArrayList<>(cups);
        shuffledOrder = new ArrayList<>(correctOrder);
        System.out.println("Correct order (hidden): " + correctOrder);
    }

    // Make cup pictures the right size
    private ImageIcon scaleImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(60, 90, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // Load all the cup pictures from files
    private void loadCupIcons() {
        cupIcons.put("Red", scaleImage("src/images/red.png"));
        cupIcons.put("Blue", scaleImage("src/images/blue.png"));
        cupIcons.put("Purple", scaleImage("src/images/purple.png"));
        cupIcons.put("Orange", scaleImage("src/images/orange.png"));
        cupIcons.put("Pink", scaleImage("src/images/pink.png"));
        cupIcons.put("Green", scaleImage("src/images/green.png"));
    }

    // Create all the parts of the game window
    private void initComponents() {
        loadCupIcons();  // Load all cup pictures first

        // Create the top part with title and scores
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(250, 250, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Game title
        JLabel titleLabel = new JLabel("Cup Memory", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Timer display
        timerLabel = new JLabel("Time: 00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // How many cups are correct
        correctLabel = new JLabel("0 Correct", SwingConstants.CENTER);
        correctLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        correctLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // How many times the player tried
        attemptsLabel = new JLabel("Attempts: 0", SwingConstants.CENTER);
        attemptsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        attemptsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Best time so far
        highScoreLabel = new JLabel("High Score: " + formatTime(bestTime), SwingConstants.CENTER);
        highScoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add everything to the top panel
        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        topPanel.add(timerLabel);
        topPanel.add(correctLabel);
        topPanel.add(attemptsLabel);
        topPanel.add(highScoreLabel);
        add(topPanel, BorderLayout.NORTH);

        // Create the middle part with 3 sections
        mainCenterPanel = new JPanel(new GridLayout(3, 1));
        mainCenterPanel.setBackground(new Color(245, 245, 255));

        // Section 1: Shows the shuffled cups
        correctPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        correctPanel.setBackground(new Color(245, 245, 255));
        correctPanel.setBorder(BorderFactory.createTitledBorder("Shuffled Cups"));
        mainCenterPanel.add(correctPanel);

        // Section 2: Shows buttons to click
        cupButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        cupButtonsPanel.setBackground(new Color(245, 245, 255));
        mainCenterPanel.add(cupButtonsPanel);

        // Section 3: Shows what the player guessed
        guessPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        guessPanel.setBackground(new Color(255, 255, 240));
        guessPanel.setBorder(BorderFactory.createTitledBorder("Your Guess"));
        mainCenterPanel.add(guessPanel);

        add(mainCenterPanel, BorderLayout.CENTER);

        // Create the Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        submitButton.setBackground(new Color(60, 179, 113));
        submitButton.setForeground(Color.BLACK);
        submitButton.setFocusPainted(false);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        submitButton.setPreferredSize(new Dimension(120, 40));
        submitButton.addActionListener(new SubmitButtonListener());

        // Create the Undo button (removes last guess)
        JButton undoButton = new JButton("Undo");
        undoButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        undoButton.setBackground(new Color(100, 149, 237));
        undoButton.setForeground(Color.BLACK);
        undoButton.setFocusPainted(false);
        undoButton.setOpaque(true);
        undoButton.setBorderPainted(false);
        undoButton.setPreferredSize(new Dimension(120, 40));
        undoButton.addActionListener(e -> undoLastGuess());

        // Create the Reset button (starts over)
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resetButton.setBackground(new Color(255, 165, 0));
        resetButton.setForeground(Color.BLACK);
        resetButton.setFocusPainted(false);
        resetButton.setOpaque(true);
        resetButton.setBorderPainted(false);
        resetButton.setPreferredSize(new Dimension(120, 40));
        resetButton.addActionListener(e -> resetGame());

        // Create the Home button (goes back to main menu)
        JButton homeButton = new JButton("Home");
        homeButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        homeButton.setBackground(new Color(200, 100, 100));
        homeButton.setForeground(Color.BLACK);
        homeButton.setFocusPainted(false);
        homeButton.setOpaque(true);
        homeButton.setBorderPainted(false);
        homeButton.setPreferredSize(new Dimension(120, 40));
        homeButton.addActionListener(e -> {
            timer.stop(); // Stop counting time
            dispose();    // Close this window
            HomeScreen.show(() -> {
                new CupMemoryGame(); // Start a new game
            });
        });

        // Put all buttons at the bottom
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(new Color(245, 245, 255));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        controlPanel.add(undoButton);
        controlPanel.add(resetButton);
        controlPanel.add(submitButton);
        controlPanel.add(homeButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    // Turn seconds into minutes:seconds format
    private String formatTime(int timeInSeconds) {
        if (timeInSeconds == Integer.MAX_VALUE) return "--:--";
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Show the correct order for 3 seconds, then shuffle
    private void showCorrectOrderTemporarily() {
        // Show each cup in the right order
        for (String color : correctOrder) {
            JLabel cup = new JLabel(cupIcons.get(color));
            correctPanel.add(cup);
        }
        correctPanel.revalidate();
        correctPanel.repaint();

        // Wait 3 seconds, then start shuffling
        new Timer(3000, e -> {
            ((Timer) e.getSource()).stop();
            animateShuffle();
        }).start();
    }

    // Mix up the cups with animation
    private void animateShuffle() {
        Timer animationTimer = new Timer(100, null);
        final int[] count = {0};

        animationTimer.addActionListener(e -> {
            // Shuffle the cups around
            Collections.shuffle(shuffledOrder);
            correctPanel.removeAll();
            for (String color : shuffledOrder) {
                JLabel cup = new JLabel(cupIcons.get(color));
                correctPanel.add(cup);
            }
            correctPanel.revalidate();
            correctPanel.repaint();

            count[0]++;
            // After shuffling 5 times, stop and let player play
            if (count[0] >= 5) {
                animationTimer.stop();
                guessPanel.setBackground(Color.WHITE);
                displayCupButtons();  // Show buttons to click
                allowInput = true;    // Let player start clicking
            }
        });
        animationTimer.start();
    }

    // Create buttons for each cup color
    private void displayCupButtons() {
        cupButtonsPanel.removeAll();
        cupButtons.clear();

        // Make a button for each color
        for (String color : colors) {
            JButton cupButton = new JButton(cupIcons.get(color));
            cupButton.setActionCommand(color);
            cupButton.setBorderPainted(false);
            cupButton.setContentAreaFilled(false);
            cupButton.setFocusPainted(false);

            // Make smaller versions for animation
            ImageIcon originalIcon = cupIcons.get(color);
            Image originalImage = originalIcon.getImage();
            Image shrunkImage = originalImage.getScaledInstance(50, 75, Image.SCALE_SMOOTH);
            ImageIcon shrunkIcon = new ImageIcon(shrunkImage);

            // What happens when player clicks the button
            cupButton.addActionListener(evt -> {
                if (!allowInput) return;  // Don't do anything if input is disabled

                // Make button shrink then grow back (animation)
                Timer shrink = new Timer(0, null);
                Timer grow = new Timer(100, null);

                shrink.addActionListener(e -> {
                    cupButton.setIcon(shrunkIcon);
                    shrink.stop();
                    grow.start();
                });

                grow.addActionListener(e -> {
                    cupButton.setIcon(originalIcon);
                    grow.stop();
                });

                shrink.start();
                addCupToGuess(evt.getActionCommand(), cupButton);
            });

            cupButtonsPanel.add(cupButton);
            cupButtons.add(cupButton);
        }
        cupButtonsPanel.revalidate();
        cupButtonsPanel.repaint();
    }

    // Add a cup to the player's guess
    private void addCupToGuess(String color, JButton sourceButton) {
        // Only add if we haven't guessed all cups yet
        if (playerGuess.size() < colors.length) {
            playerGuess.add(color);
            JLabel cupLabel = new JLabel(cupIcons.get(color));
            cupLabel.setName(color);
            guessPanel.add(cupLabel);  // Show it in the guess area
            guessPanel.revalidate();
            guessPanel.repaint();
            // Highlight the button to show it was clicked
            sourceButton.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        }
    }

    // What happens when player clicks Submit button
    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Make sure player guessed all cups
            if (playerGuess.size() < colors.length) {
                JOptionPane.showMessageDialog(null, "Please select all cups before submitting.");
                return;
            }

            // Check if the guess is right
            boolean isCorrect = checkPlayerGuess(playerGuess);

            // Count this attempt
            attempts++;
            attemptsLabel.setText("Attempts: " + attempts);

            if (isCorrect) {
                // Player won!
                timer.stop();  // Stop the timer

                // Check if this is a new best time
                if (elapsedTime < bestTime) {
                    bestTime = elapsedTime;
                    saveHighScore();
                    highScoreLabel.setText("High Score: " + formatTime(bestTime));
                }

                showCongratulationScreen();  // Show win message
            } else {
                // Player was wrong, let them try again
                JOptionPane.showMessageDialog(null, "Wrong order! Try again.");
                playerGuess.clear();
                guessPanel.removeAll();
                guessPanel.revalidate();
                guessPanel.repaint();
                // Remove highlights from buttons
                for (JButton button : cupButtons) {
                    button.setBorder(UIManager.getBorder("Button.border"));
                }
            }
        }
    }

    // Check if the player's guess matches the correct order
    private boolean checkPlayerGuess(List<String> playerGuess) {
        int correctCount = 0;
        // Compare each position
        for (int i = 0; i < correctOrder.size(); i++) {
            if (correctOrder.get(i).equals(playerGuess.get(i))) {
                correctCount++;
            }
        }
        // Update the score display
        correctLabel.setText(correctCount + " Correct");
        // Return true if all are correct
        return correctCount == correctOrder.size();
    }

    // Start the game over from the beginning
    private void resetGame() {
        generateCupOrder(); // Make new random order
        correctLabel.setText("0 Correct");
        attempts = 0;
        attemptsLabel.setText("Attempts: 0");
        playerGuess.clear(); // Clear player's guess
        
        // Clear all the display areas
        guessPanel.removeAll();
        cupButtonsPanel.removeAll();
        correctPanel.removeAll();
        guessPanel.revalidate();
        guessPanel.repaint();
        cupButtonsPanel.revalidate();
        cupButtonsPanel.repaint();
        correctPanel.revalidate();
        correctPanel.repaint();
        
        // Reset the timer
        elapsedTime = 0;
        timerLabel.setText("Time: 00:00");
        timer.restart();
        allowInput = false; // Don't let player click yet
        showCorrectOrderTemporarily(); // Show the new order briefly
    }

    // Remove the last cup from the player's guess
    private void undoLastGuess() {
        if (!allowInput) return; // Don't do anything if input is disabled
        if (!playerGuess.isEmpty()) {
            // Remove from the guess list
            playerGuess.remove(playerGuess.size() - 1);
            
            // Remove from the display
            Component[] components = guessPanel.getComponents();
            if (components.length > 0) {
                guessPanel.remove(components[components.length - 1]);
                guessPanel.revalidate();
                guessPanel.repaint();
                
                // Remove highlights from all buttons
                for (JButton button : cupButtons) {
                    button.setBorder(UIManager.getBorder("Button.border"));
                }
            }
        }
    }

    // Show a celebration window when player wins
    private void showCongratulationScreen() {
        JFrame congratsFrame = new JFrame("Congratulations!");
        congratsFrame.setSize(400, 200);
        congratsFrame.setLocationRelativeTo(this);
        congratsFrame.setLayout(new BorderLayout());

        // Victory message with time and attempts
        JLabel message = new JLabel("<html><div style='text-align: center;'>🎉 Congratulations! You guessed correctly! 🎉<br>" +
            "Time: " + formatTime(elapsedTime) + "<br>" +
            "Attempts: " + attempts + "</div></html>", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.BOLD, 16));
        congratsFrame.add(message, BorderLayout.CENTER);

        // Flashing stars animation
        JLabel animation = new JLabel("✨✨✨", SwingConstants.CENTER);
        animation.setFont(new Font("Arial", Font.PLAIN, 24));
        congratsFrame.add(animation, BorderLayout.NORTH);

        // Make the stars flash on and off
        Timer flashTimer = new Timer(500, new ActionListener() {
            boolean visible = true;
            @Override
            public void actionPerformed(ActionEvent evt) {
                animation.setVisible(visible);
                visible = !visible;
            }
        });
        flashTimer.start();

        // Button to play again
        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setForeground(Color.BLACK);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setOpaque(true);
        playAgainButton.setBorderPainted(false);
        playAgainButton.addActionListener(e -> {
            congratsFrame.dispose();  // Close this window
            resetGame(); // Start a new game
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playAgainButton);
        congratsFrame.add(buttonPanel, BorderLayout.SOUTH);

        congratsFrame.setVisible(true);
    }

    // Start the timer that counts seconds
    private void startTimer() {
        timer = new Timer(1000, e -> {
            elapsedTime++;  // Add one second
            int minutes = elapsedTime / 60;
            int seconds = elapsedTime % 60;
            // Update the display
            timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        });
        timer.start();
    }

    // Start the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomeScreen.show(() -> {
                new CupMemoryGame();
            });
        });
    }
}