package model;

import com.example.mrc.attendencesystem.entity.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dao.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

//import org.json.JSONObject

/**
 * ����������Ϣ�߳�
 */
public class ServerThread extends Thread {

    private Socket socket;
    private Gson gson;
    private OutputThread out;// ���ݽ�����д��Ϣ�̣߳���Ϊ����Ҫ���û��ظ���Ϣ��
    private OutputThreadMap map;// д��Ϣ�̻߳�����
    private SocketThreadMap socketThreadMap;// д��Ϣ�̻߳�����
    private DataInputStream inputStream;// ����������
    private InputStreamReader iReader;
    private boolean isStart = true;// �Ƿ�ѭ������Ϣ
    private long lastReceiveHeart;

    public ServerThread(Socket socket, OutputThread out, OutputThreadMap map) {
        this.socket = socket;
        this.out = out;
        this.map = map;
        try {
            inputStream = new DataInputStream(socket.getInputStream());// ʵ��������������
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStart(boolean isStart) {// �ṩ�ӿڸ��ⲿ�رն���Ϣ�߳�
        this.isStart = isStart;
    }

    @Override
    public void run() {
        // TODO ����������д�Ĳ���
        try {
            while (isStart) {
                iReader = new InputStreamReader(inputStream, "UTF-8");
                char[] buffer = new char[1024];
                int count = 0;
                String phone = null;
                StringBuilder sBuilder = new StringBuilder();
                while ((count = iReader.read(buffer, 0, buffer.length)) > -1) {
                    sBuilder.append(buffer, 0, count);
                    if (count <= 1024 && count > 0) {
                        break;
                    }
                }
                if(count < 1)//��ʱ�ͻ���socket���ӶϿ�
                    break;
                //System.out.println("tran once" + sBuilder.toString());

                gson = new GsonBuilder().setPrettyPrinting() // ��ʽ����������л���
                        .setDateFormat("yyyy-MM-dd HH:mm:ss") // ���ڸ�ʽ�����
                        .create();
                JsonReader jsonReader = new JsonReader(new StringReader(sBuilder.toString()));// ����jsonContextΪString���͵�Json����
                jsonReader.setLenient(true);
                TranObject readObject = gson.fromJson(jsonReader, TranObject.class);
                if (readObject != null) {
                    lastReceiveHeart = System.currentTimeMillis();
                    phone = readObject.getFromUser();
                    System.out.println("phone:" + phone);
                    if (readObject.getType() != TranObjectType.HEART_TEST) {
                        System.out.println("msg :" + sBuilder.toString());
                    }

                }
                /*if (System.currentTimeMillis() - lastReceiveHeart > 100000) {
                    try {
                        if (phone != null) {
                            UserDao.getUserDao().userOffLine(phone);
                            //new UserDao().updateStatus(0, phone);
                        }

                        socket.close();
                    } catch (IOException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }*/
                TranObject serverResult = execute(readObject);
                pushMessage(readObject);// ִ�����͵���Ϣ
                if (serverResult != null) {
                    map.getAll().forEach(System.out::println);
                    System.out.println("��ǰout��ַ" +map.getById(phone));
                    map.getById(phone).setMessage(serverResult);
                    //out.setMessage(serverResult);
                }
            }
            if (iReader != null) {
                iReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: handle exception
            /*if (iReader != null) {
                iReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }*/
        }

    }

    // ����ͻ��˷��͹�������Ϣ
    private TranObject execute(TranObject readObject) {
        // String responseString = null;

        boolean flag;
        UserDao userService = UserDao.getUserDao();
        GroupDao groupDao = GroupDao.getGroupDao();
        if (readObject != null) {
            String phone = readObject.getFromUser();
            // System.out.println("��ʼ����������"+socket.getInetAddress().getHostAddress());
            if (readObject.getType() == TranObjectType.HEART_TEST) {
                if (phone != null) {
                    userService.changeStateOnline(phone);
                }
                // System.out.println("��ʼ����������");
                lastReceiveHeart = System.currentTimeMillis();

                TranObject heartObject = new TranObject(TranObjectType.HEART_TEST);
                return heartObject;
            }
            switch (readObject.getType()) {
                case REGISTER:// ����û���ע��
                    User registerUser = readObject.getUser();
                    System.out.println("REGISTER");
                    boolean sign = userService.register(registerUser);
                    // ���û��ظ���Ϣ
                    TranObject registerTranObject = new TranObject(TranObjectType.REGISTER);
                    registerTranObject.setSuccess(sign);
                    return registerTranObject;
                case REGISTER_TEST: // �����˺��Ƿ����
                    User checkUser = readObject.getUser();
                    System.out.println("REGISTER_TEST");
                    flag = userService.getUser(checkUser.getPhoneNumber()) != null;
                    // ���û��ظ���Ϣ
                    TranObject checkTranObject = new TranObject(TranObjectType.REGISTER_TEST);
                    checkTranObject.setSuccess(flag);
                    return checkTranObject;
                case LOGIN:
                    User loginUser = readObject.getUser();
                    System.out.println("LOGIN");
                    flag = userService.login(loginUser);
                    TranObject loginObject = new TranObject(TranObjectType.LOGIN);
                    if (flag) {// �����¼�ɹ� ��OutputStream ����Map��
                        map.add(loginUser.getPhoneNumber(), out);
                        ArrayList<UnReceivedMessage> unReceivedMessages =
                                userService.judgeUnReceivedMessage(loginUser.getPhoneNumber());
                        userService.removeUnReceivedMessage(loginUser.getPhoneNumber());
                        if (unReceivedMessages.size() > 0) {
                            loginObject.setUnReceivedMessages(unReceivedMessages);
                        }
                    }
                    loginObject.setUser(loginUser);
                    loginObject.setSuccess(flag);
                    return loginObject;
                /*case RESET_PASSWORD:
                    User resetUser = (User) readObject.getUser();
                    System.out.println("RESET_PASSWORD");
                    flag = userService.resetPassword(resetUser);
                    TranObject resetObject = new TranObject(TranObjectType.RESET_PASSWORD);
                    if (flag) {// �����¼�ɹ�
                        resetObject.setSuccess(flag);
                    } else {
                    }
                    return resetObject;*/
                case LOGOUT:
                    String outPhone = readObject.getFromUser();
                    System.out.println("LOGOUT");
                    flag = userService.userOffLine(outPhone);
                    if (flag) {// �������״̬�ɹ��Ƴ���Ӧ��socketд�߳�
                        map.remove(outPhone);
                    }
                    return null;
                /*case UPDATE_USER:
                    User updateUser = (User) readObject.getUser();
                    System.out.println("UPDATE_USER");
                    TranObject updateObject = new TranObject(TranObjectType.UPDATE_USER);
                    if (userService.updateUser(updateUser)) {
                        System.out.println("UPDATE_USER " + true);
                        updateObject.setSuccess(true);
                    } else {
                    }
                    return updateObject;*/
                /*case SET_IMAGEPATH:
                    User imageUser = (User) readObject.getUser();
                    System.out.println("SET_IMAGEPATH");
                    flag = userService.updateImage(imageUser);
                    TranObject imageObject = new TranObject(TranObjectType.SET_IMAGEPATH);
                    if (flag) {
                        imageObject.setSuccess(flag);
                    } else {
                    }*/
                case GET_GROUPS:// ��ȡ�û������Ⱥ
                    String stuIdJ = readObject.getFromUser();
                    System.out.println("GET_GROUPS");
                    /*ArrayList<Group> groups = null;
                    groups = groupDao.getOwnerGroups(stuIdJ);
                    ArrayList<Group> groupsList = groupDao.getJoinGroups(stuIdJ, groups);*/
                    ArrayList<Group> groupsList = userService.getUserGroups(stuIdJ);
                    TranObject jGroupsObject = new TranObject(TranObjectType.GET_GROUPS);
                    jGroupsObject.setGroupList(groupsList);
                    jGroupsObject.setSuccess(true);
                    return jGroupsObject;
                case ADD_GROUP:// ���Ⱥ
                    Group groupA = readObject.getGroup();
                    System.out.println("ADD_GROUP");
                    TranObject addGroup = new TranObject(TranObjectType.ADD_GROUP);
                    if (groupDao.addGroup(groupA)) {
                        addGroup.setSuccess(true);
                    } else {
                        addGroup.setSuccess(false);
                    }
                    return addGroup;
                case DELETE_GROUP:// ɾ��Ⱥ
                    String groupID = readObject.getFromUser();
                    System.out.println("DELETE_GROUP");
                    TranObject deleteGroup = new TranObject(TranObjectType.DELETE_GROUP);
                    if (groupDao.deleteGroup(Integer.valueOf(groupID))) {
                        System.out.println("DELETE_GROUP true");
                        deleteGroup.setSuccess(true);
                    /*
					 * List<OutputThread> outputList = map.getAll(); for(int
					 * i=0;i<outputList.size();i++) { outputList.get(i).setMessage(deleteGroup); }
					 */
                    } else {
                        System.out.println("DELETE_GROUP false");
                        deleteGroup.setSuccess(false);
                    }
                    return deleteGroup;
                case SEARCH_GROUP://����Ⱥ
                    Group group = readObject.getGroup();
                    System.out.println("SEARCH_GROUP");
                    TranObject sGroup = new TranObject(TranObjectType.SEARCH_GROUP);
                    ArrayList<Group> sgroups = groupDao.getGroups(group.getGroupName());
                    boolean hasGroups = sgroups != null && sgroups.size() > 0;
                    if (hasGroups) {
                        sGroup.setSuccess(true);
                        sGroup.setGroupList(sgroups);
                    } else
                        sGroup.setSuccess(false);
                    return sGroup;
                case OUT_GROUP:// �˳�Ⱥ
                    String oStuId = readObject.getFromUser();// ��ȡ�û�Id
                    String oGroupId = readObject.getToUser();// ��ȡҪ�����Ⱥ��id
                    System.out.println("OUT_GROUP");
                    TranObject outGroup = new TranObject(TranObjectType.OUT_GROUP);
                    if (groupDao.outGroup(Integer.valueOf(oGroupId), Integer.valueOf(oStuId))) {
                        outGroup.setSuccess(true);
                    } else {
                        outGroup.setSuccess(false);
                    }
                    return outGroup;
                case GET_USER_SIGN_RECORD://��ȡ����ǩ����¼
                    String usrStuId = readObject.getFromUser();// ��ȡ�û�Id
                    String srGroupId = readObject.getToUser();// ��ȡȺ��id
                    ArrayList<GroupSignInMessage> signInfos = userService.getUserSigns(Integer.valueOf(srGroupId), Integer.valueOf(usrStuId));
                    System.out.println("GET_USER_SIGN_RECORD");
                    TranObject usrObject = new TranObject(TranObjectType.GET_USER_SIGN_RECORD);
                    usrObject.setSuccess(true);
                    usrObject.setSignInfoslist(signInfos);
                    return usrObject;
                case GET_GROUP_SIGN_RECORD://��ȡȺ����ʷǩ����¼
                    Group group1 = readObject.getGroup();
                    ArrayList<GroupSignInMessage> gsignInfos = groupDao.getGroupSigns(group1.getGroupId());
                    System.out.println("GET_GROUP_SIGN_RECORD");
                    TranObject grObject = new TranObject(TranObjectType.GET_USER_SIGN_RECORD);
                    grObject.setSuccess(true);
                    grObject.setSignInfoslist(gsignInfos);
                    return grObject;
                /*case SEND_SIGN_RESPONSE:// ����ǩ�����
                    GroupSignInMessage signInfo = readObject.getSignInfo();
                    boolean result = userService.addSigninMessage(signInfo);
                    if (result) {
                        System.out.println("����ǩ���ɹ�");
                    }*/
                case GET_GROUP_MESSAGE:
                    GroupRequest request = readObject.getRequest();
                    ArrayList<GroupMessage> groupMessages = groupDao.pagingQueryGroupMessage(request.getGroupId(), request.getCurrentPage());
                    TranObject getGroupMessageResult = new TranObject(TranObjectType.GET_GROUP_MESSAGE);
                    getGroupMessageResult.setGroupMessageArrayList(groupMessages);
                    getGroupMessageResult.setSuccess(true);
                    return getGroupMessageResult;
                case GET_SINGLE_SIGNIN_RECORD:
                    GroupMessage signInRecord = readObject.getSendGroupMessage();
                    ArrayList<GroupSignInMessage> groupSignInMessages = groupDao.getAllSingleSignInMessage(signInRecord.getMessageId());
                    TranObject tranObject = new TranObject(TranObjectType.GET_SINGLE_SIGNIN_RECORD);
                    tranObject.setSignInfoslist(groupSignInMessages);
                    tranObject.setSuccess(true);
                    return tranObject;
                case USER_SIGN_IN://�û��ڿͻ��˽����ж� ֮�������˷���ȷ��ǩ����¼
                    GroupSignInMessage signInMessage = readObject.getSignInfo();
                    boolean result = groupDao.insertConfirmSignInRecord(signInMessage);
                    TranObject signInResult = new TranObject(TranObjectType.USER_SIGN_IN);
                    signInResult.setSuccess(result);
                    return signInResult;
                case GET_GROUP_MEMBERS:
                    Group group2 = readObject.getGroup();
                    ArrayList<User> users = groupDao.getGroupMember(group2.getGroupId());
                    TranObject allUsers = new TranObject(TranObjectType.GET_GROUP_MEMBERS);
                    allUsers.setGroupUsers(users);
                    allUsers.setSuccess(true);
                case SEND_JOIN_REQUEST:
                    Group group3 = readObject.getGroup();
                    String phoneNumber = readObject.getFromUser();
                    boolean joinGroupResult = userService.joinGroup(phoneNumber,group3);
                    TranObject joinGroup = new TranObject(TranObjectType.SEND_JOIN_REQUEST);
                    joinGroup.setSuccess(joinGroupResult);
                default:
                    break;
            }

        }

        return readObject;
    }


    /**
     * ������Ҫ�������͵���Ϣ
     *
     * @param readObject
     */
    private void pushMessage(TranObject readObject) {
        UserDao userDao = UserDao.getUserDao();
        GroupDao groupDao = GroupDao.getGroupDao();
        //MessageDao messageDao = DaoFactory.getMessageDaoInstance();
        //SignDao signDao = DaoFactory.getSignDaoInstance();
        if (readObject != null) {
            switch (readObject.getType()) {
                /*case SEND_JOIN_REQUEST:// �������Ⱥ
                    Message st_Message = readObject.getMessage();
                    System.out.println("SEND_JOIN_REQUEST");
                    TranObject st_joinGroup = new TranObject(TranObjectType.GET_JOIN_REQUEST);
                    String st_receiver_id = st_Message.getReceiver_id();// ��ȡ�����ߵ�id
                    OutputThread outputThread = map.getById(st_receiver_id);// ȡ����Ӧ�����ߵ�д�߳�
                    messageDao.addMessage(st_Message);
                    if (outputThread != null) {
                        //ArrayList<Message> st_Messages = messageDao.getMessage(st_Message, 0);
                        ArrayList<Message> st_Messages = new ArrayList<>();
                        st_Messages.add(st_Message);
                        st_joinGroup.setMessages(st_Messages);
                        st_joinGroup.setSuccess(true);
                        outputThread.setMessage(st_joinGroup);
                    }
                    break;
                case SEND_JOIN_RESPONSE:// �������Ⱥ
                    Message s_Message = readObject.getMessage();
                    System.out.println("SEND_JOIN_RESPONSE");
                    if (s_Message.getResponse_type() == 1)// 1��ʾͬ���Ӧ��
                    {
                        System.out.println("ͬ��");
                        if (groupDao.checkJoin(s_Message.getGroup_id(), s_Message.getSender_id())) {
                            s_Message.setMes_content("���Ѿ���Ⱥ��Ա��");
                        } else {
                            //����Ա����Ⱥ��
                            groupDao.joinGroup(s_Message.getGroup_id(), s_Message.getSender_id());
                        }

                    }
                    TranObject s_joinGroup = new TranObject(TranObjectType.GET_JOIN_RESPONSE);
                    String s_send_id = s_Message.getReceiver_id();// ��ȡ�����ߵ�id
                    OutputThread s_outputThread = map.getById(s_send_id);// ȡ����Ӧ�����ߵ�д�߳�
                    messageDao.addMessage(s_Message);
                    if (s_outputThread != null) {
                        System.out.println("s_outputThread");
                        //ArrayList<Message> s_Messages = messageDao.getResponse(s_Message);
                        ArrayList<Message> s_Messages = new ArrayList<>();

                        s_Messages.add(s_Message);
                        s_joinGroup.setMessages(s_Messages);
                        s_joinGroup.setSuccess(true);
                        s_outputThread.setMessage(s_joinGroup);
                    }
                    break;
                case SEND_SIGN_REQUEST:// ����ǩ��֪ͨ
                    Message ss_Message = readObject.getMessage();
                    System.out.println("SEND_JOIN_GROUP");
                    TranObject ss_joinGroup = new TranObject(TranObjectType.GET_SIGN_REQUEST);
                    List<String> outputList = groupDao.getMemberCount(ss_Message.getGroup_id());
                    ss_Message.setGroupCount(String.valueOf(outputList.size()));
                    System.out.println(outputList.size());
                    for (int i = 0; i < outputList.size(); i++) {
                        ArrayList<Message> ss_Messages = messageDao.getMessage(ss_Message, 0);
                        ss_joinGroup.setMessages(ss_Messages);
                        ss_joinGroup.setSuccess(true);
                        OutputThread ss_outputThread = map.getById(outputList.get(i));// ȡ����Ӧ�����ߵ�д�߳�
                        if (ss_outputThread != null) {
                            ss_outputThread.setMessage(ss_joinGroup);
                        }

                    }*/
                case SEND_GROUP_MESSAGE:
                    //GroupMessage req
                    GroupMessage sendGroupMessage = readObject.getSendGroupMessage();
                    if (sendGroupMessage.getContentType() == 1) {//��ͨ��Ϣ
                        userDao.addOrdinaryMessage(sendGroupMessage);
                    } else {//ǩ����Ϣ
                        GroupSignInMessage signInMessage = readObject.getSignInfo();
                        userDao.addSignMessage(signInMessage);
                    }
                    //��Ⱥ�������û����һ��������Ϣ
                    System.out.println(sendGroupMessage.getGroupId());
                    Set<String> allOfflineMembers = map.getOfflineGroupMember(sendGroupMessage.getGroupId());
                    for(String offlineMember : allOfflineMembers){
                        userDao.insertUnReceivedMessage(new UnReceivedMessage(sendGroupMessage.getGroupId(),offlineMember));
                    }
                    //������Ϣ��Ⱥ�����ߵĳ�Ա ����һ��֪ͨ ֪ͨ�ó�Ա��ĳ��Ⱥ��һ������Ϣ
                    HashMap<String, OutputThread> outputThreads = OutputThreadMap.getInstance().getOnlineGroupMemberThread(sendGroupMessage.getGroupId());
                    for (String phoneNumber : outputThreads.keySet()) {
                        if(phoneNumber.equals(readObject.getFromUser()))
                            continue; // ������Ϣ��Ⱥ��Ա����ó�Ա����Ϣ
                        //System.out.println(phoneNumber);
                        TranObject tranObject = new TranObject(TranObjectType.SEND_GROUP_MESSAGE);
                        tranObject.setToUser(phoneNumber);
                        tranObject.setSendGroupMessage(sendGroupMessage);
                        tranObject.setSuccess(true);
                        outputThreads.get(phoneNumber).setMessage(tranObject, phoneNumber);
                    }
                /*case SEND_SIGN_RESPONSE:// ����ǩ�����
                    GroupSignInMessage signInfo = readObject.getSignInfo();
                    boolean result = userDao.addSigninMessage(signInfo);
                    if (result) {
                        System.out.println("����ǩ���ɹ�");
                    }
                default:
                    break;
            }
        }*/
            }
        }
    }
}