package edu.uta.cse.microservice.user.mapper;

import edu.uta.cse.thrift.user.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("Select id, username, password, official_name as officalName," +
            "mobile_number, email from pe_user where id=#{id}")
    UserInfo getUserById(@Param("id") int id);

    @Select("Select id, username, password, official_name as officalName," +
            "mobile_number, email from pe_user where username=#{username}")
    UserInfo getUserByName(@Param("username") String username);

    @Insert("Insert into pe_user (username, password, official_name, mobile_number, email)" +
            "values (#{user.username}, #{user.password}, #{user.officialName}, #{user.mobile}, #{user.email})")
    void registerUser(@Param("user") UserInfo userInfo);

//    @Select("Select u.id, u.username, u.official_name as realName, u.mobile, u.email, " +
//            "t.intro, t.stars " +
//            "from pe_user u left join pe_teacher t on u.id=t.user_id " +
//            "where u.id=#{id}")
//    UserInfo getTeacherById(@Param("id") int id);
}
