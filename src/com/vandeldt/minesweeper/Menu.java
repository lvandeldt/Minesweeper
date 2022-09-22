package com.vandeldt.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import static java.awt.GridBagConstraints.BOTH;

public class Menu extends JFrame {

    private final JTextField txt_height, txt_width, txt_mines;
    private final Menu instance;

    // Set difficulties: Arrays are {Height, Width, Mines}.
    private static final Map<String, Integer[]> DIFFICULTIES = new HashMap<>() {{
        put("E", new Integer[]{8, 8, 10});
        put("M", new Integer[]{16, 16, 40});
        put("H", new Integer[]{16, 30, 99});
    }};

    public Menu() {

        super("Minesweeper");
        this.setIconImage( Cell.ICON_MINE.getImage() );
        this.setSize(new Dimension(500, 500));

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        this.setResizable(false);

        instance = this;

        // Create panel to contain options
        JPanel pnlOptions = new JPanel();
        pnlOptions.setLayout(new GridLayout(4, 1));
        pnlOptions.setPreferredSize(new Dimension(100, 100));

        // Create action listener for radio buttons
        ActionListener difficultyChanged = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chosen_difficulty = e.getActionCommand();

                if (Objects.equals(chosen_difficulty, "C")) {
                    txt_height.setEnabled(true);
                    txt_width.setEnabled(true);
                    txt_mines.setEnabled(true);
                } else {
                    Integer[] settings = DIFFICULTIES.get(chosen_difficulty);

                    txt_height.setEnabled(false);
                    txt_width.setEnabled(false);
                    txt_mines.setEnabled(false);

                    txt_height.setText(Integer.toString(settings[0]));
                    txt_width.setText(Integer.toString(settings[1]));
                    txt_mines.setText(Integer.toString(settings[2]));

                }
            }
        };

        // Create radio button for each option.
        JRadioButton btn_easy = new JRadioButton("Easy");
        btn_easy.setActionCommand("E");
        btn_easy.addActionListener(difficultyChanged);

        JRadioButton btn_med = new JRadioButton("Medium");
        btn_med.setActionCommand("M");
        btn_med.addActionListener(difficultyChanged);
        btn_med.setSelected(true);

        JRadioButton btn_hard = new JRadioButton("Hard");
        btn_hard.setActionCommand("H");
        btn_hard.addActionListener(difficultyChanged);

        JRadioButton btn_cust = new JRadioButton("Custom");
        btn_cust.setActionCommand("C");
        btn_cust.addActionListener(difficultyChanged);

        // Add the buttons to a group.
        ButtonGroup grp_options = new ButtonGroup();
        grp_options.add(btn_easy);
        grp_options.add(btn_med);
        grp_options.add(btn_hard);
        grp_options.add(btn_cust);

        // Add each button to the panel
        pnlOptions.add(btn_easy);
        pnlOptions.add(btn_med);
        pnlOptions.add(btn_hard);
        pnlOptions.add(btn_cust);

        // Add panel to frame
        constraints.gridx = 1;
        constraints.gridy = 1;
        this.add(pnlOptions, constraints);

        // Second panel with text fields
        JPanel pnlConfig = new JPanel();
        pnlConfig.setLayout(new GridBagLayout());
        pnlConfig.setPreferredSize(new Dimension(200, 100));

        // Add text fields to panel

        // Height
        constraints.weightx = 0.5;
        constraints.gridx = 1;
        constraints.gridy = 1;
        pnlConfig.add(new JLabel("Height: "), constraints);

        constraints.gridx = 2;
        txt_height = new JTextField( Integer.toString( DIFFICULTIES.get("M")[0] ) );
        txt_height.setPreferredSize(new Dimension(50, 25));
        txt_height.setEnabled(false);
        pnlConfig.add(txt_height, constraints);

        // Width
        constraints.gridx = 1;
        constraints.gridy = 2;
        pnlConfig.add(new JLabel("Width: "), constraints);

        constraints.gridx = 2;
        txt_width = new JTextField(Integer.toString( DIFFICULTIES.get("M")[1] ) );
        txt_width.setPreferredSize(new Dimension(50, 25));
        txt_width.setEnabled(false);
        pnlConfig.add(txt_width, constraints);

        // Mines
        constraints.gridx = 1;
        constraints.gridy = 3;
        pnlConfig.add(new JLabel("Mines: "), constraints);

        constraints.gridx = 2;
        txt_mines = new JTextField(Integer.toString( DIFFICULTIES.get("M")[2] ) );
        txt_mines.setPreferredSize(new Dimension(50, 25));
        txt_mines.setEnabled(false);
        pnlConfig.add(txt_mines, constraints);

        // Add second panel to frame
        constraints.gridx = 2;
        constraints.gridy = 1;
        this.add(pnlConfig, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.fill = BOTH;
        JButton btn_confirm = new JButton("Confirm Difficulty");
        this.add(btn_confirm, constraints);

        btn_confirm.addActionListener(e -> {

            // Try statement to make sure there are no non-numeric inputs.
            try {
                int height = Integer.parseInt(txt_height.getText());
                int width = Integer.parseInt(txt_width.getText());
                int mines = Integer.parseInt(txt_mines.getText());

                if (height <= 0 || width <= 0) { // Check height and width are +ve
                    JOptionPane.showMessageDialog(instance, "Height and width must be positive integers.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (height * width == 1) { // Check there is more than 1 space on the grid.
                    JOptionPane.showMessageDialog(instance, "Grid must be larger than 1x1.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (mines <= 0 || mines >= height * width) { // Check valid num mines.
                        JOptionPane.showMessageDialog(instance, "Invalid number of mines (min: 1, max: " +
                                (height * width - 1) + ").", "Error", JOptionPane.ERROR_MESSAGE);
                } else { // If all is well, start game.
                    new Minesweeper(height, width, mines);
                    instance.dispose();
                }
            } catch(Exception execpt) {
                JOptionPane.showMessageDialog(instance, "Only numerical values are allowed for height, " +
                        "width and mines.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

    }

    public static void main(String[] args) {
        new Menu();
    }

}
