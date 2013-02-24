package com.gmail.yuyang226.contactswidget.pro.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.gmail.yuyang226.contactswidget.pro.ContactAccessor;
import com.gmail.yuyang226.contactswidget.pro.ContactsWidgetProvider;
import com.gmail.yuyang226.contactswidget.pro.R;
import com.gmail.yuyang226.contactswidget.pro.models.ContactGroup;

public class ContactsWidgetConfigurationActivity extends Activity  {
//	private static final String TAG = ContactsWidgetConfigurationActivity.class.getName();

	public static final String CONTACT_GROUP
	= "com.gmail.yuyang226.contactswidget.config.contact_group"; //$NON-NLS-1$
	public static final long CONTACT_STARRED_GROUP_ID = -999L;
	public static final long CONTACT_MY_CONTACTS_GROUP_ID = -888L;
	public static final String CONTACT_STARRED = ContactsContract.Contacts.STARRED + " = '1'"; //$NON-NLS-1$
	
	public static final String PREF_GROUP_PREFIX = "group_"; //$NON-NLS-1$
	public static final String PREF_SORTING_PREFIX = "sorting_"; //$NON-NLS-1$
	public static final String PREF_SHOWNAME_PREFIX = "showname_"; //$NON-NLS-1$
	public static final String PREF_MAXNUMBER_PREFIX = "maxnumber_"; //$NON-NLS-1$
	public static final String PREF_DIRECTDIAL_PREFIX = "directdial_"; //$NON-NLS-1$
	public static final String PREF_SHOWPHONENUMBER_PREFIX = "showphonenumber_"; //$NON-NLS-1$
	public static final String PREF_SHOWPEOPLE_PREFIX = "showpeope_"; //$NON-NLS-1$
	public static final String PREF_IMAGESIZE_PREFIX = "imagesize_"; //$NON-NLS-1$
	public static final String PREF_ENTRYLAYOUT_PREFIX = "entrylayoutid_"; //$NON-NLS-1$
	
	public static final int PREF_MAXNUMBER_DEFAULT = 20;
	public static final int PREF_MAXNUMBER_DEFAULT_HIGH = 50;
	public static final int PREF_MAXNUMBER_MIN = 1;
	public static final int PREF_MAXNUMBER_MAX = 100;
	//whether to show high resolution pictures
	public static final String PREF_HIGH_RES = "highres_"; //$NON-NLS-1$
	
	public static final String[] PREFS_PREFIX = {PREF_GROUP_PREFIX, PREF_SORTING_PREFIX, PREF_HIGH_RES, 
		PREF_SHOWNAME_PREFIX, PREF_MAXNUMBER_PREFIX, PREF_SHOWPEOPLE_PREFIX, PREF_IMAGESIZE_PREFIX,
		PREF_ENTRYLAYOUT_PREFIX};
	
	private Spinner groupList;
	private Spinner contactsSorting;
	
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	private List<ContactGroup> contactGroups;
	
	private int configActivityLayoutId;
	private int widgetEntryLayoutId;
	
	private List<String> numberValues = new ArrayList<String>(1);
	private ArrayAdapter<String> numberAdapter;
	
    /**
	 * 
	 */
	public ContactsWidgetConfigurationActivity() {
		this(R.layout.appwidget_configure, R.layout.contact_entry);
	}
	
	/**
	 * @param widgetLayoutId
	 * @param configActivityLayoutId
	 */
	protected ContactsWidgetConfigurationActivity(
			int configActivityLayoutId, int widgetEntryLayoutId) {
		super();
		this.configActivityLayoutId = configActivityLayoutId;
		this.widgetEntryLayoutId = widgetEntryLayoutId;
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Set the view layout resource to use.
        setContentView(configActivityLayoutId);

        //groups list
        createGroupListSpinner();
        
        this.contactsSorting = (Spinner)findViewById(R.id.contactSorting);
        String[] sortingOptions = getResources().getStringArray(R.array.sortingOptions);
        ArrayAdapter<String> adapter = 
        		new ArrayAdapter<String> (this, 
        				android.R.layout.simple_spinner_item, sortingOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.contactsSorting.setAdapter(adapter);
        
        numberValues.add(String.valueOf(PREF_MAXNUMBER_DEFAULT));
        numberAdapter = new ArrayAdapter<String> (ContactsWidgetConfigurationActivity.this, 
            			android.R.layout.simple_spinner_item, numberValues);
        numberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)findViewById(R.id.maxNumber)).setAdapter(numberAdapter);
        
        final NumberPickerDialog.OnNumberSetListener numSetListener = new NumberPickerDialog.OnNumberSetListener() {

			@Override
			public void onNumberSet(int number) {
				if (number > 0 && !numberValues.contains(number)) {
					//a valid number and number really changed
					numberValues.clear();
					numberValues.add(String.valueOf(number));
					numberAdapter.notifyDataSetChanged();
				}
			}
        };
       
