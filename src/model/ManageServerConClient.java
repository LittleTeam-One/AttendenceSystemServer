/**
 * ����ͻ������ӵ���
 */
package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ManageServerConClient {
	public static HashMap hm=new HashMap<String,ServerConClientThread>();
	
	//���һ���ͻ���ͨ���߳�
	public static void addClientThread(String phoneNumber, ServerConClientThread cc){
		hm.put(phoneNumber,cc);
	}
	//�õ�һ���ͻ���ͨ���߳�
	public static ServerConClientThread getClientThread(String i){
		return (ServerConClientThread)hm.get(i);
	}
	
	public static void removeClientThread(String phoneNumber) {
		hm.remove(phoneNumber);
	}
	//���ص�ǰ�����˵����
	public static List getAllOnLineUserid(){
		List list=new ArrayList();
		//ʹ�õ��������
		Iterator it=hm.keySet().iterator();
		while(it.hasNext()){
			list.add((int) it.next());
		}
		return list;
	}
	
	public static boolean isOnline(String a){
		List list=new ArrayList();
		//ʹ�õ��������
		Iterator it=hm.keySet().iterator();
		while(it.hasNext()){
			int account=(int) it.next();
			if(a.equals(account)){
				return true;
			}
		}
		return false;
	}
}
