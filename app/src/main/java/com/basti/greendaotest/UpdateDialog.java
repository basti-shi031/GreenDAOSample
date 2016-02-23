package com.basti.greendaotest;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basti.greendao.Note;

/**
 * Created by SHIBW-PC on 2016/2/23.
 */
public class UpdateDialog extends DialogFragment {

    private TextInputLayout textInputLayout,commentInputLayout;
    private EditText textEditText,commentEditText;
    private static OnClickListener onClickListener;
    private static Note note;

    static UpdateDialog newInstance(Note note1){
        note = note1;
        return new UpdateDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogview, null);
        initView(view);
        textEditText.setText(note.getText());
        commentEditText.setText(note.getComment());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setTitle("Update")
                .setPositiveButton("确定",null)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClickListener != null){
                            onClickListener.onNegativeClick();
                        }
                    }
                });
        final AlertDialog d=  builder.create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onPostiveClick();
                    }
                });
            }
        });

        return d;
    }

    private void initView(View view) {

        textInputLayout = (TextInputLayout) view.findViewById(R.id.text);
        commentInputLayout = (TextInputLayout) view.findViewById(R.id.comment);

        textInputLayout.setHint("text");
        commentInputLayout.setHint("comment");

        textEditText = textInputLayout.getEditText();
        commentEditText = commentInputLayout.getEditText();
    }

    public UpdateDialog setError(String error){
        textInputLayout.setError(error);
        return this;
    }

    public void setDialogClickListener(OnClickListener listener){
        onClickListener = listener;
    }

    public boolean checkTextInputLayoutEmpty(){
        return TextUtils.isEmpty(textEditText.getText());
    }

    public String getText(){
        return textEditText.getText().toString();
    }

    public String getComment(){
        return commentEditText.getText().toString();
    }
}
