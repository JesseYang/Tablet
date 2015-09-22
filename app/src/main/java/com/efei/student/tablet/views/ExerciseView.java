package com.efei.student.tablet.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Homework;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Question;
import com.efei.student.tablet.student.LessonActivity;
import com.efei.student.tablet.utils.NetUtils;
import com.efei.student.tablet.utils.UiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private TextView mTopTv;

    private Button mNextBtn;
    private Button mBeginBtn;

    private LinearLayout mPreTestSummaryLayout;
    private RelativeLayout mExerciseLayout;

    Timer timer;

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
        mTopTv = (TextView) v.findViewById(R.id.exercise_top_tv);

        mNextBtn = (Button) v.findViewById(R.id.next_button);
        mBeginBtn = (Button) v.findViewById(R.id.begin_button);

        mPreTestSummaryLayout = (LinearLayout) v.findViewById(R.id.pre_test_summary);
        mExerciseLayout = (RelativeLayout) v.findViewById(R.id.exercise);

        mItemA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItems();
                mItemA.setBackgroundResource(R.drawable.ic_a_pressed);
                mCurAnswer = 0;
                mNextBtn.setEnabled(true);
                mNextBtn.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        });
        mItemB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItems();
                mItemB.setBackgroundResource(R.drawable.ic_b_pressed);
                mCurAnswer = 1;
                mNextBtn.setEnabled(true);
                mNextBtn.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        });
        mItemC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItems();
                mItemC.setBackgroundResource(R.drawable.ic_c_pressed);
                mCurAnswer = 2;
                mNextBtn.setEnabled(true);
                mNextBtn.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        });
        mItemD.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItems();
                mItemD.setBackgroundResource(R.drawable.ic_d_pressed);
                mCurAnswer = 3;
                mNextBtn.setEnabled(true);
                mNextBtn.setTextColor(mContext.getResources().getColor(R.color.white));
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
                timer.cancel();

                // gather the answer and duration of the current question
                if (mCurQuestion.type.equals("choice")) {
                    mAnswer[mCurQuestionIndex] = mCurAnswer;
                } else {
                    mAnswer[mCurQuestionIndex] = -1;
                }
                mDuration[mCurQuestionIndex] = mCurDuraton;

                // if answer is not provided, move to next
                if (mCurQuestionIndex == mQuestions.length - 1) {
                    // the last question
                    if (mCurType.equals("pre_test")) {
                        JSONObject params = new JSONObject();
                        try {
                            params.put("type", "pre_test");
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

                            // show the loading page
                            mTopTv.setText("正在提交数据，请稍后");
                            mExerciseLayout.setVisibility(GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (mCurType.equals("exercise")) {
                        JSONObject params = new JSONObject();
                        try {
                            params.put("type", "exercise");
                            params.put("exercise_id", mExercise.server_id);
                            params.put("question_id", mCurQuestion.server_id);
                            params.put("auth_key", mAuthKey);
                            params.put("answer", mAnswer[0]);
                            params.put("duration", mDuration[0]);
                            UploadAnswerTask uploadAnswerTask = new UploadAnswerTask();
                            uploadAnswerTask.execute(params);
                            ((LessonActivity)mContext).afterExercise();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    clearItems();
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
                if (mCurType.equals("pre_test")) {
                    // show the summary page
                    mExerciseLayout.setVisibility(GONE);
                    mPreTestSummaryLayout.setVisibility(VISIBLE);
                    mTopTv.setText("课前例题完成情况");
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

        if (type.equals("post_test")) {
            mTopTv.setText("课后测试：请在讲义上完成，并如实提交答案");
            mCurType = "post_test";
            mExercise = lesson.get_exercise(this.mContext, "post_test");

            if (mExercise == null) {
                return false;
            }
            mQuestions = mExercise.questions(this.mContext);
            mCurQuestionIndex = 0;
            mCurQuestion = mQuestions[mCurQuestionIndex];
            mAnswer = new int[mQuestions.length];
            mDuration = new int[mQuestions.length];
        }

        if (type.equals("pre_test")) {
            mTopTv.setText("课前例题：请在讲义上完成，并如实提交答案");
            mCurType = "pre_test";
            mExercise = lesson.get_exercise(this.mContext, "pre_test");

            if (mExercise == null) {
                return false;
            }
            mQuestions = mExercise.questions(this.mContext);
            mCurQuestionIndex = 0;
            mCurQuestion = mQuestions[mCurQuestionIndex];
            mAnswer = new int[mQuestions.length];
            mDuration = new int[mQuestions.length];
        }

        if (type.equals("exercise")) {
            mTopTv.setText("练习：请在讲义上完成，并如实提交答案");
            mCurType = "exercise";
            mExercise = lesson.get_exercise(this.mContext, "exercise");
            if (mExercise == null) {
                return false;
            }
            mCurQuestionIndex = 0;
            mCurQuestion = ((LessonActivity)mContext).mCurExercise;
            mQuestions = new Question[1];
            mQuestions[0] = mCurQuestion;
            mAnswer = new int[1];
            mDuration = new int[1];
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
        TextView content = UiUtils.generateTextView();
        content.setText(UiUtils.richTextToSpannable(ele));
        parent.addView(content);
    }

    public void renderElement(String[] ele, LinearLayout parent) {
        TextView content = UiUtils.generateTextView();
        content.setText(UiUtils.richTextToSpannable(ele));
        parent.addView(content);
    }

    public void renderQuestion() {
        mCurAnswer = -1;
        mCurDuraton = 0;
        // 1. fill the content
        LinearLayout contentLayout = (LinearLayout)findViewById(R.id.content_layout);
        contentLayout.removeAllViews();

        TextView temp;
        renderElement(mCurQuestion.content, contentLayout);

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
        mTextTimeTip.setText(mContext.getResources().getString(R.string.question_time_tip_short).replace("v1", String.valueOf(mCurQuestion.duration)));
        timer = new Timer();
        timer.schedule(new CountTime(0), 1000);

        if (mCurQuestion.type.equals("choice")) {
            mNextBtn.setEnabled(false);
            mNextBtn.setTextColor(mContext.getResources().getColor(R.color.title_bar_search_hint_text_color));
        }

        if (mCurQuestionIndex == mQuestions.length - 1) {
            mNextBtn.setText("提交");
        } else {
            mNextBtn.setText("下一题");
        }
    }

    Handler handler = new Handler(){

        public void handleMessage(Message msg) {
            mCurDuraton++;
            int second = msg.what;
            int minute = second / 60;
            second = second % 60;
            if (minute < mCurQuestion.duration) {
                if (mCurQuestion.duration - minute != mCurQuestion.duration)
                    mTextTimeTip.setText(mContext.getResources().getString(R.string.question_time_tip).replace("v1", String.valueOf(mCurQuestion.duration)).replace("v2", String.valueOf(mCurQuestion.duration - minute)));
            } else {
                mTextTimeTip.setText("你已经花了" + minute + "分" + second + "秒，还是先继续，过一会听听老师的讲解吧 :-)");
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
