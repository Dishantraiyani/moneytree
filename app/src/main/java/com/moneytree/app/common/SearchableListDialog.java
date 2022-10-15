package com.moneytree.app.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.moneytree.app.R;

import java.io.Serializable;
import java.util.List;

public class SearchableListDialog extends DialogFragment implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener, TextWatcher {

    private static final String ITEMS = "items";

    private ArrayAdapter listAdapter;

    private ListView _listViewItems;

    private SearchableItem _searchableItem;

    private OnSearchTextChanged _onSearchTextChanged;

    private EditText _searchView;

    private String _strTitle;

    private String _strPositiveButtonText;

    private DialogInterface.OnClickListener _onClickListener;

    public SearchableListDialog() {

    }

    public static SearchableListDialog newInstance(List items) {
        SearchableListDialog multiSelectExpandableFragment = new
                SearchableListDialog();

        Bundle args = new Bundle();
        args.putSerializable(ITEMS, (Serializable) items);

        multiSelectExpandableFragment.setArguments(args);

        return multiSelectExpandableFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Getting the layout inflater to inflate the view in an alert dialog.
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        // Crash on orientation change #7
        // Change Start
        // Description: As the instance was re initializing to null on rotating the device,
        // getting the instance from the saved instance
        if (null != savedInstanceState) {
            _searchableItem = (SearchableItem) savedInstanceState.getSerializable("item");
        }
        // Change End

        View rootView = inflater.inflate(R.layout.layout_searchable_dialog, null);
        setData(rootView);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(rootView);

        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return dialog;
    }

    // Crash on orientation change #7
    // Change Start
    // Description: Saving the instance of searchable item instance.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("item", _searchableItem);
        super.onSaveInstanceState(outState);
    }
    // Change End

    public void setTitle(String strTitle) {
        _strTitle = strTitle;
    }

    public void setPositiveButton(String strPositiveButtonText) {
        _strPositiveButtonText = strPositiveButtonText;
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        _strPositiveButtonText = strPositiveButtonText;
        _onClickListener = onClickListener;
    }

    public void setOnSearchableItemClickListener(SearchableItem searchableItem) {
        this._searchableItem = searchableItem;
    }

    public void setOnSearchTextChangedListener(OnSearchTextChanged onSearchTextChanged) {
        this._onSearchTextChanged = onSearchTextChanged;
    }

    private void setData(View rootView) {
        _searchView = rootView.findViewById(R.id.et_search);
        ImageView ivClose = rootView.findViewById(R.id.iv_close);
        _searchView.addTextChangedListener(this);
        _searchView.clearFocus();
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(_searchView.getWindowToken(), 0);

		ivClose.setOnClickListener(view -> _searchView.setText(""));

        List items = (List) getArguments().getSerializable(ITEMS);

        _listViewItems = (ListView) rootView.findViewById(R.id.listItems);

        //create the adapter by passing your ArrayList data
        listAdapter = new ArrayAdapter(getActivity(), R.layout.layout_spinner_item,
                items);

        //attach the adapter to the list
        _listViewItems.setAdapter(listAdapter);

        _listViewItems.setTextFilterEnabled(true);

        _listViewItems.setOnItemClickListener((parent, view, position, id) -> {
			_searchableItem.onSearchableItemClicked(listAdapter.getItem(position), position);
			getDialog().dismiss();
		});
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        _searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        listAdapter.filterData(s);

        return true;
    }

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		if (TextUtils.isEmpty(charSequence.toString())) {
//                _listViewItems.clearTextFilter();
			((ArrayAdapter) _listViewItems.getAdapter()).getFilter().filter(null);
		} else {
			((ArrayAdapter) _listViewItems.getAdapter()).getFilter().filter(charSequence.toString());
		}
		if (null != _onSearchTextChanged) {
			_onSearchTextChanged.onSearchTextChanged(charSequence.toString());
		}
	}

	@Override
	public void afterTextChanged(Editable editable) {

	}

	public interface SearchableItem<T> extends Serializable {
        void onSearchableItemClicked(T item, int position);
    }

    public interface OnSearchTextChanged {
        void onSearchTextChanged(String strText);
    }
}
