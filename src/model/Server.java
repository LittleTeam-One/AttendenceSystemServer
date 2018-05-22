package model;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import com.example.mrc.attendencesystem.entity.Message;
import com.example.mrc.attendencesystem.entity.MessageType;
import com.example.mrc.attendencesystem.entity.User;

import dao.UserDao;

public class Server {
	public Server(){
		ServerSocket ss = null;
		try {
			ss=new ServerSocket(5469);
			System.out.println("������������ in "+new Date());
			while(true){
				System.out.println(ss.toString());
				Socket s=ss.accept();
				//���ܿͻ��˷�������Ϣ
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				User u=(User) ois.readObject();
				Message m=new Message();
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
		        if(u.getOperation().equals("login")){ //��¼
		        	String phoneNumber=u.getPhoneNumber();
		        	UserDao udao=new UserDao();
		        	boolean b=udao .login(phoneNumber, u.getPassword());//�������ݿ���֤�û�
					if(b){
						System.out.println("["+phoneNumber+"]�����ˣ�");
						//�������ݿ��û�״̬
						udao.changeState(phoneNumber, 1);
						//�õ�������Ϣ
						String user= phoneNumber;
						//����һ���ɹ���½����Ϣ����������������Ϣ
						m.setType(MessageType.SUCCESS);
						m.setContent(user);
						oos.writeObject(m);
						ServerConClientThread cct=new ServerConClientThread(s);//����һ���̣߳��ø��߳���ÿͻ��˱�������
						ManageServerConClient.addClientThread(u.getPhoneNumber(),cct);
						cct.start();//������ÿͻ���ͨ�ŵ��߳�
					}else{
						m.setType(MessageType.FAIL);
						oos.writeObject(m);
					}
		        }else if(u.getOperation().equals("register")){
		        	UserDao udao=new UserDao();
		        	if(udao.register(u)){
		        		//����һ��ע��ɹ�����Ϣ��
						m.setType(MessageType.SUCCESS);
						oos.writeObject(m);
		        	}
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

}

