package com.example.cafeis.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class CongestionController {


    @GetMapping("/congestion")
    public String congestionPage() {
        return "congestion";
    }


    @GetMapping("/congestion/home")
    public String congestionHomePage() {
        return "congestion";
    }


}
