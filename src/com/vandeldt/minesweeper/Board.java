package com.vandeldt.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

public class Board extends JPanel {

    // Initialise board attributes.
    public static int num_revealed = 0;
    public static int flags_remaining;
    private int board_height;
    private int board_width;
    private int num_mines;
    private boolean game_over = false;
    private Cell[][] cells;

    // Constructor
    public Board(int height, int width, int mines) {

        // Set attributes
        this.board_height = height;
        this.board_width = width;
        this.num_mines = mines;

        this.cells = new Cell[board_height][board_width];

        flags_remaining = mines;

        // Set layout of panel so cells display properly.
        this.setLayout(new GridLayout(board_height, board_width));

        // Nested loop to create and add each cell to the panel. (row i, col j)
        for (int i = 0; i < board_height; i++) {
            for (int j = 0; j < board_width; j++) {

                // Create new cell in array
                this.cells[i][j] = new Cell();

                // Add listener so we can click cells.
                this.cells[i][j].addMouseListener(new MouseListener() {
                    // We only need mousePressed.
                    public void mousePressed(MouseEvent e) {
                        // Find out which cell has been clicked and by which mouse button.
                        Cell trigger = (Cell) e.getSource();
                        int mouse_button = e.getButton();

                        // Update board.
                        update(trigger, mouse_button);
                    }

                    // Declare other functions to satisfy interface.
                    public void mouseClicked(MouseEvent e) {}
                    public void mouseReleased(MouseEvent e) {}
                    public void mouseEntered(MouseEvent e) {}
                    public void mouseExited(MouseEvent e) {}
                });

                this.add(cells[i][j]);
            }
        }

        // Once all cells are set up, go back and link all neighbours to eachother.
        for(int i = 0; i < board_height; i++) {
            for(int j = 0; j < board_width; j++) {
                for(int d_row = -1; d_row <= 1; d_row++) {
                    for(int d_col = -1; d_col <= 1; d_col++) {
                        // Check we're not adding this cell and that we're adding a valid index.
                        if ((d_row != 0 || d_col != 0) && (i + d_row >= 0 && j + d_col >= 0 &&
                                i + d_row < board_height && j + d_col < board_width)) {
                            this.cells[i][j].addNeighbour(this.cells[i + d_row][j + d_col]);
                        }
                    }
                }
            }
        }

        // Place mines so that the game can begin.
        armMines(num_mines);

    }

    // Board update method.
    public void update(Cell trigger, int button) {

        // Make sure game hasn't ended yet.
        if (!game_over) {

            if (button == 1) { // Left click

                switch (trigger.getState()) {

                    case DEFAULT -> { // If cell unrevealed...

                        if (trigger.isMine() && num_revealed == 0) { // ... move mine if we hit it first go.
                            trigger.setMine(false);
                            armMines(1);
                            trigger.reveal();
                        } else if (trigger.isMine()) { // ... trip mine if not first go.
                            trigger.setBackground(Cell.TRIPPED_MINE_COL);
                            endGame();
                            return;
                        } else { // ... reveal if not a mine.
                            trigger.reveal();
                        }

                    }
                    case REVEALED -> { // If cell is revealed...
                        // Check if we can chord the cell
                        ChordResult result = trigger.getChordStatus();
                        if (result == ChordResult.CHORDABLE) {
                            trigger.chord();
                        } else if (result == ChordResult.NEIGHBOUR_IS_MINE) {
                            trigger.setBackground(Cell.PROXY_TRIPPED_MINE_COL);
                            endGame();
                        }
                    }

                }

            } else if (button == 3) { // Right click
                trigger.nextFlagState();
            }

            updateParent();

            // Check win.
            if (num_revealed == (board_height * board_width) - num_mines) {
                // TODO: Game win logic.
                System.out.println("Win");
                game_over = true;
            }

        }

    }

    // Method to update labels on parent
    public void updateParent() {
        // Update labels on parent window
        Container parent = this.getParent();
        JLabel lbl_remaining_flags = (JLabel) parent.getComponentAt(1, 1);
        JLabel lbl_score = (JLabel) parent.getComponentAt(parent.getWidth() - 1, 1);

        lbl_remaining_flags.setText( Integer.toString(flags_remaining) );
        lbl_score.setText( Integer.toString(num_revealed) );
    }

    // Method to place mines. Take in mines to place as argument so that we can move just 1 mine if we need to.
    public void armMines(int mines_to_place) {
        Random rnd = new Random();

        while(mines_to_place > 0) {
            // Generate random board position.
            int i = rnd.nextInt(board_height);
            int j = rnd.nextInt(board_width);

            // Make sure that the random position is not already a mine, and that the cell isn't revealed in case we
            // are moving a mine.
            if (!this.cells[i][j].isMine() && this.cells[i][j].getState() != State.REVEALED) {
                this.cells[i][j].setMine(true);
                mines_to_place--;
            }
        }

    }

    // Method to reset the board
    public void reset() {
        // Loop over each cell and reset.
        for (Cell[] row : cells) {
            for (Cell cell: row) {
                cell.setMine(false);
                cell.setState(State.DEFAULT);
            }
        }

        // Place mines in new places.
        armMines(num_mines);

        // Reset attributes.
        game_over = false;
        num_revealed = 0;
        flags_remaining = num_mines;

        // Finally update parent window.
        updateParent();
    }

    // Method to execute everything needed to end the game.
    public void endGame() {
        // Set game over so board is no longer intractable.
        game_over = true;

        // Reveal all unflagged mine locations and highlight mistakes.
        for (int i = 0; i < board_height; i++) {
            for (int j = 0; j < board_width; j++) {
                if (cells[i][j].getState() != State.FLAGGED && cells[i][j].isMine()) {
                    // Show mine
                    if (cells[i][j].getBackground() != Color.RED) { // Don't affect triggered cell.
                        cells[i][j].setIcon(Cell.ICON_MINE);
                    } else {
                        cells[i][j].setIcon(Cell.ICON_BOOM);
                    }
                } else if (cells[i][j].getState() == State.FLAGGED && !cells[i][j].isMine()) {
                    cells[i][j].setIcon(Cell.ICON_NOPE);
                }
            }
        }
    }
}
