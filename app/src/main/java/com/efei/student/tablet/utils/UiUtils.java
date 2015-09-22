package com.efei.student.tablet.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public final class UiUtils
{
    private static final char IMG_PLACE_HOLDER = 'F';
    private static final String START_PROMPT_MATH = "math_";
    private static final String START_PROMPT_EQU = "equ_";
    private static final String START_PROMPT_FIG = "fig_";
    private static final String START_PROMPT_UND = "und_";
    private static final String START_PROMPT_SUB = "sub_";
    private static final String START_PROMPT_SUP = "sup_";
    private static final String START_PROMPT_ITA = "ita_";

    private static final String URL_API_IMAGE = "public/download/";

    private UiUtils()
    {
    }

    /**
     * content��items�Լ�answer_content����˫��Ԫ���ţ�$$����Ϊ��ʼ����ֹ��</br> 1.Ƕ�빫ʽ��</br> 2.ͼƬ��</br> 3.���и�ʽ������</br> ����أ� <li>und_blabla����ʾblabla�Ǵ����»��ߵ� <li>
     * sub_blabla����ʾblabla���±� <li>sup_blabla����ʾblabla���ϱ� <li>ita_blabla����ʾblabla��б�� <li>
     * equ_{name}*{width}*{height}����ʾһ����ʽͼƬ������nameΪ�ù�ʽͼƬ���ļ�����widthΪͼƬ��ȣ�heightΪͼƬ�߶ȡ���ͼƬ�����ص�ַΪ��#{image server}/public/download/#{name}.png�� <li>
     * math_{name}*{width}*{height}����equ_{name}*{width}*{height}��ȫһ�� <li>fig_{name}*{width}*{height}����ʾһ��ͼƬ���������ͬ��
     */
    public static SpannableString richTextToSpannable(final String txt)
    {
        final StringBuilder sbTmp = new StringBuilder();
        List<CharacterStyleInfo> csis = new ArrayList<CharacterStyleInfo>();

        String[] txtsBy$ = txt.split("\\$\\$");
        for (String txtBy$ : txtsBy$)
        {
            if (TextUtils.isBlank(txtBy$))
                continue;
            if (txtBy$.startsWith(START_PROMPT_MATH))
                parseImgAndConstructTmpText(sbTmp, csis, txtBy$, START_PROMPT_MATH);
            else if (txtBy$.startsWith(START_PROMPT_EQU))
                parseImgAndConstructTmpText(sbTmp, csis, txtBy$, START_PROMPT_EQU);
            else if (txtBy$.startsWith(START_PROMPT_FIG))
                parseImgAndConstructTmpText(sbTmp, csis, txtBy$, START_PROMPT_FIG);
            else if (txtBy$.startsWith(START_PROMPT_UND))
            // parseStyleTextAndConstructTmpText(sbTmp, csis, txtBy$, new UnderlineSpan());
            {
                String realString = txtBy$.substring(4);
                if (TextUtils.isBlank(realString))
                {
                    final int undSize = realString.length();
                    for (int i = 0; i < undSize; i++)
                        sbTmp.append('_');
                } else
                    parseStyleTextAndConstructTmpText(sbTmp, csis, txtBy$, new UnderlineSpan());
            } else if (txtBy$.startsWith(START_PROMPT_ITA))
                parseStyleTextAndConstructTmpText(sbTmp, csis, txtBy$, new StyleSpan(android.graphics.Typeface.ITALIC));
            else if (txtBy$.startsWith(START_PROMPT_SUP))
                parseStyleTextAndConstructTmpText(sbTmp, csis, txtBy$, new SuperscriptSpan());
            else if (txtBy$.startsWith(START_PROMPT_SUB))
                parseStyleTextAndConstructTmpText(sbTmp, csis, txtBy$, new SubscriptSpan());
            else
                sbTmp.append(txtBy$);
        }

        SpannableString ss = new SpannableString(sbTmp.toString());

        for (CharacterStyleInfo info : csis)
            ss.setSpan(info.imgSpan, info.posStart, info.posEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // ss.setSpan(new SuperscriptSpanAdjuster(0.0), 0, ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static TextView generateTextView() {
        TextView content = new TextView(EfeiApplication.getContext());
        content.setTextSize(28);
        content.setTextColor(EfeiApplication.getContext().getResources().getColor(android.R.color.black));
        return content;
    }

    public static SpannableString richTextToSpannable(final String[] lines)
    {
        if (lines.length == 0)
            return new SpannableString("");
        StringBuilder sb = new StringBuilder();
        for (String line : lines)
            sb.append(line).append('\n');
        sb.replace(sb.length() - 1, sb.length(), "");
        return richTextToSpannable(sb.toString());
    }

    private static void parseImgAndConstructTmpText(final StringBuilder newSb, List<CharacterStyleInfo> smis, String txtBy$, String startPrompt)
    {
        CharacterStyleInfo smi = parseImageSync(newSb.length(), txtBy$, startPrompt);
        smis.add(smi);
        newSb.append(IMG_PLACE_HOLDER);
    }

    private static void parseStyleTextAndConstructTmpText(final StringBuilder newSb, List<CharacterStyleInfo> smis, String txtBy$, CharacterStyle style)
    {
        String realString = txtBy$.substring(4);
        smis.add(new CharacterStyleInfo(newSb.length(), newSb.length() + realString.length(), style));
        newSb.append(realString);
    }

    private static CharacterStyleInfo parseImageSync(final int imgPos, String txtImage, String startPrompt)
    {
        String[] txtsByStar = txtImage.split("\\*");

        String imageFile = txtsByStar[0].substring(startPrompt.length()) + "." + txtsByStar[1];

        File storageRoot = Environment.getExternalStorageDirectory();
        File imgFile = new File(storageRoot, "/efei/images/" + imageFile);
        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        BitmapDrawable bmpDrawable = new BitmapDrawable(EfeiApplication.getContext().getResources(), bmp);

        final float fRadio = EfeiApplication.getContext().getResources().getDisplayMetrics().density * (float)2;
        bmpDrawable.setBounds(0, 0, (int) (Float.parseFloat(txtsByStar[2]) * fRadio), (int) (Float.parseFloat(txtsByStar[3]) * fRadio));
        ImageSpan img = new VerticalImageSpan(bmpDrawable);
        return new CharacterStyleInfo(imgPos, img);
    }

    private static class CharacterStyleInfo
    {
        final private int posStart;
        final private int posEnd;
        final private CharacterStyle imgSpan;

        public CharacterStyleInfo(int pos, CharacterStyle cs)
        {
            this.posStart = pos;
            this.posEnd = posStart + 1;
            this.imgSpan = cs;
        }

        public CharacterStyleInfo(int posStart, int posEnd, CharacterStyle imgSpan)
        {
            this.posStart = posStart;
            this.posEnd = posEnd;
            this.imgSpan = imgSpan;
        }

    }

    @Deprecated
    private static class CenteredImageSpan extends ImageSpan
    {
        public CenteredImageSpan(final Drawable drawable)
        {
            this(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
        }

        public CenteredImageSpan(final Drawable drawable, final int verticalAlignment)
        {
            super(drawable, verticalAlignment);
        }

        // Redefined locally because it is a private member from DynamicDrawableSpan
        private Drawable getCachedDrawable()
        {
            WeakReference<Drawable> wr = mDrawableRef;
            Drawable d = null;

            if (wr != null)
                d = wr.get();

            if (d == null)
            {
                d = getDrawable();
                mDrawableRef = new WeakReference<Drawable>(d);
            }

            return d;
        }

        private WeakReference<Drawable> mDrawableRef;
        private int transY;

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
        {
            Drawable b = getCachedDrawable();
            canvas.save();

            // int bCenter = b.getIntrinsicHeight() / 2;
            // int fontTop = paint.getFontMetricsInt().top;
            // int fontBottom = paint.getFontMetricsInt().bottom;
            // int transY = (bottom - b.getBounds().bottom) - (((fontBottom - fontTop) / 2) - bCenter);
            transY = (int) (b.getBounds().height() - paint.getTextSize() > 5 ? bottom - b.getBounds().bottom * 0.7f : bottom - b.getBounds().bottom);

            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm)
        {
            Drawable d = getCachedDrawable();
            Rect rect = new Rect(d.getBounds());
            rect.offset(0, transY);

            if (fm != null)
            {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            return rect.right;
        }
    }


    private static class VerticalImageSpan extends ImageSpan
    {

        public VerticalImageSpan(Drawable drawable)
        {
            super(drawable);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt)
        {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null)
            {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fontMetricsInt.ascent = -bottom;
                fontMetricsInt.top = -bottom;
                fontMetricsInt.bottom = top;
                fontMetricsInt.descent = top;
            }
            return rect.right;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
        {
            Drawable drawable = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }

}
