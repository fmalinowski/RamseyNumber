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
		int beginColumnIndex;
		MatrixIndexes mi;
		
		mi = new MatrixIndexes();
		
		graph = this.getInitialGraph();

		fifoEdge = new FIFOEdge(TABOO_SIZE);
		
		bestCliquesCount = graph.getBestCount();
		
		while (this.isThreadAlive() && graph.size() < MAX_GRAPH_SIZE) {
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
				
			} 
			else {				
				// If matrixSize / numberOfMatrixSplits is not integer
				// then the last big squares (last rows) need to be bigger
				// for instance if size 81 and split is 4, last split will be from 59 to 80!

				for (int currentRowMatrixSplitGroup = 1; 
						currentRowMatrixSplitGroup <= this.numberOfMatrixSplits && this.isThreadAlive(); 
						currentRowMatrixSplitGroup++) {					
					
					mi = this.getRowAndColumnIndexes(currentRowMatrixSplitGroup, graph.size());
					
					// If we the client square has at least some spots in the upper triangle
					if (mi.lastColumnIndexExcluded > mi.beginRowIndex) {
						
						// We should skip this loop if we know that we will have a square out of upper triangle
						for (int rowIndex = mi.beginRowIndex; rowIndex < mi.lastRowIndexExcluded && this.isThreadAlive(); rowIndex++) {
						
							// Only compute this row for the upper triangle!
							beginColumnIndex = Math.max(mi.beginColumnIndex, rowIndex + 1);
//							System.out.println("Ibegin:" + mi.beginRowIndex + " | IendEx:" + mi.lastRowIndexExcluded + 
//									" | JBegin:" + beginColumnIndex + " | JEnd: " + mi.lastColumnIndexExcluded + 
//									" | PosInMatrix:" + this.clientPositionInMatrixSplit + 
//									" | rowGroup:" + currentRowMatrixSplitGroup);
						
							for (int columnIndex = beginColumnIndex; columnIndex < mi.lastColumnIndexExcluded && this.isThreadAlive(); columnIndex++) {
								// We flip the value of the cell in the graph
								graph.flipValue(rowIndex, columnIndex);
							
								// We check if number of cliques decreased: it's a good
								// thing
								cliquesCount = new CliqueCounter(graph.getRawGraph())
									.getMonochromaticSubcliquesCountWithTerminate(bestCliquesCount);

								if ((cliquesCount < bestCliquesCount)
										&& !fifoEdge.findEdge(rowIndex, columnIndex)) {
									bestCliquesCount = cliquesCount;
//									System.out.println("FOUND BEST COUNT " + bestCliquesCount + " - bestI:" + rowIndex + " | bestJ:" + columnIndex + " |} graphSize:" + graph.size());
									
//									System.out.println("FLIP BEST VALUE  " + bestCliquesCount + " - bestI:" + rowIndex + " | bestJ:" + columnIndex + " |} graphSize:" + graph.size());

									// We taboo this configuration for the graph so that we don't
									// visit it again
									fifoEdge.insertEdge(rowIndex, columnIndex);
									
									// Save the latest best graph being computed
									graph.setBestCount(bestCliquesCount);
									this.setStrategyStatus(Strategy.Status.BEING_COMPUTED, graph);

									// BEGIN OF DEBUG PRINTS AREA
									LOGGER.debug("Graph size: " + graph.size() + ", ");
									LOGGER.debug("Best count: " + bestCliquesCount + ", ");
									LOGGER.debug("Best edge: (" + rowIndex + "," + columnIndex + "), ");
									LOGGER.debug("New color: " + graph.getValue(rowIndex, columnIndex)
											+ "\n");
									// END OF DEBUG PRINTS AREA
								}
								else {
									// Set back the original value
									graph.flipValue(rowIndex, columnIndex);	
								}					
							}
						}
					}
					
				}
			}
		}

		System.out.println("Thread killed!");
		
//		fifoEdge.resetFIFO();
	}
	
