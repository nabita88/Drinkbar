package com.example.cafeis.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class MenuOrderController {

    @GetMapping("/coffee_order")
    public String coffeeOrder(@RequestParam(required = false) String branchNo, HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            return "redirect:/coffee-order-static.html?" + queryString;
        }
        return "redirect:/coffee-order-static.html";
    }
}

