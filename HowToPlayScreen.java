import java.awt.*;
import javax.swing.*;

public class HowToPlayScreen {

    // Show the instructions window when player clicks "How to Play"
    public static void showInstructions() {
        // Create a new window for the instructions
        JFrame helpFrame = new JFrame("How to Play");
        helpFrame.setSize(480, 320); 
        helpFrame.setLocationRelativeTo(null); 
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window, not whole program

        // Main panel that holds everything
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 255));  
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Stack things top to bottom
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Add padding around edges

        // Create the title with brain emoji
        JLabel title = new JLabel("How to Play");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        title.setAlignmentX(Component.CENTER_ALIGNMENT); 
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Add space below title
        panel.add(title);

        // Create the main instruction text
        JTextArea instructions = new JTextArea(
            "Goal:\nMemorize the correct cup color order!\n\n" +
            "Steps:\n" +
            "1. Watch carefully as the cups shuffle.\n" +
            "2. Click the correct order of cups.\n" +
            "3. If correct, you win! Otherwise, try again.\n\n" +
            "Tip:\nFocus closely and don't blink!"
        );
        
        // Set up how the text area looks and behaves
        instructions.setEditable(false);// Player can't type in it
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        instructions.setBackground(new Color(245, 245, 255));
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(instructions);  // Add the instructions to the panel

        // Create the Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 16)); 
        closeButton.setFocusPainted(false);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setMaximumSize(new Dimension(120, 35)); 
        closeButton.addActionListener(e -> helpFrame.dispose());

        // Add some space before the button, then add the button
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(closeButton);

        // Put the panel in the window and show it
        helpFrame.setContentPane(panel);
        helpFrame.setVisible(true);
    }
}