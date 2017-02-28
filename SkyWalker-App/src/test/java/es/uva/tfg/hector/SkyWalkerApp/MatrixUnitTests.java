package es.uva.tfg.hector.SkyWalkerApp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Matrix class tests.
 * @author Héctor Del Campo Pando
 */
public class MatrixUnitTests {

    /*
     * Constructors
     */
    @Test
    public void constructWithData () {

        final double[][] data = new double[5][5];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] = 1;
            }
        }

        final Matrix matrix = new Matrix(data);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                assertEquals(data[i][j], matrix.get(i,j), 0);
            }
        }

    }

    /*
     * Multiply method
     */
    @Test
    public void multiply3x3 () {

        final double[][] data1 = new double[][] {{1,2,3}, {4,5,6}, {7,8,9}};
        final double[][] data2 = new double[][] {{9,8,7}, {6,5,4}, {3,2,1}};
        final double[][] expected = new double[][] {{30,24,18}, {84,69,54}, {138,114,90}};

        final Matrix m1 = new Matrix(data1);
        final Matrix m2 = new Matrix(data2);

        final Matrix result = m1.multiply(m2);

        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[0].length; j++) {
                assertEquals(expected[i][j], result.get(i,j), 0);
            }
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void multiplyInvalidSize () {

        final double[][] data1 = new double[][] {{1,2,3}};
        final double[][] data2 = new double[][] {{9,8,7}};

        final Matrix m1 = new Matrix(data1);
        final Matrix m2 = new Matrix(data2);

        m1.multiply(m2);

    }

}
