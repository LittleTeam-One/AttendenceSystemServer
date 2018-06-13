package model;

import dao.GroupDao;

import java.util.*;

/**
 * ���д�̵߳�
 */
public class OutputThreadMap {
	private HashMap<String, OutputThread> map;
	private static OutputThreadMap instance;

	// ˽�й���������ֹ������ʵ�����Ķ���
	private OutputThreadMap() {
		map = new HashMap<String, OutputThread>();
	}

	// ����ģʽ�������ṩ�ö���
	public synchronized static OutputThreadMap getInstance() {
		if (instance == null) {
			instance = new OutputThreadMap();
		}
		return instance;
	}

	// ���д�̵߳ķ���
	public synchronized void add(String phone, OutputThread out) {
		map.put(phone, out);
	}

	// �Ƴ�д�̵߳ķ���
	public synchronized void remove(String phone) {
		OutputThread outputThread = map.get(phone);
		outputThread.setStart(false);//��ֹ�߳� ������Դ
	    map.remove(phone);
	}

	// ȡ��д�̵߳ķ���,Ⱥ�ĵĻ������Ա���ȡ����Ӧд�߳�
	public synchronized OutputThread getById(String id) {
		return map.get(id);
	}

	// �õ�����д�̷߳��������������������û����͹㲥
	public synchronized List<OutputThread> getAll() {
		List<OutputThread> list = new ArrayList<OutputThread>();
		for (Map.Entry<String, OutputThread> entry : map.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}

	//��ȡ�������ߵ�Ⱥ��Ա����߳�
    public synchronized HashMap<String,OutputThread> getOnlineGroupMemberThread(int groupId){
        HashMap<String,OutputThread> hashMap = new HashMap<>();
        Set<String> allGroupMemberName = GroupDao.getGroupDao().getGroupMemberName(groupId);
        Set<String> allOnlineMemberName = map.keySet();
        allOnlineMemberName.retainAll(allGroupMemberName);
        for(String phoneNumber : allOnlineMemberName){
            hashMap.put(phoneNumber,map.get(phoneNumber));
        }
        return hashMap;
    }

    public synchronized Set<String> getOfflineGroupMember(int groupId){
        Set<String> allGroupMemberName = GroupDao.getGroupDao().getGroupMemberName(groupId);
        Set<String> allOnlineMemberName = map.keySet();
        allGroupMemberName.removeAll(allOnlineMemberName);
        return allGroupMemberName;
    }

}
