package com.oj.system.service;

import com.oj.common.core.domain.R;
import com.oj.system.controller.LoginResult;
import com.oj.system.domain.LoginDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface SysUserService {
    R<Void> login(@RequestBody LoginDTO loginDTO);
}
