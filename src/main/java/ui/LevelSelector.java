package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import model.Board;
import model.Fox;
import model.Mushroom;
import model.Piece;
import model.Rabbit;
import model.Fox.FoxType;
import resources.Resources;
import view.GameView;

/**
 * This class represents the level selector for the game.
 * 
 * @author John Breton
 * @author Dani Hashweh
 */
public class LevelSelector extends JFrame implements ActionListener {
    
    private static final double BUTTON_Y_FACTOR = 0.20;
    private static final int BUTTON_X_FACTOR = 3;
    private static final int FONT_SIZE = 28;
    private static final double BOARD_DISPLAY_SIZE = 3.4;
    private static final double X_SCALE_FACTOR = 0.05;
    private static final int Y_SCALE_FACTOR = 17;
    
    private static final BevelBorder SELECTED = new BevelBorder(BevelBorder.RAISED, Color.RED, Color.RED);
    private static final BevelBorder DEFAULT = new BevelBorder(BevelBorder.RAISED, Color.BLACK, Color.BLACK);
    
    private JButton btnStartSelectLevel, btnMainMenu, btnCustomLevels, btnNextPage, btnLastPage, btnLeftLevel,
            btnMiddleLevel, btnRightLevel;
    private JTextPane levelLabelLeft, levelLabelMiddle, levelLabelRight;
    private JPanel pageButtons, actionButtons, allButtons, textPanel, boards, mainPanel;
    private JLabel[][] tiles1, tiles2, tiles3;
    
    private List<Board> allDefaultLevels, allCustomLevels;
    private int pageNumber, lastPage;
    private boolean custom;