        ((Spinner)findViewById(R.id.maxNumber)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					NumberPickerDialog newFragment = new NumberPickerDialog(ContactsWidgetConfigurationActivity.this,
							R.string.setMaxNumberTitle,
							PREF_MAXNUMBER_MIN, PREF_MAXNUMBER_MAX, Integer.parseInt(numberValues.get(0)),
							numSetListener);
					newFragment.show();
				}
				return true;
			}
        	
        });
        
        View view = findViewById(R.id.showPeopleApp);
        if (view != null) {
        	((CheckBox)view).setChecked(canShowPeopleApp());
        }
        
        final CheckBox checkShowName = (CheckBox)findViewById(R.id.checkShowName);
        final CheckBox checkNameOverlay = (CheckBox)findViewById(R.id.checkNameOverlay);
        boolean supportNameAtBottom = supportContactNameBottom();
    	checkNameOverlay.setVisibility(supportNameAtBottom ? View.VISIBLE : View.GONE);
        if (checkNameOverlay != null && supportNameAtBottom) {
        	checkShowName.setOnCheckedChangeListener(new OnCheckedChangeListener() {

        		@Override
        		public void onCheckedChanged(CompoundButton buttonView,
        				boolean isChecked) {
        			checkNameOverlay.setEnabled(checkShowName.isChecked());
        			checkNameOverlay.setChecked(checkShowName.isChecked());
        		}
        	});
        }
        
        final boolean hasPhoneCapability = hasPhoneCapability();
        
        final View directDialView = findViewById(R.id.checkDirectDial);
        if (directDialView instanceof CheckBox) {
        	directDialView.setVisibility(canDirectDial() && hasPhoneCapability ? View.VISIBLE : View.GONE);
        	final View phoneNumberView = findViewById(R.id.checkShowPhoneNumber);
            if (phoneNumberView != null) {
            	phoneNumberView.setVisibility(canDirectDial() && hasPhoneCapability ? View.VISIBLE : View.GONE);
            	phoneNumberView.setEnabled(canDirectDial());
            	if (directDialView.getVisibility() == View.VISIBLE) {
            		((CheckBox)directDialView).setOnCheckedChangeListener(new OnCheckedChangeListener() {

            			@Override
            			public void onCheckedChanged(CompoundButton buttonView,
            					boolean isChecked) {
            				phoneNumberView.setEnabled(((CheckBox)directDialView).isChecked());
            				((CheckBox)phoneNumberView).setChecked(phoneNumberView.isEnabled());
            			}
            		});
            	}
            }
        }
        
        setupButtons();

        // Find the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }
    
    private void createGroupListSpinner() {
    	this.groupList = (Spinner)findViewById(R.id.groupList);
        Collection<ContactGroup> contactGroups = new ContactAccessor().getContactGroups(getApplicationContext(), 
				0);
		ContactsWidgetConfigurationActivity.this.contactGroups = new ArrayList<ContactGroup>(contactGroups.size());
		List<String> groupNames = new ArrayList<String>(contactGroups.size());
        for (ContactGroup group : contactGroups) {
        	if (group.getTitle() != null) {
        		groupNames.add(group.getTitle());
        		ContactsWidgetConfigurationActivity.this.contactGroups.add(group);
        	}
        }
        ArrayAdapter<String> adapter = 
        	new ArrayAdapter<String> (ContactsWidgetConfigurationActivity.this, 
        			android.R.layout.simple_spinner_item, groupNames.toArray(new String[groupNames.size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.groupList.setAdapter(adapter);
    }
    
    private void setupButtons() {
    	Button okButton = (Button)findViewById(R.id.saveButton);
    	okButton.setOnClickListener(mOnClickListener);
    	
    	Button cancelButton = (Button)findViewById(R.id.cancelButton);
    	cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
    	});
    }
    
    protected void savePreferences(final Context context, final int appWidgetId) {
        String sortString = null;
        switch(contactsSorting.getSelectedItemPosition()) {
        case 2:
        	sortString = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"; //$NON-NLS-1$
        	break;
        case 1:
        	sortString = ContactsContract.Contacts.LAST_TIME_CONTACTED + " DESC"; //$NON-NLS-1$
        	break;
        case 0:
        
        default:
        	sortString = ContactsContract.Contacts.TIMES_CONTACTED + " DESC"; //$NON-NLS-1$
        	break;
        }
        saveSortingString(context, appWidgetId, sortString);
        ContactGroup selectedGroup = this.contactGroups.size() > 0 ? 
        		this.contactGroups.get(groupList.getSelectedItemPosition()) : new ContactGroup(
    					ContactsWidgetConfigurationActivity.CONTACT_MY_CONTACTS_GROUP_ID, 
    					null, context.getString(R.string.myContacts));
        String selection = "";
        if (selectedGroup.getGroupId() == CONTACT_STARRED_GROUP_ID) {
        	selection = CONTACT_STARRED;
        } else if (selectedGroup.getGroupId() == CONTACT_MY_CONTACTS_GROUP_ID) {
        	//all contacts
        } else {
        	selection = String.valueOf(contactGroups.get(groupList.getSelectedItemPosition()).getGroupId());
        }
        saveSelectionString(context, appWidgetId, selection);
        
        CheckBox showName = (CheckBox)findViewById(R.id.checkShowName);
		saveShowName(context, appWidgetId, showName.isChecked());
        CheckBox showHighRes = (CheckBox)findViewById(R.id.showHighRes);
		saveShowHighRes(context, appWidgetId, showHighRes.isChecked());
		saveMaxNumber(context, appWidgetId, Integer.parseInt(this.numberValues.get(0)));
		
		boolean showPeopleApp = false;
		View view = findViewById(R.id.showPeopleApp);
		if (view instanceof CheckBox) {
			showPeopleApp = view.getVisibility() == View.VISIBLE && ((CheckBox)view).isChecked();
			saveShowPeopleApp(context, appWidgetId, showPeopleApp);
		}
		
		//calculate the most appropriate image size based on the pixel density
		final int imageSize = calculateImageSize();
		saveImageSize(context, appWidgetId, imageSize);
		
		final CheckBox checkNameOverlay = (CheckBox)findViewById(R.id.checkNameOverlay);
		if (checkNameOverlay != null && checkNameOverlay.isChecked()) {
			this.widgetEntryLayoutId = R.layout.contact_entry_name_overlay;
		}
		saveEntryLayoutId(context, appWidgetId, this.widgetEntryLayoutId);
		
        // Push widget update to surface with newly set prefix
		final boolean showPepopleApplication = showPeopleApp;
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		        ContactsWidgetProvider.updateAppWidget(context, appWidgetManager,
		        		appWidgetId, widgetEntryLayoutId, showPepopleApplication, new Rect(0, 0, imageSize, imageSize));
		        
			}
		});
    }
    
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
    	public void onClick(View v) {
    		savePreferences(ContactsWidgetConfigurationActivity.this, mAppWidgetId);
    		// Make sure we pass back the original appWidgetId
    		Intent resultValue = new Intent();
    		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
    		setResult(RESULT_OK, resultValue);
    		finish();
    	}
    };
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.ic_menus, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MainActivity.onOptionsItemSelected(getApplicationContext(), this, item);
	}
    
    protected int getImageSizeId() {
		return R.dimen.size_small;
	}
    
    /**
     * @return True if can show People app shortcut or false otherwise
     */
    protected boolean canShowPeopleApp() {
    	return false;
    }
    
    /**
     * @return True if the widget could support direct dial or false otherwise
     */
    protected boolean canDirectDial() {
    	return false;
    }
    
    /**
     * @return True if is stack view or false otherwise
     */
    protected boolean isStackView() {
    	return false;
    }
    
    protected boolean supportContactNameBottom() {
    	return true;
    }
    
    private boolean hasPhoneCapability() {
    	/*final PackageManager pkgManager = getPackageManager();
    	return pkgManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    			|| pkgManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)
    			|| pkgManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM)
    			|| pkgManager.hasSystemFeature(PackageManager.FEATURE_SIP)
    			|| pkgManager.hasSystemFeature(PackageManager.FEATURE_SIP_VOIP);*/
    	return true;
    }
    
    private int calculateImageSize() {
    	return (int)(getResources().getDimension(getImageSizeId())
				/ getResources().getDisplayMetrics().density);
    }
    
    
 // Write the prefix to the SharedPreferences object for this widget
    public static void saveSortingString(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_SORTING_PREFIX, 0).edit();
        prefs.putString(PREF_SORTING_PREFIX + appWidgetId, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static String loadSortingString(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SORTING_PREFIX, 0);
        String prefix = prefs.getString(PREF_SORTING_PREFIX + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return ContactsContract.Contacts.TIMES_CONTACTED + " DESC"; //$NON-NLS-1$
        }
    }
    
    public static void deletePreference(Context context, int appWidgetId, String prefix) {
    	 SharedPreferences.Editor prefs = context.getSharedPreferences(prefix, 0).edit();
         prefs.remove(prefix + appWidgetId);
         prefs.commit();
    }
    
    public static void saveSelectionString(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_GROUP_PREFIX, 0).edit();
        prefs.putString(PREF_GROUP_PREFIX + appWidgetId, text);
        prefs.commit();
    }

    public static String loadSelectionString(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GROUP_PREFIX, 0);
        String prefix = prefs.getString(PREF_GROUP_PREFIX + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return CONTACT_STARRED;
        }
    }
    
    //whether to show high resolution pictures
    public static void saveShowHighRes(Context context, int appWidgetId, boolean showHighRes) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_HIGH_RES, 0).edit();
        prefs.putString(PREF_HIGH_RES + appWidgetId, String.valueOf(showHighRes));
        prefs.commit();
    }

    public static boolean loadShowHighRes(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_HIGH_RES, 0);
        String value = prefs.getString(PREF_HIGH_RES + appWidgetId, Boolean.TRUE.toString());
        return Boolean.valueOf(value);
    }
    
    //maximum number of contacts to be shown
    public static void saveMaxNumber(Context context, int appWidgetId, int maxNumber) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_MAXNUMBER_PREFIX, 0).edit();
        prefs.putInt(PREF_MAXNUMBER_PREFIX + appWidgetId, maxNumber);
        prefs.commit();
    }

    public static int loadMaxNumbers(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_MAXNUMBER_PREFIX, 0);
        int value = prefs.getInt(PREF_MAXNUMBER_PREFIX + appWidgetId, PREF_MAXNUMBER_DEFAULT_HIGH);
        return value;
    }
    
    public static void saveShowName(Context context, int appWidgetId, boolean showName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_SHOWNAME_PREFIX, 0).edit();
        prefs.putString(PREF_SHOWNAME_PREFIX + appWidgetId, String.valueOf(showName));
        prefs.commit();
    }
    
    public static boolean loadShowName(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SHOWNAME_PREFIX, 0);
        String value = prefs.getString(PREF_SHOWNAME_PREFIX + appWidgetId, Boolean.TRUE.toString());
        return Boolean.valueOf(value);
    }
    
    public static void saveSupportDirectDial(Context context, int appWidgetId, boolean supportDirectDial) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_DIRECTDIAL_PREFIX, 0).edit();
        prefs.putString(PREF_DIRECTDIAL_PREFIX + appWidgetId, String.valueOf(supportDirectDial));
        prefs.commit();
    }
    
    public static boolean loadSupportDirectDial(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_DIRECTDIAL_PREFIX, 0);
        String value = prefs.getString(PREF_DIRECTDIAL_PREFIX + appWidgetId, Boolean.FALSE.toString());
        return Boolean.valueOf(value);
    }
    
    public static void saveShowPhoneNumber(Context context, int appWidgetId, boolean showPhoneNumber) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_SHOWPHONENUMBER_PREFIX, 0).edit();
        prefs.putString(PREF_SHOWPHONENUMBER_PREFIX + appWidgetId, String.valueOf(showPhoneNumber));
        prefs.commit();
    }
    
    public static boolean loadShowPhoneNumber(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SHOWPHONENUMBER_PREFIX, 0);
        String value = prefs.getString(PREF_SHOWPHONENUMBER_PREFIX + appWidgetId, Boolean.TRUE.toString());
        return Boolean.valueOf(value);
    }
    
    public static void saveShowPeopleApp(Context context, int appWidgetId, boolean showPeopleApp) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_SHOWPEOPLE_PREFIX, 0).edit();
        prefs.putString(PREF_SHOWPEOPLE_PREFIX + appWidgetId, String.valueOf(showPeopleApp));
        prefs.commit();
    }
    
    public static boolean loadShowPeopleApp(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SHOWPEOPLE_PREFIX, 0);
        String value = prefs.getString(PREF_SHOWPEOPLE_PREFIX + appWidgetId, Boolean.FALSE.toString());
        return Boolean.valueOf(value);
    }
    
    public static void saveImageSize(Context context, int appWidgetId, int imageSize) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_IMAGESIZE_PREFIX, 0).edit();
        prefs.putInt(PREF_IMAGESIZE_PREFIX + appWidgetId, imageSize);
        prefs.commit();
    }
    
    public static int loadImageSize(Context context, int appWidgetId, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_IMAGESIZE_PREFIX, 0);
        return prefs.getInt(PREF_IMAGESIZE_PREFIX + appWidgetId, defaultValue);
    }
    
    public static void saveEntryLayoutId(Context context, int appWidgetId, int layoutId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_ENTRYLAYOUT_PREFIX, 0).edit();
        prefs.putInt(PREF_ENTRYLAYOUT_PREFIX + appWidgetId, layoutId);
        prefs.commit();
    }
    
    public static int loadEntryLayoutId(Context context, int appWidgetId, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_ENTRYLAYOUT_PREFIX, 0);
        return prefs.getInt(PREF_ENTRYLAYOUT_PREFIX + appWidgetId, defaultValue);
    }
    
}