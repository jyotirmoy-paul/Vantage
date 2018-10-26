package com.example.mr_paul.vantage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class PostAd extends Fragment {

    // widgets variables
    Spinner chooseSaleOrRent;
    EditText adTitle;
    EditText priceOfProduct;
    ImageView adCoverImage;
    LinearLayout uploadPhoto;
    EditText adDescription;
    Button postAdButton;
    Button adPhoto01;
    Button adPhoto02;

    // important variables
    String choice;
    String userUniqueKey;
    String userUID;
    String adTitleText;
    String priceOfProductText;
    String adDescriptionText;
    Uri photo_01=null;
    Uri photo_02=null;
    Uri photo_03=null;
    String userName;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PostAd() {
        // Required empty public constructor
    }

    public static PostAd newInstance(String param1, String param2) {
        PostAd fragment = new PostAd();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        // The main Cover Photo image selection
        if(requestCode == 100 && resultCode == RESULT_OK){
            photo_01 = data.getData();
            uploadPhoto.setVisibility(View.GONE);
            adCoverImage.setVisibility(View.VISIBLE);
            // resize the image and show it
            try{
                InputStream inputStream = getContext().getContentResolver().openInputStream(photo_01);
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                bmp = getResizedBitmap(bmp,1000,430); // best fit for almost all phone
                adCoverImage.setImageBitmap(bmp);
            }
            catch (Exception e){
                Log.i("Photo #01 -->","Image not Found");
            }
            adCoverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                    startActivityForResult(Intent.createChooser(intent,"Complete Action Using"),100);
                }
            });
            adPhoto01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                    startActivityForResult(Intent.createChooser(intent,"Complete Action Using"),101);
                }
            });

        }



        if(requestCode == 101 && resultCode == RESULT_OK){
            photo_02 = data.getData();
            // change background of the button ---> resize the image and show it
            try{
                InputStream inputStream = getContext().getContentResolver().openInputStream(photo_02);
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                bmp = getResizedBitmap(bmp,150,150);

                // finally changing the properties of the button
                adPhoto01.setBackground(new BitmapDrawable(getResources(),bmp));
                adPhoto01.setText("");
            }
            catch (Exception e){
                Log.i("Photo #01 -->","Image not Found");
            }
            adPhoto02.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                    startActivityForResult(Intent.createChooser(intent,"Complete Action Using"),102);
                }
            });
        }



        if(requestCode == 102 && resultCode == RESULT_OK){
            photo_03 = data.getData();
            // change background of the button ---> resize the image and show it
            try{
                InputStream inputStream = getContext().getContentResolver().openInputStream(photo_03);
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                bmp = getResizedBitmap(bmp,300,300);
                // finally changing the properties of the button
                adPhoto02.setBackground(new BitmapDrawable(getResources(),bmp));
                adPhoto02.setText("");
            }
            catch (Exception e){
                Log.i("Photo #01 -->","Image not Found");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_ad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // reference all the widgets
        chooseSaleOrRent = view.findViewById(R.id.choose_sale_or_rent);
        adTitle = view.findViewById(R.id.ad_title);
        priceOfProduct = view.findViewById(R.id.price_of_product);
        adCoverImage = view.findViewById(R.id.ad_cover_image);
        uploadPhoto = view.findViewById(R.id.upload_photo_layout);
        adDescription = view.findViewById(R.id.ad_description);
        postAdButton = view.findViewById(R.id.post_ad_button);
        adPhoto01 = view.findViewById(R.id.ad_photo_01);
        adPhoto02 = view.findViewById(R.id.ad_photo_02);

        adPhoto01.setClickable(false);
        adPhoto02.setClickable(false);


        // registering onClickListener on the MainCoverPhoto
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"Complete Action Using"),100);
            }
        });


        // registering submit button click
        postAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // collect all the advertisement data and send it to the next activity

                // check for ad title ----> max 80 chars and min 10 chars
                adTitleText = adTitle.getText().toString().trim();
                if (adTitleText.length() < 10 || adTitleText.length() > 80) {
                    adTitle.setError("10 to 80 characters only");
                    adTitle.requestFocus();
                    return;
                }


                // check for the ad description ---> 2000 chars available
                adDescriptionText = adDescription.getText().toString().trim();
                if (adDescriptionText.length() < 10 || adDescriptionText.length() > 2000) {
                    adDescription.setError("11 to 2000 characters only");
                    adDescription.requestFocus();
                    return;
                }

                // get the choice
                choice = chooseSaleOrRent.getSelectedItem().toString();
                if(choice.equals("Ad for Sale")){
                    // then the price becomes relevant

                    // check for the price
                    priceOfProductText = priceOfProduct.getText().toString().trim();
                    if(priceOfProductText.length() == 0){
                        priceOfProduct.setError("Price can't be empty!");
                        priceOfProduct.requestFocus();
                        return;
                    }

                    if(priceOfProductText.length() > 9){
                        priceOfProduct.setError("It can't price more than a Billion rupees!");
                        priceOfProduct.requestFocus();
                        return;
                    }

                }
                else{
                    priceOfProductText = "";
                }

                // the following lines of code checks size of the photo to be uploaded don't exceed
                // 4 MB fo size and also a cover photo must be uploaded to proceed
                if(photo_01 == null){
                    Toast.makeText(getContext(),
                            "Cover Photo is Mandatory", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    File file = new File(photo_01.getPath());
                    long size = file.length();
                    if(size > 4000000){
                        Toast.makeText(getContext(),
                                "Cover Photo can't be greater than 4 Mb!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if(photo_02 != null){
                    File file = new File(photo_02.getPath());
                    long size = file.length();
                    if(size > 4000000){
                        Toast.makeText(getContext(),
                                "Photo #01 can't be greater than 4 Mb!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if(photo_03 != null){
                    File file = new File(photo_03.getPath());
                    long size = file.length();
                    if(size > 4000000){
                        Toast.makeText(getContext(),
                                "Photo #03 can't be greater than 4 Mb!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                SharedPreferences sharedPreferences = getContext()
                        .getSharedPreferences("userInformation",Context.MODE_PRIVATE);
                userUID = sharedPreferences.getString("userUID","");
                userUniqueKey = sharedPreferences.getString("userUniqueKey","");
                userName = sharedPreferences.getString("userName","");


                Intent intent = new Intent(getContext(),AdPostingActivity.class);
                intent.putExtra("choice",choice);
                intent.putExtra("userUID",userUID);
                intent.putExtra("adTitleText",adTitleText);
                intent.putExtra("price",priceOfProductText);
                intent.putExtra("description",adDescriptionText);
                intent.putExtra("photo_01",photo_01.toString());

                if(photo_02 == null){intent.putExtra("photo_02","");}
                else{intent.putExtra("photo_02",photo_02.toString());}

                if(photo_03 == null){intent.putExtra("photo_03","");}
                else{intent.putExtra("photo_03",photo_03.toString());}

                intent.putExtra("userUniqueKey",userUniqueKey);
                intent.putExtra("userName",userName);

                startActivity(intent);
                getActivity().finish();

            }
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
        void onFragmentInteraction(Uri uri);
    }


    // important utility function
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // creating a matrix for manupulation
        Matrix matrix = new Matrix();

        // resizing the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreating the bit map
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
