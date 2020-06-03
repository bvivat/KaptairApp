package com.example.kaptair;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;


interface SimpleDialogCallback {
    //Callback a implementer par le fragment creant ce dialog si Type = YES_NO
    void positiveBtnClicked();

    void negativeBtnClicked();
}

interface SimpleDialogInputCallback {
    //Callback a implementer par le fragment creant ce dialog si Type = YES_NO
    void inputPositiveBtnClicked(String input);

    void inputNegativeBtnClicked();
}

public class SimpleDialog extends DialogFragment {
    public static final String ARG_TITLE = "Title";
    public static final String ARG_MESSAGE = "Message";
    public static final String ARG_ICON = "Icon";
    public static final String ARG_TYPE = "Type";

    public static final int TYPE_YES_NO = 0;
    public static final int TYPE_OK = 1;
    public static final int TYPE_INPUT = 2;

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
                // On cree un dialog ok avec les arguments passés

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
                // On cree un dialog yes/no avec les arguments passés

                final SimpleDialogCallback callback;
                try {
                    callback = (SimpleDialogCallback) getParentFragment();
                } catch (ClassCastException e) {
                    throw new ClassCastException("Calling Fragment must implement SimpleDialogCallback");
                }

                final AlertDialog.Builder builder2 = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.ConfirmDialog));
                builder2.setMessage(message)
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
                        .setTitle(title)
                        .setIcon(icon);

                return builder2.create();

            case TYPE_INPUT:
                // On cree un dialog yes/no avec les arguments passés

                final SimpleDialogInputCallback callbackInput;
                try {
                    callbackInput = (SimpleDialogInputCallback) getParentFragment();
                } catch (ClassCastException e) {
                    throw new ClassCastException("Calling Fragment must implement SimpleDialogInputCallback");
                }

                final AlertDialog.Builder builder3 = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.ConfirmDialog));

                final EditText input = new EditText(new ContextThemeWrapper(getContext(), R.style.InputDialog));
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder3.setView(input);

                builder3.setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                callbackInput.inputPositiveBtnClicked(input.getText().toString());
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                callbackInput.inputNegativeBtnClicked();
                            }
                        })
                        .setTitle(title)
                        .setIcon(icon);

                return builder3.create();

            default:
                final AlertDialog.Builder builder0 = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.ConfirmDialog));
                builder0.setMessage(message)
                        .setTitle(title);
                return builder0.create();


        }


    }

}

