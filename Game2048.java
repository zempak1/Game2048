package com.javarush.games.game2048;

import com.javarush.engine.cell.*;

import java.util.Arrays;

public class Game2048 extends Game {

    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();

    }

    private void clearField() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                gameField[y][x] = 0;
            }
        }
    }

    private void createGame() {
        clearField();
        score = 0;
        setScore(score);
        createNewNumber();
        createNewNumber();
    }

    private void createNewNumber() {

        if (getMaxTileValue() == 2048) {
            win();
        } else {
            int x;
            int y;
            do {
                x = getRandomNumber(SIDE);    //Ищем случайную пустую ячейку в матрице
                y = getRandomNumber(SIDE);
            } while (gameField[y][x] != 0);

            int n = getRandomNumber(10);        //С вероятностью 10% ячейка примет значение 4
            if (n == 9) {
                gameField[y][x] = 4;
            }                            //И 2 с вероятностью 90%
            else {
                gameField[y][x] = 2;
            }
        }
    }

    private void drawScene() {

        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                setCellColoredNumber(x, y, gameField[y][x]);
            }
        }
    }


    private Color getColorByValue(int value) {
        if (value == 0) {
            return Color.BURLYWOOD;
        } else if (value == 2) {
            return Color.GREEN;
        } else if (value == 4) {
            return Color.DARKGREEN;
        } else if (value == 8) {
            return Color.YELLOWGREEN;
        } else if (value == 16) {
            return Color.YELLOW;
        } else if (value == 32) {
            return Color.ORANGE;
        } else if (value == 64) {
            return Color.DARKORANGE;
        } else if (value == 128) {
            return Color.RED;
        } else if (value == 256) {
            return Color.DARKRED;
        } else if (value == 512) {
            return Color.PINK;
        } else if (value == 1024) {
            return Color.VIOLET;
        } else if (value == 2048) {
            return Color.DARKVIOLET;
        }
        return null;
    }

    private void setCellColoredNumber(int x, int y, int value) {
        Color color = getColorByValue(value);

        if (value == 0) {
            setCellValueEx(x, y, color, "");
        } else {
            setCellValueEx(x, y, color, Integer.toString(value));
        }
    }

    private boolean compressRow(int[] row) {

        int[] tmpRow = new int[row.length];

        for (int i = 0; i < row.length; i++) {
            tmpRow[i] = row[i];
        }

        for (int i = row.length - 1; 0 < i; i--) {
            for (int j = 0; j < i; j++) {
                if (row[j] == 0) {
                    int m = row[j];
                    row[j] = row[j + 1];
                    row[j + 1] = m;
                }
            }
        }
        return !Arrays.equals(tmpRow, row);
    }

    private boolean mergeRow(int[] row) {

        int[] tmpRow = new int[row.length];

        for (int i = 0; i < row.length; i++) {
            tmpRow[i] = row[i];
        }

        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] == row[i + 1]) {
                row[i] = row[i] * 2;
                row[i + 1] = 0;
                score += row[i];
                setScore(score);
            }
        }
        return !Arrays.equals(tmpRow, row);
    }

    private void rotateClockwise() {
        //Поворачиваем матрицу по часовой стрелке на 90градусов
        int[][] arr = new int[SIDE][SIDE];
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                arr[x][SIDE - 1 - y] = gameField[y][x];
            }
        }
        gameField = arr;


    }

    // БЛОК ПРОВЕРОК

    private int getMaxTileValue() {
        int maxValue = 0;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (gameField[y][x] > maxValue) {
                    maxValue = gameField[y][x];
                }
            }
        }
        return maxValue;
    }

    private boolean canUserMove() {
        int countZero = 0;
        int equalNeighbor = 0;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (gameField[y][x] == 0) {
                    countZero++;
                }
                if (y > 0 && gameField[y - 1][x] == gameField[y][x]
                        || y < SIDE - 1 && gameField[y + 1][x] == gameField[y][x]
                        || x > 0 && gameField[y][x - 1] == gameField[y][x]
                        || x < SIDE - 1 && gameField[y][x + 1] == gameField[y][x]) {

                    equalNeighbor++;
                }
            }

        }
        if (countZero > 0 || (equalNeighbor > 0 && countZero == 0)) {
            return true;
        } else {
            return false;
        }
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.AQUA, "YOU WIN", Color.BLACK, 50);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "YOU LOOSE", Color.ANTIQUEWHITE, 50);
    }


    // БЛОК УПРАВЛЕНИЯ

    @Override
    public void onKeyPress(Key key) {

        if (isGameStopped && key == Key.SPACE) {
            isGameStopped = false;
            createGame();
            drawScene();
        } else if (!isGameStopped) {

            if (!canUserMove()) {
                gameOver();
            } else {
                if (key == Key.LEFT) {
                    moveLeft();
                    drawScene();
                }
                if (key == Key.RIGHT) {
                    moveRight();
                    drawScene();
                }
                if (key == Key.UP) {
                    moveUp();
                    drawScene();
                }
                if (key == Key.DOWN) {
                    moveDown();
                    drawScene();
                }
            }
        }
    }

    private void moveLeft() {
        boolean bool = true;
        boolean bool2 = true;
        int count = 0;
        for (int i = 0; i < SIDE; i++) {
            bool = compressRow(gameField[i]);
            bool2 = mergeRow(gameField[i]);
            if (bool2) {
                compressRow(gameField[i]);
            }
            if (bool || bool2) {
                count++;
            }
        }
        if (count > 0) {
            createNewNumber();
        }
    }

    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        boolean bool = true;
        boolean bool2 = true;
        int count = 0;
        for (int i = 0; i < SIDE; i++) {
            bool = compressRow(gameField[i]);
            bool2 = mergeRow(gameField[i]);
            if (bool2) {
                compressRow(gameField[i]);
            }
            if (bool || bool2) {
                count++;
            }
        }
        if (count > 0) {
            createNewNumber();
        }

        rotateClockwise();
        rotateClockwise();

    }

    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        boolean bool = true;
        boolean bool2 = true;
        int count = 0;
        for (int i = 0; i < SIDE; i++) {
            bool = compressRow(gameField[i]);
            bool2 = mergeRow(gameField[i]);
            if (bool2) {
                compressRow(gameField[i]);
            }
            if (bool || bool2) {
                count++;
            }
        }
        if (count > 0) {
            createNewNumber();
        }

        rotateClockwise();

    }

    private void moveDown() {
        rotateClockwise();
        boolean bool = true;
        boolean bool2 = true;
        int count = 0;
        for (int i = 0; i < SIDE; i++) {
            bool = compressRow(gameField[i]);
            bool2 = mergeRow(gameField[i]);
            if (bool2) {
                compressRow(gameField[i]);
            }
            if (bool || bool2) {
                count++;
            }
        }
        if (count > 0) {
            createNewNumber();
        }

        rotateClockwise();
        rotateClockwise();
        rotateClockwise();

    }
}
