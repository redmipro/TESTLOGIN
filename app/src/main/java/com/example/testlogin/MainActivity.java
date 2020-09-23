package com.example.testlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {


    //
    //Botón de declaración de diseño, editar textos
    Button login;
    EditText username,password;
    ProgressBar progressBar;
    //----------------------------------------------

    //Declaración de variables de conexión
    Connection con;
    String un,pass,db,ip;
    //----------------------------------------------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Obtener valores de botón, textos y barra de progreso
        login = (Button) findViewById(R.id.button);
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // ------------------------------------------------------------

        // Declaración de IP del servidor, nombre de usuario, nombre de base de datos y contraseña
        ip = "192.168.43.225";
        db = "databaselogin";
        un = "sa";
        pass = "123456";
        // -----------------------------------------------------------------------------------

        // Configuración de la función cuando se hace clic en el botón de inicio de sesión
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckLogin checkLogin = new CheckLogin();// este es el Asynctask, que se usa para procesar en segundo plano para reducir la carga en el proceso de la aplicación
                checkLogin.execute("");
            }
        });
        //-----------------------------------------------------------------------------------------------------------------------------------------
    }

    public class CheckLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(MainActivity.this , "Inicio de sesión exitoso" , Toast.LENGTH_LONG).show();
                //finish();
            }
        }
        @Override
        protected String doInBackground(String... params)
        {
            String usernam = username.getText().toString();
            String passwordd = password.getText().toString();
            if(usernam.trim().equals("")|| passwordd.trim().equals(""))
                z = "Introduzca nombre de usuario y contraseña.";
            else
            {
                try
                {
                    con = connectionclass(un, pass, db, ip);        // Conectarse a la base de datos
                    if (con == null)
                    {
                        z = "Verifique su acceso a Internet!";
                    }
                    else
                    {
                        // Cambie la siguiente consulta de acuerdo con su propia base de datos.
                        String query = "select * from login where user_name= '" + usernam.toString() + "' and pass_word = '"+ passwordd.toString() +"'  ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            z = "Inicio de sesión exitoso";
                            isSuccess=true;
                            con.close();
                        }
                        else
                        {
                            z = "Credenciales no válidas!";
                            isSuccess = false;
                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = ex.getMessage();
                }
            }
            return z;
        }
    }


    @SuppressLint("NewApi")
    public Connection connectionclass(String user, String password, String database, String server)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + database + ";user=" + user+ ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }
}
