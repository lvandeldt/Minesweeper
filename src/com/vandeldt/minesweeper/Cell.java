package com.vandeldt.minesweeper;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Cell extends JButton {

    // Cell constants
    static final int CELL_WIDTH = 26;
    static final int CELL_HEIGHT = 26;
    static final Color DEF_BG_COL = Color.LIGHT_GRAY;
    static final Color DEF_BD_COL = Color.GRAY;
    static final Color TRIPPED_MINE_COL = Color.RED;
    static final Color PROXY_TRIPPED_MINE_COL = Color.ORANGE;
    static final Color REVEALED_CELL_COL = Color.WHITE;
    static final Color DEF_FG_COL = Color.BLACK;
    static final Color[] ADJ_TEXT_COL = {
            Color.WHITE, // 0
            Color.BLUE, // 1
            new Color(0, 100, 0), // 2
            Color.RED, // 3
            new Color(139, 0, 139), // 4
            new Color(128,0,0), // 5
            new Color(48, 213, 200), // 6
            Color.BLACK, // 7
            Color.GRAY, // 8
    };

    // Icons (Mostly by Sirea, http://www.rw-designer.com/user/5920)

    static final ImageIcon ICON_FLAG = scaledIcon("images/flag.png"); // Flag
    static final ImageIcon ICON_QUESTION = scaledIcon("images/question.png"); // Possible flag
    static final ImageIcon ICON_MINE = scaledIcon("images/mine.png"); // Mine Icon
    static final ImageIcon ICON_BOOM = scaledIcon("images/explosion.png"); // Triggered Mine Icon
    static final ImageIcon ICON_NOPE = scaledIcon("images/no-bomb.png"); // Wrong Flag Icon

    // Initialise cell attributes
    private List<Cell> neighbours = new ArrayList<>();
    private State state = State.DEFAULT;
    private boolean is_mine = false;

    // Constructor
    public Cell() {

        super();

        // Set up default appearance
        this.setBackground( DEF_BG_COL );
        this.setForeground( DEF_FG_COL );
        this.setBorder(new LineBorder(DEF_BD_COL));
        this.setPreferredSize(new Dimension(CELL_WIDTH, CELL_HEIGHT));
        this.setHorizontalAlignment(CENTER);
        this.setVerticalAlignment(CENTER);

        this.setContentAreaFilled(true);

    }

    // Cell right click behaviour (Flagging)
    public void nextFlagState() {
        switch (this.state) {
            case DEFAULT -> {
                this.state = State.FLAGGED;

                // Change cell to show flagged
                this.setIcon(ICON_FLAG);

                // Remove flag from remaining pool
                Board.flags_remaining--;
            }
            case FLAGGED -> {
                this.state = State.POSSIBLE;

                // Change cell to show possible
                this.setIcon(ICON_QUESTION);

                // Add flag back to remaining pool.
                Board.flags_remaining++;
            }
            case POSSIBLE -> {
                this.state = State.DEFAULT;

                // Remove flag indicators.
                this.setIcon(null);
            }
        }
    }

    // Method to check whether we can chord this cell
    public ChordResult getChordStatus() {
        int flagged_neighbours = 0;
        boolean mine_found = false;
        Cell mine_location = new Cell();

        // Loop over each neighbour to count flags and see if there are any unflagged mines.
        for (Cell neighbour: neighbours) {
            if (neighbour.getState() == State.FLAGGED) {
                flagged_neighbours++;
            } else if (neighbour.is_mine) {
                mine_found = true;
                mine_location = neighbour;
            }
        }

        // If there are enough flagged neighbours...
        if (flagged_neighbours == this.getAdjacentMines()) {
            // ... check we didn't find any unflagged mines.
            if (mine_found) {
                mine_location.setBackground(TRIPPED_MINE_COL);
                return ChordResult.NEIGHBOUR_IS_MINE;
            } else {
                // If no mines we can chord.
                return ChordResult.CHORDABLE;
            }
        } else {
            return ChordResult.NOT_ENOUGH_FLAGS;
        }

    }

    // Method to get the number of mines adjacent to this cell
    private int getAdjacentMines() {
        int adjacent = 0;

        // Loop over each neighbour to count mines
        for (Cell neighbour: neighbours) {
            if (neighbour.is_mine) {
                adjacent++;
            }
        }

        return adjacent;
    }

    // Method to reveal cell
    public void reveal() {
        // Check that cell isn't already revealed or flagged first (makes chord function easier)
        if (this.state == State.DEFAULT) {

            // If for some reason we try to reveal a mine, throw an error.
            if (this.is_mine) {
                throw new Error("Cannot reveal a mined cell!");
            }

            this.state = State.REVEALED;

            // Change appearance to show revealed.
            this.setBackground(REVEALED_CELL_COL);

            Board.num_revealed++;

            // Count adjacent mines and update cell appearance
            int adjacent_mines = this.getAdjacentMines();
            if (adjacent_mines > 0) {

                this.setText(Integer.toString(adjacent_mines));
                this.setForeground(ADJ_TEXT_COL[adjacent_mines]);

            } else {

                // Reveal adjacent cells if no mines adjacent
                this.chord();

            }
        }

    }

    // Reveal cells adjacent to this one
    public void chord() {
        // Loop over neighbours and reveal.
        for (Cell neighbour: neighbours) {
            neighbour.reveal();
        }
    }

    // Getters and setters


    // Add neighbour to neighbours list:
    public void addNeighbour(Cell new_neighbour) {
        this.neighbours.add( new_neighbour );
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;

        // If setting back to default, change appearance back.
        if (this.state == State.DEFAULT) {
            this.setBackground(DEF_BG_COL);
            this.setForeground(DEF_FG_COL);
            this.setText("");
            this.setIcon(null);
        }
    }

    public boolean isMine() {
        return is_mine;
    }

    public void setMine(boolean mine) {
        is_mine = mine;
    }

    private static ImageIcon scaledIcon(String path) {
        ImageIcon unscaled = new ImageIcon(Cell.class.getResource( path ));
        Image scaledImage = unscaled.getImage().getScaledInstance(CELL_WIDTH - 2, CELL_HEIGHT - 2, 0);

        return new ImageIcon(scaledImage);
    }
}