    /**
     * Construct a new LevelSelector.
     */
    public LevelSelector() {
        // Initializing default values for a few variables.
        custom = false;
        pageNumber = 1;
        
        /*
         * Storing lists containing the stored levels in the JSON document.
         * Also determining what the last page is based on the number of levels
         * in the default level list.
         */
        allDefaultLevels = Resources.getAllDefaultBoards();
        allCustomLevels = Resources.getAllUserBoards();
        determineLastPageNumber(allDefaultLevels);

        // Set the layout and background of the level selector JFrame.
        this.setContentPane(new JLabel(Resources.LEVEL_SELECTOR_BACKGROUND));
        this.getContentPane().setLayout(new BorderLayout());
        
        // Create the JTextPanes to display level names
        setUpJTextArea(levelLabelLeft = new JTextPane());
        setUpJTextArea(levelLabelMiddle = new JTextPane());
        setUpJTextArea(levelLabelRight = new JTextPane());

        // Create the JButtons used for interacting with the level selector.
        // Also ensuring that the start and last page button are disabled initially.
        setUpMenuButton(btnStartSelectLevel = new JButton("Start"));
        setUpMenuButton(btnMainMenu = new JButton("Back to Main Menu"));
        setUpMenuButton(btnCustomLevels = new JButton("Go to Custom Levels"));
        setUpMenuButton(btnNextPage = new JButton("Next Page"));
        setUpMenuButton(btnLastPage = new JButton("Last Page"));
        btnStartSelectLevel.setEnabled(false);
        btnLastPage.setEnabled(false);

        // Create the "tiles" for the board preview.
        setUpTiles(tiles1 = new JLabel[5][5]);
        setUpTiles(tiles2 = new JLabel[5][5]);
        setUpTiles(tiles3 = new JLabel[5][5]);

        // Creating the buttons that will hold the board previews for the levels.
        setUpLevelDisplayButton(btnLeftLevel = new JButton(), 1);
        setUpLevelDisplayButton(btnMiddleLevel = new JButton(), 2);
        setUpLevelDisplayButton(btnRightLevel = new JButton(), 3);

        // Creating a JPanel to hold the buttons associated with moving up or down a page.
        // Also adding the associated buttons to the panel, along with glue.
        setUpJPanel(pageButtons = new JPanel(), true);
        pageButtons.add(Box.createHorizontalGlue());
        pageButtons.add(btnLastPage);
        pageButtons.add(Box.createHorizontalGlue());
        pageButtons.add(btnNextPage);
        pageButtons.add(Box.createHorizontalGlue());

        /* 
         * Creating a JPanel to hold the buttons associated with returning to
         * the main menu, starting a level, and going to the custom/default level
         * selection screen.
         * Also adding the associated buttons to the panel, along with glue.
         */
        setUpJPanel(actionButtons = new JPanel(), true);
        actionButtons.add(Box.createHorizontalGlue());
        actionButtons.add(btnMainMenu);
        actionButtons.add(Box.createHorizontalGlue());
        actionButtons.add(btnStartSelectLevel);
        actionButtons.add(Box.createHorizontalGlue());
        actionButtons.add(btnCustomLevels);
        actionButtons.add(Box.createHorizontalGlue());

        // Creating a JPanel to store all of the buttons for the level selector.
        allButtons = new JPanel();
        allButtons.setOpaque(false);
        allButtons.setLayout(new BoxLayout(allButtons, BoxLayout.PAGE_AXIS));
        allButtons.add(pageButtons);
        allButtons.add(Box.createRigidArea(new Dimension(0, (int) Resources.SIDE_LENGTH / 25)));
        allButtons.add(actionButtons);
        allButtons.add(Box.createRigidArea(new Dimension(0, (int) Resources.SIDE_LENGTH / 50)));

        // Creating a JPanel to store the board previews for the levels on the current page.
        setUpJPanel(boards = new JPanel(), true);
        boards.add(Box.createHorizontalGlue());
        boards.add(btnLeftLevel);
        boards.add(Box.createHorizontalGlue());
        boards.add(btnMiddleLevel);
        boards.add(Box.createHorizontalGlue());
        boards.add(btnRightLevel);
        boards.add(Box.createHorizontalGlue());
        
        // Creating a JPanel to store all of the level names on the current page.
        setUpJPanel(textPanel = new JPanel(), true);
        textPanel.add(Box.createHorizontalGlue());
        textPanel.add(levelLabelLeft);
        textPanel.add(Box.createHorizontalGlue());
        textPanel.add(levelLabelMiddle);
        textPanel.add(Box.createHorizontalGlue());
        textPanel.add(levelLabelRight);
        textPanel.add(Box.createHorizontalGlue());
        
        // Creating a JPanel to store the other JPanels created above.
        setUpJPanel(mainPanel = new JPanel(), false);
        mainPanel.add(boards);
        mainPanel.add(textPanel);
        
        // Ensuring the level displays have been updated with the levels from page 1.
        updateView(allDefaultLevels);

        // Adding everything to and subsequently setting up the JFrame.
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(allButtons, BorderLayout.SOUTH);
        this.add(Box.createRigidArea(new Dimension(0, (int) (Resources.SIDE_LENGTH / 3.5))), BorderLayout.NORTH);
        this.setTitle("Level Selector");
        GUIUtilities.configureFrame(this);
    }
    
    /**
     * Initialize a 2D array of JLabels to prepare for storing an image of 
     * a piece depending on the level being previewed.
     * 
     * @param tiles The JLabel 2D array being set up
     */
    private void setUpTiles(JLabel[][] tiles) {
        for (int y = 0; y < Board.SIZE; y++) {
            for (int x = 0; x < Board.SIZE; x++) {
                tiles[x][y] = new JLabel();
                tiles[x][y].setOpaque(false);
            }
        }
    }

    /**
     * Initialize a JPanel with default behaviour.
     * 
     * @param panel The JPanel being set up
     * @param layoutType True for horizontal, false for vertical
     */
    private void setUpJPanel(JPanel panel, boolean layoutType) {
        panel.setOpaque(false);
        if (layoutType) {
            panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        } else {
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        }
    }
    
