package de.uni_due.paluno.se.palaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.uni_due.paluno.se.palaver.fragment.fragment_friendlist;
import de.uni_due.paluno.se.palaver.fragment.fragment_setting;

public class User_Interface_Activity extends AppCompatActivity implements View.OnClickListener {

    TextView username;

    private Fragment frag_list;
    private Fragment frag_setting;

    private TextView tv_list;
    private TextView tv_setting;

    private ImageView image_list;
    private ImageView image_setting;

    private LinearLayout linear_list;
    private LinearLayout linear_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interface_main);

        username =findViewById(R.id.username_interface_activity);

        SharedPreferences loginUser_SP = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        String name = loginUser_SP.getString("username", "");
        username.setText(name);

        //View Initialization
        Init_View();

        //OnClickEvent Initialization
        Init_clickEvent();

        //Fragment
        Init_Fragment(2);

        //initial
        image_list.setImageResource(R.drawable.person_green);
        tv_list.setTextColor(getResources().getColor(R.color.colorAccent));

    }

    //HideAllfragment
    private void hideAllFragment(FragmentTransaction transaction)
    {
        if (frag_list != null){
            transaction.hide(frag_list);
        }

        if (frag_setting!= null){
            transaction.hide(frag_setting);
        }
    }

    public void Init_Fragment(int index)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //transaction start
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //hide all Fragment
        hideAllFragment(transaction);
        switch (index){

            case 2:
                if (frag_list == null){
                    frag_list = new fragment_friendlist();
                    transaction.add(R.id.frame_content,frag_list);
                }
                else{
                    transaction.show(frag_list);
                }
                break;

            case 3:
                if (frag_setting == null){
                    frag_setting = new fragment_setting();
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

    private void Init_clickEvent(){
        linear_list.setOnClickListener(this);
        linear_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        restartButton();
        switch (view.getId()){
            case R.id.line_layout_friendlist:
                image_list.setImageResource(R.drawable.person_green);
                tv_list.setTextColor(getResources().getColor(R.color.colorgreen));
                Init_Fragment(2);
                break;
            case R.id.line_layout_setting:
                image_setting.setImageResource(R.drawable.setting_green);
                tv_setting.setTextColor(getResources().getColor(R.color.colorgreen));
                Init_Fragment(3);
                break;
        }
    }

    private void restartButton(){
        image_setting.setImageResource(R.drawable.setting);
        image_list.setImageResource(R.drawable.person);
        tv_setting.setTextColor(getResources().getColor(R.color.colorgrey));
        tv_list.setTextColor(getResources().getColor(R.color.colorgrey));
    }

    public void Init_View()
    {
        image_list =findViewById(R.id.icon_friendlist);
        image_setting= findViewById(R.id.icon_setting);

        linear_list = findViewById(R.id.line_layout_friendlist);
        linear_setting = findViewById(R.id.line_layout_setting);

        tv_list =  findViewById(R.id.textview_friendlist);
        tv_setting =  findViewById(R.id.textview_setting);
    }
}