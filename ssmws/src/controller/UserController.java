package controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mapper.UserMapper;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import po.User;
import po.UserCustom;
import service.RelationshipService;
import service.UserService;


@Controller
public class UserController {
	@Autowired
	RelationshipService relationshipService;
	@Autowired
	UserService userService;
	
	@RequestMapping("/login")
	public String login(String username,HttpServletResponse response,HttpSession session,HttpServletRequest request) throws IOException{
		request.setAttribute("username", username);
		request.setAttribute("list",relationshipService.selectUserListbyUserA(username));
		return "index1.jsp";
	}
	@RequestMapping("/getUserListByUserId")
	public void getUserListByUserId(HttpServletRequest request,HttpServletResponse response,String userId,int index,int len) throws IOException{
		ArrayList<User> list= userService.selectUserListbyUserid_page("%"+userId+"%",(index-1)*len,len);
		JSONObject jsonObject=new JSONObject();
		jsonObject.element("userlist", list);
		jsonObject.element("sum", (userService.getCountOfUserByUserid("%"+userId+"%")-1)/len+1);
		jsonObject.element("now", index);
		response.getWriter().print(jsonObject.toString());
	}
	@RequestMapping("/loginByUserid")
	public String login1(String username,String password,HttpServletResponse response,HttpSession session,HttpServletRequest request) throws IOException{
		UserCustom userCustom=userService.selectUserMessageByUserId(username);
		
		if(userCustom!=null){
			System.out.print(userCustom);
			if(!userCustom.getPassword().equals(password)) return null;
			request.setAttribute("user", userCustom);
			request.setAttribute("messages",new JSONObject().element("messages", userCustom.getMessages()).toString());
			
		}else {
			ArrayList<User> list=userService.selectUserListbyUserid(username);
			if(list.size()==0) return null;
			if(!list.get(0).getPassword().equals(password)) return null;
			request.setAttribute("user", list.get(0));
			request.setAttribute("messages",new JSONObject().element("messages", new ArrayList()).toString());
		}
		return "userIndex.jsp";
	}
	@RequestMapping("/getFridendListJSON")
	public void getFridendListJSON(String userA,HttpServletResponse response) throws IOException{
		ArrayList<User> list=relationshipService.selectUserListbyUserA(userA);
		JSONObject jsonObject=new JSONObject();
		jsonObject.element("userids", list);
		response.getWriter().print(jsonObject.toString());
	}
	@RequestMapping("/deleteRelationship")
	public void deleteRelationship(String userA,String userB,HttpServletResponse response) throws IOException{
		relationshipService.deleteRelationship(userA, userB);
		response.getWriter().print("ok");
	}
}
