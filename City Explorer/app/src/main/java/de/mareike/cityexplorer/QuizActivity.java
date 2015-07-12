package de.mareike.cityexplorer;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import de.mareike.cityexplorer.R;

public class QuizActivity extends Activity {
    List<Question> quesList;
    int score=0;
    int qid=0;
    Question currentQ;
    Integer markerID;
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
                markerID= extras.getInt("MarkerID");
            }
        } else {
            markerID = (Integer) savedInstanceState.getSerializable("MarkerID");
        }

        DbHelper db = new DbHelper(this);
        quesList=db.getAllQuestions();

        txtQuestion=(TextView)findViewById(R.id.textView1);
        rda=(RadioButton)findViewById(R.id.radio0);
        rdb=(RadioButton)findViewById(R.id.radio1);
        rdc=(RadioButton)findViewById(R.id.radio2);
        butNext=(Button)findViewById(R.id.button1);

        if (markerID == 1) {
            qid = 0;
        }
        else if (markerID == 2) {
            qid = 5;
        }
        else if (markerID == 3) {
            qid = 10;
        }
        else if (markerID ==4) {
            qid = 15;
        }
        else if (markerID ==5) {
            qid = 20;
        }
        else if (markerID ==6) {
            qid = 25;
        }
        else if (markerID ==7) {
            qid = 30;
        }

        currentQ = quesList.get(qid);
        setQuestionView();

        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup grp=(RadioGroup)findViewById(R.id.radioGroup1);
                if (grp.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(getBaseContext(),getString(R.string.toast_quiz_no_answer), Toast.LENGTH_LONG).show();
                }
                else
                {

                    RadioButton answer=(RadioButton)findViewById(grp.getCheckedRadioButtonId());
                    if(currentQ.getANSWER().equals(answer.getText()))
                    {
                        score++;
                    }
                    grp.clearCheck();
                    if (markerID == 1) {
                        if(qid<5) {
                            currentQ = quesList.get(qid);
                            setQuestionView();
                        }else{
                            startResultActivity();
                        }
                    }
                    else if (markerID == 2) {
                        if(qid<10) {
                            currentQ = quesList.get(qid);
                            setQuestionView();
                        }else if (qid==10){
                            startResultActivity();
                        }
                    }
                    else if (markerID == 3) {
                        if(qid<15) {
                            currentQ = quesList.get(qid);
                            setQuestionView();
                        }else if (qid==15){
                            startResultActivity();
                        }
                    }
                    else if (markerID == 4) {
                        if(qid<20) {
                            currentQ = quesList.get(qid);
                            setQuestionView();
                        }else if (qid==20){
                            startResultActivity();
                        }
                    }
                    else if (markerID == 5) {
                        if(qid<25) {
                            currentQ = quesList.get(qid);
                            setQuestionView();
                        }else if (qid==25){
                            startResultActivity();
                        }
                    }
                    else if (markerID == 6) {
                        if(qid<30) {
                            currentQ = quesList.get(qid);
                            setQuestionView();
                        }else if (qid==30){
                            startResultActivity();
                        }
                    }
                    else if (markerID == 7) {
                        if(qid<35) {
                            currentQ = quesList.get(qid);
                            setQuestionView();
                        }else if (qid==35){
                            startResultActivity();
                        }
                    }

                }

            }
        });
    }

    private void startResultActivity() {
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putInt("score", score);
        b.putInt("markerID",markerID);
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
