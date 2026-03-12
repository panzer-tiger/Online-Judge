package com.oj.judge.service;

import com.oj.judge.domain.SandBoxExecuteResult;

import java.util.List;

public interface SandboxPoolService {
    SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList);
}
