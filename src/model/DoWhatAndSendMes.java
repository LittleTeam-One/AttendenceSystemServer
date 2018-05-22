package model;
import java.io.ObjectOutputStream;
import java.util.List;

import com.example.mrc.attendencesystem.entity.Message;
import com.example.mrc.attendencesystem.entity.MessageType;

import dao.GroupDao;
import dao.UserDao;

public class DoWhatAndSendMes {
	static UserDao udao=new UserDao();
	static GroupDao gdao=new GroupDao();
	
	public static void sendMes(Message m){
		try{
			//ȡ�ý����˵�ͨ���߳�
			ServerConClientThread scc=ManageServerConClient.getClientThread(m.getReceiver());
			ObjectOutputStream oos=new ObjectOutputStream(scc.s.getOutputStream());
			//������˷�����Ϣ
			oos.writeObject(m);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sendBuddyList(Message m){
		try{
			//�������ݿ⣬���غ����б�˳��Ⱥ�б�
			//String res=udao.getBuddy(m.getSender())+","+gdao.getGroup());
			//���ͺ����б��ͻ���
			ServerConClientThread scc=ManageServerConClient.getClientThread(m.getSender());
			ObjectOutputStream oos=new ObjectOutputStream(scc.s.getOutputStream());
			Message ms=new Message();
			ms.setType(MessageType.RET_ONLINE_FRIENDS);
			//ms.setContent(res);
			oos.writeObject(ms);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void delBuddy(Message m){
		try{
			if(udao.delBuddy(m.getSender(), m.getReceiver())){
				ServerConClientThread scc=ManageServerConClient.getClientThread(m.getSender());
				ObjectOutputStream oos=new ObjectOutputStream(scc.s.getOutputStream());
				Message ms=new Message();
				ms.setType(MessageType.SUCCESS);
				oos.writeObject(ms);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sendGroupMes(Message m){
		
		try{
			List<String> list=gdao.getGroupMember(m.getReceiver());
			for(String raccount : list){
				//��ֻ֧�������ߵ�Ⱥ��Ա������Ϣ
				if(ManageServerConClient.isOnline(raccount)){
					ServerConClientThread scc=ManageServerConClient.getClientThread(m.getSender());
					ObjectOutputStream oos=new ObjectOutputStream(scc.s.getOutputStream());
					//ֻ��ı�����ߺͷ�������Ϣ
					m.setSender(m.getReceiver());
					m.setReceiver(raccount);
					oos.writeObject(m);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

