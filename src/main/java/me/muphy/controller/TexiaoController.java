package me.muphy.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/")
public class TexiaoController {

    @RequestMapping(value = "/texiao")
    public void texiao(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://47.106.139.21:7002/texiao");
    }

}
