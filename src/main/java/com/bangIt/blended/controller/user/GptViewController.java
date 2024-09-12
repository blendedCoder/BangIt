package com.bangIt.blended.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GptViewController {

    @GetMapping("/gptApi")
    public String GptApi() {
        return "views/user/gpt/gpt";
    }
}