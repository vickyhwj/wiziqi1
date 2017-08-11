package service;

import java.util.ArrayList;








import po.User;
import po.UserCustom;
import po.UserVo;
import mapper.RelationshipMapper;
import mapper.UserMapper;

public class UserService {
	UserMapper userMapper;

	
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}


	public ArrayList<User> selectUserListbyUserid(String userid){
		UserVo userVo=new UserVo();
		userVo.setUser(new User());
		userVo.getUser().setUserid(userid);
		return (ArrayList<User>) userMapper.selectUserListbyUserVo(userVo);
	}
	public ArrayList<User> selectUserListbyUserid_page(String userid,int index,int len){
		UserVo userVo=new UserVo();
		userVo.setUser(new User());
		userVo.getUser().setUserid(userid);
		userVo.setIndex(index);
		userVo.setLen(len);
		return (ArrayList<User>) userMapper.selectUserListbyUserVo(userVo);
	}
	public Integer getCountOfUserByUserid(String userid){
		UserVo userVo=new UserVo();
		userVo.setUser(new User());
		userVo.getUser().setUserid(userid);
		return userMapper.getCountOfUserByUserVo(userVo);
	}
	public UserCustom selectUserMessageByUserId(String userId){
		return userMapper.selectUserMessageByUserId(userId);
	}
}
