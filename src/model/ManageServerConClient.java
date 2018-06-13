/**
 * ����ͻ������ӵ���
 */
package model;
import dao.GroupDao;

import java.util.*;


public class ManageServerConClient {
	public static HashMap hm=new HashMap<String,ServerConClientThread>();
	
	//���һ���ͻ���ͨ���߳�
	public synchronized static void addClientThread(String phoneNumber, ServerConClientThread cc){
		hm.put(phoneNumber,cc);
	}
	//�õ�һ���ͻ���ͨ���߳�
	public static ServerConClientThread getClientThread(String i){
		return (ServerConClientThread)hm.get(i);
	}

	/*public static synchronized ArrayList<ServerConClientThread> getOnlineGroupMemberThread(int groupId){
	    ArrayList<ServerConClientThread> serverConClientThreads = new ArrayList<>();
        Set<String> allGroupMemberName = GroupDao.getGroupDao().getGroupMemberName(groupId);
        Set<String> allOnlineMemberName = hm.keySet();
        allOnlineMemberName.retainAll(allGroupMemberName);
        for(String phoneNumber : allOnlineMemberName){
            serverConClientThreads.add((ServerConClientThread) hm.get(phoneNumber));
        }
	    return serverConClientThreads;
    }*/
	
	public synchronized static void removeClientThread(String phoneNumber) {
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
