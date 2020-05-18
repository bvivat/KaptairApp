package com.example.kaptair;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;


interface SimpleDialogCallback {
    void positiveBtnClicked();
    void negativeBtnClicked();
}
public class SimpleDialog extends DialogFragment {
    public static final String ARG_TITLE = "Title"; //TODO Changer les bundles sous cette forme
    public static final String ARG_MESSAGE = "Message";
    public static final String ARG_ICON = "Icon";
    public static final String ARG_TYPE = "Type";

    public static final int TYPE_YES_NO = 0;
    public static final int TYPE_OK = 1;

    public SimpleDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String message = args.getString(ARG_MESSAGE);
        int icon = args.getInt(ARG_ICON);
        int type = args.getInt(ARG_TYPE);

        switch (type) {
            case TYPE_OK:

                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.ConfirmDialog));
                builder.setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setTitle(title)
                        .setIcon(icon);
                return builder.create();

            case TYPE_YES_NO:

                final SimpleDialogCallback callback;
                try {
                    callback = (SimpleDialogCallback) getParentFragment();
                }catch (ClassCastException e){
                    throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
                }

                final AlertDialog.Builder builder2 = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.ConfirmDialog));
                        builder2.setMessage(R.string.preferencesDeleteDialogBody)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        callback.positiveBtnClicked();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        callback.negativeBtnClicked();
                                    }
                                })
                                .setTitle(R.string.preferencesDeleteDialogTitle)
                                .setIcon(icon);

                return builder2.create();

                default:
                    final AlertDialog.Builder builder3 = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.ConfirmDialog));
                    builder3.setMessage(message)
                            .setTitle(title);
                    return builder3.create();


        }


    }

}

