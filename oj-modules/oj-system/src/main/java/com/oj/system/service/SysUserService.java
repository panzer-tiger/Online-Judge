package com.oj.system.service;

import com.oj.common.core.domain.R;
import com.oj.common.core.domain.vo.LoginUserVO;
import com.oj.system.domain.sysuser.dto.LoginDTO;
import com.oj.system.domain.sysuser.dto.SysUserSaveDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface SysUserService {
    R<String> login(@RequestBody LoginDTO loginDTO);

    int add(SysUserSaveDTO saveDTO);

    R<LoginUserVO> getInfo(String token);

    boolean logout(String token);
}
