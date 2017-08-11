package mapper;

import java.util.List;

import po.User;
import po.UserCustom;
import po.UserVo;

public interface UserMapper {
	List<User> selectUserListbyUserVo(UserVo userVo);
	Integer getCountOfUserByUserVo(UserVo userVo);
	UserCustom selectUserMessageByUserId(String userId);
	void updateWinAddOne(String userid);
	void updateFaillAddOne(String userid);
}
