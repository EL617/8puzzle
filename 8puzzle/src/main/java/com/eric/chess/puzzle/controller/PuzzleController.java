package com.eric.chess.puzzle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PuzzleController {
 
    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }
 
}
