package edu.ucsb.cs290cloud.ramseychecker;

public class CliqueCounter {
	
	private static final int SUBCLIQUE_SIZE = 7;
	
	private int[][] graph;
	private int graphSize = 0;
	
	public CliqueCounter(int[][] graph) {
		// row number: graph.length | column number: graph[0].length
		
		if (graph.length < this.SUBCLIQUE_SIZE) {
			System.err.println("The graph has " + graph.length + 
					" rows and should have at least " + this.SUBCLIQUE_SIZE + 
					" rows to look for monochromatic subcliques");
			return;
		}
		if (graph[0].length < this.SUBCLIQUE_SIZE) {
			System.err.println("The graph has " + graph[0].length + 
					" columns and should have at least " + this.SUBCLIQUE_SIZE + 
					" columns to look for monochromatic subcliques");
			return;
		}
		
		this.graph = graph;
		this.graphSize = graph.length;
	}
	
	/*
	 * The graph is fully connected and we check only values above the diagonal.
	 */
	public int getMonochromaticSubcliquesCount() {
		int i, j, k, l, m, n, o;
		int count = 0;
		
		for (i = 0; i < this.graphSize - SUBCLIQUE_SIZE + 1; i++) {
			for (j = i+1; j < this.graphSize - SUBCLIQUE_SIZE + 2; j++) {
				for (k = j+1; k < this.graphSize - SUBCLIQUE_SIZE + 3; k++) {
					if (this.graph[i][j] == this.graph[i][k] &&
						this.graph[i][j] == this.graph[j][k]) {
						
						for(l = k+1; l < this.graphSize - SUBCLIQUE_SIZE + 4; l++) {
							if (this.graph[i][j] == this.graph[i][l] &&
								this.graph[i][j] == this.graph[j][l] &&
								this.graph[i][j] == this.graph[k][l]) {
								
								for(m = l+1; m < this.graphSize - SUBCLIQUE_SIZE + 5; m++) {
									if (this.graph[i][j] == this.graph[i][m] &&
										this.graph[i][j] == this.graph[j][m] && 
										this.graph[i][j] == this.graph[k][m] &&
										this.graph[i][j] == this.graph[l][m]) {
										
										for(n = m+1; n < this.graphSize - SUBCLIQUE_SIZE + 6; n++) {
											if (this.graph[i][j] == this.graph[i][n] &&
												this.graph[i][j] == this.graph[j][n] &&
												this.graph[i][j] == this.graph[k][n] &&
												this.graph[i][j] == this.graph[l][n] &&
												this.graph[i][j] == this.graph[m][n]) {
												
												for(o = n+1; o < this.graphSize - SUBCLIQUE_SIZE + 7; o++) {
													if (this.graph[i][j] == this.graph[i][o] &&
														this.graph[i][j] == this.graph[j][o] &&
														this.graph[i][j] == this.graph[k][o] &&
														this.graph[i][j] == this.graph[l][o] &&
														this.graph[i][j] == this.graph[m][o] &&
														this.graph[i][j] == this.graph[n][o]) {
														
														count++;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return count;
	}

	public int getMonochromaticSubcliquesCountWithTerminate(int terminateAt) {
		int i, j, k, l, m, n, o;
		int count = 0;

		for (i = 0; i < this.graphSize - SUBCLIQUE_SIZE + 1; i++) {
			for (j = i+1; j < this.graphSize - SUBCLIQUE_SIZE + 2; j++) {
				for (k = j+1; k < this.graphSize - SUBCLIQUE_SIZE + 3; k++) {
					if (this.graph[i][j] == this.graph[i][k] &&
							this.graph[i][j] == this.graph[j][k]) {

						for(l = k+1; l < this.graphSize - SUBCLIQUE_SIZE + 4; l++) {
							if (this.graph[i][j] == this.graph[i][l] &&
									this.graph[i][j] == this.graph[j][l] &&
									this.graph[i][j] == this.graph[k][l]) {

								for(m = l+1; m < this.graphSize - SUBCLIQUE_SIZE + 5; m++) {
									if (this.graph[i][j] == this.graph[i][m] &&
											this.graph[i][j] == this.graph[j][m] &&
											this.graph[i][j] == this.graph[k][m] &&
											this.graph[i][j] == this.graph[l][m]) {

										for(n = m+1; n < this.graphSize - SUBCLIQUE_SIZE + 6; n++) {
											if (this.graph[i][j] == this.graph[i][n] &&
													this.graph[i][j] == this.graph[j][n] &&
													this.graph[i][j] == this.graph[k][n] &&
													this.graph[i][j] == this.graph[l][n] &&
													this.graph[i][j] == this.graph[m][n]) {

												for(o = n+1; o < this.graphSize - SUBCLIQUE_SIZE + 7; o++) {
													if (this.graph[i][j] == this.graph[i][o] &&
															this.graph[i][j] == this.graph[j][o] &&
															this.graph[i][j] == this.graph[k][o] &&
															this.graph[i][j] == this.graph[l][o] &&
															this.graph[i][j] == this.graph[m][o] &&
															this.graph[i][j] == this.graph[n][o]) {

														count++;
														if(count >= terminateAt)
														{
															return count;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return count;
	}
}
