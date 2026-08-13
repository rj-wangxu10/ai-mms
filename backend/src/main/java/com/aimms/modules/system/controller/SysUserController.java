package com.aimms.modules.system.controller;

import com.aimms.common.R;
import com.aimms.modules.system.entity.SysUser;
import com.aimms.modules.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping
    public R<List<SysUser>> list(@RequestParam(required = false) Integer deptId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (deptId != null) {
            wrapper.eq(SysUser::getDeptId, deptId);
        }
        wrapper.orderByAsc(SysUser::getId);
        return R.ok(sysUserService.list(wrapper));
    }

    @GetMapping("/{id}")
    public R<SysUser> getById(@PathVariable Integer id) {
        return R.ok(sysUserService.getById(id));
    }

    @PostMapping
    public R<Void> save(@RequestBody SysUser user) {
        sysUserService.save(user);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Integer id, @RequestBody SysUser user) {
        user.setId(id);
        sysUserService.updateById(user);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        sysUserService.removeById(id);
        return R.ok();
    }
}
