package model;

import java.util.HashMap;

public class ServerThreadMap {
    private HashMap<String, ServerThread> threadHashMap;
    private static ServerThreadMap serverThreadMap;

    public static synchronized ServerThreadMap getInstance() {
        if(serverThreadMap == null)
            serverThreadMap = new ServerThreadMap();
        return serverThreadMap;
    }

    public ServerThreadMap(){
        threadHashMap = new HashMap<>();
    }

    private void add(String phoneNumber, ServerThread serverThread){
        threadHashMap.put(phoneNumber,serverThread);
    }

    private void remove(String phoneNumber){
        ServerThread serverThread = threadHashMap.get(phoneNumber);
        serverThread.setStart(false);//��ֹ�߳� ִ������
        threadHashMap.remove(phoneNumber);//�Ƴ���Ԫ��
    }
}
