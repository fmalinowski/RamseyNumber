package edu.ucsb.cs290cloud.strategies;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import edu.ucsb.cs290cloud.commons.FIFOEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Strategy1Distributed extends Strategy {
	static Logger LOGGER = LoggerFactory.getLogger(Strategy1Distributed.class);
	
	final class MatrixIndexes {
		int beginRowIndex;
		int lastRowIndexExcluded;
		int beginColumnIndex;
		int lastColumnIndexExcluded;
	}

	public static final int TABOO_SIZE = 500;
	public static final int MAX_GRAPH_SIZE = 150;
	public static final int HIGH_LIMIT_CLIQUE_COUNT = 9999999;
	
	private int numberOfMatrixSplits;
	private int clientPositionInMatrixSplit;
	
	public Strategy1Distributed(int numberOfMatrixSplits, int clientPositionInMatrixSplit) {	
		this.numberOfMatrixSplits = numberOfMatrixSplits; // from 1 to graphSize
		this.clientPositionInMatrixSplit = clientPositionInMatrixSplit; // from 1 to numberOfMatrixSplits included 
	}
	
	@Override
	public void runStrategy() {
		GraphWithInfos graph;
		FIFOEdge fifoEdge;
		int cliquesCount, bestCliquesCount;
		int best_i, best_j;
		int numberMatrixRowsPerClient;
		int beginRowIndex, lastRowIndexExcluded;
		MatrixIndexes matrixIndexes;
		
		matrixIndexes = new MatrixIndexes();
		
		graph = this.getInitialGraph();

		fifoEdge = new FIFOEdge(TABOO_SIZE);
		best_i = -1;
		best_j = -1;

		while (graph.size() < MAX_GRAPH_SIZE) {
			cliquesCount = new CliqueCounter(graph.getRawGraph())
					.getMonochromaticSubcliquesCount();

			// 0 subclique so we found a counter example
			if (cliquesCount == 0) {
				// BEGIN OF DEBUG PRINTS AREA
				LOGGER.info("Found a counter-example");
				LOGGER.info("size: " + graph.size());
				LOGGER.info(graph.printGraph());
				LOGGER.info("--------------");
				// END OF DEBUG PRINTS AREA
				
				// Save the counter example and end the computation because  
				// we have found a counter example
				graph.setBestCount(0);
				this.setStrategyStatus(Strategy.Status.COUNTER_EXAMPLE, graph);
				return;
				
			} else {
				// We flip an edge, record the new count and unflip the edge.
				// We'll remember the best flip and keep it next time
				// We work only with the upper part of the matrix
				bestCliquesCount = HIGH_LIMIT_CLIQUE_COUNT;
				
				// TODO DONT FORGET that if matrixSize / numberOfMatrixSplits is not integer
				// then the last big squares (last rows) need to be bigger
				// for instance size 81 and split is 4. last split will be from 60 to 81!
				// (59 to 80)
				numberMatrixRowsPerClient = graph.size() / this.numberOfMatrixSplits;
				for (int i = 0; i < this.numberOfMatrixSplits; i++) {					
					matrixIndexes = this.getRowIndexes(i, graph.size());
					beginRowIndex = matrixIndexes.beginRowIndex;
					lastRowIndexExcluded = matrixIndexes.lastRowIndexExcluded;
					
					// We should skip this loop if we know that we will have a square out of upper triangle
					for (int rowIndex = beginRowIndex; rowIndex < lastRowIndexExcluded; rowIndex++) {
						// TODO COLUMNS NOW
						int beginColumnIndex, lastColumnIndexExcluded;
						
						matrixIndexes = this.getRowIndexes(i, graph.size());
						beginColumnIndex = matrixIndexes.beginColumnIndex;
						lastColumnIndexExcluded = matrixIndexes.lastColumnIndexExcluded;
						
						// TODO only compute for the upper triangle!
						
						for (int columnIndex = beginColumnIndex; columnIndex < lastColumnIndexExcluded; columnIndex++) {
							
							// We do that only if we are in the upper triangle... 
							// TODO Needs to catch before if we are in the upper triangle
							// to avoid looping unnecessarily
							
							if (columnIndex > rowIndex) {
								// We flip the value of the cell in the graph
								graph.flipValue(rowIndex, columnIndex);
							
								// We check if number of cliques decreased: it's a good
								// thing
								cliquesCount = new CliqueCounter(graph.getRawGraph())
										.getMonochromaticSubcliquesCountWithTerminate(bestCliquesCount);

								if ((cliquesCount < bestCliquesCount)
										&& !fifoEdge.findEdge(rowIndex, columnIndex)) {
									bestCliquesCount = cliquesCount;
									best_i = rowIndex;
									best_j = columnIndex;
								}

								// Set back the original value
								graph.flipValue(rowIndex, columnIndex);
							}							
						}
					}
				}
				

//				for (int i = 0; i < graph.size(); i++) {
//					for (int j = i + 1; j < graph.size(); j++) {
//
//						// We flip the value of the cell in the graph
//						graph.flipValue(i, j);
//
//						// We check if number of cliques decreased: it's a good
//						// thing
//						cliquesCount = new CliqueCounter(graph.getRawGraph())
//								.getMonochromaticSubcliquesCountWithTerminate(bestCliquesCount);
//
//						if ((cliquesCount < bestCliquesCount)
//								&& !fifoEdge.findEdge(i, j)) {
//							bestCliquesCount = cliquesCount;
//							best_i = i;
//							best_j = j;
//						}
//
//						// Set back the original value
//						graph.flipValue(i, j);
//					}
//				}

				if (bestCliquesCount == HIGH_LIMIT_CLIQUE_COUNT) {
					System.out.println("No best edge found, terminating");
					return;
				}

				// We keep the best flip we saw
				graph.flipValue(best_i, best_j);

				// We taboo this configuration for the graph so that we don't
				// visit it again
				fifoEdge.insertEdge(best_i, best_j);
				
				// Save the latest best graph being computed
				graph.setBestCount(bestCliquesCount);
				this.setStrategyStatus(Strategy.Status.BEING_COMPUTED, graph);

				// BEGIN OF DEBUG PRINTS AREA
				LOGGER.debug("Graph size: " + graph.size() + ", ");
				LOGGER.debug("Best count: " + bestCliquesCount + ", ");
				LOGGER.debug("Best edge: (" + best_i + "," + best_j + "), ");
				LOGGER.debug("New color: " + graph.getValue(best_i, best_j)
						+ "\n");
				// END OF DEBUG PRINTS AREA
			}
		}

		fifoEdge.resetFIFO();
	}
	
	protected MatrixIndexes getRowIndexes(int currentRowMatrixSplitGroup, int graphSize) {
		MatrixIndexes matrixIndexes;
		int numberMatrixRowsPerClient;
		
		matrixIndexes = new MatrixIndexes();
		numberMatrixRowsPerClient = graphSize / this.numberOfMatrixSplits;
		
		matrixIndexes.beginRowIndex = currentRowMatrixSplitGroup * numberMatrixRowsPerClient;
		
		if (currentRowMatrixSplitGroup == this.numberOfMatrixSplits - 1) {
			matrixIndexes.lastRowIndexExcluded = graphSize;
		}
		else {
			matrixIndexes.lastRowIndexExcluded = (matrixIndexes.beginRowIndex + numberMatrixRowsPerClient);
		}
		
		return matrixIndexes;
	}
	
	protected MatrixIndexes getColumnIndexes(int currentRowMatrixSplitGroup, int graphSize) {
		MatrixIndexes matrixIndexes;
		int beginColumnIndex, lastColumnIndexExcluded, numberMatrixRowsPerClient;
		
		matrixIndexes = new MatrixIndexes();
		numberMatrixRowsPerClient = graphSize / this.numberOfMatrixSplits;
		
		// If groupRow is even then the client attribution order is: 1 2 3 4 5
		// If groupRow is odd then the client attribution order is:  5 4 3 2 1
		
		// even 0, 2 , 4...
		if (currentRowMatrixSplitGroup % 2 == 0) {
			beginColumnIndex = (this.clientPositionInMatrixSplit - 1) * numberMatrixRowsPerClient;
			
			lastColumnIndexExcluded = (this.clientPositionInMatrixSplit == this.numberOfMatrixSplits) ? graphSize :
				(beginColumnIndex + numberMatrixRowsPerClient);
		}
		else {
			beginColumnIndex = (this.numberOfMatrixSplits - this.clientPositionInMatrixSplit) * numberMatrixRowsPerClient;
			lastColumnIndexExcluded = beginColumnIndex + numberMatrixRowsPerClient;
			
			lastColumnIndexExcluded = (this.clientPositionInMatrixSplit == 1) ? graphSize :
				(beginColumnIndex + numberMatrixRowsPerClient);
		}
		
		matrixIndexes.beginColumnIndex = beginColumnIndex;
		matrixIndexes.lastColumnIndexExcluded = lastColumnIndexExcluded;
		
		return matrixIndexes;
	}

}
