package edu.ucsb.cs290cloud.strategies;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucsb.cs290cloud.strategies.Strategy1Distributed.MatrixIndexes;

public class Strategy1DistributedTest {

	@Test
	public void testGetRowIndexes() {
		Strategy1Distributed s;
		MatrixIndexes matrixIndexes;
		
		s = new Strategy1Distributed(4, 2);
		matrixIndexes = s.getRowIndexes(1, 80);
		assertEquals(0, matrixIndexes.beginRowIndex);
		assertEquals(20, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(2, 80);
		assertEquals(20, matrixIndexes.beginRowIndex);
		assertEquals(40, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(3, 80);
		assertEquals(40, matrixIndexes.beginRowIndex);
		assertEquals(60, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(4, 80);
		assertEquals(60, matrixIndexes.beginRowIndex);
		assertEquals(80, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(4, 81);
		assertEquals(60, matrixIndexes.beginRowIndex);
		assertEquals(81, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(4, 82);
		assertEquals(60, matrixIndexes.beginRowIndex);
		assertEquals(82, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(4, 83);
		assertEquals(60, matrixIndexes.beginRowIndex);
		assertEquals(83, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(1, 84);
		assertEquals(0, matrixIndexes.beginRowIndex);
		assertEquals(21, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(2, 84);
		assertEquals(21, matrixIndexes.beginRowIndex);
		assertEquals(42, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(3, 84);
		assertEquals(42, matrixIndexes.beginRowIndex);
		assertEquals(63, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(4, 84);
		assertEquals(63, matrixIndexes.beginRowIndex);
		assertEquals(84, matrixIndexes.lastRowIndexExcluded);
		
		s = new Strategy1Distributed(17, 3);
		
		matrixIndexes = s.getRowIndexes(1, 120);
		assertEquals(0, matrixIndexes.beginRowIndex);
		assertEquals(7, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(2, 120);
		assertEquals(7, matrixIndexes.beginRowIndex);
		assertEquals(14, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(3, 120);
		assertEquals(14, matrixIndexes.beginRowIndex);
		assertEquals(21, matrixIndexes.lastRowIndexExcluded);
		
		matrixIndexes = s.getRowIndexes(17, 120);
		assertEquals(112, matrixIndexes.beginRowIndex);
		assertEquals(120, matrixIndexes.lastRowIndexExcluded);
	}
	
	@Test
	public void testGetColumnIndexes() {
		Strategy1Distributed s;
		MatrixIndexes matrixIndexes;
		
		s = new Strategy1Distributed(4, 1);
		
		matrixIndexes = s.getColumnIndexes(1, 80);
		assertEquals(0, matrixIndexes.beginColumnIndex);
		assertEquals(20, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(2, 80);
		assertEquals(60, matrixIndexes.beginColumnIndex);
		assertEquals(80, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(3, 80);
		assertEquals(0, matrixIndexes.beginColumnIndex);
		assertEquals(20, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(4, 80);
		assertEquals(60, matrixIndexes.beginColumnIndex);
		assertEquals(80, matrixIndexes.lastColumnIndexExcluded);
		
		s = new Strategy1Distributed(4, 2);
		
		matrixIndexes = s.getColumnIndexes(1, 80);
		assertEquals(20, matrixIndexes.beginColumnIndex);
		assertEquals(40, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(2, 80);
		assertEquals(40, matrixIndexes.beginColumnIndex);
		assertEquals(60, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(3, 80);
		assertEquals(20, matrixIndexes.beginColumnIndex);
		assertEquals(40, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(4, 80);
		assertEquals(40, matrixIndexes.beginColumnIndex);
		assertEquals(60, matrixIndexes.lastColumnIndexExcluded);
		
		s = new Strategy1Distributed(4, 4);
		
		matrixIndexes = s.getColumnIndexes(1, 80);
		assertEquals(60, matrixIndexes.beginColumnIndex);
		assertEquals(80, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(2, 80);
		assertEquals(0, matrixIndexes.beginColumnIndex);
		assertEquals(20, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(1, 81);
		assertEquals(60, matrixIndexes.beginColumnIndex);
		assertEquals(81, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(2, 81);
		assertEquals(0, matrixIndexes.beginColumnIndex);
		assertEquals(20, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(3, 81);
		assertEquals(60, matrixIndexes.beginColumnIndex);
		assertEquals(81, matrixIndexes.lastColumnIndexExcluded);
		
		matrixIndexes = s.getColumnIndexes(4, 81);
		assertEquals(0, matrixIndexes.beginColumnIndex);
		assertEquals(20, matrixIndexes.lastColumnIndexExcluded);
	}

}
