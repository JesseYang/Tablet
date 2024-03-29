package com.efei.student.tablet.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import com.efei.student.tablet.models.ActionLog;
import com.efei.student.tablet.models.Homework;
import com.efei.student.tablet.models.Lesson;
import com.efei.student.tablet.models.Question;
import com.efei.student.tablet.student.LessonActivity;
import com.efei.student.tablet.utils.GlobalUtils;
import com.efei.student.tablet.utils.NetUtils;
import com.efei.student.tablet.utils.TextUtils;
import com.efei.student.tablet.utils.UiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class ExerciseView extends FrameLayout {

    private Lesson mLesson;
    private ViewGroup mAnchor;
    private Context mContext;
    private View mRoot;
    public String mCurType;
    private boolean mShowing;
    public Homework mExercise;
    private Question[] mQuestions;
    private String[] mQuestionIdAry;
    private int mCurQuestionIndex;
    public Question mCurQuestion;
    private int mCurAnswer;
    private int mCurDuraton;
    private int[] mAnswer;
    private int[] mDuration;
    private String mAuthKey;

    private boolean mRingPlayed;

    private ImageView mItemA;
    private ImageView mItemB;
    private ImageView mItemC;
    private ImageView mItemD;
    private ImageView mItemE;

    private ImageView mBlankFinish;
    private ImageView mBlankAbandon;

    private ImageView mBlankRight;
    private ImageView mBlankWrong;

    private ImageView mAnalysisFinish;
    private ImageView mAnalysisAbandon;

    private TextView mTextTimeTip;
    private TextView mTopTv;

    private Button mNextBtn;
    private ImageView mBeginBtn;
    private ImageView mOverBtn;

    private LinearLayout mPreTestSummaryLayout;
    private LinearLayout mPostTestSummaryLayout;
    private RelativeLayout mExerciseLayout;

    private LinearLayout itemsLayout;
    private LinearLayout blankAnswerLayout;
    private LinearLayout analysisAnswerLayout;
    private LinearLayout blankAnswerContentLayout;
    private LinearLayout blankAnimationLayout;
    private LinearLayout mPreTestSummaryStatLayout;
    private LinearLayout mPostTestSummaryStatLayout;

    private TextView mPreTestSummaryText;
    private TextView mPostTestSummaryText;

    private boolean btnFrozen;
    public boolean mAdmin;
    public boolean mComplete;

    final Animator.AnimatorListener next = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            moveToNext();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };


    private int abadon = 0;

    Timer timer;

    public ExerciseView(Context context) {
        super(context);
        mContext = context;
        mLesson = ((LessonActivity)context).mLesson;
        mAdmin = GlobalUtils.isAdmin(mContext);
        // mComplete = Progress.getProgress(context, mLesson).equals("is_complete");
        mComplete = ((LessonActivity)context).mComplete;
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
        itemsLayout = (LinearLayout) v.findViewById(R.id.items_layout);
        blankAnswerLayout = (LinearLayout) v.findViewById(R.id.blank_answer);
        analysisAnswerLayout = (LinearLayout) v.findViewById(R.id.analysis_answer);
        blankAnimationLayout = (LinearLayout) v.findViewById(R.id.blank_animation);
        blankAnswerContentLayout = (LinearLayout) v.findViewById(R.id.blank_answer_content);
        mPreTestSummaryStatLayout = (LinearLayout) v.findViewById(R.id.pre_test_summary_stat);
        mPostTestSummaryStatLayout = (LinearLayout) v.findViewById(R.id.post_test_summary_stat);

        mPreTestSummaryText = (TextView) v.findViewById(R.id.pre_test_summary_text);
        mPostTestSummaryText = (TextView) v.findViewById(R.id.post_test_summary_text);

        mItemA = (ImageView) v.findViewById(R.id.itemImage_A);
        mItemB = (ImageView) v.findViewById(R.id.itemImage_B);
        mItemC = (ImageView) v.findViewById(R.id.itemImage_C);
        mItemD = (ImageView) v.findViewById(R.id.itemImage_D);
        mItemE = (ImageView) v.findViewById(R.id.itemImage_E);

        mBlankFinish = (ImageView) v.findViewById(R.id.blank_finish);
        mBlankAbandon = (ImageView) v.findViewById(R.id.blank_abandon);

        mBlankRight = (ImageView) v.findViewById(R.id.blank_right);
        mBlankWrong = (ImageView) v.findViewById(R.id.blank_wrong);

        mAnalysisFinish = (ImageView) v.findViewById(R.id.analysis_finish);
        mAnalysisAbandon = (ImageView) v.findViewById(R.id.analysis_abandon);

        mTextTimeTip = (TextView) v.findViewById(R.id.question_time_tip);
        mTopTv = (TextView) v.findViewById(R.id.exercise_top_tv);

        mNextBtn = (Button) v.findViewById(R.id.next_button);
        mBeginBtn = (ImageView) v.findViewById(R.id.begin_button);
        mOverBtn = (ImageView) v.findViewById(R.id.over_button);

        mPreTestSummaryLayout = (LinearLayout) v.findViewById(R.id.pre_test_summary);
        mPostTestSummaryLayout = (LinearLayout) v.findViewById(R.id.post_test_summary);
        mExerciseLayout = (RelativeLayout) v.findViewById(R.id.exercise);

        mItemA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { btnClickHandler(mItemA);}
        });
        mItemB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { btnClickHandler(mItemB);    }
        });
        mItemC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { btnClickHandler(mItemC);    }
        });
        mItemD.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { btnClickHandler(mItemD); }
        });
        mItemE.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { btnClickHandler(mItemE); }
        });

        mBlankFinish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mBlankFinish.setBackgroundResource(R.drawable.ic_ok_pressed);
                blankAnswerContentLayout.setVisibility(VISIBLE);
                ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(blankAnimationLayout, "translationX", -2048);
                mSlidInAnimator.setDuration(200);
                mSlidInAnimator.start();
            }
        });

        mBlankAbandon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClickHandler(mBlankAbandon);
            }
        });
        mBlankRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { btnClickHandler((mBlankRight)); }
        });
        mBlankWrong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { btnClickHandler(mBlankWrong); }
        });

        mAnalysisFinish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { btnClickHandler(mAnalysisFinish); }
        });
        mAnalysisAbandon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { btnClickHandler(mAnalysisAbandon); }
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
                moveToNext();
            }
        });

        mOverBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { ((LessonActivity)mContext).returnToCourse(); }
        });
    }

    public void btnClickHandler(ImageView btn) {
        if (btnFrozen)
            return;
        ImageView[] btns = new ImageView[] { mItemA, mItemB, mItemC, mItemD, mItemE, mBlankAbandon, mBlankRight, mBlankWrong, mAnalysisFinish, mAnalysisAbandon};
        int[] answer = new int[] { 0, 1, 2, 3, -1, -1, 1, -1, 0, -1 };
        int[] resId = new int[] { R.drawable.ic_a_pressed, R.drawable.ic_b_pressed, R.drawable.ic_c_pressed, R.drawable.ic_d_pressed, R.drawable.ic_e_pressed, R.drawable.ic_unok_pressed, R.drawable.ic_ok_pressed, R.drawable.ic_unok_pressed, R.drawable.ic_ok_pressed, R.drawable.ic_unok_pressed };
        int index = Arrays.asList(btns).indexOf(btn);
        btn.setBackgroundResource(resId[index]);
        mCurAnswer = answer[index];
        waitForNext();
    }

    public void waitForNext() {
        btnFrozen = true;
        ObjectAnimator mSlideOutAnimator = ObjectAnimator.ofFloat(mExerciseLayout, "translationX", 0);
        mSlideOutAnimator.setDuration(200);
        mSlideOutAnimator.start();
        mSlideOutAnimator.addListener(next);
    }

    public void moveToNext() {

        if (!mAdmin && !mComplete) {
            // first stop the timer for the current question
            timer.cancel();
            // gather the answer and duration of the current question
            mAnswer[mCurQuestionIndex] = mCurAnswer;
            mDuration[mCurQuestionIndex] = mCurDuraton;
            mQuestionIdAry[mCurQuestionIndex] = mCurQuestion.server_id;
        }

        if (mCurAnswer == -1) {
            abadon++;
        } else {
            abadon = 0;
        }

        // if answer is not provided, move to next
        if (mCurQuestionIndex == mQuestions.length - 1) {
            // the last question
            if (mCurType.equals("pre_test")) {
                JSONObject params = new JSONObject();
                try {
                    params.put("type", "pre_test");
                    params.put("exercise_id", mExercise.server_id);
                    params.put("question_id", TextUtils.join(mQuestionIdAry, ","));
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
                    UploadAnswerTask uploadAnswerTask = new UploadAnswerTask(this.mContext);
                    uploadAnswerTask.execute(params);

                    // show the loading page
                    mTopTv.setText("正在提交数据，请稍后");
                    mExerciseLayout.setVisibility(GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (mCurType.equals("post_test")){
                JSONObject params = new JSONObject();
                try {
                    params.put("type", "post_test");
                    params.put("exercise_id", mExercise.server_id);
                    params.put("question_id", TextUtils.join(mQuestionIdAry, ","));
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
                    UploadAnswerTask uploadAnswerTask = new UploadAnswerTask(this.mContext);
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
                    params.put("question_id", TextUtils.join(mQuestionIdAry, ","));
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
                    UploadAnswerTask uploadAnswerTask = new UploadAnswerTask(this.mContext);
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

    private class UploadAnswerTask extends AsyncTask<JSONObject, Void, JSONObject> {

        private Context mContext;
        public UploadAnswerTask(Context context) {
            this.mContext = context;
        }


        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            if (mAdmin || mComplete) {
                return null;
            } else {
                String response = NetUtils.post(this.mContext, "/tablet/tablet_answers", params[0]);
                try {
                    JSONObject jsonRes = new JSONObject(response);
                    return jsonRes;
                } catch (Exception e) {
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject retval) {
            // move to the next page based on the exercise type
            try {
                if (mCurType.equals("pre_test")) {
                    // show the pre test summary page
                    mExerciseLayout.setVisibility(GONE);
                    mPreTestSummaryLayout.setVisibility(VISIBLE);
                    mPostTestSummaryLayout.setVisibility(GONE);
                    int correct = 0, incorrect = 0, unknown = 0;
                    for (int i = 0; i < mQuestions.length; i++) {
                        Button b = new Button(mContext);
                        b.setText(String.valueOf(i + 1));
                        int is_answer_correct = mQuestions[i].is_answer_correct(mAnswer[i]);
                        if (is_answer_correct > 0) {
                            b.setTextColor(mContext.getResources().getColor(R.color.white));
                            b.setBackgroundResource(R.drawable.ic_right);
                            correct++;
                        } else if (is_answer_correct < 0) {
                            b.setTextColor(mContext.getResources().getColor(R.color.white));
                            b.setBackgroundResource(R.drawable.ic_wrong);
                            incorrect++;
                        } else {
                            b.setTextColor(mContext.getResources().getColor(R.color.black));
                            b.setBackgroundResource(R.drawable.ic_unknown);
                            unknown++;
                        }
                        mPreTestSummaryStatLayout.addView(b);
                    }
                    String msg = "";
                    if (mComplete) {
                        msg = "你之前已经完成本讲学习。";
                    } else if (mAdmin) {
                        msg = "";
                    } else if (unknown == 0) {
                        msg = "统计结果：正确" + String.valueOf(correct) + "道，错误" + String.valueOf(incorrect) + "道。";
                    } else {
                        msg = "统计结果：正确" + String.valueOf(correct) + "道，错误" + String.valueOf(incorrect) + "道，待批改" + String.valueOf(unknown) + "道。";
                    }
                    mPreTestSummaryText.setText(msg);
                    mTopTv.setText("课前例题完成情况");
                    ActionLog.create_new(mContext, mLesson.server_id, ActionLog.ENTRY_PRE_TEST_RESULT);
                }
                if (mCurType.equals("post_test")) {

                    mExerciseLayout.setVisibility(GONE);
                    mPreTestSummaryLayout.setVisibility(GONE);
                    mPostTestSummaryLayout.setVisibility(VISIBLE);
                    int correct = 0, incorrect = 0, unknown = 0;
                    for (int i = 0; i < mQuestions.length; i++) {
                        final int q_index = i;
                        Button b = new Button(mContext);
                        b.setText(String.valueOf(i + 1));
                        if (mAdmin || mComplete) {
                            b.setTextColor(mContext.getResources().getColor(R.color.black));
                            b.setBackgroundResource(R.drawable.ic_unknown);
                            unknown++;
                        } else {
                            int is_answer_correct = mQuestions[i].is_answer_correct(mAnswer[i]);
                            if (is_answer_correct > 0) {
                                b.setTextColor(mContext.getResources().getColor(R.color.white));
                                b.setBackgroundResource(R.drawable.ic_right);
                                correct++;
                            } else if (is_answer_correct < 0) {
                                b.setTextColor(mContext.getResources().getColor(R.color.white));
                                b.setBackgroundResource(R.drawable.ic_wrong);
                                incorrect++;
                            } else {
                                b.setTextColor(mContext.getResources().getColor(R.color.black));
                                b.setBackgroundResource(R.drawable.ic_unknown);
                                unknown++;
                            }
                        }
                        mPostTestSummaryStatLayout.addView(b);
                        b.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((LessonActivity) mContext).playQuestionVideo(mQuestions[q_index].video_url);
                            }
                        });
                    }
                    mPostTestSummaryText.setText("统计结果：正确" + String.valueOf(correct) + "道，错误" + String.valueOf(incorrect) + "道。");
                    mTopTv.setText("课后测试完成情况");
                    ActionLog.create_new(mContext, mLesson.server_id, ActionLog.ENTRY_POST_TEST_RESULT);
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
        mItemE.setBackgroundResource(R.drawable.ic_e_unpressed);

        mBlankFinish.setBackgroundResource(R.drawable.ic_ok_unpressed);
        mBlankAbandon.setBackgroundResource(R.drawable.ic_unok_unpressed);
        mBlankRight.setBackgroundResource(R.drawable.ic_ok_unpressed);
        mBlankWrong.setBackgroundResource(R.drawable.ic_unok_unpressed);
        mAnalysisFinish.setBackgroundResource(R.drawable.ic_ok_unpressed);
        mAnalysisAbandon.setBackgroundResource(R.drawable.ic_unok_unpressed);
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
        ((LessonActivity)mContext).adjustBrightness();
    }

    public boolean show(Lesson lesson, String type) {

        mRingPlayed = false;

        if (!type.equals("keep")) {
            mExerciseLayout.setVisibility(VISIBLE);
            mPreTestSummaryLayout.setVisibility(GONE);
            mPostTestSummaryLayout.setVisibility(GONE);
        }

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
            mQuestionIdAry = new String[mQuestions.length];

            // for admin user or completed lessons, directly to the summary page
            if (mAdmin || mComplete) {
                mCurQuestionIndex = mQuestions.length - 1;
                moveToNext();
            } else {
                renderQuestion();
            }
        } else if (type.equals("pre_test")) {
            if (mAdmin || mComplete)
                return false;
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
            mQuestionIdAry = new String[mQuestions.length];
            renderQuestion();
        } else if (type.equals("exercise")) {
            if (mAdmin || mComplete)
                return false;
            mTopTv.setText("练习：请在讲义上完成，并如实提交答案");
            mCurType = "exercise";
            mExercise = lesson.get_exercise(this.mContext, "exercise");
            if (mExercise == null) {
                return false;
            }
            mQuestions = ((LessonActivity)mContext).mCurExercise;
            mCurQuestionIndex = 0;
            mCurQuestion = mQuestions[mCurQuestionIndex];
            mAnswer = new int[mQuestions.length];
            mDuration = new int[mQuestions.length];
            mQuestionIdAry = new String[mQuestions.length];
            renderQuestion();
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

        ((LessonActivity)mContext).adjustBrightness(0F);

        return true;
    }

    public void renderElement(String ele, LinearLayout parent) {
        parent.removeAllViews();
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

        int index = Arrays.asList(mExercise.q_ids).indexOf(mCurQuestion.server_id);

        String homework = "";
        if (mCurType == "pre_test") {
            homework = "例";
        } else if (mCurType == "exercise") {
            homework = "练";
        } else {
            homework = "测";
        }

        String q_type = "";
        if (mCurQuestion.type.equals("choice")) {
            q_type = "【" + homework + String.valueOf(index + 1) + ". 选择题】";
        } else if (mCurQuestion.type.equals("blank")) {
            q_type = "【" + homework + String.valueOf(index + 1) + ". 填空题】";
        } else if (mCurQuestion.type.equals("analysis")) {
            q_type = "【" + homework + String.valueOf(index + 1) + ". 解答题】";
        }

        mCurQuestion.content[0] = q_type + mCurQuestion.content[0];
        renderElement(mCurQuestion.content, contentLayout);

        // 2. fill the items
        if (mCurQuestion.type.equals("choice")) {
            blankAnswerLayout.setVisibility(GONE);
            analysisAnswerLayout.setVisibility(GONE);
            itemsLayout.setVisibility(VISIBLE);
            renderElement(mCurQuestion.items[0], (LinearLayout) findViewById(R.id.itemContent_A));
            renderElement(mCurQuestion.items[1], (LinearLayout) findViewById(R.id.itemContent_B));
            renderElement(mCurQuestion.items[2], (LinearLayout) findViewById(R.id.itemContent_C));
            renderElement(mCurQuestion.items[3], (LinearLayout) findViewById(R.id.itemContent_D));
            renderElement(cry_content(), (LinearLayout) findViewById(R.id.itemContent_E));
        } else if (mCurQuestion.type.equals("blank")) {
            itemsLayout.setVisibility(GONE);
            analysisAnswerLayout.setVisibility(GONE);
            blankAnswerLayout.setVisibility(VISIBLE);
            renderElement("正确答案：" + mCurQuestion.answer_content[0], (LinearLayout) findViewById(R.id.blank_answer_content));
            blankAnswerContentLayout.setVisibility(INVISIBLE);
            blankAnimationLayout.setTranslationX(0);
            renderElement("正确 $$fig_happy*png*20*20$$", (LinearLayout) findViewById(R.id.blank_right_content));
            renderElement("错误 $$fig_cry*png*20*20$$", (LinearLayout) findViewById(R.id.blank_wrong_content));
            renderElement("做完啦 $$fig_happy*png*20*20$$", (LinearLayout) findViewById(R.id.blank_yes_content));
            renderElement(cry_content(), (LinearLayout) findViewById(R.id.blank_no_content));
        } else if (mCurQuestion.type.equals("analysis")) {
            itemsLayout.setVisibility(GONE);
            blankAnswerLayout.setVisibility(GONE);
            analysisAnswerLayout.setVisibility(VISIBLE);
            renderElement("做完啦 $$fig_happy*png*20*20$$", (LinearLayout) findViewById(R.id.analysis_yes_content));
            renderElement(cry_content(), (LinearLayout) findViewById(R.id.analysis_no_content));
        }

        // render the time tip
        mTextTimeTip.setText(mContext.getResources().getString(R.string.question_time_tip_short).replace("v1", String.valueOf(mCurQuestion.duration)));
        timer = new Timer();
        timer.schedule(new CountTime(0), 1000);


        mNextBtn.setEnabled(false);
        mNextBtn.setTextColor(mContext.getResources().getColor(R.color.title_bar_search_hint_text_color));

        /*
        if (mCurQuestionIndex == mQuestions.length - 1) {
            mNextBtn.setText("提交");
        } else {
            mNextBtn.setText("下一题");
        }
        */
        btnFrozen = false;
    }

    public String cry_content() {
        if (abadon == 1) {
            return "不会做 $$fig_cry*png*20*20$$";
        } else {
            return "不会做 $$fig_cry*png*20*20$$";
            // return "还是不会做 $$fig_cry*png*20*20$$ $$fig_cry*png*20*20$$";
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
                if (mRingPlayed == false) {
                    mRingPlayed = true;
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(mContext.getApplicationContext(), notification);
                    r.play();
                }
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
