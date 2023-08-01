import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;

public class GameOfLife extends Application {
	
	Stage mainStage, menuStage;
	Scene mainScene, menuScene;
	Button startButton, resetButton, loadButton, stopButton;
	HBox mainHPane;
	VBox menuPane, mainVPane;
	Canvas fieldCanvas;
	TextField width, height;
	GraphicsContext g;
	int widthA, heightA, nei;
	int[][] board, copy;
	boolean change, stop;
	ComboBox<String> patterns;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;
		mainStage.setTitle("Game of Life");
		mainHPane = new HBox();
		mainHPane.setSpacing(20);
		mainHPane.setPadding(new Insets(15,10,10,15));
		mainVPane = new VBox();
		mainVPane.setSpacing(20);
		mainVPane.setPadding(new Insets(15,10,10,15));
		mainScene = new Scene(mainVPane, 1750, 900);
		loadButton = new Button("Load Simulation");
		menuStage = new Stage();
		menuStage.setTitle("Welcome to the Game of Life");
		change = true;
		stop = false;
		width = new TextField();
		width.setPromptText("Width of field (10 - 100)");
		width.setMaxWidth(200);
		width.setFocusTraversable(false);
		height = new TextField();
		height.setPromptText("Height of field (5 - 50)");
		height.setMaxWidth(200);
		height.setFocusTraversable(false);
		loadButton.setOnAction(e -> {
			try {
				Integer.parseInt(width.getText());
				Integer.parseInt(height.getText());
				if(Integer.parseInt(width.getText()) > 100 || Integer.parseInt(width.getText()) < 10)
					throw new Exception();
				if(Integer.parseInt(height.getText()) > 50 || Integer.parseInt(height.getText()) < 5)
					throw new Exception();
				fieldCanvas.setWidth(1700);
				fieldCanvas.setHeight(750);
				g = fieldCanvas.getGraphicsContext2D();
				g.setFill(Color.LIGHTGRAY);
				g.fillRect(0, 0, fieldCanvas.getWidth(), fieldCanvas.getHeight());
				drawGrid();
				board = new int[(int) (fieldCanvas.getHeight() / heightA)][(int) (fieldCanvas.getWidth() / widthA)];
				copy = new int[board.length][board[0].length];
				menuStage.close();
				mainStage.show();
			}
			catch(Exception ex) {
			}
		});
		menuPane = new VBox();
		menuPane.setSpacing(50);
		menuPane.getChildren().addAll(width, height, loadButton);
		menuScene = new Scene(menuPane, 500,300);
		fieldCanvas = new Canvas();

		initCanvasClicked();
		
		patterns = new ComboBox<>();
		patterns.getItems().addAll("Blinker", "Latin Cross", "H", "r-Pentomino", "Clock", "Glider", "O", "Beacon", "Toad", "Light-weight spaceship", "Custom");
		patterns.setValue("Custom");
		patterns.show();

		mainHPane.getChildren().addAll(startButton = new Button("START"), resetButton = new Button("RESET"), stopButton = new Button("STOP"), patterns);

		initCanvasButtons();

		mainVPane.getChildren().addAll(mainHPane, fieldCanvas);

