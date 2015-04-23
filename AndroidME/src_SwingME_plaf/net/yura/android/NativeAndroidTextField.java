package net.yura.android;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Canvas.InputHelper;
import javax.microedition.lcdui.TextBox;

import net.yura.android.lcdui.FontManager;
import net.yura.android.plaf.AndroidBorder;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

/**
 * TODO:
 *      if the native textobx is open, and setText is called on the SwingME component, it does not update the text
 *
 * DONEish:
 *      first click does not set the carret position to the correct place (this is normal behaviour on native Android)
 *      expand on multi-line text does not work
 *      addCaretListener does not work
 *      {@link TextComponent#changedUpdate(int, int) } is not called when text is entered
 *
 * a letter is added
 * we get a onTextChanged event
 * this updates the text in SwingME and asks for a repaint and revalidate
 * onDraw is called after the swingme repaint happens
 * this updats the size of the android editText
 *
 * @author Yura Mamyrin
 */
public class NativeAndroidTextField implements InputHelper,ChangeListener {

    /**
     * @see android.text.method.PasswordTransformationMethod
     */
    public static char DOT = '\u2022';

    private EditText editText; // this is the Android Component
    private TextBox textBox; // this is the J2ME component
    private TextComponent textField; // this is the SwingME component

    public NativeAndroidTextField() {

//System.out.println("[NativeAndroidTextField] ##################### construct");

    }

    private static final ChangeListener starter = new ChangeListener() {
        @Override
        public void changeEvent(Component source, int num) {
            if (num==Component.FOCUS_GAINED) {
                ((TextComponent)source).openNativeEditor();
            }
        }
    };

    public static void init() {
        TextComponent.STAR = DOT;
        TextBox.inputHelperClass = NativeAndroidTextField.class;
        TextComponent.staticFocusListener = starter;
    }


    public boolean onCheckIsTextEditor() {
        return false;
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return null;
    }

    public void start(TextBox textBox) {

        // if we are already started, do not start again, or screen will flicker
        if (editText!=null) {
            return;
        }

        final View view = textBox.getCanvasView();

//System.out.println("[NativeAndroidTextField] ##################### start");

        //textField = (Component)textBox.getCommandListener();

        Window window = DesktopPane.getDesktopPane().getSelectedFrame();
        textField = (TextComponent)window.getFocusOwner();

        this.textBox = textBox;

        editText = new NativeEditText(view);

        editText.setGravity( Gravity.TOP ); // on HTC the defualt is center, so we need to set it to top

        //ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //editText.setLayoutParams( lp ); // does nothing
        //editText.setScrollContainer(false); // does nothing
        //editText.setScroller(null); // does nothing



        midp2android();

        setNativeSettings(editText, textField, textBox.getConstraints(), textBox.getInitialInputMode());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {

                try {
                    android2swing();
                }
                catch (Exception ex) {
                    //#debug debug
                    Logger.warn("[NativeAndroidTextField] error in onTextChanged "+editText+" PLEASE SHOW YURA!!!", ex);
                }

            }
            @Override
            public void afterTextChanged(Editable edtbl) {

            }
        });

        textField.setForeground( 0x00FFFFFF ); // make it not have any foreground color, so it paints nothing

        // if we are not using the native android border
        if (!(textField.getBorder() instanceof AndroidBorder)) {

            editText.setBackgroundDrawable(null); // this removed the border from the edittext

            // does not get it 100% correct, so we only use it when we remove the native border
            Border insets = textField.getInsets();
            int margin = textField.getMargin();
            editText.setPadding(insets.getLeft()+margin, insets.getTop()+margin, insets.getRight()+margin, insets.getBottom()+margin);

        }


        // if we are using a system font in this text field, lets use it in the edittext
        javax.microedition.lcdui.Font font = textField.getFont().getFont();
        if (font!=null) {
            editText.setTextSize( TypedValue.COMPLEX_UNIT_PX, FontManager.getFont(font).getPaint().getTextSize() );
        }



        android.graphics.Paint.FontMetricsInt fm = editText.getPaint().getFontMetricsInt();
        int mTopPadding = fm.top - fm.ascent; // this gives a negative number, {@link android.text.StaticLayout#getTopPadding()} we add it to the current top padding to make it smaller
        int mBottomPadding = fm.bottom - fm.descent;
        // to avoid internal scrolling, we remove any bottom padding, at worst, it may overlap the bottom border
        //editText.setPadding(editText.getPaddingLeft(), editText.getPaddingTop()+ mTopPadding, editText.getPaddingRight(), 0);
        editText.setPadding(editText.getPaddingLeft(), editText.getPaddingTop() +mTopPadding, editText.getPaddingRight(), editText.getPaddingBottom() -mBottomPadding );
        //editText.setPadding(editText.getPaddingLeft(), editText.getPaddingTop()+ mTopPadding, editText.getPaddingRight(), textField.getInsets().getBottom()+((TextComponent)textField).getMargin() );




        // this updates the position of the component
        onDraw();






        // add it to the parent
        ViewGroup group = (ViewGroup)view.getParent();
        int count = group.getChildCount();
        List<View> remove = new ArrayList<View>(1);
        for (int c=0;c<count;c++) {
            View child = group.getChildAt(c);
            if (child != view) {
                remove.add(child);
            }
        }
        if (remove.size()!=1 || remove.get(0)!=editText ) { // do not do a add remove if you are simply adding then removing the same textbox
            for (View child:remove) {
                group.removeView(child);
            }
            group.addView(editText);
        }


        // get focus
        editText.requestFocus();


        TextComponent.staticFocusListener = this;
