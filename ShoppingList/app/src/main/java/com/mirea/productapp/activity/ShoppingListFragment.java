

package com.mirea.productapp.activity;

import android.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mirea.productapp.R;
import com.mirea.productapp.shoppinglist.ListItem;
import com.mirea.productapp.shoppinglist.ShoppingList;

import org.jetbrains.annotations.NotNull;

public class ShoppingListFragment extends Fragment implements EditBar.EditBarListener {

    private EditBar editBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerListAdapter adapter;
    private View rootView;
    private ShoppingList shoppingList;

    public static ShoppingListFragment newInstance(ShoppingList shoppingList) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        fragment.setShoppingList(shoppingList);
        return fragment;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
        connectList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @NotNull ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shoppinglist, container, false);

        recyclerView = rootView.findViewById(R.id.shoppingListView);
        registerForContextMenu(recyclerView);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        editBar = new EditBar(rootView, getActivity());
        editBar.addEditBarListener(this);
        editBar.enableAutoHideFAB(recyclerView);

        if (savedInstanceState != null) {
            editBar.restoreState(savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        editBar.removeEditBarListener(this);
        editBar.hide();
        super.onDestroyView();
    }

    private void connectList() {
        if (shoppingList != null && adapter != null) {
            adapter.connectShoppingList(shoppingList);
        }
        if (shoppingList != null && editBar != null) {
            editBar.connectShoppingList(shoppingList);
        }
    }

    @Override
    public void onStop() {
        adapter.disconnectShoppingList();
        editBar.disconnectShoppingList();
        super.onStop();
    }

    @Override
    public void onActivityCreated(@NotNull Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new RecyclerListAdapter(getActivity());
        connectList();
        adapter.registerRecyclerView(recyclerView);
        adapter.setOnItemLongClickListener(new RecyclerListAdapter.ItemLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                ListItem listItem = shoppingList.get(position);
                editBar.showEdit(position, listItem.getDescription(), listItem.getQuantity(), listItem.getPrice());
                return true;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public boolean onBackPressed() {
        if (editBar.isVisible()) {
            editBar.hide();
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        editBar.saveState(outState);
    }

    @Override
    public void onEditSave(int position, String description, String quantity, String price) {
        shoppingList.editItem(position, description, quantity, price);
        editBar.hide();
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onNewItem(String description, String quantity, String price) {
        shoppingList.add(description, quantity, price);
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }

    public void removeAllCheckedItems() {
        shoppingList.removeAllCheckedItems();
    }
}
