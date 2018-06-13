package model;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.mrc.attendencesystem.entity.Message;
import com.example.mrc.attendencesystem.entity.MessageType;
import com.example.mrc.attendencesystem.entity.User;

import dao.UserDao;

public class Server {

    public static ExecutorService mSocketExecutorService;
    private ServerSocket mServerSocket;
    private static Server sServer;
    private static boolean sIsStart = true;

    public synchronized static Server getServer(){
        if(sServer == null){
            sServer = new Server();
        }
        return sServer;
    }

	public Server(){
		try {
            mSocketExecutorService = Executors.newCachedThreadPool();
            mServerSocket=new ServerSocket(5469);
            start();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	private void start(){
        System.out.println("������������ in "+new Date());
        try {
            while(sIsStart){
                System.out.println(mServerSocket.toString());
                Socket s=mServerSocket.accept();
                String socketAddress = s.getInetAddress().toString();
                System.out.println(socketAddress + "�ͻ���������");
                //���ܿͻ��˷�������Ϣ
                if (s.isConnected()) {
                    mSocketExecutorService.execute(new SocketTask(s));// ��ӵ��̳߳�
                }
                /*ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
                User u=(User) ois.readObject();
                Message m=new Message();
                ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
                if(u.getOperation().equals("login")){ //��¼
                    String phoneNumber=u.getPhoneNumber();
                    UserDao udao=new UserDao();
                    boolean b=udao.login(phoneNumber, u.getPassword());//�������ݿ���֤�û�
                    if(b){
                        System.out.println("["+phoneNumber+"]�����ˣ�");
                        //�������ݿ��û�״̬
                        udao.changeStateOnline(phoneNumber, 1);
                        //�õ�������Ϣ
                        String user= phoneNumber;
                        //����һ���ɹ���½����Ϣ����������������Ϣ
                        m.setType(MessageType.SUCCESS);
                        m.setContent(user);
                        oos.writeObject(m);
                        ServerConClientThread cct=new ServerConClientThread(s,phoneNumber);//����һ���̣߳��ø��߳���ÿͻ��˱�������
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
                }*/
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void quit(){
        try {
            sIsStart = false;
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final class SocketTask implements Runnable {
        private Socket socket = null;
        private ServerThread in;
        private OutputThread out;
        private OutputThreadMap outputThreadMap;
        private String phoneNumber;


        public SocketTask(Socket socket) {
            this.socket = socket;
            outputThreadMap = OutputThreadMap.getInstance();
            //socketThreadMap = SocketThreadMap.getInstance();
        }

        @Override
        public void run() {
            out = new OutputThread(socket, outputThreadMap);// ��ʵ����д��Ϣ�߳�,���Ѷ�Ӧ�û���д�̴߳���map�������У�
            in = new ServerThread(socket, out, outputThreadMap);// ��ʵ��������Ϣ�߳�
            out.setStart(true);
            in.setStart(true);
            in.start();
            out.start();
        }
    }

}