//	@Override
//	public void runStrategy() {
//		GraphWithInfos graph;
//		FIFOEdge fifoEdge;
//		int cliquesCount, bestCliquesCount;
//		Boolean hasFoundBestCount;
//		int best_i, best_j;
//		int beginColumnIndex;
//		MatrixIndexes mi;
//		
//		mi = new MatrixIndexes();
//		
//		graph = this.getInitialGraph();
//
//		fifoEdge = new FIFOEdge(TABOO_SIZE);
//		best_i = -1;
//		best_j = -1;
//		hasFoundBestCount = false;
//		
//		bestCliquesCount = graph.getBestCount();
//		
//		while (this.blinker == Thread.currentThread() && graph.size() < MAX_GRAPH_SIZE) {
//			cliquesCount = new CliqueCounter(graph.getRawGraph())
//					.getMonochromaticSubcliquesCount();
//
//			// 0 subclique so we found a counter example
//			if (cliquesCount == 0) {
//				// BEGIN OF DEBUG PRINTS AREA
//				LOGGER.debug("Found a counter-example");
//				LOGGER.debug("size: " + graph.size());
//				LOGGER.debug(graph.printGraph());
//				LOGGER.debug("--------------");
//				// END OF DEBUG PRINTS AREA
//				
//				// Save the counter example and end the computation because  
//				// we have found a counter example
//				graph.setBestCount(0);
//				this.setStrategyStatus(Strategy.Status.COUNTER_EXAMPLE, graph);
//				return;
//				
//			} 
//			else {				
//				// If matrixSize / numberOfMatrixSplits is not integer
//				// then the last big squares (last rows) need to be bigger
//				// for instance if size 81 and split is 4, last split will be from 59 to 80!
//
//				for (int currentRowMatrixSplitGroup = 1; 
//						currentRowMatrixSplitGroup <= this.numberOfMatrixSplits && this.blinker == Thread.currentThread(); 
//						currentRowMatrixSplitGroup++) {					
//					
//					mi = this.getRowAndColumnIndexes(currentRowMatrixSplitGroup, graph.size());
//					
//					// If we the client square has at least some spots in the upper triangle
//					if (mi.lastColumnIndexExcluded > mi.beginRowIndex) {
//						
//						// We should skip this loop if we know that we will have a square out of upper triangle
//						for (int rowIndex = mi.beginRowIndex; rowIndex < mi.lastRowIndexExcluded && this.blinker == Thread.currentThread(); rowIndex++) {
//						
//							// Only compute this row for the upper triangle!
//							beginColumnIndex = Math.max(mi.beginColumnIndex, rowIndex + 1);
//							System.out.println("Ibegin:" + mi.beginRowIndex + " | IendEx:" + mi.lastRowIndexExcluded + 
//									" | JBegin:" + beginColumnIndex + " | JEnd: " + mi.lastColumnIndexExcluded + 
//									" | PosInMatrix:" + this.clientPositionInMatrixSplit + 
//									" | rowGroup:" + currentRowMatrixSplitGroup);
//						
//							for (int columnIndex = beginColumnIndex; columnIndex < mi.lastColumnIndexExcluded && this.blinker == Thread.currentThread(); columnIndex++) {
//								// We flip the value of the cell in the graph
//								graph.flipValue(rowIndex, columnIndex);
//							
//								// We check if number of cliques decreased: it's a good
//								// thing
//								cliquesCount = new CliqueCounter(graph.getRawGraph())
//									.getMonochromaticSubcliquesCountWithTerminate(bestCliquesCount);
//
//								if ((cliquesCount < bestCliquesCount)
//										&& !fifoEdge.findEdge(rowIndex, columnIndex)) {
//									bestCliquesCount = cliquesCount;
//									best_i = rowIndex;
//									best_j = columnIndex;
//									hasFoundBestCount = true;
//									System.out.println("FOUND BEST COUNT " + bestCliquesCount + " - bestI:" + best_i + " | bestJ:" + best_j + " |} graphSize:" + graph.size());
//								}
//
//								// Set back the original value
//								graph.flipValue(rowIndex, columnIndex);						
//							}
//						}
//					}
//					
//				}
//
//				if (hasFoundBestCount) {
//					System.out.println("FLIP BEST VALUE  " + bestCliquesCount + " - bestI:" + best_i + " | bestJ:" + best_j + " |} graphSize:" + graph.size());
//					
//					// We keep the best flip we saw
//					graph.flipValue(best_i, best_j);
//
//					// We taboo this configuration for the graph so that we don't
//					// visit it again
//					fifoEdge.insertEdge(best_i, best_j);
//					
//					// Save the latest best graph being computed
//					graph.setBestCount(bestCliquesCount);
//					this.setStrategyStatus(Strategy.Status.BEING_COMPUTED, graph);
//					
//					hasFoundBestCount = false;
//
//					// BEGIN OF DEBUG PRINTS AREA
//					LOGGER.debug("Graph size: " + graph.size() + ", ");
//					LOGGER.debug("Best count: " + bestCliquesCount + ", ");
//					LOGGER.debug("Best edge: (" + best_i + "," + best_j + "), ");
//					LOGGER.debug("New color: " + graph.getValue(best_i, best_j)
//							+ "\n");
//					// END OF DEBUG PRINTS AREA
//				}
//			}
//		}
//
//		System.out.println("Thread killed!");
//		
////		fifoEdge.resetFIFO();
//	}
	
	protected MatrixIndexes getRowIndexes(int currentRowMatrixSplitGroup, int graphSize) {
		MatrixIndexes matrixIndexes;
		int numberMatrixRowsPerClient;
		
		matrixIndexes = new MatrixIndexes();
		numberMatrixRowsPerClient = (int) Math.floor(graphSize / this.numberOfMatrixSplits);
		
		matrixIndexes.beginRowIndex = (currentRowMatrixSplitGroup - 1) * numberMatrixRowsPerClient;
		
		if (currentRowMatrixSplitGroup == this.numberOfMatrixSplits) {
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
		
		// odd 1, 3 , ...
		if (currentRowMatrixSplitGroup % 2 == 1) {
			beginColumnIndex = (this.clientPositionInMatrixSplit - 1) * numberMatrixRowsPerClient;
			
			lastColumnIndexExcluded = (this.clientPositionInMatrixSplit == this.numberOfMatrixSplits) ? graphSize :
				(beginColumnIndex + numberMatrixRowsPerClient);
		}
		// even 2, 4, ...
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
	
	protected MatrixIndexes getRowAndColumnIndexes(int currentRowMatrixSplitGroup, 
			int graphSize) {
		MatrixIndexes resultMatrixIndexes, matrixIndexes;
		
		resultMatrixIndexes = new MatrixIndexes();
		
		matrixIndexes = this.getRowIndexes(currentRowMatrixSplitGroup, graphSize);
		resultMatrixIndexes.beginRowIndex = matrixIndexes.beginRowIndex;
		resultMatrixIndexes.lastRowIndexExcluded = matrixIndexes.lastRowIndexExcluded;
		
		matrixIndexes = this.getColumnIndexes(currentRowMatrixSplitGroup, graphSize);
		resultMatrixIndexes.beginColumnIndex = matrixIndexes.beginColumnIndex;
		resultMatrixIndexes.lastColumnIndexExcluded = matrixIndexes.lastColumnIndexExcluded;
		
		return resultMatrixIndexes;
	}

}
