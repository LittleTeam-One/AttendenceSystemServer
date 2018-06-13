package model;

import java.net.Socket;
import java.util.HashMap;

/***
 *  ���гɹ����ӵ�socketʵ���� ����һ��socket��һ���û��˺�
 */
public class SocketThreadMap {
	private HashMap<Integer, Socket> map;
	private Socket socket;//���ӵ�socket
	private int userId;//�û��˺�
	public static SocketThreadMap instance;
	// ˽�й���������ֹ������ʵ�����Ķ���
	private SocketThreadMap() {
		map = new HashMap<Integer, Socket>();
	}

	// ����ģʽ�������ṩ�ö���
	public synchronized static SocketThreadMap getInstance() {
		if (instance == null) {
			instance = new SocketThreadMap();
		}
		return instance;
	}
	
	public SocketThreadMap(Socket socket,int userId)
	{
		this.socket = socket;
		this.userId = userId;
	}
	// ���д�̵߳ķ���
	public synchronized void add(Integer id, Socket socket) {
		this.userId = id;
		this.socket = socket;
		map.put(userId, socket);
	}

	// �Ƴ�д�̵߳ķ���
	public synchronized void remove(Integer id) {
		map.remove(id);
	}

	// ȡ��Socket�ķ���,Ⱥ�ĵĻ������Ա���ȡ����ӦSocket
	public synchronized Socket getById(Integer id) {
		return map.get(id);
		
	}
	public int getMapSize()
	{
		return map.size();
	}
	
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket mSocket) {
		this.socket = mSocket;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int mUserId) {
		this.userId = mUserId;
	}
}
