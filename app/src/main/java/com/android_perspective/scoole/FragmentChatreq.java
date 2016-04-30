package com.android_perspective.scoole;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sai Karthik on 3/19/2016.
 */
public class FragmentChatreq extends Fragment {
    private ArrayAdapter<String> listOfParents;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_chatreq,container,false);

        String[] stringParents = {
          "Prabhakar","Balaram","Ramnath","Ashish","Arman","Jayant","Arun","Vamshi","Vivek","Ramtej","Sagar","Kaushik","Karthik"
        };
        final String[] emailParents = {
          "kotaprabhakar@scoole.com","balaramakrishna@scoole.com","ramnath@scoole.com","ashish@scoole.com","arman@scoole.com",
                "jayant@scoole.com","arun@scoole.com","vamshi@scoole.com","vivek@scoole.com","ramtej@scoole.com",
                "sagar@scoole.com","kaushik@scoole.com","kvrkarthik@scoole.com"
        };
        List<String> listParents = new ArrayList<String>(Arrays.asList(stringParents));
        listOfParents = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_chat,
                R.id.textViewChat,
                listParents
        );
        final ListView chatList = (ListView) rootView.findViewById(R.id.chatreqList);
        chatList.setAdapter(listOfParents);
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatActivityIntent = new Intent(getActivity(), ChatActivity.class);
                String title = (String)chatList.getItemAtPosition(position);
                String emailID = (String) emailParents[position];
                chatActivityIntent.putExtra("Title",title);
                chatActivityIntent.putExtra("Email",emailID);
                startActivity(chatActivityIntent);
            }
        });

        return rootView;
    }
}