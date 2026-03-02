package com.oj.system.test.domin;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;

@TableName("tb_test")
public class Test {
    private Integer testId;
    private String title;
    private String content;

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
