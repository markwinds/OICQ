package com.example.mark.oicq.activity;

import com.example.mark.oicq.adapter.SoftHideKeyBoardUtil;
import com.example.mark.oicq.classes.ChatList;
import com.example.mark.oicq.classes.Friend;
import com.example.mark.oicq.classes.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mark.oicq.R;
import com.example.mark.oicq.adapter.MessageAdapter;
import com.example.mark.oicq.context.MyApplication;
import com.example.mark.oicq.server.ServerManager;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static List<ChatList> chatLists=new ArrayList<>();
    private static List<Message> messageList=new ArrayList<>();
    private EditText inputText;
    private Button sendButton;
    private static RecyclerView chatRecyclerView;
    private static MessageAdapter messageAdapter;
    private static String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //SoftHideKeyBoardUtil.assistActivity(this);  //解决键盘遮挡问题
        chatRecyclerView=findViewById(R.id.chat_recycle_view);
        inputText=findViewById(R.id.chat_edit);
        sendButton=findViewById(R.id.chat_send);
        TextView titleName=findViewById(R.id.chat_friend_name);

        titleName.setText(friendName);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        chatRecyclerView.setLayoutManager(layoutManager);
        messageList=getListByName(friendName);
        messageAdapter=new MessageAdapter(messageList);
        chatRecyclerView.setAdapter(messageAdapter);
        chatRecyclerView.scrollToPosition(messageList.size()-1);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=inputText.getText().toString();
                if(!content.equals("")){
                    addMessage(friendName,content,Message.MESSAGE_TYPE_SEND);
//                    Message msg=new Message(content,Message.MESSAGE_TYPE_SEND);
//                    messageList.add(msg);
//                    messageAdapter.notifyItemInserted(messageList.size()-1);
                    inputText.setText("");
                    //chatRecyclerView.scrollToPosition(messageList.size()-1);
                    String message = "[SENDMESSAGE]:[" + HomePageActivity.getMyUsername() + ", " + friendName + ", " + content + "]";
                    ServerManager.getServerManager().sendMessage(MyApplication.getContext(), message);
                }
            }
        });
    }


    public static String getFriendName(){
        return friendName;
    }


    public static void setFriendName(String name){
        friendName=name;
    }


    /*根据用户名读取相应的消息队列*/
    public static List<Message> getListByName(String name){
        for(int i=0;i<chatLists.size();i++){
            if(name.equals(chatLists.get(i).getFriendName())){
                return chatLists.get(i).getMessageList();
            }
        }
        ChatList chatList=new ChatList(friendName); //如果还没有该朋友的消息队列则创建
        chatLists.add(chatList);
        return chatList.getMessageList();
    }


    /*将该好友的消息加入到对应名字的用户队列中*/
    public static void addMessage(String name,String content,int type){
        for(int i=0;i<chatLists.size();i++){
            if(name.equals(chatLists.get(i).getFriendName())){
                List<Message> mlist = chatLists.get(i).getMessageList();
                mlist.add(new Message(content,type));
                ChatList clist=new ChatList(name);
                clist.setMessageList(mlist);
                chatLists.set(i,clist);
                if(name.equals(friendName)){    //只有当前打开的聊天界面用户名和收到消息的对象相同时才通知控件更新界面
                    messageList=getListByName(friendName);
                    messageAdapter.notifyItemInserted(messageList.size()-1);
                    chatRecyclerView.scrollToPosition(messageList.size()-1);
                }
                return;
            }
        }
        List<Message> mlist = new ArrayList<>();
        mlist.add(new Message(content,type));
        ChatList clist=new ChatList(name);
        clist.setMessageList(mlist);
        chatLists.add(clist);
        if(name.equals(friendName)){
            messageList=getListByName(friendName);
            messageAdapter.notifyDataSetChanged();
            chatRecyclerView.scrollToPosition(messageList.size()-1);
        }
    }


    public static void closeAll(){
        chatLists.clear();
    }

}
