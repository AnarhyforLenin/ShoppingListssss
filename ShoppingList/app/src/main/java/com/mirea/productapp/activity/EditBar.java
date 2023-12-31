
package com.mirea.productapp.activity;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mirea.productapp.R;
import com.mirea.productapp.shoppinglist.ShoppingList;

import java.util.HashSet;
import java.util.Set;

public class EditBar implements ShoppingList.ShoppingListListener {
    private static final String KEY_SAVED_DESCRIPTION = "SAVED_DESCRIPTION";
    private static final String KEY_SAVED_QUANTITY = "SAVED_QUANTITY";
    private static final String KEY_SAVED_PRICE = "SAVED_PRICE";
    private static final String KEY_SAVED_MODE = "SAVED_MODE";
    private static final String KEY_SAVE_IS_VISIBLE = "SAVE_IS_VISIBLE";
    private final Context ctx;
    private final RelativeLayout layout;
    private final EditText descriptionText;
    private final EditText quantityText;
    private final EditText priceText;
    private final TextView duplicateWarnText;
    private Mode mode;
    private EditBarListener listener;
    private final FloatingActionButton fab;
    private int position;
    private final Set<String> descriptionIndex = new HashSet<>();
    private ShoppingList shoppingList;

    public EditBar(View boundView, final Context ctx) {
        this.ctx = ctx;
        this.layout = boundView.findViewById(R.id.layout_add_item);
        final ImageButton button = boundView.findViewById(R.id.button_add_new_item);
        this.descriptionText = boundView.findViewById(R.id.new_item_description);
        this.quantityText = boundView.findViewById(R.id.new_item_quantity);
        this.priceText = boundView.findViewById(R.id.new_item_price);
        this.duplicateWarnText = boundView.findViewById(R.id.text_warn);
        this.mode = Mode.ADD;

        layout.setVisibility(View.GONE);

        quantityText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onConfirm();
                return true;
            }
        });

        priceText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onConfirm();
                return true;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm();
            }
        });

        setButtonEnabled(button, false);
        descriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.equals("")) {
                    setButtonEnabled(button, false);
                } else {
                    setButtonEnabled(button, true);
                }
                checkDuplicate(str);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fab = boundView.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                showAdd();
            }
        });
    }

    private void checkDuplicate(String str) {
        if (mode != Mode.ADD || !isVisible()) {
            return;
        }
        if (descriptionIndex.contains(str.toLowerCase())) {
            duplicateWarnText.setText(ctx.getString(R.string.duplicate_warning, str));
            duplicateWarnText.setVisibility(View.VISIBLE);
        } else {
            duplicateWarnText.setVisibility(View.GONE);
        }
    }

    private void setButtonEnabled(ImageButton button, boolean enabled) {
        button.setEnabled(enabled);
        button.setClickable(enabled);
        button.setImageAlpha(enabled ? 255 : 100);
    }

    private void onConfirm() {
        String desc = descriptionText.getText().toString();
        String qty = quantityText.getText().toString();
        String prc = priceText.getText().toString();

        if (desc.equals("")) {
            Toast.makeText(ctx, R.string.error_description_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mode == Mode.ADD) {
            listener.onNewItem(desc, qty, prc);
            descriptionText.requestFocus();
        } else if (mode == Mode.EDIT) {
            listener.onEditSave(position, desc, qty, prc);
        }
        hide();
        descriptionText.setText("");
        quantityText.setText("");
        priceText.setText("");
    }

    public void showEdit(int position, String description, String quantity, String price) {
        this.position = position;
        prepare(Mode.EDIT, description, quantity, price);
        show();
    }

    public void showAdd() {
        prepare(Mode.ADD, "", "", "");
        show();
    }

    private void prepare(Mode mode, String description, String quantity, String prc) {
        this.mode = mode;
        quantityText.setText(quantity);
        priceText.setText(prc);
        descriptionText.setText("");
        descriptionText.append(description);
    }

    public void enableAutoHideFAB(RecyclerView view) {
        final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            private final int slop = ViewConfiguration.get(ctx).getScaledPagingTouchSlop();
            private float start = -1;
            private float triggerPosition = -1;

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (isNewEvent(e1)) {
                    start = e1.getY();
                }
                final float end = e2.getY();

                if (end - start > slop) {
                    showFAB();
                    start = end;
                } else if (end - start < -slop) {
                    hideFAB();
                    start = end;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            private boolean isNewEvent(MotionEvent e1) {
                boolean isNewEvent = e1 != null && !(e1.getY() == triggerPosition);
                if (isNewEvent) {
                    triggerPosition = e1.getY();
                }
                return isNewEvent;
            }
        };

        final GestureDetector detector = new GestureDetector(ctx, gestureListener);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
    }

    private void showFAB() {
        if (!isVisible()) {
            fab.show();
        }
    }

    private void hideFAB() {
        fab.hide();
    }

    private void show() {
        layout.setVisibility(View.VISIBLE);
        descriptionText.requestFocus();
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(descriptionText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hide() {
        descriptionText.clearFocus();
        quantityText.clearFocus();
        layout.setVisibility(View.GONE);
        duplicateWarnText.setText("");
        duplicateWarnText.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
        }
        fab.show();
        fab.requestFocus();
    }

    public boolean isVisible() {
        return layout.getVisibility() != View.GONE;
    }

    public void addEditBarListener(EditBarListener l) {
        listener = l;
    }

    public void removeEditBarListener(EditBarListener l) {
        if (l == listener) {
            listener = null;
        }
    }

    public void saveState(Bundle state) {
        state.putString(KEY_SAVED_DESCRIPTION, descriptionText.getText().toString());
        state.putString(KEY_SAVED_QUANTITY, quantityText.getText().toString());
        state.putString(KEY_SAVED_PRICE, priceText.getText().toString());
        state.putBoolean(KEY_SAVE_IS_VISIBLE, isVisible());
        state.putSerializable(KEY_SAVED_MODE, mode);
    }

    public void restoreState(Bundle state) {
        String description = state.getString(KEY_SAVED_DESCRIPTION);
        String quantity = state.getString(KEY_SAVED_QUANTITY);
        String price = state.getString(KEY_SAVED_PRICE);
        Mode mode = (Mode) state.getSerializable(KEY_SAVED_MODE);
        if (state.getBoolean(KEY_SAVE_IS_VISIBLE)) {
            prepare(mode, description, quantity, price);
            layout.setVisibility(View.VISIBLE);
            fab.hide();
        }
    }

    public void connectShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
        this.shoppingList.addListener(this);
        onShoppingListUpdate(shoppingList, null);
    }

    public void disconnectShoppingList() {
        if (shoppingList != null) {
            shoppingList.removeListener(this);
            shoppingList = null;
        }
    }

    @Override
    public void onShoppingListUpdate(ShoppingList list, ShoppingList.Event e) {
        if (mode == Mode.EDIT) {
            hide();
            return;
        }
        descriptionIndex.clear();
        descriptionIndex.addAll(list.createDescriptionIndex());
        checkDuplicate(descriptionText.getText().toString());
    }

    public interface EditBarListener {
        void onEditSave(int position, String description, String quantity, String price);

        void onNewItem(String description, String quantity, String price);
    }

    private enum Mode {
        EDIT, ADD
    }


}
