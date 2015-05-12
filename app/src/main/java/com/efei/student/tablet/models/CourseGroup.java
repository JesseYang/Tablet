package com.efei.student.tablet.models;

import android.content.Context;

public class CourseGroup {

    public Context mContext;

    public Course left_course;
    public Course right_course;


    public CourseGroup(Context context, Course left_course, Course right_course) {
        this.mContext = context;

        this.left_course = left_course;
        this.right_course = right_course;
    }
}
