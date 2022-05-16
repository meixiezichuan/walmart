package com.walmart_6g.walmartUserService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart_6g.walmartUserService.entity.RoleAccess;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;
@Mapper
@Repository
public interface RoleAccessMapper extends BaseMapper<RoleAccess> {
}
