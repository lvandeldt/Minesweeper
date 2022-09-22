package com.vandeldt.minesweeper;

import javax.swing.*;
import java.awt.*;

public class Minesweeper extends JFrame {

    // Interface components
    public JLabel lbl_remaining_flags;
    public JLabel lbl_score;

    private final Board board;

    // Constructor
    public Minesweeper(int height, int width, int mines) {

        // Create JFrame and set up window
        super("Minesweeper");
        this.setIconImage( Cell.ICON_MINE.getImage() );
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        this.setResizable(false);

        board = new Board(height, width, mines);

        // Set up UI.

        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = 1;

        // Remaining flags label
        lbl_remaining_flags = new JLabel(Integer.toString(Board.flags_remaining));
        lbl_remaining_flags.setHorizontalAlignment(0);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0.45;
        add(lbl_remaining_flags, constraints);

        // Score label
        lbl_score = new JLabel(Integer.toString(Board.num_revealed));
        lbl_score.setHorizontalAlignment(0);
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.weightx = 0.45;
        add(lbl_score, constraints);

        // Reset button
        JButton btn_reset = new JButton("Reset");
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.weightx = 0.1;

        btn_reset.addActionListener(e -> board.reset());

        add(btn_reset, constraints);

        // Add board to window
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        add(board, constraints);

        // Show window
        this.pack();
        this.setVisible(true);
    }
}
