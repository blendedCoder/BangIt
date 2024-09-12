package com.bangIt.blended.controller.user;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bangIt.blended.service.user.GptService;

@RestController  // Removed @Controller
@RequiredArgsConstructor
public class GptController {

    private final GptService gptService;

    // 질문을 받아 GPT API에 요청을 보내고 응답을 반환하는 엔드포인트
    @PostMapping("/ask")
    @ResponseBody
    public String askGpt(@RequestBody String question) {
        return gptService.getGptResponse(question);
    }
}