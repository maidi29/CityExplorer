package de.marmor.discover;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class QuizActivity extends Activity {
    List<Question> quesList;
    int score=0;
    int qid=0;
    Question currentQ;
    String markerID;
    TextView txtQuestion;
    RadioButton rda, rdb, rdc;
    Button butNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                markerID= null;
            } else {
                markerID= extras.getString("MarkerID");
            }
        } else {
            markerID = (String) savedInstanceState.getSerializable("MarkerID");
        }

        DbHelper db = new DbHelper(this);
        quesList=db.getAllQuestions();

        txtQuestion=(TextView)findViewById(R.id.textView1);
        rda=(RadioButton)findViewById(R.id.radio0);
        rdb=(RadioButton)findViewById(R.id.radio1);
        rdc=(RadioButton)findViewById(R.id.radio2);
        butNext=(Button)findViewById(R.id.button1);

        if (markerID.equals(getString(R.string.Title1))) {
            qid = 0;
        }
        if (markerID.equals(getString(R.string.Title2))) {
            qid = 5;
        }
        if (markerID.equals(getString(R.string.Title3))) {
            qid = 10;
        }

        currentQ = quesList.get(qid);
        setQuestionView();

        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup grp=(RadioGroup)findViewById(R.id.radioGroup1);
                RadioButton answer=(RadioButton)findViewById(grp.getCheckedRadioButtonId());
                if(currentQ.getANSWER().equals(answer.getText()))
                {
                    score++;
                }
                if (markerID.equals(getString(R.string.Title1))) {
                    if(qid<5) {
                        currentQ = quesList.get(qid);
                        setQuestionView();
                     }else{
                        startResultActivity();
                    }
                }
                if (markerID.equals(getString(R.string.Title2))) {
                    if(qid<10) {
                        currentQ = quesList.get(qid);
                        setQuestionView();
                    }else if (qid==10){
                        startResultActivity();
                    }
                }
                if (markerID.equals(getString(R.string.Title3))) {
                    if(qid<15) {
                        currentQ = quesList.get(qid);
                        setQuestionView();
                    }else if (qid==15){
                        startResultActivity();
                    }
                }
            }
        });
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quiz_layout, menu);
        return true;
    }*/
    private void startResultActivity() {
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putInt("score", score);
        b.putString("markerID",markerID);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void setQuestionView()
    {
        txtQuestion.setText(currentQ.getQUESTION());
        rda.setText(currentQ.getOPTA());
        rdb.setText(currentQ.getOPTB());
        rdc.setText(currentQ.getOPTC());
        qid++;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        lockScreenRotation(Configuration.ORIENTATION_PORTRAIT);
    }

    private void lockScreenRotation(int orientation)
    {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
