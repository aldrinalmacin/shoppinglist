package com.raidrin.shoppinglist;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * FileName: AddModify.java
 * 
 * @author Aldrin Jerome Almacin
 *         <p>
 *         <b>Date: </b>November 13, 2012
 *         </p>
 *         <p>
 *         <b>Description: </b>AddModify is a class that extends Activity which
 *         lets the user add an item, modify an existing one, give the list a
 *         name/modify an existing one, and save the given changes into the
 *         database.
 *         </p>
 * 
 */
public class AddModify extends Activity {

	private static final int MAX_ITEMS = 20; // The maximum number of items in a
												// single shopping list.

	private Context context; // Used to save a copy of the context so that it
								// can be used throughout the class.

	private TableLayout shoppingListTableLayout; // The tableLayout that holds
													// all the shopping lists.

	private TextView modifyCreateTextView; // The TextView that shows whether
											// the user is in the modify or in
											// the create mode.
	private EditText shoppingListEditText; // The edit text that takes the
											// shopping list name from the user.

	private Button cancelButton; // The button used to cancel the AddModify
									// activity.
	private Button saveButton; // The button used to allow the saving of the
								// shopping list items

	private Controller controller; // The controller which is used to get
									// processed information from the database.

	private String shoppingListName; // The name of the shopping list that is
										// given by the user.

	private boolean createMode; // The createMode that states whether the
								// activity is on create/modify.

	private int shoppingListId; // The id of the shoppingList being modified.

	/**
	 * When the application starts/created, onCreate method is executed.
	 * Therefore, all initializations are done in this method.
	 * 
	 * @param savedInstanceState
	 *            The saved state of the application
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// In order for this override to be valid. A call to the super method
		// must be done.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_modify); // Set the content view to
												// add_modify. File:
												// add_modify.xml
		context = this; // set the context to this.
		controller = new Controller(context); // Create an instance of
												// Controller.

		// Find the appropriate views from the XML and reference them through
		// the saved variables.
		shoppingListTableLayout = (TableLayout) findViewById(R.id.shoppingListTableLayout);
		modifyCreateTextView = (TextView) findViewById(R.id.modifyCreateTextView);
		shoppingListEditText = (EditText) findViewById(R.id.shoppingListEditText);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		saveButton = (Button) findViewById(R.id.saveButton);

		// Select all the text inside the shoppingListEditText
		shoppingListEditText.selectAll();

		// set the OnClickListener anonymous inner class of the cancelButton.
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish(); // Close this activity.
			} // End of onClick method.
		} // End of OnClickListener
				);

		// set the OnClickListener anonymous inner class of the saveButton.
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Take the shopping list id passed by the Main Activity to this
				// intent.
				// And save it to the shoppingListId field
				shoppingListId = getIntent().getIntExtra(
						ShoppingListApp.SHOPPING_LIST_ID, -1);

				// Save the shopping list name provided by the user to the
				// shoppingListName field.
				shoppingListName = shoppingListEditText.getText().toString();

				// If the mode is on modify, then update the tableName then
				// delete all the shoppingListItems that is a child of the given
				// shopping list.
				if (!createMode) {
					// Update the table name.
					controller.updateShoppingListName(shoppingListId,
							shoppingListName);
					// Delete all shopping list items. The reason for this is to
					// make the modification simple.
					// The data that were now in the EditTexts are the ones to
					// be saved.
					controller
							.deleteAllShoppingListItemsByShoppingId(shoppingListId);
				} // End of CreateMode if.

				// A flag that states whether the items are valid/invalid.
				// Only when the user gave a valid shopping list name, item
				// name, and quantity will this be valid.
				boolean itemsAddedValid = false;

				// If the shoppingListEditText has a value, then create or add
				// the items in the database
				if (!shoppingListEditText.getText().toString().equals(""))
					// Call the method to add items. The method returns whether
					// the values that are added are valid/invalid.
					// meaning the rows have at least one row. This is used to
					// check if the Dialog should be shown.
					itemsAddedValid = controller.createOrModifyShoppingList(
							shoppingListName, MAX_ITEMS,
							shoppingListTableLayout, createMode);

				// If the items added are valid then that means that the save is
				// done and this activity should now close
				if (itemsAddedValid)
					finish(); // Close this activity
				else
					// Show the alert dialog that states the reason why the
					// items are not saved.
					// When the fix button is clicked, the dialog will just
					// close.
					showAlertDialog(getString(R.string.shopping_list_error),
							getString(R.string.you_need_at_least),
							getString(R.string.fix),
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								} // End of onClick
							} // End of OnClickListener anonymous inner class
					);
			} // End of onClick
		} // End of OnClickListener anonymous inner class
		);

		// Create the rows at runtime.
		runtimeCreateRows();

		// Check whether the mode is create/modify and set the right text to be
		// shown to the user.
		// Then set the mode to what the current process is.
		switch (getIntent().getIntExtra(ShoppingListApp.CREATE_MODIFY, -1)) {
		case ShoppingListApp.CREATE_REQUEST:
			modifyCreateTextView.setText(getString(R.string.create_new_list));
			createMode = true;
			break;
		case ShoppingListApp.MODIFY_REQUEST:
			createMode = false;
			fillUpForms(); // Fill up all the forms from the values that the
							// modified form already possess.
			modifyCreateTextView.setText(getString(R.string.modify_list));
			break;
		} // End of Switch
	} // End of onCreate

	/**
	 * The back button key on the phone. Do nothing when the back button on the
	 * mobile phone is pressed. The reason is that we want the user to use the
	 * cancel button instead to close this activity.
	 */
	@Override
	public void onBackPressed() {
	}

	/**
	 * The AlertDialog that will be shown on the screen is created and shown.
	 * 
	 * @param title
	 *            The title of the AlertDialog box
	 * @param message
	 *            The message in the AlertDialog box
	 * @param buttonText
	 *            The text inside the OK button
	 * @param buttonOkListener
	 *            the listener for the OK button
	 */
	private void showAlertDialog(String title, String message,
			String buttonText, AlertDialog.OnClickListener buttonOkListener) {
		// Get the AlertDialog Builder class and save it in a variable.
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// Set the title, message, OK button, and the cancel listener of the
		// AlertDialog
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);

		// Positive button is using the buttonOKListener which is passed as a
		// parameter
		// as its event listener.
		alertDialogBuilder.setPositiveButton(buttonText, buttonOkListener);

		// Create the AlertDialog and show it on the screen
		alertDialogBuilder.create().show();
	} // End of showAlertDialog

	/**
	 * Used to fill up the forms when items are being modified.
	 */
	private void fillUpForms() {
		// Get the shoppingListId passed to this Activity when called via
		// Intent.
		int shoppingListId = getIntent().getIntExtra(
				ShoppingListApp.SHOPPING_LIST_ID, -1);
		// then call the controller that fills up the shopping items in this
		// Activity.
		controller.fillUpShoppingItems(shoppingListId, shoppingListEditText,
				shoppingListTableLayout);
	} // End of fillUpForms

	/**
	 * Create the rows at runtime. These rows will contain data about each item
	 * to be added in a list. The name of these views is ShoppingListItem. The
	 * items are added in the shoppingListTableLayout.
	 */
	private void runtimeCreateRows() {
		for (int i = 0; i < MAX_ITEMS; i++)
			shoppingListTableLayout.addView(new ShoppingListItem(this));
	} // End of runtimeCreateRows
} // End of AddModify Class