//        // get old focusListeners, we will keep this copy
//        focusListeners = textField.getFocusListeners();
//        // remove old focusListeners
//        for (int c=0;c<focusListeners.length;c++) {
//            textField.removeFocusListener( focusListeners[c] );
//        }
//        // add ourselves to listen for focus
//        textField.addFocusListener(this);

    }

//    ChangeListener[] focusListeners;

    public void onLayout() {

        Border insets = textField.getInsets();

        int w = textField.getWidthWithBorder();

        int h = textField.getPreferredHeight();

        editText.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED) );

        int h2 = editText.getMeasuredHeight() -insets.getTop() -insets.getBottom();

        if (h!=h2) {

//System.out.println("[NativeAndroidTextField] ##################### layout");

            textField.setPreferredSize(textField.getPreferredWidth(), h2 );

            DesktopPane.mySizeChanged(textField);
        }

    }

    int tx=-100,ty=-100,tw=-100,th=-100; // some random none valid numbers
    public void onDraw() {

        // this is the only swing me specific method

        int x = textField.getXOnScreen();
        int y = textField.getYOnScreen();
        int w = textField.getWidth();
        int h = textField.getHeight();

        // if the location has changed since last paint we need to move the component
        if (x!=tx || y!=ty || w!=tw || h!=th) {

//Log.d("YURA", "[NativeAndroidTextField] ##################### draw "+x+" "+tx+" "+y+" "+ty+" "+w+" "+tw+" "+h+" "+th);

            Border insets = textField.getInsets();
            int sx = x-insets.getLeft();
            int sy = y-insets.getTop();
            int sw = w+insets.getLeft()+insets.getRight();
            int sh = h+insets.getTop()+insets.getBottom();

            // we need to set the width and height AS WELL as the layout, or HTC phones will not wrap the text correctly
            editText.setWidth(sw); // the width is def wrong on HTC so we HAVE to set it
            //editText.setHeight(h);

            editText.layout(sx, sy, sx+sw, sy+sh );

            // this is a bit of a hack, not sure why this should be here, but without it, when we rotate the screen the line wrap does not adjust
            // but for some reason if we do not call this every time after changing the width it does not wrap the lines correctly
            if (textField instanceof TextArea) {
                // Calling this on honeycomb has a side-effect of removing the keyboard controls, so we can not call it every time
                if (w!=tw) {
                    // the method we need this to call is nullLayouts(); but in other version of android this method may not call nullLayouts();, so what then???
                    editText.setHorizontallyScrolling( false ); // !((TextArea)textField).getLineWrap() // even when we do NOT wrap lines, in SwingME its not the TextArea that does the scrolling but the ScrollPane its in
                }
            }

            Handler handler = editText.getHandler();
            if (handler!=null) {
                final EditText et = editText;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // this NEEDS to run on the handler thread or it will throw nullpointer
                            et.bringPointIntoView( et.getSelectionEnd() );
                        }
                        catch(Throwable th) {
                            //#debug debug
                            Logger.warn("error with " + et, th);
                        }
                    }
                });
            }

            tx=x;
            ty=y;
            tw=w;
            th=h;
        }
    }

    @Override
    public void changeEvent(Component source, int num) {
        if (num==Component.FOCUS_LOST) {

            // set the staticFocusListener back to what it used to be
            TextComponent.staticFocusListener = starter;

//            textField.removeFocusListener(this);
//
//            // set old focusListeners back
//            for (int c=0;c<focusListeners.length;c++) {
//                textField.addFocusListener( focusListeners[c] );
//            }
//            // fire old focusListeners
//            for (int c=0;c<focusListeners.length;c++) {
//                focusListeners[c].changeEvent(source, num);
//            }
//            focusListeners = null;

            close();
        }
    }

    public void close() {

//System.out.println("[NativeAndroidTextField] ##################### close");

        Canvas.CanvasView view = textBox.getCanvasView();

        view.requestFocus();

        // remove it

        final ViewGroup group = (ViewGroup)view.getParent();
        int count = group.getChildCount();
        final List<View> remove = new ArrayList<View>(1);
        for (int c=0;c<count;c++) {
            View child = group.getChildAt(c);
            if (child != view) {
                remove.add(child);
            }
        }


        // hide it from everything first, or it will be visible for the next paint
        for (View child:remove) {
           child.setVisibility( View.GONE );
        }


        // this is some fucked up crack induced crazy shit that android makes you do to simply remove a view
        group.post(new Runnable() {
            @Override
            public void run() {
                for (View child:remove) {
                    group.removeView(child);
                }
            }
        });

        // if we cleared the font, set it back
        textField.setForeground( Style.NO_COLOR );


        textField.setPreferredSize(textField.getPreferredWidth(), -1 );
        DesktopPane.mySizeChanged(textField);

        android2swing();

        // reset everything in the class
        view.clearInputHelper();
        editText = null;
        tx=-100;ty=-100;tw=-100;th=-100;

    }

    void midp2android() {

        int caret = textField.getCaretPosition();

        editText.setText(textBox.getString()); // this resets caret to 0 in the EditText

        // HACK to put the caret at the start of the line if your text is longer then 0
        // http://groups.google.com/group/android-developers/browse_thread/thread/d1c64f4c23b3c83b
        if (caret==0 && editText.getText().length() > 0) {
            editText.setSelection(1);
            editText.extendSelection(0);
        }
        // END HACK

        editText.setSelection( caret );
    }

    void android2swing() {
        // set text back

        String aText = String.valueOf( editText.getText() );

        if (!aText.equals(textBox.getString())) {
            textBox.setString(aText);
        }

        // fire ok button
        // this will set the text back into SwingME, and then close the native input
        // we do NOT want to do this as this closes the keyboard, and we do not want to do that
        //textBox.fireCommand(javax.microedition.lcdui.Command.OK);

        // just set the text back on the text component
        String mText = textBox.getString();
        if (!mText.equals( textField.getText() )) { // ALWAYS USE getText and NOT getValue, as getValue can give a Integer Object back for a number mode textbox
            textField.setText( mText );
        }

        int caret = editText.getSelectionEnd();

        if (textField.getCaretPosition() != caret) {
            textField.setCaretPosition( caret ); // in android no direct way to get the caret
        }
    }



    private static void fireActionPerformed(TextField tf) {
        final ActionListener[] als = tf.getActionListeners();
        for (ActionListener al:als) {
            al.actionPerformed( tf.getActionCommand() );
        }
    }

    class NativeEditText extends EditText {

        View view;

        public NativeEditText(View view) {
            super( AndroidMeActivity.DEFAULT_ACTIVITY );
            this.view = view;
        }

        /*
        // THIS DOES NOT WORK ON HTC DESIRE, does lots of crazy jumping around as soon as you start to type
        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                InputConnection in = super.onCreateInputConnection(outAttrs);

                // do not expand out the UI
                outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;

                return in;
        }
        */

        /**
         * this normally returns the min height of the background Drawable
         * so as we want there not to be any special min height, we return 0
         */
        protected int getSuggestedMinimumHeight() {
            return 0;
        }

        /**
         * in Android style, NOT swingME style
         * [0] = left
         * [1] = top
         * [2] = right
         * [3] = bottom
         */
        public int[] getVisibleRect() {
            int[] vis = textField.getVisibleRect();
            Border insets = textField.getInsets();
            vis[0] = vis[0] +insets.getLeft();
            vis[1] = vis[1] +insets.getTop();
            vis[2] = vis[0]+vis[2];
            vis[3] = vis[1]+vis[3];
            return vis;
        }

        @Override
        public void draw(android.graphics.Canvas canvas) {

            canvas.save();

            int[] vis = getVisibleRect();

            // we need to take internal scrolling into account, as none wraping textfields scroll left and right
            int mScrollX = getScrollX();
            int mScrollY = getScrollY();

            canvas.clipRect(vis[0] + mScrollX, vis[1] + mScrollY, vis[2] + mScrollX, vis[3] + mScrollY);

            super.draw(canvas);

            canvas.restore();
        }


        //private boolean mScrolled;
        //Override
        //public void cancelLongPress() {
        //    super.cancelLongPress();
        //    mScrolled = true;
        //}

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            final int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {

                int[] vis = getVisibleRect();

                // person has clicked somewhere outside us, we should not consume the event
                if (event.getX()<vis[0] || event.getX()>vis[2] || event.getY()<vis[1] || event.getY()>vis[3]) {
                    return false;
                }

                //mScrolled = false;
            }
            //if (mScrolled) {
            //    return true;
            //}

            return super.onTouchEvent(event);
        }

        /**
         * this is called from the constructor, on creation of the NativeEditText with a value of 0
         */
        @Override
        protected void onSelectionChanged(int selStart, int selEnd) {

        	try {
	            if (view!=null) { // we check the view is not null to make sure this is not the call from the constructor

	                int caretPosition = textField.getCaretPosition();

	                if (caretPosition!= selEnd) {
	                    textField.setCaretPosition(selEnd);
	                }
	            }
        	}
        	catch (Throwable th) {
        		//#debug debug
        		Logger.warn("Error in onSelectionChanged selStart="+selStart+" selEnd="+selEnd+" textField: >"+textField+"< >"+this+"< >"+getText()+"< PLEASE SHOW YURA!!!", th);
        	}
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {

            int swingMeCode = Canvas.getKeyCode(event);

            Window win = textField.getWindow();
            Button b = win.findMnemonicButton(swingMeCode);
            if (b!=null) {
                android2swing(); // send text to swingME

                //b.fireActionPerformed();// WE CAN NOT FIRE THE BUTTON DIRECTLY, as this may be the menu button
                boolean use = view.onKeyDown(keyCode, event); // we pass this event to AndroidME, for it to send it to SwingME

                // ========== SWING 2 MIDP ==========
                // get the text from swingME to J2ME
                String val = textField.getText();
                textBox.setString( val );

                midp2android(); // set the text back into the android editText
                return use;
            }

            boolean use = super.onKeyDown(keyCode, event);
            if (!use) return view.onKeyDown(keyCode, event);
            return true;
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            boolean use = super.onKeyUp(keyCode, event);
            if (!use) return view.onKeyUp(keyCode, event);
            return true;
        }

        @Override
        public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
            boolean use = super.onKeyMultiple(keyCode, repeatCount, event);
            if (!use) return view.onKeyMultiple(keyCode, repeatCount, event);
            return true;
        }

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            boolean use = super.onKeyPreIme(keyCode, event);
            if (!use) return view.onKeyPreIme(keyCode, event);
            return true;
        }

        @Override
        public boolean onKeyShortcut(int keyCode, KeyEvent event) {
            boolean use = super.onKeyShortcut(keyCode, event);
            if (!use) return view.onKeyShortcut(keyCode, event);
            return true;
        }

        /* need new version of sdk for this
        @Override
        public boolean onKeyLongPress(int keyCode, KeyEvent event) {
            boolean use = super.onKeyLongPress(keyCode, event);
            if (!use) return view.onKeyLongPress(keyCode, event);
            return true;
        }
        */

    }

    public static void setNativeSettings(final EditText editText, TextComponent textField, int constraints,String initialInputMode) {

        boolean singleLine = (textField instanceof TextField);
        editText.setSingleLine( singleLine );
        int inputType = TextBox.getInputType(constraints, initialInputMode);
        if (!singleLine) {
            inputType = inputType | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE;
        }
        editText.setInputType( inputType ); // this has to be done AFTER setSingleLine or passwords will break

        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter( textField.getMaxSize() ) });

        if (singleLine) { // actions only supported on single friend
            final TextField tf = (TextField)textField;
            if (tf.getActionListeners().length>0) {

                editText.setImeOptions( EditorInfo.IME_ACTION_DONE ); // DONE is returned by default (IME_ACTION_UNSPECIFIED), but we STILL have to set it, or getImeOptions will return 0

                editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == editText.getImeOptions() ) { // actionId is 6(IME_ACTION_DONE) is none is set. getImeOptions returns 0(IME_NULL) if none is set
                            fireActionPerformed(tf);
                        }
                        return true;
                    }
                });
            }
        }
    }

}
