package es.uva.tfg.hector.SkyWalkerApp.services;

/**
 * Mathematical matrix class using doubles.
 * @author Héctor Del Campo Pando
 */
public class Matrix {

    /**
     * Num of rows and columns.
     */
    private final int rows, cols;

    /**
     * Data of the matrix.
     */
    private final double[][] data;

    /**
     * Constructs a new T Matrix with the given sizes and filled with 0.
     */
    public Matrix (final int rows, final int cols) {
        this.rows = rows;
        this.cols = cols;
        data = new double[rows][cols];
    }

    /**
     * Constructs a new T Matrix with the given values.
     * @param data to set.
     */
    public Matrix (final double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = data;
    }

    /**
     * Constructs a lineal matrix, aka vector.
     * @param vector container of data.
     *//*
    public Matrix (final double[] vector) {
        this.rows = 1;
        this.cols = vector.length;
        this.data = new double[rows][cols];

        for (int i = 0; i < cols; i++) {
            data[0][i] = vector[i];
        }

    }*/

    /**
     * Retrieves the matrix multiplication as this*rightFactor.
     * @param rightFactor the right multiplier matrix.
     */
    public Matrix multiply(final Matrix rightFactor) {

        if (cols != rightFactor.getRows()) {
            throw new IllegalArgumentException("Cannot multiply with this sizes");
        }

        double[][] newData = new double[rows][rightFactor.getCols()];

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < rightFactor.getCols(); j++) {
                for(int k = 0; k < cols; k++) {
                    newData[i][j] += data[i][k] * rightFactor.get(k, j);
                }
            }
        }

        return new Matrix(newData);

    }

    /**
     * Sets the matrix position to the given element.
     * @param row of the position.
     * @param col of the position.
     * @param element to save.
     */
    public void set(final int row, final int col, double element) {
        data[row][col] = element;
    }

    /**
     * Retrieves a matrix element.
     * @param row of the element.
     * @param col of the element.
     * @return the element itself.
     */
    public double get(final int row, final int col) {
        return data[row][col];
    }

    /**
     * Retrieves the number of rows.
     * @return the number of rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Retrieves the number of columns.
     * @return the number of columns.
     */
    public int getCols() {
        return cols;
    }

}
