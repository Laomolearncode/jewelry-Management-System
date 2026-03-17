package com.jewelry.pims.mapper;

import com.jewelry.pims.domain.SystemEntities;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SystemMapper {

    @Select("select id, username, password, real_name, phone, status, created_at, updated_at from sys_user where username = #{username} limit 1")
    SystemEntities.User findUserByUsername(String username);

    @Select("select id, username, password, real_name, phone, status, created_at, updated_at from sys_user where id = #{id}")
    SystemEntities.User findUserById(Long id);

    @Select("select count(1) from sys_user where username = #{username}")
    int countUserByUsername(String username);

    @Insert("insert into sys_user(username, password, real_name, phone, status, created_at, updated_at) values(#{username}, #{password}, #{realName}, #{phone}, #{status}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(SystemEntities.User user);

    @Update("update sys_user set status = #{status}, updated_at = now() where id = #{id}")
    int updateUserStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("select id, username, real_name, phone, status, created_at, updated_at from sys_user order by id desc")
    List<SystemEntities.User> listUsers();

    @Select("select id, role_code, role_name, description, status from sys_role where status = 1 order by id")
    List<SystemEntities.Role> listRoles();

    @Select("select id, permission_code, permission_name, description from sys_permission order by id")
    List<SystemEntities.Permission> listPermissions();

    @Insert("insert into sys_user_role(user_id, role_id) values(#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Delete("delete from sys_user_role where user_id = #{userId}")
    int deleteUserRoles(Long userId);

    @Select("""
            select r.role_code
            from sys_role r
            inner join sys_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId}
            order by r.id
            """)
    List<String> listRoleCodesByUserId(Long userId);

    @Select("""
            select distinct p.permission_code
            from sys_permission p
            inner join sys_role_permission rp on rp.permission_id = p.id
            inner join sys_user_role ur on ur.role_id = rp.role_id
            where ur.user_id = #{userId}
            order by p.permission_code
            """)
    List<String> listPermissionCodesByUserId(Long userId);

    @Select("""
            select t.id, t.user_id, t.token, t.expire_at, t.created_at
            from sys_user_token t
            where t.token = #{token} and t.expire_at > now()
            limit 1
            """)
    SystemEntities.UserToken findValidToken(String token);

    @Insert("insert into sys_user_token(user_id, token, expire_at, created_at) values(#{userId}, #{token}, #{expireAt}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertToken(SystemEntities.UserToken token);

    @Delete("delete from sys_user_token where token = #{token}")
    int deleteToken(String token);

    @Delete("delete from sys_user_token where user_id = #{userId}")
    int deleteTokensByUserId(Long userId);

    @Insert("""
            insert into sys_operate_log(user_id, username, method, path, permission_code, status_code, duration_ms, request_body, response_body, ip, created_at)
            values(#{userId}, #{username}, #{method}, #{path}, #{permissionCode}, #{statusCode}, #{durationMs}, #{requestBody}, #{responseBody}, #{ip}, now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOperateLog(SystemEntities.OperateLog log);

    @Select("""
            select id, user_id, username, method, path, permission_code, status_code, duration_ms, request_body, response_body, ip, created_at
            from sys_operate_log order by id desc limit 200
            """)
    List<SystemEntities.OperateLog> listOperateLogs();
}
