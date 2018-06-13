package model;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.example.mrc.attendencesystem.entity.Message;
import com.example.mrc.attendencesystem.entity.MessageType;

public class ServerConClientThread extends Thread {
	Socket s;
	private String mPhoneNumber;
	public ServerConClientThread(Socket s, String phoneNumber){
		this.s=s;
        mPhoneNumber = phoneNumber;
	}

	public void run() {
		while(true){
			ObjectInputStream ois = null;
			Message m = null;
			try {
				ois=new ObjectInputStream(s.getInputStream());
				System.out.println("you");
				m=(Message) ois.readObject();
				System.out.println(m);
				//�Դӿͻ���ȡ�õ���Ϣ���������жϣ�����Ӧ�Ĵ���
				if(m.getType().equals(MessageType.COM_MES)){//�������ͨ��Ϣ��
					DoWhatAndSendMes.sendMes(m);
				}else if(m.getType().equals(MessageType.GROUP_MES)){ //�����Ⱥ��Ϣ
					//DoWhatAndSendMes.sendGroupMes(m);
				}else if(m.getType().equals(MessageType.GET_ONLINE_FRIENDS)){//�������������б�
					DoWhatAndSendMes.sendBuddyList(m);
				}else if(m.getType().equals(MessageType.DEL_BUDDY)){ //�����ɾ������
					DoWhatAndSendMes.delBuddy(m);
				}
			} catch (Exception e) {
				try {
				    ManageServerConClient.removeClientThread(mPhoneNumber);
                    ois.close();
					s.close();
				} catch (IOException e1) {
				    e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
}

