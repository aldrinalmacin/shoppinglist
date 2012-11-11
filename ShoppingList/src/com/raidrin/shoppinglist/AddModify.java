package com.raidrin.shoppinglist;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
public class AddModify extends Activity {

    private static final int MAX_ITEMS = 20;
	private static final String TAG_NAME = "Debug";
	private Button cancelButton;
	private Button saveButton;

	private Intent mainIntent;
	private Context context;
	private TableLayout shoppingListTableLayout;
	private EditText shoppingListEditText;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_modify);
        context = this;

        shoppingListEditText = (EditText) findViewById(R.id.shoppingListEditText);
        shoppingListEditText.selectAll();
    	shoppingListTableLayout = (TableLayout)findViewById(R.id.shoppingListTableLayout);
    	
        mainIntent = new Intent(context,ShoppingListApp.class);
        
        cancelButton = (Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivity(mainIntent);
				onDestroy();
			}
		});
        saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				for(int i=0;i<MAX_ITEMS;i++)
				{
					ShoppingListItem tempItem = (ShoppingListItem)shoppingListTableLayout.getChildAt(i);
					if(tempItem.getShoppingListName() != "" && tempItem.getQuantity()>0)
					{
				        Log.e(TAG_NAME, "Add Item: "+tempItem.getShoppingListName()+" Quantity: "+tempItem.getQuantity());
				        Controller controller = new Controller(context);
				        controller.addItem(tempItem.getShoppingListName(),tempItem.getQuantity());
				        controller.close();
			        }
				}				
				startActivity(mainIntent);
			}
		});
        
        runtimeCreateRows();
        
    }

    private void runtimeCreateRows() {
    	for(int i=0;i<MAX_ITEMS;i++)
    	{
    		shoppingListTableLayout.addView(new ShoppingListItem(this));
    	}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_modify, menu);
        return true;
    }
}
