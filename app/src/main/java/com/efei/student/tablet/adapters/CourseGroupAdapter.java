package com.efei.student.tablet.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.efei.student.tablet.R;
import com.efei.student.tablet.models.Course;
import com.efei.student.tablet.models.CourseGroup;
import com.efei.student.tablet.models.Teacher;
import com.efei.student.tablet.student.CourseActivity;
import com.efei.student.tablet.student.ListActivity;
import com.efei.student.tablet.utils.FileUtils;
import com.efei.student.tablet.utils.Subject;
import com.efei.student.tablet.utils.TextUtils;

import java.util.ArrayList;

public class CourseGroupAdapter extends ArrayAdapter<CourseGroup> {

    ListActivity activity;
    Context mContext;

    public CourseGroupAdapter(Context context, int resource, ArrayList<CourseGroup> items) {
        super(context, resource, items);
        mContext = context;
        this.activity = (ListActivity)context;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        // get the current lesson object
        final CourseGroup courseGroup = getItem(position);



        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        converterView = inflater.inflate(R.layout.student_course_group_item, null);


        // inflate the left view
        final Course leftCourse = courseGroup.left_course;

        TextView left_teacher = (TextView) converterView.findViewById(R.id.course_item_left_teacher);
        if (leftCourse.subject == Subject.MATH) {
            left_teacher.setBackgroundResource(R.drawable.course_math_teacher);
        } else {
            left_teacher.setBackgroundResource(R.drawable.course_phy_teacher);
        }
        Teacher leftTeacher = leftCourse.teacher();
        left_teacher.setText(mContext.getResources().getString(R.string.course_item_teacher_prefix) + TextUtils.insertBR(leftCourse.get_teacher_name()));

        ImageView leftAvatar = (ImageView) converterView.findViewById(R.id.course_item_left_avatar);
        Uri imgUri = Uri.parse(FileUtils.get_avatar_local_uri(leftTeacher));
        leftAvatar.setImageURI(imgUri);
        leftAvatar.setScaleType(ImageView.ScaleType.FIT_XY);

        TextView left_course_name = (TextView) converterView.findViewById(R.id.course_item_left_name);
        left_course_name.setText(leftCourse.name);

        TextView left_course_desc = (TextView) converterView.findViewById(R.id.course_item_left_desc);
        left_course_desc.setText(Html.fromHtml("<b>" + mContext.getResources().getString(R.string.course_item_desc_prefix) + "</b>" + leftCourse.desc));

        final View left_course = converterView.findViewById(R.id.left_course);

        left_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*  test video play
                Intent intent = new Intent(mContext, LessonActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, leftCourse.lessons()[0].server_id);
                mContext.startActivity(intent);
                */

                Intent intent = new Intent(mContext, CourseActivity.class).
                        putExtra(Intent.EXTRA_TEXT, leftCourse.server_id);
                mContext.startActivity(intent);
            }
        });


        // inflate the right view
        Course rightCourse = courseGroup.right_course;

        if (rightCourse == null) {
            converterView.findViewById(R.id.right_course).setVisibility(View.INVISIBLE);
            return converterView;
        }
        converterView.findViewById(R.id.right_course).setVisibility(View.VISIBLE);

        TextView right_teacher = (TextView) converterView.findViewById(R.id.course_item_right_teacher);
        if (rightCourse.subject == Subject.MATH) {
            right_teacher.setBackgroundResource(R.drawable.course_math_teacher);
        } else {
            right_teacher.setBackgroundResource(R.drawable.course_phy_teacher);
        }
        Teacher rightTeacher = rightCourse.teacher();
        right_teacher.setText(mContext.getResources().getString(R.string.course_item_teacher_prefix) + TextUtils.insertBR(rightCourse.get_teacher_name()));

        ImageView rightAvatar = (ImageView) converterView.findViewById(R.id.course_item_right_avatar);
        imgUri = Uri.parse(FileUtils.get_avatar_local_uri(rightTeacher));
        rightAvatar.setImageURI(imgUri);
        rightAvatar.setScaleType(ImageView.ScaleType.FIT_XY);

        TextView right_course_name = (TextView) converterView.findViewById(R.id.course_item_right_name);
        right_course_name.setText(rightCourse.name);

        TextView right_course_desc = (TextView) converterView.findViewById(R.id.course_item_right_desc);
        right_course_desc.setText(Html.fromHtml("<b>" + mContext.getResources().getString(R.string.course_item_desc_prefix) + "</b>" + rightCourse.desc));



        return converterView;
    }
}
