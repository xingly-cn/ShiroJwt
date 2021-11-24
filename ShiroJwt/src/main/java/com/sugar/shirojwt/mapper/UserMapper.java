package com.sugar.shirojwt.mapper;

import com.sugar.shirojwt.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author sugar
 * @since 2021-11-24
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
