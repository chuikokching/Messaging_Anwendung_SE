package de.uni_due.paluno.se.palaver;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import de.uni_due.paluno.se.palaver.fragment.Fragment_chat;
import de.uni_due.paluno.se.palaver.fragment.Fragment_list;
import de.uni_due.paluno.se.palaver.fragment.Fragment_setting;

public class UserInterfaceActivity extends AppCompatActivity implements View.OnClickListener {

   // private Fragment frag_chat;
    private Fragment frag_list;
    private Fragment frag_setting;

    //private LinearLayout linear_chat;
    private LinearLayout linear_list;
    private LinearLayout linear_setting;

   // private ImageView image_chat;
    private ImageView image_list;
    private ImageView image_setting;

  //  private TextView tv_chat;
    private TextView tv_list;
    private TextView tv_setting;

    TextView username;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinterface_main);

        username = (TextView)findViewById(R.id.user_userinterface);

        intent = getIntent();
        username.setText(intent.getStringExtra("username"));

        //View Initialization
        InitView();

        //OnClickEvent Initialization
        InitEvent();

        //Fragment
        InitFragment(2);

        //default situation
        image_list.setImageResource(R.drawable.me_select);
        tv_list.setTextColor(getResources().getColor(R.color.colorAccent));
    }


    public void InitView()
    {
       // linear_chat = (LinearLayout) findViewById(R.id.line2);
        linear_list = (LinearLayout) findViewById(R.id.line3);
        linear_setting = (LinearLayout) findViewById(R.id.line1);

       // image_chat = (ImageView) findViewById(R.id.ic_2);
        image_list = (ImageView) findViewById(R.id.ic_3);
        image_setting= (ImageView) findViewById(R.id.ic_1);

       // tv_chat = (TextView) findViewById(R.id.textview_2);
        tv_list = (TextView) findViewById(R.id.textview_3);
        tv_setting = (TextView) findViewById(R.id.textview_1);
    }

    public void InitFragment(int index)
    {

        FragmentManager fragmentManager = getSupportFragmentManager();
        //transaction start
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();

        //hide all Fragment
        hideAllFragment(transaction);
        switch (index){
//            case 1:
//                if (frag_chat == null){
//                    frag_chat = new Fragment_chat();
//                    transaction.add(R.id.frame_content,frag_chat);
//                }
//                else{
//                    transaction.show(frag_chat);
//                }
//                break;

            case 2:
                if (frag_list == null){
                    frag_list = new Fragment_list();
                    transaction.add(R.id.frame_content,frag_list);
                }
                else{
                    transaction.show(frag_list);
                }
                break;

            case 3:
                if (frag_setting == null){
                    frag_setting = new Fragment_setting();
                    transaction.add(R.id.frame_content,frag_setting);
                }
                else{
                    transaction.show(frag_setting);
                }
                break;

        }
        //commit transaction
        transaction.commit();
    }

    private void InitEvent(){
        //Listener
        //linear_chat.setOnClickListener(this);
        linear_list.setOnClickListener(this);
        linear_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {


        restartButton();

        switch (view.getId()){
//            case R.id.line2:
//
//                image_chat.setImageResource(R.drawable.wechat_select);
//                tv_chat.setTextColor(getResources().getColor(R.color.colorgreen));
//                InitFragment(1);
//                break;

            case R.id.line3:
                image_list.setImageResource(R.drawable.me_select);
                tv_list.setTextColor(getResources().getColor(R.color.colorgreen));
                InitFragment(2);
                break;

            case R.id.line1:
                image_setting.setImageResource(R.drawable.find_select);
                tv_setting.setTextColor(getResources().getColor(R.color.colorgreen));
                InitFragment(3);
                break;
        }
    }

    //HideAllfragment
    private void hideAllFragment(android.support.v4.app.FragmentTransaction transaction){
//        if (frag_chat != null){
//            transaction.hide(frag_chat);
//        }

        if (frag_list != null){
            transaction.hide(frag_list);
        }

        if (frag_setting!= null){
            transaction.hide(frag_setting);
        }

    }

    private void restartButton(){

        image_setting.setImageResource(R.drawable.find_normal);
        //image_chat.setImageResource(R.drawable.wechat_normal);
        image_list.setImageResource(R.drawable.me_normal);

        //grey
       // tv_chat.setTextColor(getResources().getColor(R.color.colorgrey));
        tv_setting.setTextColor(getResources().getColor(R.color.colorgrey));
        tv_list.setTextColor(getResources().getColor(R.color.colorgrey));
    }
}
