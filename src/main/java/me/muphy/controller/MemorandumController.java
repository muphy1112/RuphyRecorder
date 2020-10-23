package me.muphy.controller;

import me.muphy.entity.MemorandumEntity;
import me.muphy.mapper.MemorandumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/p")
public class MemorandumController {

    @Autowired
    private MemorandumMapper mapper;

    @RequestMapping("/qm")
    List<MemorandumEntity> queryMemorandum(String subject) {
        return mapper.queryMemorandum(0, 0, subject);
    }
}
