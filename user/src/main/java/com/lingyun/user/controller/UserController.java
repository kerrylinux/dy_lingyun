package com.lingyun.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.lingyun.user.entity.UserEntity;
import com.lingyun.user.service.UserService;
import com.lingyun.user.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户管理",tags = {"用户管理"},produces = MediaType.APPLICATION_JSON_VALUE)

@RestController
@RequestMapping("user")
public class UserController {
   Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    /**
     * 查询
     * @return
     */
    @ApiResponses({ @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 500, message = "服务器内部异常"),
            @ApiResponse(code = 401, message = "权限不足") })
    @RequestMapping(value = "/",method = RequestMethod.GET)
    @ApiOperation(value = "用户数据列表分页", notes = "用户管理", tags = "用户管理")
    public ResponseEntity<Page<UserVo>> list(String  phoneNum, Long roleId, Integer pageNum, Integer pageSize) {
        if(null==pageSize ||pageSize ==0){
            pageSize=20;
        }
        if(null==pageNum || pageNum==0){
            pageNum=1;
        }
        if(StringUtils.isEmpty(phoneNum)){
            phoneNum=null;
        }

        Pageable pageable= new PageRequest(pageNum,pageSize);
        logger.info("开始用户数据列表");
        Page<UserVo> list = this.userService.findallByRoleIdAdIphone(phoneNum,roleId,pageable);
        logger.info("用户数据列表结束");

        return ResponseEntity.ok(list);
    }

    /**
     * 保存用户
     * @return
     */
    @ApiResponses({ @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 500, message = "服务器内部异常"),
            @ApiResponse(code = 401, message = "权限不足") })
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "添加/编辑用户", notes = "用户管理", tags = "用户管理")
    public ResponseEntity<String> saveUseer(UserEntity userEntity){
        logger.info("开始添加用户");
        UserEntity user= userService.save(userEntity);
        if(null!=user || user.getId()!=null){
            logger.info("结束添加用户");
            return  ResponseEntity.status(201).body("操作成功");
        }
        logger.info("结束添加用户");
        return ResponseEntity.ok("操作失败");
    }
    @RequestMapping(value = "/name",method = RequestMethod.GET)
    @ApiOperation(value = "查找用户", notes = "通过用户名查找用户", tags = "用户管理")
    public ResponseEntity<UserEntity> findByName(String username){
        logger.info("这里是用户中心,获取userName: "+username);
        UserEntity userEntity=userService.findOneByName(username);
        return ResponseEntity.ok(userEntity);
    }
    @RequestMapping(value = "/one/{userId}",method = RequestMethod.GET)
    @ApiOperation(value = "通过id查找用户", notes = "通过id查找用户", tags = "用户管理")
    public ResponseEntity<UserEntity> getUser(@PathVariable("userId") Integer userId){
        if(null==userId){
               return ResponseEntity.ok(null);
        }
        UserEntity userEntity=userService.getOne(Long.valueOf(userId));
        return ResponseEntity.ok(userEntity);
    }
    @RequestMapping(value = "validata",method = RequestMethod.POST)
    public ResponseEntity<JSONObject> validataUser(String userName, String passWord){
        logger.info("开始验证登录="+userName+"------"+passWord);
        ResponseEntity<JSONObject> userEntity=userService.findByUserNameAndPassowrd(userName,passWord);

        return userEntity;
    }

}
