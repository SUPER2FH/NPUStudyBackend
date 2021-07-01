package com.hhstudygroup.npustudy.service.impl;

import com.hhstudygroup.npustudy.domain.User;
import com.hhstudygroup.npustudy.domain.UserToken;
import com.hhstudygroup.npustudy.service.UserService;
import com.hhstudygroup.npustudy.service.UserTokenService;
import com.hhstudygroup.npustudy.configuration.property.SystemConfig;
import com.hhstudygroup.npustudy.configuration.spring.cache.CacheConfig;
import com.hhstudygroup.npustudy.repository.UserTokenMapper;
import com.hhstudygroup.npustudy.utility.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class UserTokenServiceImpl extends BaseServiceImpl<UserToken> implements UserTokenService {

    private final static String CACHE_NAME = "NPUStudy:token";
    private final UserTokenMapper userTokenMapper;
    private final UserService userService;
    private final SystemConfig systemConfig;
    private final CacheConfig cacheConfig;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UserTokenServiceImpl(UserTokenMapper userTokenMapper, UserService userService, SystemConfig systemConfig, CacheConfig cacheConfig, RedisTemplate<String, Object> redisTemplate) {
        super(userTokenMapper);
        this.userTokenMapper = userTokenMapper;
        this.userService = userService;
        this.systemConfig = systemConfig;
        this.cacheConfig = cacheConfig;
        this.redisTemplate = redisTemplate;
    }


    @Override
    @Transactional
    public UserToken bind(User user) {
        user.setModifyTime(new Date());
        userService.updateByIdFilter(user);
        return insertUserToken(user);
    }


    @Override
    public UserToken checkBind(String openId) {
        User user = userService.selectByWxOpenId(openId);
        if (null != user) {
            return insertUserToken(user);
        }
        return null;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#token", unless = "#result == null")
    public UserToken getToken(String token) {
        return userTokenMapper.getToken(token);
    }

    @Override
    public UserToken insertUserToken(User user) {
        Date startTime = new Date();
        Date endTime = DateTimeUtil.addDuration(startTime, systemConfig.getWx().getTokenToLive());
        UserToken userToken = new UserToken();
        userToken.setToken(UUID.randomUUID().toString());
        userToken.setUserId(user.getId());
        userToken.setWxOpenId(user.getWxOpenId());
        userToken.setCreateTime(startTime);
        userToken.setEndTime(endTime);
        userToken.setUserName(user.getUserName());
        userService.updateByIdFilter(user);
        userTokenMapper.insertSelective(userToken);
        String key = cacheConfig.simpleKeyGenerator(CACHE_NAME, userToken.getToken());
        redisTemplate.opsForValue().set(key, userToken, systemConfig.getWx().getTokenToLive());
        return userToken;
    }

    @Override
    @Transactional
    public void unBind(UserToken userToken) {
        User user = userService.selectById(userToken.getUserId());
        user.setModifyTime(new Date());
        user.setWxOpenId(null);
        userService.updateById(user);
        userTokenMapper.deleteByPrimaryKey(userToken.getId());
        String key = cacheConfig.simpleKeyGenerator(CACHE_NAME, userToken.getToken());
        redisTemplate.delete(key);
    }

}