		menuStage.setScene(menuScene);
		menuStage.show();
		mainStage.setScene(mainScene);
	}
	
	private void initCanvasButtons() {
		stopButton.setOnAction(e -> {
			stop = true;
		});

		startButton.setOnAction(e -> {
			stop = false;
			Timeline t = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					step();
				}
			}));				
			t.setCycleCount(Timeline.INDEFINITE);
			t.play();
		});

		resetButton.setOnAction(e -> {
			stop = true;
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					if(board[i][j] == 1) {
						board[i][j] = 0;
						copy[i][j] = 0;
						g.setFill(Color.LIGHTGRAY);
						g.fillRect(j * widthA + 1, i * heightA + 1, widthA - 2, heightA - 2);
					}
				}
			}
		});
	}

	private void initCanvasClicked() {
		fieldCanvas.setOnMouseClicked(e -> {
			int clickY = (int)(e.getY() / heightA);
			int clickX = (int)(e.getX() / widthA);
			g.setFill(Color.BLACK);
			if(patterns.getValue().equals("Custom")) {
				if(board[clickY][clickX] == 1) {
					g.setFill(Color.LIGHTGRAY);
					board[clickY][clickX] = 0;
				}
				else {
					board[clickY][clickX] = 1;
				}
				g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
			}
			else if(patterns.getValue().equals("Blinker")) {
				for(int i = 0; i < 3; i++) {
					clickY = (int)(e.getY() / heightA) + i;
					if(clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
			}
			else if(patterns.getValue().equals("Latin Cross")) {
				for(int i = 0; i < 4; i++) {
					clickY = (int)(e.getY() / heightA) + i;
					if(clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
				clickY = (int)(e.getY() / heightA) + 1;
				for(int i = -1; i < 2; i++) {
					clickX = (int)(e.getX() / widthA) + i;
					if(clickX < board[0].length && clickX > 0 && clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
			}
			else if(patterns.getValue().equals("H")) {
				for(int ii = 0; ii < 3; ii += 2)
					for(int i = 0; i < 3; i++) {
						clickY = (int)(e.getY() / heightA) + i;
						clickX = (int)(e.getX() / widthA) + ii;
						if(clickY < board.length && clickX < board[0].length && board[clickY][clickX] == 0) {
							board[clickY][clickX] = 1;
							g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
						}
					}
				clickY = (int)(e.getY() / heightA) + 1;
				clickX = (int)(e.getX() / widthA) + 1;
				if(clickY < board.length && clickX < board[0].length && board[clickY][clickX] == 0) {
					board[clickY][clickX] = 1;
					g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
				}
			}
			else if(patterns.getValue().equals("r-Pentomino")) {
				for(int i = 0; i < 3; i++) {
					clickY = (int)(e.getY() / heightA) + i;
					if(clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
				clickY = (int)(e.getY() / heightA);
				clickX = (int)(e.getX() / widthA) + 1;
				if(clickX < board[0].length && board[clickY][clickX] == 0) {
					board[clickY][clickX] = 1;
					g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
				}
				clickY++;
				clickX -= 2;
				if(clickX > 0 && clickY < board.length && board[clickY][clickX] == 0) {
					board[clickY][clickX] = 1;
					g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
				}
			}
			else if(patterns.getValue().equals("Clock")) {
				for(int i = 0; i < 3; i++) {
					clickY = (int)(e.getY() / heightA) + 1;
					clickX = (int)(e.getX() / widthA) + i;
					if(i == 2)
						clickY--;
					if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
				for(int i = 1; i < 4; i++) {
					clickY = (int)(e.getY() / heightA) + 2;
					clickX = (int)(e.getX() / widthA) + i;
					if(i == 1)
						clickY++;
					if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
			}
			else if(patterns.getValue().equals("Light-weight spaceship")) {
				for(int i = 0; i < 4; i++) {
					clickY = (int)(e.getY() / heightA) + i;
					clickX = (int)(e.getX() / widthA);
					if(i == 0)
						clickX++;
					if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
				for(int i = 1; i < 5; i++) {
					clickY = (int)(e.getY() / heightA) + 3;
					clickX = (int)(e.getX() / widthA) + i;
					if(i == 4)
						clickY--;
					if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
			}
			else if(patterns.getValue().equals("Glider")) {
				for(int i = 0; i < 2; i++) {
					clickY = (int)(e.getY() / heightA);
					clickX = (int)(e.getX() / widthA) + i;
					if(i == 0)
						clickY++;
					if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
				for(int i = 0; i < 3; i++) {
					clickY = (int)(e.getY() / heightA) + 2;
					clickX = (int)(e.getX() / widthA) + i;
					if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
						board[clickY][clickX] = 1;
						g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
					}
				}
			}
			else if(patterns.getValue().equals("O")) {
				for(int ii = 0; ii < 5; ii += 4)
					for(int i = 1; i < 4; i++) {
						clickY = (int)(e.getY() / heightA) + ii;
						clickX = (int)(e.getX() / widthA) + i;
						if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
							board[clickY][clickX] = 1;
							g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
						}
					}
				for(int ii = 0; ii < 5; ii += 4)
					for(int i = 1; i < 4; i++) {
						clickY = (int)(e.getY() / heightA) + i;
						clickX = (int)(e.getX() / widthA) + ii;
						if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
							board[clickY][clickX] = 1;
							g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
						}
					}
			}
			else if(patterns.getValue().equals("Toad")) {
				for(int ii = 0; ii < 2; ii++)
					for(int i = 0; i < 3; i++) {
						clickY = (int)(e.getY() / heightA) + ii;
						clickX = (int)(e.getX() / widthA) + i + ii;
						if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
							board[clickY][clickX] = 1;
							g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
						}
					}
			}
			else if(patterns.getValue().equals("Beacon")) {
				for(int iii = 0; iii < 3; iii += 2)
					for(int ii = 0; ii < 2; ii++)
						for(int i = 0; i < 2; i++) {
							clickY = (int)(e.getY() / heightA) + ii + iii;
							clickX = (int)(e.getX() / widthA) + i + iii;
							if(clickX < board[0].length && clickY < board.length && board[clickY][clickX] == 0) {
								board[clickY][clickX] = 1;
								g.fillRect(clickX * widthA + 1, clickY * heightA + 1, widthA - 2, heightA - 2);
							}
						}
			}
		});
	}

	public void step() {
		if(!stop) {
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					if((i == 0 || i == board.length - 1) && j > 0 && j < board[0].length - 1 && board[i][j-1] == 1 && board[i][j+1] == 1) {				// 
							stop = true;																												//
							System.out.println("Finished, because area got trespassed");																// checks whether a cell would generate outside the field
					}																																	//
					else if((j == 0 || j == board[0].length - 1) && i > 0 && i < board.length - 1 && board[i - 1][j] == 1 && board[i + 1][j] == 1) {	//
						stop = true;
						System.out.println("Finished, because area got trespassed");
					}
					nei = neighbours(board, i, j);
					if(nei > 0) {
						if((nei < 2 || nei > 3) && board[i][j] == 1) {
							copy[i][j] = 0;
							g.setFill(Color.LIGHTGRAY);
							g.fillRect(j * widthA + 1, i * heightA + 1, widthA - 2, heightA - 2);
						}
						else if((nei == 2 || nei == 3) && board[i][j] == 1) {
							copy[i][j] = 1;
						}
						else if(nei == 3) {
							copy[i][j] = 1;
							g.setFill(Color.BLACK);
							g.fillRect(j * widthA + 1, i * heightA + 1, widthA - 2, heightA - 2);
						}
						else {
							copy[i][j] = 0;
							g.setFill(Color.LIGHTGRAY);
						}
					}
					else if(board[i][j] == 1) {
						copy[i][j] = 0;
						g.setFill(Color.LIGHTGRAY);
						g.fillRect(j * widthA + 1, i * heightA + 1, widthA - 2, heightA - 2);
					}
				}
			}
			
			if(testEqual(board, copy)) {
				System.out.println("Finished, because nothing has changed.");
				stop = true;
			}
			board = updateField(board, copy);
		}
	}
	
	public void drawGrid() {
		widthA = Integer.parseInt(width.getText());
		heightA = Integer.parseInt(height.getText());

		while(fieldCanvas.getWidth() % widthA != 0)
			widthA++;
		while(fieldCanvas.getHeight() % heightA != 0)
			heightA++;

		widthA = (int) fieldCanvas.getWidth() / widthA;
		heightA = (int) fieldCanvas.getHeight() / heightA;
		drawLines();
	}
	
	public boolean testEqual(int[][] a, int[][] b) {
		boolean equal = true;
		for(int i = 0; i < a.length; i++) {
			for(int j = 0; j < a[i].length; j++) {
				if(a[i][j] != b[i][j])
					equal = false;
			}
		}
		return equal;
	}
	
	public void drawLines() {
		g.setStroke(Color.GRAY);
		for(int i = 1; i < fieldCanvas.getWidth(); i++) {
			for(int j = 1; j < fieldCanvas.getHeight(); j++) {
				if(j % heightA == 0)
					g.strokeLine(0, j, fieldCanvas.getWidth(), j);
				if(i % widthA == 0)
					g.strokeLine(i, 0, i, fieldCanvas.getHeight());
			}
		}
	}
	
	public int neighbours(int[][] field, int y, int x) {
		int neiA = 0;
		for(int i = x - 1; i <= x + 1; i++) {
			if(i >= 0 && i < field[0].length && (y - 1) >= 0 && field[y-1][i] == 1)
				neiA++;
		}
		for(int i = x - 1; i <= x + 1; i++) {
			if(i >= 0 && i < field[0].length && (y + 1) < field.length && field[y+1][i] == 1)
				neiA++;
		}
		if(x > 0 && field[y][x-1] == 1)
			neiA++;
		if(x < (field[0].length - 1) && field[y][x+1] == 1)
			neiA++;
		return neiA;
	}
	
	public int[][] updateField(int[][] a, int[][] b) {
		for(int i = 0; i < b.length; i++) {
			for(int j = 0; j < b[i].length; j++) {
				a[i][j] = b[i][j];
			}
		}
		return a;
	}
}
