package com.example.mr_paul.vantage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.etsy.android.grid.StaggeredGridView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


import static android.content.Context.MODE_PRIVATE;

public class NewsFeed extends Fragment {

    // layout variable
    LinearLayout gridViewContainer;
    StaggeredGridView adGridView;
    LinearLayout preLoading;

    // firebase variable
    DatabaseReference databaseReference;

    // other imp variables
    ArrayList<ServerData> arrayList;
    ServerDataAdapter serverDataAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NewsFeed() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewsFeed newInstance(String param1, String param2) {
        NewsFeed fragment = new NewsFeed();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // referencing to the views
        adGridView = view.findViewById(R.id.grid_view);
        preLoading = view.findViewById(R.id.pre_loading);
        gridViewContainer = view.findViewById(R.id.grid_view_container);

        gridViewContainer.setVisibility(View.GONE);
        preLoading.setVisibility(View.VISIBLE);

        // if the user gets a new message, show in the news feed area
        final LinearLayout newMessageLayout = view.findViewById(R.id.new_message_layout);
        ImageView closeNewMessageLayout = view.findViewById(R.id.close_new_message_layout);

        closeNewMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make the view gone
                newMessageLayout.setVisibility(View.GONE);
            }
        });

        newMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call intent to show all chats activity and close the layout
                startActivity(new Intent(getActivity(),ShowAllChatsActivity.class));
                newMessageLayout.setVisibility(View.GONE);
            }
        });


        arrayList = new ArrayList<>();
        serverDataAdapter = new ServerDataAdapter(getContext(),R.layout.news_feed_layout,arrayList);


        // creating firebase datebase reference
        databaseReference = FirebaseDatabase
                .getInstance().getReference().child("Advertisement Data");

        // listening to any change in the referenced area of database
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                arrayList.add(dataSnapshot.getValue(ServerData.class));
                serverDataAdapter.notifyDataSetChanged();

                if(gridViewContainer.getVisibility() == View.GONE){
                    // make the grid view visible and make the linear layout gone
                    gridViewContainer.setVisibility(View.VISIBLE);
                    preLoading.setVisibility(View.GONE);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                String deletedAdKey = dataSnapshot.getValue(ServerData.class).getAdKey();

                // deletes the item in O(n) time

                // search in the first array list
                for(int i=0;i<arrayList.size();i++){
                    if(deletedAdKey.equals(arrayList.get(i).getAdKey())){
                        arrayList.remove(i);
                        serverDataAdapter.notifyDataSetChanged();
                        break;
                    }
                }

                if(arrayList.isEmpty()){
                    gridViewContainer.setVisibility(View.GONE);
                    preLoading.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adGridView.setAdapter(serverDataAdapter);


        // show ad in detail after user click on an ad in News Feed (first adlistview)
        adGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView coverPhoto = view.findViewById(R.id.ad_cover_image);
                CardView cardView = view.findViewById(R.id.card_view);

                Pair<View,String> p1 = new Pair<>((View)coverPhoto,"adCoverTransition");


                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity()
                                ,p1);
                Intent intent = new Intent(getActivity(),DetailAdActivity.class);
                intent.putExtra("serverData",arrayList.get(position));
                startActivity(intent,options.toBundle());
            }
        });


        // check for new message
        DatabaseReference databaseRefForNewMessage = FirebaseDatabase.getInstance()
                .getReference().child("Chatting Information")
                .child(getActivity().getSharedPreferences("userInformation",MODE_PRIVATE)
                        .getString("userUID",""));
        databaseRefForNewMessage.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // the user got a new message
                if(newMessageLayout.getVisibility() == View.GONE){
                    newMessageLayout.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
