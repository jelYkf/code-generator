package com.rune;

import com.rune.mapper.FileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author sedate
 * @date 2023/8/11 14:22
 * @description
 */
@SpringBootTest
public class ApplicationTest {

    @Resource
    FileMapper fileMapper;

    @Test
    public void clean() {

    }

}
