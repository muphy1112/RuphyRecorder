package me.muphy.controller;

import me.muphy.entity.MemorandumEntity;
import me.muphy.mapper.MemorandumMapper;
import me.muphy.servicce.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/memorandum")
public class MemorandumController {

    @Autowired
    private MemorandumMapper mapper;

    @Autowired
    private FileService fileService;

    @RequestMapping("/query")
    List<MemorandumEntity> queryMemorandum(String s) {
        return mapper.queryMemorandum(0, 0, s);
    }

    @RequestMapping("/save")
    public int saveMemorandum(MemorandumEntity entity) {
        return mapper.saveMemorandum(entity);
    }

    @RequestMapping("/xss")
    public void saveXss(String s) throws IOException {
        MemorandumEntity entity = new MemorandumEntity();
        entity.setSubject("xss");
        entity.setContent(s);
        mapper.saveMemorandum(entity);
        fileService.exportFile("/payload/user_icon.jpg");
    }

}
