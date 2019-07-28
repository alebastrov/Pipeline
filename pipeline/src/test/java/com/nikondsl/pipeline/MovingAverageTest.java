package com.nikondsl.pipeline;

import com.nikondsl.streamreader.util.MovingAverage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MovingAverageTest {
	private MovingAverage movingAverage;
	
	@Before
	public void setUp() {
		movingAverage = new MovingAverage(3);
	}
	
	@Test
	public void empty() {
		assertEquals(0.0, movingAverage.next(0.0), 0.0000001);
	}
	
	@Test
	public void emptyTripple() {
		movingAverage.next(0.0);
		movingAverage.next(0.0);
		assertEquals(0.0, movingAverage.next(0.0), 0.0000001);
	}
	
	@Test
	public void singleNumber() {
		assertEquals(1.5, movingAverage.next(1.5), 0.0000001);
	}
	
	@Test
	public void doubleNumbers() {
		movingAverage.next(3.0);
		assertEquals(2.0, movingAverage.next(1.0), 0.0000001);
	}
	
	@Test
	public void trippleNumbers() {
		movingAverage.next(3.0);
		movingAverage.next(0.0);
		assertEquals(1.333333, movingAverage.next(1.0), 0.000001);
	}
	
	@Test
	public void fourNumbers() {
		movingAverage.next(3.0);
		movingAverage.next(1.0);
		movingAverage.next(1.0);
		assertEquals(1.333333, movingAverage.next(2.0), 0.000001);
	}
	
	@Test
	public void fiveNumbers() {
		movingAverage.next(3.0);
		movingAverage.next(4.0);
		movingAverage.next(1.0);
		movingAverage.next(2.0);
		assertEquals(1.833333, movingAverage.next(2.5), 0.000001);
	}
}
