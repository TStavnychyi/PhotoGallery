package com.example.tstv.photogallery.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.tstv.photogallery.R;
import com.squareup.picasso.Picasso;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;

/**
 * Created by tstv on 02.07.2017.
 */

public class LongPressDialogFragment extends DialogFragment {
    private BlurDialogEngine mBlurEngine;
    private static final String ARG_IMAGE = "image_from_adapter";
    private ImageView mImageView;
    private String image_url;
    public static LongPressDialogFragment newInstance(String url){

        Bundle args = new Bundle();
        args.putString(ARG_IMAGE, url);

        LongPressDialogFragment dialog = new LongPressDialogFragment();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlurEngine = new BlurDialogEngine(getActivity());

        image_url = getArguments().getString(ARG_IMAGE);

        mBlurEngine.setBlurRadius(4);
        mBlurEngine.setDownScaleFactor(5.0f);
        mBlurEngine.setUseRenderScript(true);
        mBlurEngine.setBlurActionBar(true);
        mBlurEngine.setUseRenderScript(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();


        if (dialog != null){
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        int currentAnimation = dialog.getWindow().getAttributes().windowAnimations;
        if (currentAnimation == 0){
            dialog.getWindow().getAttributes().windowAnimations
                    = R.style.BlurDialogFragment_Default_Animation;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlurEngine.onResume(getRetainInstance());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mBlurEngine.onDismiss();
    }
    @Override
    public void onDestroyView() {
        if (getDialog() != null) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_fragment_blur, null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        v.setMinimumWidth((int)(displayRectangle.width() * 0.6f));
        v.setMinimumHeight((int)(displayRectangle.height() * 0.6f));

        mImageView = (ImageView) v.findViewById(R.id.dialog_image_view);

        Picasso.with(getContext()).load(image_url).into(mImageView);

        Dialog d = new AlertDialog.Builder(getActivity())
                .setView(mImageView)
                .create();


        return d;
    }
}
