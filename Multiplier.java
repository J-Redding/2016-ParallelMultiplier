//Parallel and Distributed Computing
//Assignment 1
//Jonathon Redding
//a1666727

import java.util.*;

public class Multiplier implements Runnable{
	//Setting up global variables
	public int[][] matrixA;
	public int[][] matrixB;
	public int[][] productMatrix;
	private int startingRow;
	public int rowsPerThread;
	public int dimensions;

	//Fills matrix entries with random numbers.
	public static void generateMatrices(int dimensions, int[][] matrixA, int[][] matrixB) {
		Random rand = new Random();
		for(int i = 0; i < dimensions; i++) {
			for(int j = 0; j < dimensions; j++) {
				//Random entries are integers between 0 and 99 inclusive.
				matrixA[i][j] = rand.nextInt(100);
				matrixB[i][j] = rand.nextInt(100);
			}
		}
	}

	//Thread constructor.
	//Pass in which row of matrixA the thread will start at, dimensions of matrices, how many rows it should calculate, original matrices, and the product matrix.
	public Multiplier(int startingRow, int dimensions, int rowsPerThread, int[][] matrixA, int[][] matrixB, int[][] productMatrix) {
		this.startingRow = startingRow;
		this.dimensions = dimensions;
		this.rowsPerThread = rowsPerThread;
		this.matrixA = matrixA;
		this.matrixB = matrixB;
		this.productMatrix = productMatrix;
	}

	//Run is the threads' main function.
	public void run(){
		//Create the row entries for the product matrix.
		//Each thread will calculate 'rowsPerThread' amount of entries.
		for(int i = 0; i < this.rowsPerThread; i++) {
			for (int j = 0; j < dimensions; j++) {
				for (int k = 0; k < dimensions; k++) {
					productMatrix[startingRow][j] += matrixA[startingRow][k] * matrixB[k][j];
				}
			}

			//Move to the next row of productMatrix, and matrixA.
			startingRow++;
		}
	}

	public static void main(String[] args) {
		int dimensions = Integer.parseInt(args[0]);
		int threads = Integer.parseInt(args[1]);
		int rowsPerThread = dimensions/threads;
		int[][] matrixA = new int[dimensions][dimensions];
		int[][] matrixB = new int[dimensions][dimensions];
		generateMatrices(dimensions, matrixA, matrixB);
		//Create the productMatrix. Should be the same size as original matrices.
		int[][] productMatrix = new int[dimensions][dimensions];
		//Create an array to hold the threads.
		Thread[] tarray = new Thread[threads];
		int currentThread = 0;
		//Create threads, and put them in tarray.
		if (threads <= dimensions) {
			for(int i = 0; i < threads; i++) {
				if (i == threads - 1) {
					//If the amount of rows dows not split evenly between the amount of threads, the final thread will pick up the extra rows.
					if (dimensions % threads != 0) {
						tarray[i] = new Thread(new Multiplier(i, dimensions, (rowsPerThread + (dimensions - threads * rowsPerThread)), matrixA, matrixB, productMatrix));
					}

					//Otherwise, the rows split evenly between threads.
					else {
						tarray[i] = new Thread(new Multiplier(i, dimensions, rowsPerThread, matrixA, matrixB, productMatrix));
					}
				}

				else {
					tarray[i] = new Thread(new Multiplier(i, dimensions, rowsPerThread, matrixA, matrixB, productMatrix));
				}
			}

			//Start the threads.
			for(int i = 0; i < threads; i++) {
				tarray[i].start();
			}

			try {
				for(int i = 0; i < threads; i++) {
					tarray[i].join();
				}
			}

			catch(Exception exp) {
				exp.printStackTrace();
			}
		}

		//If there are more threads than rows/columns, there are too many threads.
		//Some threads do nothing.
		//Make one thread for each row. Each thread calculates one row of productMatrix.
		else {
			for(int i = 0; i < dimensions; i++) {
				tarray[i] = new Thread(new Multiplier(i, dimensions, 1, matrixA, matrixB, productMatrix));
			}

			for(int i = 0; i < dimensions; i++) {
				tarray[i].start();
			}

			try {
				for(int i = 0; i < dimensions; i++) {
					tarray[i].join();
				}
			}

			catch(Exception exp) {
				exp.printStackTrace();
			}
		}

		long sum = 0;
		//Sum up the elements along the diagonal of the product matrix.
		for(int i = 0; i < dimensions; i++) {
			sum += productMatrix[i][i];
		}

		//Print sum value.
		System.out.println(sum);
	}
}