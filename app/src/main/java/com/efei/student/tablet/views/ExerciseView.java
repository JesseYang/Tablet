package com.efei.student.tablet.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Homework;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Question;
import com.efei.student.tablet.student.LessonActivity;
import com.efei.student.tablet.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ExerciseView extends FrameLayout {

    private Lesson mLesson;
    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    private String mCurType;
    private boolean mShowing;
    public Homework mExercise;
    private Question[] mQuestions;
    private int mCurQuestionIndex;
    private Question mCurQuestion;
    private int mCurAnswer;
    private int mCurDuraton;
    private int[] mAnswer;
    private int[] mDuration;
    private String mAuthKey;

    private ImageView mItemA;
    private ImageView mItemB;
    private ImageView mItemC;
    private ImageView mItemD;

    private TextView mTextTimeTip;

    private Button mNextBtn;
    private Button mBeginBtn;

    private LinearLayout mPreTestSummaryLayout;
    private LinearLayout mExerciseLayout;

    Timer timer = new Timer();

    public ExerciseView(Context context) {
        super(context);
        mContext = context;
        mLesson = ((LessonActivity)context).mLesson;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MyPref", 0);
        mAuthKey = sharedPreferences.getString("auth_key", "");
    }

    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.exercise, null);
        initControllerView(mRoot);
        return mRoot;
    }

    private void initControllerView(View v) {
        mItemA = (ImageView) v.findViewById(R.id.itemImage_A);
        mItemB = (ImageView) v.findViewById(R.id.itemImage_B);
        mItemC = (ImageView) v.findViewById(R.id.itemImage_C);
        mItemD = (ImageView) v.findViewById(R.id.itemImage_D);

        mTextTimeTip = (TextView) v.findViewById(R.id.question_time_tip);

        mNextBtn = (Button) v.findViewById(R.id.next_button);
        mBeginBtn = (Button) v.findViewById(R.id.begin_button);

        mPreTestSummaryLayout = (LinearLayout) v.findViewById(R.id.pre_test_summary);
        mExerciseLayout = (LinearLayout) v.findViewById(R.id.exercise);

        mItemA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItems();
                mItemA.setBackgroundResource(R.drawable.ic_a_pressed);
                mCurAnswer = 0;
                mNextBtn.setEnabled(true);
            }
        });
        mItemB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItems();
                mItemB.setBackgroundResource(R.drawable.ic_b_pressed);
                mCurAnswer = 1;
                mNextBtn.setEnabled(true);
            }
        });
        mItemC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItems();
                mItemC.setBackgroundResource(R.drawable.ic_c_pressed);
                mCurAnswer = 2;
                mNextBtn.setEnabled(true);
            }
        });
        mItemD.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItems();
                mItemD.setBackgroundResource(R.drawable.ic_d_pressed);
                mCurAnswer = 3;
                mNextBtn.setEnabled(true);
            }
        });

        mBeginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurType.equals("pre_test")) {
                    ((LessonActivity)mContext).afterPreTest();
                }
            }
        });

        mNextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // first stop the timer for the current question
                Message message = new Message();
                message.what = -1;
                handler.sendMessage(message);

                // gather the answer and duration of the current question
                mAnswer[mCurQuestionIndex] = mCurAnswer;
                mDuration[mCurQuestionIndex] = mCurDuraton;

                // if answer is not provided, move to next
                if (mCurQuestionIndex == mQuestions.length - 1) {
                    // the last question
                    // todo: upload the answer

                    JSONObject params = new JSONObject();
                    try {
                        params.put("exercise_id", mExercise.server_id);
                        params.put("auth_key", mAuthKey);
                        JSONObject tablet_answer = new JSONObject();
                        JSONArray answer_ary = new JSONArray();
                        for (int i = 0; i < mAnswer.length; i++) {
                            answer_ary.put(mAnswer[i]);
                        }
                        JSONArray duration_ary = new JSONArray();
                        for (int i = 0; i < mDuration.length; i++) {
                            duration_ary.put(mDuration[i]);
                        }
                        tablet_answer.put("answer", answer_ary);
                        tablet_answer.put("duration", duration_ary);
                        params.put("tablet_answer", tablet_answer);
                        UploadAnswerTask uploadAnswerTask = new UploadAnswerTask();
                        uploadAnswerTask.execute(params);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // move to the next question
                    mCurQuestionIndex++;
                    mCurQuestion = mQuestions[mCurQuestionIndex];
                    renderQuestion();
                    clearItems();
                }
            }
        });
    }

    private class UploadAnswerTask extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            String response = NetUtils.post("/tablet/tablet_answers", params[0]);
            try {
                JSONObject jsonRes = new JSONObject(response);
                return jsonRes;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject retval) {
            // move to the next page based on the exercise type
            try {
                if (mCurType == "pre_test") {
                    // show the summary page
                    mExerciseLayout.setVisibility(GONE);
                    mPreTestSummaryLayout.setVisibility(VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void clearItems() {
        mItemA.setBackgroundResource(R.drawable.ic_a_unpressed);
        mItemB.setBackgroundResource(R.drawable.ic_b_unpressed);
        mItemC.setBackgroundResource(R.drawable.ic_c_unpressed);
        mItemD.setBackgroundResource(R.drawable.ic_d_unpressed);
    }

    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
        } catch (IllegalArgumentException ex) {
        }
        mShowing = false;
    }

    public boolean show(Lesson lesson, String type) {

        mExerciseLayout.setVisibility(VISIBLE);
        mPreTestSummaryLayout.setVisibility(GONE);

        if (type == "pre_test") {
            mCurType = "pre_test";
            mExercise = lesson.get_pre_test(this.mContext);

            if (mExercise == null) {
                return false;
            }
            mQuestions = mExercise.questions(this.mContext);
            mCurQuestionIndex = 0;
            mCurQuestion = mQuestions[mCurQuestionIndex];
            mAnswer = new int[mQuestions.length];
            mDuration = new int[mQuestions.length];
        }

        if (!mShowing && mAnchor != null) {

            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.FILL
            );

            mAnchor.addView(this, tlp);
            mShowing = true;
        }

        // show the question
        renderQuestion();

        return true;
    }

    public void renderElement(String ele, LinearLayout parent) {
        String[] ele_ary = ele.split("\\$\\$");
        for (int i = 0; i < ele_ary.length; i++) {
            if (ele_ary[i] == "") {
                continue;
            }
            if (ele_ary[i].startsWith("fig")) {
                // insert an image
                ImageView content = new ImageView(this.mContext);
                File storageRoot = Environment.getExternalStorageDirectory();
                String t1 = ele_ary[i].split("_")[1];
                String[] t2 = t1.split("\\*");
                String name = t2[0] + "." + t2[1];
                File imgFile = new File(storageRoot, "/efei/images/" + name);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    content.setImageBitmap(myBitmap);
                }
                parent.addView(content);
            } else {
                // insert a textview
                TextView content = new TextView(this.mContext);
                content.setText(ele_ary[i]);
                content.setTextSize(20);
                parent.addView(content);
            }
        }
    }

    public void renderQuestion() {
        mCurAnswer = -1;
        mCurDuraton = 0;
        // 1. fill the content
        LinearLayout contentLayout = (LinearLayout)findViewById(R.id.content_layout);
        contentLayout.removeAllViews();

        for (int i = 0; i < mCurQuestion.content.length; i++) {
            renderElement(mCurQuestion.content[i], contentLayout);
        }

        // 2. fill the items
        LinearLayout itemsLayout = (LinearLayout)findViewById(R.id.items_layout);
        if (mCurQuestion.type.equals("choice")) {
            itemsLayout.setVisibility(VISIBLE);
            ((LinearLayout) findViewById(R.id.itemContent_A)).removeAllViews();
            ((LinearLayout) findViewById(R.id.itemContent_B)).removeAllViews();
            ((LinearLayout) findViewById(R.id.itemContent_C)).removeAllViews();
            ((LinearLayout) findViewById(R.id.itemContent_D)).removeAllViews();
            renderElement(mCurQuestion.items[0], (LinearLayout) findViewById(R.id.itemContent_A));
            renderElement(mCurQuestion.items[1], (LinearLayout) findViewById(R.id.itemContent_B));
            renderElement(mCurQuestion.items[2], (LinearLayout) findViewById(R.id.itemContent_C));
            renderElement(mCurQuestion.items[3], (LinearLayout) findViewById(R.id.itemContent_D));
        } else {
            itemsLayout.setVisibility(GONE);
        }

        // render the time tip
        mTextTimeTip.setText(mContext.getResources().getString(R.string.question_time_tip).replace("v1", String.valueOf(mCurQuestion.duration)).replace("v2", String.valueOf(mCurQuestion.duration)));
        timer.schedule(new CountTime(0), 1000);

        if (mCurQuestion.type.equals("choice")) {
            mNextBtn.setEnabled(false);
        }

        if (mCurQuestionIndex == mQuestions.length - 1) {
            mNextBtn.setText("提交");
        }
    }

    Handler handler = new Handler(){

        public void handleMessage(Message msg) {
            mCurDuraton++;
            int second = msg.what;
            if (second == -1) {
                super.handleMessage(msg);
                return;
            }
            int minute = second / 60;
            second = second % 60;
            if (minute < mCurQuestion.duration) {
                mTextTimeTip.setText(mContext.getResources().getString(R.string.question_time_tip).replace("v1", String.valueOf(mCurQuestion.duration)).replace("v2", String.valueOf(mCurQuestion.duration - minute)));
            } else {
                mTextTimeTip.setText("你已经花了" + minute + "分" + second + "秒，还是先听听老师的讲解吧 :-)");
            }
            super.handleMessage(msg);
        }
    };

    class CountTime extends TimerTask {

        private final int time;
        CountTime ( int time )
        {
            this.time = time;
        }

        public void run() {
            Message message = new Message();
            message.what = time;
            handler.sendMessage(message);
            if (mShowing) {
                timer.schedule(new CountTime(this.time + 1), 1000);
            }
        }
    }
}