    /**
     * Initialize a JButton used for interacting with the level selector with default behaviour.
     * 
     * @param button The JButton being set up
     */
    private void setUpMenuButton(JButton button) {
        button.setMaximumSize(new Dimension((int) (Resources.SIDE_LENGTH / BUTTON_X_FACTOR), (int) (BUTTON_Y_FACTOR * Resources.SIDE_LENGTH)));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("Times New Roman", Font.PLAIN, FONT_SIZE));
        button.addActionListener(this);
    }

    /**
     * Initialize a JTextPane with default behaviour.
     * 
     * @param text the JTextPane being set up
     */
    private void setUpJTextArea(JTextPane text) {
        StyledDocument doc = text.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        text.setEditable(false);
        text.setOpaque(false);
        text.setFont(new Font("Times New Roman", Font.PLAIN, FONT_SIZE));
        text.setForeground(Color.WHITE);
        text.setHighlighter(null);
    }

    /**
     * Initialize a JButton used to display the level previews.
     * 
     * @param button The JButton being set up for level preview display purposes
     */
    private void setUpLevelDisplayButton(JButton button, int tileNumber) {
        button.setIcon(new ImageIcon(
                Resources.BOARD.getImage().getScaledInstance((int) (Resources.SIDE_LENGTH / BOARD_DISPLAY_SIZE),
                        (int) (Resources.SIDE_LENGTH / BOARD_DISPLAY_SIZE), Image.SCALE_SMOOTH)));
        button.setOpaque(false);
        button.setBorder(DEFAULT);
        button.setContentAreaFilled(false);
        button.setLayout(new GridLayout(5, 5));
        button.setMaximumSize(new Dimension((int) (Resources.SIDE_LENGTH / BOARD_DISPLAY_SIZE), (int) (Resources.SIDE_LENGTH / BOARD_DISPLAY_SIZE)));
        button.addActionListener(this);
        button.setPreferredSize(new Dimension((int) (Resources.SIDE_LENGTH / BOARD_DISPLAY_SIZE), (int) (Resources.SIDE_LENGTH / BOARD_DISPLAY_SIZE)));
        for (int y = 0; y < Board.SIZE; y++) {
            for (int x = 0; x < Board.SIZE; x++) {
                switch (tileNumber) {
                case 1: {
                    button.add(tiles1[x][y]);
                    break;
                }
                case 2: {
                    button.add(tiles2[x][y]);
                    break;
                }
                case 3: {
                    button.add(tiles3[x][y]);
                }
                }
            }
        }
    }

    /**
     * Update the view of the main level selector JFrame. This involves updating
     * all of the JButtons responsible for displaying board previews. 
     * Some math needs to be done to determine how many JButtons should be updated
     * depending on the number of levels remaining in the lists containing all of the
     * levels for both the default and custom levels.
     * 
     * @param levelList The list we are using to update the level previews
     */
    private void updateView(List<Board> levelList) {
        clearSelectedBorder();
        if (pageNumber != lastPage) {
            updateLevelPreview(tiles1, levelList.get(pageNumber * 3 - 3));
            updateLevelPreview(tiles2, levelList.get(pageNumber * 3 - 2));
            updateLevelPreview(tiles3, levelList.get(pageNumber * 3 - 1));
            btnMiddleLevel.setEnabled(true);
            btnRightLevel.setEnabled(true);
            if (!custom) {
                levelLabelLeft.setText("Level " + levelList.get(pageNumber * 3 - 3).getName());
                levelLabelMiddle.setText("Level " + levelList.get(pageNumber * 3 - 2).getName());
                levelLabelRight.setText("Level " + levelList.get(pageNumber * 3 - 1).getName());
            } else {
                levelLabelLeft.setText(levelList.get(pageNumber * 3 - 3).getName());
                levelLabelMiddle.setText(levelList.get(pageNumber * 3 - 2).getName());
                levelLabelRight.setText(levelList.get(pageNumber * 3 - 1).getName());
            }
        } else {
            switch (levelList.size() % 3) {
            case 1:
                updateLevelPreview(tiles1, levelList.get(pageNumber * 3 - 3));
                updateLevelPreview(tiles2, new Board("Empty"));
                updateLevelPreview(tiles3, new Board("Empty"));
                if (!custom) {
                    levelLabelLeft.setText("Level " + levelList.get(pageNumber * 3 - 3).getName());
                } else {
                    levelLabelLeft.setText(levelList.get(pageNumber * 3 - 3).getName());
                }
                btnMiddleLevel.setEnabled(false);
                btnRightLevel.setEnabled(false);
                levelLabelMiddle.setText("Empty");
                levelLabelRight.setText("Empty");
                break;
            case 2:
                updateLevelPreview(tiles1, levelList.get(pageNumber * 3 - 3));
                updateLevelPreview(tiles2, levelList.get(pageNumber * 3 - 2));
                updateLevelPreview(tiles3, new Board("Empty"));
                if (!custom) {
                    levelLabelLeft.setText("Level " + levelList.get(pageNumber * 3 - 3).getName());
                    levelLabelMiddle.setText("Level " + levelList.get(pageNumber * 3 - 2).getName());
                } else {
                    levelLabelLeft.setText(levelList.get(pageNumber * 3 - 3).getName());
                    levelLabelMiddle.setText(levelList.get(pageNumber * 3 - 2).getName());
                }
                btnRightLevel.setEnabled(false);
                levelLabelRight.setText("Empty");
                break;
            }
        }
        this.revalidate();
        this.repaint();
    }

    /**
     * Update the level previews for the current page.
     * 
     * @param tiles The tiles being updated with images from the new level
     * @param board The board used to update the images displayed on the tiles
     */
    private void updateLevelPreview(JLabel[][] tiles, Board board) {
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null) {
                    if (piece instanceof Mushroom) {
                        tiles[x][y].setIcon(new ImageIcon(
                                Resources.MUSHROOM.getImage().getScaledInstance((int) (Resources.SIDE_LENGTH * X_SCALE_FACTOR),
                                        (int) (Resources.SIDE_LENGTH * X_SCALE_FACTOR), Image.SCALE_SMOOTH)));
                        tiles[x][y].setHorizontalAlignment(SwingConstants.CENTER);
                    } else if (piece instanceof Rabbit) {
                        try {
                            tiles[x][y].setIcon(new ImageIcon(((ImageIcon) Resources.class
                                    .getDeclaredField("RABBIT_" + ((Rabbit) (piece)).getColour()).get(Resources.class))
                                            .getImage().getScaledInstance((int) (Resources.SIDE_LENGTH * X_SCALE_FACTOR),
                                                    (int) Resources.SIDE_LENGTH / Y_SCALE_FACTOR, Image.SCALE_SMOOTH)));
                            tiles[x][y].setHorizontalAlignment(SwingConstants.CENTER);
                        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                            Resources.LOGGER.error("Could not obtain the required field from the Resources class", e);
                        }
                    } else {
                        try {
                            tiles[x][y].setIcon(new ImageIcon(((ImageIcon) Resources.class
                                    .getDeclaredField("FOX_" + ((Fox) (piece)).getFoxType() + "_" + ((Fox) (piece)).getDirection())
                                    .get(Resources.class)).getImage().getScaledInstance((int) (Resources.SIDE_LENGTH * X_SCALE_FACTOR),
                                     (int) (Resources.SIDE_LENGTH / Y_SCALE_FACTOR), Image.SCALE_SMOOTH)));
                            tiles[x][y].setHorizontalAlignment(SwingConstants.CENTER);
                            if (((Fox) (piece)).getFoxType() == FoxType.HEAD) {
                                switch (((Fox) (piece)).getDirection()) {
                                case LEFT:
                                    tiles[x][y].setHorizontalAlignment(SwingConstants.RIGHT);
                                    break;
                                case RIGHT:
                                    tiles[x][y].setHorizontalAlignment(SwingConstants.LEFT);
                                    break;
                                default:
                                    break;
                                }
                            } else {
                                switch (((Fox) (piece)).getDirection()) {
                                case LEFT:
                                    tiles[x][y].setHorizontalAlignment(SwingConstants.LEFT);
                                    break;
                                case RIGHT:
                                    tiles[x][y].setHorizontalAlignment(SwingConstants.RIGHT);
                                    break;
                                default:
                                    break;
                                }
                            }
                        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                            Resources.LOGGER.error("Could not obtain the required field from the Resources class", e);
                        }
                    }
                } else {
                    tiles[x][y].setIcon(null);
                }
            }
        }
    }

    /**
     * Modify border of the selected level preview JButton to stand out
     * against the borders of the other level preview JButtons.
     * 
     * @param button The JButton that was selected
     */
    private void levelSelected(JButton button) {
        clearSelectedBorder();
        button.setBorder(SELECTED);
        btnStartSelectLevel.setEnabled(true);
    }

    /**
     * Clear the borders on the level preview JButtons
     */
    private void clearSelectedBorder() {
        btnLeftLevel.setBorder(DEFAULT);
        btnMiddleLevel.setBorder(DEFAULT);
        btnRightLevel.setBorder(DEFAULT);
    }
    
    /**
     * Calculate the last page for a given level list.
     * 
     * @param levelList The list for which the last page is being calculated for
     */
    private void determineLastPageNumber(List<Board> levelList) {
        lastPage = levelList.size() / 3;
        if (levelList.size() % 3 != 0) 
            lastPage++;
    }

    /**
     * Determine the appropriate action based on the JButton that was just pressed. 
     * 
     * @param e The ActionEvent that has just taken place
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnStartSelectLevel) {
            this.dispose();
            if (btnLeftLevel.getBorder().equals(SELECTED)) {
                int level = (pageNumber * 3) - 2;
                if (!custom) {
                    SwingUtilities.invokeLater(new GameView(Resources.getDefaultBoardByLevel(level), level));
                } else {
                    SwingUtilities.invokeLater(new GameView(allCustomLevels.get(pageNumber * 3 - 3), - 1));
                }
            } else if (btnMiddleLevel.getBorder().equals(SELECTED)) {
                int level = (pageNumber * 3) - 1;
                if (!custom) {
                    SwingUtilities.invokeLater(new GameView(Resources.getDefaultBoardByLevel(level), level));
                } else {
                    SwingUtilities.invokeLater(new GameView(allCustomLevels.get(pageNumber * 3 - 2), - 1));
                }
            } else {
                int level = (pageNumber * 3);
                if (!custom) {
                    SwingUtilities.invokeLater(new GameView(Resources.getDefaultBoardByLevel(level), level));
                } else {
                    SwingUtilities.invokeLater(new GameView(allCustomLevels.get(pageNumber * 3 - 1), - 1));
                }
            }
        } else if (e.getSource() == btnMainMenu) {
            this.dispose();
            SwingUtilities.invokeLater(MainMenu::new);
        } else if (e.getSource() == btnLeftLevel || e.getSource() == btnMiddleLevel || e.getSource() == btnRightLevel) {
            levelSelected((JButton) e.getSource());
        } else if (e.getSource() == btnNextPage) {
            pageNumber++;
            btnLastPage.setEnabled(true);
            btnNextPage.setEnabled(pageNumber != lastPage);
            btnStartSelectLevel.setEnabled(false);
            if (custom) {
                updateView(allCustomLevels);
            } else {
                updateView(allDefaultLevels);
            }
        } else if (e.getSource() == btnLastPage) {
            pageNumber--;
            btnNextPage.setEnabled(true);
            btnLastPage.setEnabled(pageNumber != 1);
            btnStartSelectLevel.setEnabled(false);
            if (custom) {
                updateView(allCustomLevels);
            } else {
                updateView(allDefaultLevels);
            }
        } else if (e.getSource() == btnCustomLevels) {
        	if (allCustomLevels.size() == 0) {
        		GUIUtilities.displayMessageDialog(this, "Could not locate any custom levels.\nTry making some in the level builder!", "No custom levels found");
        	} else {
        		pageNumber = 1;
        		if (custom) {
        			custom = false;
        			btnCustomLevels.setText("Go to Custom Levels");
        			determineLastPageNumber(allDefaultLevels);
        			updateView(allDefaultLevels);
        		} else {
        			custom = true;
        			btnCustomLevels.setText("Go to Default Levels");
        			determineLastPageNumber(allCustomLevels);
        			updateView(allCustomLevels);
        		}
        		btnNextPage.setEnabled(lastPage != 1);
        		btnLastPage.setEnabled(false);
        	}
        }
    }
}
