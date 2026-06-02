package com.example.booksocial_frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaviconController {

  @GetMapping("/favicon.ico")
  public String favicon() {
    return "redirect:/images/booksocial-icon.svg";
  }
}
