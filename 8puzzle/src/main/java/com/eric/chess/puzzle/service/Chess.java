/**
 * Copyright (c) 2019 Versedeco All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Versedeco.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Versedeco.
 *
 * VERSEDECO MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. VERSEDECO SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package com.eric.chess.puzzle.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import org.springframework.stereotype.Component;

@Component
public class Chess {

	public static final int BLANK = 9;
	public static final int DEFAULT_SHUFFLE_ITERATION = 2000;
	
	public static final int GO_LEFT = 0b0001;
	public static final int GO_RIGHT = 0b0010;
	public static final int GO_UP = 0b0100;
	public static final int GO_DOWN = 0b1000;
	
	private static final int[] AVAIL_MOVES = new int[]{0b1010, 0b1011, 0b1001, 0b1110, 0b1111, 0b1101, 0b0110, 0b0111, 0b0101};
	
	private int[] cells = new int[]{1,2,3,4,5,6,7,8,9};
	private int positionOfBlank = 8;
		
	public static final int[] STANDARD_ROUTE = new int[] {GO_LEFT,GO_LEFT,GO_UP,GO_RIGHT,GO_RIGHT,GO_UP,GO_LEFT,GO_LEFT};
	
	public Chess() {
		
	}
	
	public Chess(int[] cells) {
		setCells(cells);
	}
	
	
	
	private static final Random RND = new Random();
	private static int getRandomMove(int position) {
		int pointer = RND.nextInt(1000)%4;
		int direction = AVAIL_MOVES[position] & (0b0001 << pointer);
		if(direction > 0) {
			return direction;
		}
		pointer = ((pointer & 0b0001) > 0) ? pointer -1 : pointer + 1;
		return AVAIL_MOVES[position] & (0b0001 << pointer);
	}
	
	private void swap(int d1, int d2) {
		int d = cells[d2];
		cells[d2] = cells[d1];
		cells[d1] = d;
	}
	public void move(int direction) {
//		System.out.println("Direction:" + direction);
		switch(direction) {
		case GO_LEFT:
//			System.out.println("<--");
			swap(positionOfBlank, positionOfBlank - 1);
			positionOfBlank -= 1;
			return;
		case GO_RIGHT:
//			System.out.println("-->");
			swap(positionOfBlank, positionOfBlank + 1);
			positionOfBlank += 1;
			return;
		case GO_UP:
//			System.out.println("^^^");
			swap(positionOfBlank, positionOfBlank - 3);
			positionOfBlank -= 3;
			return;
		case GO_DOWN:
//			System.out.println("vvv");
			swap(positionOfBlank, positionOfBlank + 3);
			positionOfBlank += 3;
			return;
		}
		throw new RuntimeException("Unsupported Movement");
	}
	
	public int ivernum(int[] cells) {
		int ivernum = 0;
		for(int i = 0; i < cells.length; i++) {
			for(int j = i; j < cells.length; j++) {
				if(cells[i] > cells[j]) ivernum++;
			}
		}
		return ivernum;
	}
	
	public int[] shuffle(int[] cells) {
		Random rdm = new Random();
		cells[cells.length - 1] = 9;
		for(int i = cells.length - 2; i >= 0; i--) {
			int rnd = rdm.nextInt(i + 1);
			int tmp = cells[rnd];
			cells[rnd] = cells[i];
			cells[i] = tmp;
		}
		
		if(ivernum(cells) % 2 == 0) {
			return cells;
		}
		else {
			shuffle(cells);
			return cells;
		}
		
	}
	
	public void shuffleChess(int[] cells) {
		Random rdn = new Random();
		int rnd = rdn.nextInt(STANDARD_ROUTE.length);
		for(int i = 0; i <= rnd; i++) {
			move(STANDARD_ROUTE[i]);
		}
	}
	
//	public void shuffle(int iteration) {
//		for(int i=0; i< iteration; i++) {
//			int d = getRandomMove(positionOfBlank);
//			move(d);
//		}
//	}
//	
//	public void shuffle() {
//		shuffle(DEFAULT_SHUFFLE_ITERATION);
//	}
		
	private void setCells(int[] cells) {
		for(int i=0; i< cells.length; i++) {
			this.cells[i] = cells[i];
			if(this.cells[i] == BLANK) {
				positionOfBlank = i;
			}
		}
	}
	
	public int[] getCells() {
		return cells;
	}
	
	public class MoveStateBase {
		//current cell state
		public int[] cells;
		public int positionOfBlank;
		public int direction;
		
		public String strState;
		
		protected void setCells(int[] cells) {
			this.cells = cells;
			setStrStateAndBlankPosition();
		}
		
		public int[] getCells() {
			return cells;
		}
		
		public MoveStateBase(int[] cells, int direction) {
			setCells(cells);
			this.direction = direction;
		}
		
		public MoveStateBase cloneState() {
			MoveStateBase state = new MoveStateBase(cells.clone(), direction);
			return state;
		}
		
		private void setStrStateAndBlankPosition() {
			positionOfBlank = -1;
			StringBuffer sb = new StringBuffer();
			for(int i=0; i< cells.length; i++) {
				sb.append("," + cells[i]);
				if(cells[i] == BLANK) {
					positionOfBlank = i;
				}
			}
			strState = sb.substring(1);
		}
		
		private void swap(int d1, int d2) {
			int d = cells[d2];
			cells[d2] = cells[d1];
			cells[d1] = d;
		}
		
		public void move(int direction) {
			switch(direction) {
			case GO_LEFT:
				swap(positionOfBlank, positionOfBlank - 1);
				break;
			case GO_RIGHT:
				swap(positionOfBlank, positionOfBlank + 1);
				break;
			case GO_UP:
				swap(positionOfBlank, positionOfBlank - 3);
				break;
			case GO_DOWN:
				swap(positionOfBlank, positionOfBlank + 3);
				break;
			}
			setStrStateAndBlankPosition();
		}
		
		public boolean solved() {
			for(int i=0; i< cells.length; i++) {
				if(cells[i] != (i+1))
					return false;
			}
			return true;
		}
	}
	
	public class MoveState extends MoveStateBase{
		public static final int ALGORITHM_NORMAL = 1;
		public static final int ALGORITHM_UPDATED = 2;
		
		//left available moves
		public int availMoves;
		public int algorithm;
		
		//BFS
		public MoveState parent;
		
		public MoveState(int[] cells, int direction) {
			this(cells, direction, ALGORITHM_UPDATED);
		}
		
		public MoveState(int[] cells, int direction, int algorithm) {
			super(cells, direction);
			this.availMoves = AVAIL_MOVES[positionOfBlank];
			this.algorithm = algorithm;
		}
		
		//BFS
		public List<MoveState> getChildrenState(){
			List<MoveState> list = new ArrayList<>();
			for(int i=0; i< 4; i++) {
				int tmpd = 0b0001 << i;
				if((tmpd & AVAIL_MOVES[positionOfBlank]) > 0) {
					MoveStateBase baseState = this.cloneState();
					baseState.move(tmpd);
					MoveState s = new MoveState(baseState.cells, tmpd, this.algorithm);
					s.parent = this;
					list.add(s);
				}
			}
			return list;
		}
		
		//DFS
		public MoveState getNextTry() {
			if(availMoves == 0) {
				return null;
			}
			MoveStateBase state = this.cloneState();
			int direction = -1;
			switch(algorithm) {
			case ALGORITHM_NORMAL:
				direction = 0b0001;
				for(int i=0; i< 4; i++) {
					if(((direction << i) & availMoves) > 0) {
						direction = direction << i;
						break;
					}
				}
				state.move(direction);
				break;
			case ALGORITHM_UPDATED:
				int value = Integer.MAX_VALUE;
				for(int i=0; i<4; i++) {
					int tmpd = 0b0001 << i;
					if((tmpd & availMoves) > 0) {
						MoveStateBase tmpState = this.cloneState();
						tmpState.move(tmpd);
						int v = getValueFactorOfMove(tmpState.cells, tmpd);
						if(v < value) {
							value = v;
							direction = tmpd;
							state = tmpState;
						}
					}
				}
				break;
			}
			availMoves = availMoves & (direction ^ 0b1111);
			return new MoveState(state.cells, direction, algorithm);
		}
		
		private int getValueFactorOfMove(int[] cells, int direction) {
			int count = 0;
			for(int i=0; i< cells.length; i++) {
				count += Math.abs(cells[i] - i - 1);
			}
			return count;
		}
	}
	
	public class MoveStateStar extends MoveStateBase{
		//current step (hFactor)
		public int steps;
		//g factor
		public int gFactor;
		
		public MoveStateStar parent;
		
		public MoveStateStar(int[] cells, int direction, int steps, MoveStateStar parent) {
			super(cells, direction);
			this.steps = steps;
			this.parent = parent;
			gFactor = countFactor();
		}
		
		public int getSteps() {
			return steps;
		}
		
		//g factor = unmatched count
		private int countFactor() {
			int count = 0;
			for(int i=0; i< cells.length; i++) {
				if(cells[i] != i+1) {
					count++;
				}
			}
			return count;
		}
		
		public int getFactor() {
			return steps + gFactor;
		}
		
		public List<MoveStateStar> getChildrenState(){
			List<MoveStateStar> list = new ArrayList<>();
			for(int i=0; i< 4; i++) {
				int tmpd = 0b0001 << i;
				if((tmpd & AVAIL_MOVES[positionOfBlank]) > 0) {
					MoveStateBase baseState = this.cloneState();
					baseState.move(tmpd);
					MoveStateStar s = new MoveStateStar(baseState.cells, tmpd, steps+1, this);
					list.add(s);
				}
			}
			return list;
		}
	}
	
	private MoveStateStar findLowestFactorState(Map<String, MoveStateStar> set) {
		if(set == null || set.isEmpty()) return null;
		int factor = Integer.MAX_VALUE;
		MoveStateStar s = null;
		for(MoveStateStar mss: set.values()) {
			if(mss.getFactor() < factor) {
				s = mss;
				factor = mss.getFactor();
			}
		}
		return s;
	}
	
	//DFS
	public void solveDFS(int algorithm) {
		long startTime = System.currentTimeMillis();
		Deque<MoveState> statesDeque = new ArrayDeque<>(); 
		Set<String> stateHistory = new HashSet<String>();
		
		MoveState initState = new MoveState(cells, -1, algorithm);
		statesDeque.add(initState);
		stateHistory.add(initState.strState);
		MoveState workState = initState;
		while(true) {
			if(workState.solved()) {
				System.out.println("Solved");
				break;
			}
			MoveState nextState = workState.getNextTry();
			if(nextState == null) {
				//rollback to previous step
				workState = statesDeque.removeLast();
			}else if(stateHistory.contains(nextState.strState)){
				//this try is existed, need another try
				//do nothing
			} else {
				//add new step
				statesDeque.addLast(nextState);
				stateHistory.add(nextState.strState);
				workState = nextState;
			}
		}
		
//		System.out.println("Move Steps:" + statesDeque.size());
//		Iterator<MoveState> it = statesDeque.iterator();
//		while(it.hasNext()) {
//			MoveState state = it.next();
//			Chess c = new Chess(state.cells);
//			System.out.println(c);
//			System.out.println("========= " + displayDirection(state.direction) + " ============");
//		}
//		System.out.println(this);
		System.out.println("Move Steps:" + statesDeque.size());
		System.out.println("History States Number:" + stateHistory.size());
		System.out.println("Used:" + (System.currentTimeMillis() - startTime) + "ms");
	}
	
	//BFS
	public void solveBFS() {
		long startTime = System.currentTimeMillis();
		Deque<MoveState> statesDeque = new ArrayDeque<>();
		Set<String> stateHistory = new HashSet<String>();
		MoveState initState = new MoveState(cells, -1);
		statesDeque.addLast(initState);
		stateHistory.add(initState.strState);
		MoveState solved = null;
		while(solved == null && (!statesDeque.isEmpty())) {
			MoveState workState = statesDeque.removeFirst();
			List<MoveState> states = workState.getChildrenState();
			Iterator<MoveState> it = states.iterator();
			while(it.hasNext()) {
				MoveState s = it.next();
				if(s.solved()) {
					solved = s;
					break;
				}else if(stateHistory.contains(s.strState)) {
					//drop it
					//do nothing
				}else {
					//System.out.println("Add History State:" + workState.strState);
					statesDeque.add(s);
					stateHistory.add(s.strState);
				}
			}
		}
		if(solved != null) {
			System.out.println("Solved");
			
			Stack<MoveState> stack = new Stack<MoveState>();
			stack.push(solved);
			while(solved.parent != null) {
				solved = solved.parent;
				stack.push(solved);
			}
			int steps = stack.size() -1;
//			while(!stack.empty()) {
//				MoveState state = stack.pop();
//				Chess c = new Chess(state.cells);
//				
//				System.out.println("========= " + displayDirection(state.direction) + " ============");
//				System.out.println(c);
//			}
			System.out.println("Move Steps:" + steps);
			System.out.println("Used:" + (System.currentTimeMillis() - startTime) + "ms");
		} else {
			System.out.println("Cannot find an answer.");
		}
	}
	
	public List<int[]> solveAStar() {
		long startTime = System.currentTimeMillis();
		Map<String, MoveStateStar> openSet = new HashMap<String, MoveStateStar>();
		Map<String, MoveStateStar> closeSet = new HashMap<String, MoveStateStar>();
		MoveStateStar initState = new MoveStateStar(cells, -1, 0, null);
		MoveStateStar workState = initState;
		MoveStateStar solved = null;
		while(solved == null) {
			for(MoveStateStar s: workState.getChildrenState()) {
				if(s.solved()) {
					solved = s;
					break;
				}
				if(openSet.containsKey(s.strState)) {
					MoveStateStar os = openSet.get(s.strState);
					if(os.getFactor() > s.getFactor()) {
						openSet.put(s.strState, s);
					}
				}else if(closeSet.containsKey(s.strState)) {
					MoveStateStar cs = closeSet.get(s.strState);
					if(cs.getFactor() > s.getFactor()) {
						closeSet.remove(s.strState);
						openSet.put(s.strState, s);
					}
				}else {
					//add to open set
					openSet.put(s.strState, s);
				}
			}
			workState = findLowestFactorState(openSet);
			if(workState == null) {
				throw new RuntimeException("Cannot solve it.");
			}
			//move workState to closeSet
			openSet.remove(workState.strState);
			closeSet.put(workState.strState, workState);
		}
		System.out.println("Solved");
		Stack<MoveStateStar> stack = new Stack<MoveStateStar>();
		stack.push(solved);
		while(solved.parent != null) {
			solved = solved.parent;
			stack.push(solved);
		}
		int steps = stack.size() -1;
//		while(!stack.empty()) {
//			MoveStateStar state = stack.pop();
//			Chess c = new Chess(state.cells);
//			
//			System.out.println("========= " + displayDirection(state.direction) + " ============");
//			System.out.println(c);
//		}
		List<int[]> result = new ArrayList<int[]>();
		for(int i = 0; i < stack.size(); i++) {
			result.add(stack.get(i).getCells());
		}
		
		System.out.println("Move Steps:" + steps);
		System.out.println("OpenSet Remains:" + openSet.size());
		System.out.println("CloseSet Size:" + closeSet.size());
		System.out.println("Used:" + (System.currentTimeMillis() - startTime) + "ms");
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i< cells.length; i++) {
			sb.append((cells[i] == BLANK ? " " : cells[i]) + "\t");
			if(i%3 == 2) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	public void reset() {
		cells = new int[]{1,2,3,4,5,6,7,8,9};
		positionOfBlank = 8;
	}
	
	public static void main(String[] args) {
//		Chess chess = new Chess();
//		chess.shuffle();
//		System.out.println("Shffle Finished:");
//		System.out.println(chess);
////		System.out.println("====DFS====");
////		chess.solveDFS(MoveState.ALGORITHM_UPDATED);
////		System.out.println("====BFS====");
////		chess.solveBFS();
//		System.out.println("====ASTAR====");
//		chess.solveAStar();
		Chess chess = new Chess();
		int[] c = new int[9];
		c = chess.shuffle(chess.getCells());
		int num = chess.ivernum(c);
		System.out.println(num + "\n");
		for(int i = 0; i < c.length; i++) {
			System.out.println(c[i]);
		}

	}
}
