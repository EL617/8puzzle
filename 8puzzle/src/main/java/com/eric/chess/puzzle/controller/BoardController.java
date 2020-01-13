package com.eric.chess.puzzle.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eric.chess.puzzle.model.Account;
import com.eric.chess.puzzle.service.Chess;


@RestController
public class BoardController {

	@Autowired 
	private Chess chess;
	
	@RequestMapping(value = "/hello/shuffle", method = RequestMethod.POST)
	@ResponseBody
	public List<Integer> shuffle() {
		List<Integer> clist = new ArrayList<Integer>();
		int[] cells = new int[]{1,2,3,4,5,6,7,8,9};
		chess.shuffle(cells);
		Chess c = new Chess(cells);
		c.shuffleChess(cells);
		for(int i = 0; i < c.getCells().length; i++) {
			clist.add(c.getCells()[i]);
		}
		return clist;
	}
	
	@RequestMapping(value = "/hello/solve", method = RequestMethod.POST)
	@ResponseBody
	public List<int[]> solve(@RequestBody List<Integer> cells){
		int[] ret = new int[cells.size()];
		for(int i = 0;i < ret.length;i++)
		  ret[i] = cells.get(i);
		Chess chess = new Chess(ret);
		List<int[]> result = new ArrayList<int[]>();
		
		result = chess.solveAStar();

		List<int[]> rtn = result.subList(0, result.size());
		Collections.reverse(rtn);
//		for(int j = 0; j < result.get(result.size() - 2).length; j++) {
//			rtn.add(result.get(result.size() - 2)[j]);
//		}		
		return rtn;
	}
	
	@RequestMapping(value = "/hello/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String loginIn(HttpSession session, Account account) {
		//HttpSession session = request.getSession();
		//String user = request.getParameter("name");
		//String steps = request.getParameter("steps");
		if(account.getUserName() == null) {
			return "false";
		}
		session.setAttribute("LoginName", account.getUserName());
		session.setAttribute("Steps", account.getStep());
		session.setAttribute("Account", account);
		return "success";
		
	}
}
