package com.basti.greendaotest;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.basti.greendao.Note;
import com.basti.greendao.NoteDao;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout textInputLayout, commentInputLayout;
    private EditText textEditText, commentEditText;
    private Button bt_add, bt_search,bt_searchAll,bt_deleteAll;
    private ListView listView;

    private BastiApp app;

    private NoteDao noteDao;
    private Query query;
    private SQLiteDatabase db;

    private List<Note> list;
    private NoteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (BastiApp) getApplication();
        initView();
        initEvent();
        initDates();
    }

    private void initDates() {
        list = new ArrayList<>();
        mAdapter = new NoteAdapter(list, this);
        listView.setAdapter(mAdapter);

        noteDao = app.getDaoSession().getNoteDao();
        db = app.getDb();
        searchAllNote();
        mAdapter.notifyDataSetChanged();
    }

    private void searchAllNote() {
        list.clear();
        String textColumn = NoteDao.Properties.Id.columnName;
        String orderBy = textColumn + " COLLATE LOCALIZED ASC";
        //cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(), null, null, null, null, orderBy);
        query = noteDao.queryBuilder()
                .orderAsc(NoteDao.Properties.Id)
                .build();
        list.addAll(query.list());
    }

    private void initEvent() {
        //删全部
        bt_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteDao.deleteAll();
                list.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

        //查所有
        bt_searchAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAllNote();
                mAdapter.notifyDataSetChanged();
            }
        });

        //增
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        //根据内容查
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNoteByContent();
                mAdapter.notifyDataSetChanged();
            }
        });

        //改
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                Note note = list.get(position);

                final UpdateDialog dialog = UpdateDialog.newInstance(note);

                //dialog.setCancelable(false);
                dialog.setDialogClickListener(new OnClickListener() {
                    @Override
                    public void onPostiveClick() {
                        if (dialog.checkTextInputLayoutEmpty()){
                            dialog.setError("error,text cannot be null");
                        }else {
                            updateNote(position,dialog.getText(),dialog.getComment());
                            mAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                });

                dialog.show(getFragmentManager(),"");
                return true;
            }
        });

        //根据id删
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long deleteId = list.get(position).getId();
                //noteDao.deleteByKey(deleteId);
                /*query = noteDao.queryBuilder()
                        .where(NoteDao.Properties.Text.eq("测试2"))
                        .build();
                noteDao.deleteInTx(query.list());*/
                list.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateNote(int position, String text, String comment) {

        Note note = list.get(position);
        note.setText(text);
        note.setComment(comment);

        noteDao.update(note);

        searchAllNote();

    }

    private void searchNoteByContent() {

        if (TextUtils.isEmpty(textEditText.getText()) && TextUtils.isEmpty(commentEditText.getText())){
            textInputLayout.setError("error,text and comment cannot be null at the sametime");
            commentInputLayout.setError("error,text and comment cannot be null at the sametimel");
            return;
        }

        String text = textEditText.getText().toString();
        String comment = commentEditText.getText().toString();

        textInputLayout.setErrorEnabled(false);
        commentInputLayout.setErrorEnabled(false);

        QueryBuilder tempQueryBuilder = noteDao.queryBuilder();
        if (!TextUtils.isEmpty(text)){
            tempQueryBuilder.where(NoteDao.Properties.Text.like("%"+text+"%"));
            //tempQueryBuilder.where(NoteDao.Properties.Text.eq(text));
        }
        if (!TextUtils.isEmpty(comment)){
            tempQueryBuilder.where(NoteDao.Properties.Comment.like("%"+comment+"%"));
            //tempQueryBuilder.where(NoteDao.Properties.Comment.eq(comment));
        }

        query = tempQueryBuilder.orderAsc(NoteDao.Properties.Id)
                .build();

        list.clear();
        list.addAll(query.list());
    }

    private void addNote() {

        if (TextUtils.isEmpty(textEditText.getText())) {
            textInputLayout.setError("error,text cannot be null");
            return;
        }

        String text = textEditText.getText().toString();
        String comment = commentEditText.getText().toString();
        textInputLayout.setErrorEnabled(false);
        Note note = new Note(null, text, comment);
        noteDao.insert(note);

        searchAllNote();
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        textInputLayout = (TextInputLayout) findViewById(R.id.text);
        commentInputLayout = (TextInputLayout) findViewById(R.id.comment);
        textInputLayout.setHint("text");
        commentInputLayout.setHint("comment");

        textEditText = textInputLayout.getEditText();
        commentEditText = commentInputLayout.getEditText();

        bt_add = (Button) findViewById(R.id.add);
        bt_search = (Button) findViewById(R.id.search);
        bt_searchAll = (Button) findViewById(R.id.searchAll);
        bt_deleteAll = (Button) findViewById(R.id.deleteAll);

        listView = (ListView) findViewById(R.id.listview);
    }
}
