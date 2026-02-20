package com.oj.system.service;

import com.oj.common.core.domain.R;
import com.oj.system.domain.LoginDTO;
import com.oj.system.domain.SysUserSaveDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface SysUserService {
    R<String> login(@RequestBody LoginDTO loginDTO);

    int add(SysUserSaveDTO saveDTO);
}
