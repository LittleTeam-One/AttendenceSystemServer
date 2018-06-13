package model;

import com.example.mrc.attendencesystem.entity.TranObject;
import com.example.mrc.attendencesystem.entity.TranObjectType;
import com.example.mrc.attendencesystem.entity.UnReceivedMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.UserDao;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 */
public class OutputThread extends Thread {
    @SuppressWarnings("unused")
    private OutputThreadMap map;
    private SocketThreadMap socketThreadMap;
    //private ObjectOutputStream oos;
    private OutputStreamWriter oStreamWriter;
    private DataOutputStream dataOutputStream;
    private TranObject object;
    private boolean isStart = true;// ѭ����־λ
    private Socket socket;
    private String mPhoneNumber;//�û��绰����Ϊ�� ��ʶ���߳�
    private ArrayBlockingQueue<TranObject> blockingQueue = new ArrayBlockingQueue<>(5);

    public OutputThread(Socket socket, OutputThreadMap map) {
        this.socket = socket;
        this.map = map;
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());// �ڹ���������ʵ�������������
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    // ����д��Ϣ�̣߳���������Ϣ֮�󣬻���run���������Խ�Լ��Դ
    public void setMessage(TranObject object) {
        blockingQueue.add(object);
    }

    public void setMessage(TranObject object,String phoneNumber){
        blockingQueue.add(object);
        mPhoneNumber = phoneNumber;
    }

    @Override
    public void run() {
        try {

            while (isStart) {
                // û����Ϣд����ʱ���̵߳ȴ�
                object = blockingQueue.take();
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()  //��ʽ����������л���
                        .setDateFormat("yyyy-MM-dd HH:mm:ss") //���ڸ�ʽ�����
                        .create();
                oStreamWriter = new OutputStreamWriter(dataOutputStream, "UTF-8");
                String outputString = gson.toJson(object);
                //dataOutputStream.writeInt(outputString.length());
                //dataOutputStream.write(outputString.getBytes());
                //dataOutputStream.flush();
                //StringBuffer sBuilder = new StringBuffer();
                //sBuilder.append(outputString);
                oStreamWriter.write(outputString);
                oStreamWriter.flush();
                if (object != null && object.getType() != TranObjectType.HEART_TEST) {
                    System.out.println(outputString);
                }

            }
            if (oStreamWriter != null) {
                oStreamWriter.close();
            }
            if (dataOutputStream != null)// ѭ�������󣬹ر������ͷ���Դ
                dataOutputStream.close();
            if (socket != null)
                socket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (object.getType() == TranObjectType.SEND_GROUP_MESSAGE) {
                UserDao userDao = UserDao.getUserDao();
                userDao.insertUnReceivedMessage(new UnReceivedMessage(
                        object.getSendGroupMessage().getGroupId(),mPhoneNumber));
            }
            e.printStackTrace();
        }
    }

}
