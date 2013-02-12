package com.example.facebookapplication;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.DownloadManager.Request;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.HttpMethod;
import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
//import com.facebook.android.BaseRequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.internal.Utility;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	// Your Facebook APP ID
	private static String APP_ID = "Your facebook application ID"; // Replace with your App ID
	public static String response;
	public static String[] array = new String[10000];
	public static String[] array_birthday = new String[10000];
	public static String [] array_name=new String[1000];
	public static String[] array_id = new String[10000];
	public static String [] postToWallWithID=new String[100];
	public static String[] token;
	// Instance of Facebook Class
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;

	// Buttons
	Button btnFbLogin;
	Button btnFbGetProfile;
	Button btnPostToWall;
	Button btnSayHappyBirthday;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnFbLogin = (Button) findViewById(R.id.btn_fblogin);
		btnFbGetProfile = (Button) findViewById(R.id.btn_get_profile);
		btnPostToWall = (Button) findViewById(R.id.btn_fb_post_to_wall);
		//btnShowAccessTokens = (Button) findViewById(R.id.btn_show_access_tokens);
		btnSayHappyBirthday=(Button)findViewById(R.id.btn_say_happy_birthday);
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		/**
		 * Login button Click event
		 * */
		btnFbLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("Image Button", "button Clicked");
				loginToFacebook();
			}
		});

		/**
		 * Getting facebook Profile info
		 * */
		btnFbGetProfile.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View v) {
				getProfileInformation();
			}
		});

		/**
		 * Posting to Facebook Wall
		 * */
		btnPostToWall.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View v) {
				postToWall();
			}
		});

		/**
		 * Showing Access Tokens
		 * */
		/*btnShowAccessTokens.setOnClickListener(new View.OnClickListener() {

	 
			public void onClick(View v) {
			 
			}
		});*/
		/**
		 * Show friends birthday at today 
		 */
		btnSayHappyBirthday.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String fql = "	SELECT uid,name,birthday_date FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())";
				Bundle parameters = new Bundle();
				parameters.putString("query", fql);
				parameters.putString("method", "fql.query");
				try {
					  response = facebook.request(parameters);
					  parseBirthdates(response);
					  //Compare Birthday Date after that post message
					//GetIdForPostToWall(array_id,array_birthday);
					postToFriendsWall();
					Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	 
		protected void postToFriendsWall(){
		   /* try {
		     {
		            String response = facebook.request("100005043183562");
		            Bundle params = new Bundle();
		            params.putString("message", "test message");  
		            //params.putString("caption", caption);
		            //params.putString("picture", picURL);

		            response = facebook.request("100005043183562" + "/feed", params, "POST");       

		            Log.d("Tests",response);
		            if (response == null || response.equals("") || 
		                    response.equals("false")) {
		                Log.v("Error", "Blank response");
		            }
		        }
		    }catch(Exception e){
		        e.printStackTrace();
		    }*/
			/*try{
		        Bundle parameters = new Bundle();
		        JSONObject attachment = new JSONObject();

		        try {
		            attachment.put("message", "Messages");
		            attachment.put("name", "happy birthday to you");
		        } catch (JSONException e) {
		        }
		        parameters.putString("attachment", attachment.toString());
		        parameters.putString("message", "Happy birthday");
		        parameters.putString("target_id", "100005043183562"); // target Id in which you need to Post 
		        parameters.putString("method", "stream.publish");
		        String  response = facebook.request(parameters);       
		        Log.v("response", response);
		    }
		    catch(Exception e){}*/
			/* String message = "Post this to my wall";

             Bundle params = new Bundle();             

             params.putString("message", message);
 
             .request("100005043183562/feed", params, "POST", new WallPostRequestListener());*/
			AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(facebook);
			 Bundle params = new Bundle();
			 params.putString("message", "happy birthday");
			         

			 mAsyncFbRunner.request("100005043183562/feed", params, "POST", new WallPostRequestListener(),null);  

         }
		 
	
	public void GetIdForPostToWall(String [] array_id,String [] array_birthday)
	{
		Time today = new Time(Time.getCurrentTimezone());
		//En fazla ayný anda 100 kiþinin dogum günü olduðu düþünülmüþtür.
		int i_id=0;
	    today.setToNow();
		int systemDateTimeNowMonth = today.month;
        int systemDateTimeNowDay = today.monthDay;
        int parsedMonthDay=0;
        int parsedMonth=0;
		for (int i = 0; i < token.length; i++) {
			if(array_birthday[i]!=null)
			{
				/**
				 * compare array_birthday
				 * parse according to /
				 */
				 String[] parsedToken=array_birthday[i].split("/");
				 parsedMonth = Integer.parseInt(parsedToken[0]);
				 parsedMonthDay = Integer.parseInt(parsedToken[1]);
				 if((parsedMonth+1)==systemDateTimeNowMonth && parsedMonthDay==systemDateTimeNowDay)
				 {
					 postToWallWithID[i_id] =array_id[i];
					 i_id++;
				 }
			}
		}
	}
/**
 * parse birthdate and id from 
 */
	public void parseBirthdates(String response)
	{
		
		 String[][] array_names = new String[10000][2];
		String[] SayHiForId=new String[10];
        int i = 0;
        int i_id = 0;
        int i_birthday = 0;
        String friends_name = null;
        String birthday = null;
        String new_birthday = null;
        int i_compare = 0;
        int success_count = 0;
        String day = null;
        String month = null;
        int i_day = 0;
        String[] dayCompare = new String[3];
    
        int i_sayHi = 0;
        int i_name=0;
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        int systemDateTimeNowMonth = today.month;
        int systemDateTimeNowDay = today.monthDay;
	 token=response.split("\"");  
	 for (int j = 0; j < token.length; j++) 
	 {
		 array[j] = token[j].toString();
	 }
		  for (int a=0; a<token.length; a++) 
        {
            if (i >= 2)
            {
                if (array[i - 2].equals("name"))
                {
                	array_name[i_name] = array[i];
                    i_name++;
                }
                if (array[i - 1].equals("birthday_date"))
                {
                	if(array[i].contains("null"))
                	{
                		array_birthday[i_birthday]="null";
                		i_birthday++;
                	}
                	else
                	{
                		array_birthday[i_birthday] =array[i+1].replace("\\", "");
                		i_birthday++;
                	}
                	//array_birthday[i_birthday] =array[i];
                    //new_birthday = birthday.replace('\\','/');
                    //i_birthday++;
                }
                if (array[i - 1].equals("uid"))
                {
                    String temp;
                    temp=array[i].replace(":", "");
                    temp=temp.replace(",", "");
                    array_id[i_id] = temp;
                    i_id++;
                }
            }
            i++;
        }
		  }
		
	/**
	 * Function to login into facebook
	 * */
	@SuppressWarnings("deprecation")
	public void loginToFacebook() {

		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
			
			btnFbLogin.setVisibility(View.INVISIBLE);
			
			// Making get profile button visible
			btnFbGetProfile.setVisibility(View.VISIBLE);

			// Making post to wall visible
			btnPostToWall.setVisibility(View.VISIBLE);

			// Making show access tokens button visible
			//btnShowAccessTokens.setVisibility(View.VISIBLE);
			btnSayHappyBirthday.setVisibility(View.VISIBLE);

			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this,
					new String[] { "email", "publish_stream" },
					new DialogListener() {

					 
						public void onCancel() {
							// Function to handle cancel event
						}

					 
						public void onComplete(Bundle values) {
							// Function to handle complete event
							// Edit Preferences and update facebook acess_token
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();

							// Making Login button invisible
							btnFbLogin.setVisibility(View.INVISIBLE);

							// Making logout Button visible
							btnFbGetProfile.setVisibility(View.VISIBLE);

							// Making post to wall visible
							btnPostToWall.setVisibility(View.VISIBLE);

							// Making show access tokens button visible
							//btnShowAccessTokens.setVisibility(View.VISIBLE);
							// Making show say happy birthday 
							btnSayHappyBirthday.setVisibility(View.VISIBLE);
						}

					 
						public void onError(DialogError error) {
							// Function to handle error

						}

					 
						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors

						}

					});
		}
	}

	 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}


	/**
	 * Get Profile information by making request to Facebook Graph API
	 * */
	@SuppressWarnings("deprecation")
	public void getProfileInformation() {
		mAsyncRunner.request("me", new RequestListener() {
			 
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				String json = response;
				try {
					// Facebook Profile JSON data
					JSONObject profile = new JSONObject(json);
					
					// getting name of the user
					final String name = profile.getString("name");
					
					// getting email of the user
					final String email = profile.getString("email");
					
					runOnUiThread(new Runnable() {

				 
						public void run() {
							Toast.makeText(getApplicationContext(), "Name: " + name + "\nEmail: " + email, Toast.LENGTH_LONG).show();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		 
			public void onIOException(IOException e, Object state) {
			}

	 
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}
 
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}
 
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	/**
	 * Function to post to facebook wall
	 * */
	@SuppressWarnings("deprecation")
	public void postToWall() {
		// post on user's wall.
		facebook.dialog(this, "feed", new DialogListener() {

		 
			public void onFacebookError(FacebookError e) {
			}

		 
			public void onError(DialogError e) {
			}
 
			public void onComplete(Bundle values) {
				//Toast.makeText(getApplicationContext() , "You succesfully posted", Toast.LENGTH_LONG);
				try {
		 
					runOnUiThread(new Runnable() {

				 
						public void run() {
							Toast.makeText(getApplicationContext(), "You succesfully posted", Toast.LENGTH_LONG).show();
						}

					});

					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			 
			public void onCancel() {
			}
		});

	}

	/**
	 * Function to show Access Tokens
	 * */
	/*public void showAccessTokens() {
		String access_token = facebook.getAccessToken();

		Toast.makeText(getApplicationContext(),
				"Access Token: " + access_token, Toast.LENGTH_LONG).show();
	}*/
	
	/**
	 * Function to Logout user from Facebook
	 * */
	public void logoutFromFacebook() {
		mAsyncRunner.logout(this, new RequestListener() {
		 
			public void onComplete(String response, Object state) {
				Log.d("Logout from Facebook", response);
				if (Boolean.parseBoolean(response) == true) {
					runOnUiThread(new Runnable() {

				 
						public void run() {
							// make Login button visible
							btnFbLogin.setVisibility(View.VISIBLE);

							// making all remaining buttons invisible
							btnFbGetProfile.setVisibility(View.INVISIBLE);
							btnPostToWall.setVisibility(View.INVISIBLE);
							btnSayHappyBirthday.setVisibility(View.INVISIBLE);
							//btnShowAccessTokens.setVisibility(View.INVISIBLE);
						}

					});

				}
			}
			public void onIOException(IOException e, Object state) {
			}
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}
	
	private void sayHappyBirthday()
	{
	 /*
		String fqlQuery = "select uid, name, pic_square, is_app_user from user where uid in (select uid2 from friend where uid1 = me())";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Session session = Session.getActiveSession();
		Request request = new Request(session, 
		    "/fql", 
		    params, 
		    HttpMethod.GET, 
		    new Request.Callback(){ 
		        public void onCompleted(Response response) {
		        Log.i(TAG, "Got results: " + response.toString());
		    }
		});
		Request.executeBatchAsync(request);*/
		
	}
	private class WallPostRequestListener extends BaseRequestListener {

	    public void onComplete(final String response) {
	        Log.d("Facebook-Example", "Got response: " + response);
	        String message = "<empty>";
	        try {
	            JSONObject json = Util.parseJson(response);
	            message = json.getString("message");
	        } catch (JSONException e) {
	            Log.w("Facebook-Example", "JSON Error in response");
	        } catch (FacebookError e) {
	            Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
	        }
	        
	        
	    }

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			
		}
	}
	
}
 