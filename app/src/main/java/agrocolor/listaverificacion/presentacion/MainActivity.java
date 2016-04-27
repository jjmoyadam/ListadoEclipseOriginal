package agrocolor.listaverificacion.presentacion;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final int POSICION_NUEVA_LISTA = 0;

	/*
     DECLARACIONES
     */
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private enum Modo
    {
    	ListadoLV,
    	PuntoControl    	
    }
    
    private Modo modo;
    
    private CharSequence activityTitle;
    private CharSequence itemTitle;
    private String[] tagTitles;
    private FachadaExcel fachadaExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        modo = Modo.ListadoLV;
        fachadaExcel = new FachadaExcel(this);
        fachadaExcel.crearDirectorio();
        itemTitle = activityTitle = getTitle();
        tagTitles = getResources().getStringArray(R.array.Tags);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Setear una sombra sobre el contenido principal cuando el drawer se despliegue
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        //Crear elementos de la lista
        
        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        /*
        items.add(new DrawerItem(tagTitles[0], R.drawable.ic_html));
        items.add(new DrawerItem(tagTitles[1], R.drawable.ic_css));
        items.add(new DrawerItem(tagTitles[2], R.drawable.ic_javascript));
        items.add(new DrawerItem(tagTitles[3], R.drawable.ic_angular));
        items.add(new DrawerItem(tagTitles[4], R.drawable.ic_python));
        items.add(new DrawerItem(tagTitles[5], R.drawable.ic_ruby));
        */
        items = itemsInicio();

        // Relacionar el adaptador y la escucha de la lista del drawer
        drawerList.setAdapter(new DrawerListAdapter(this, items));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Habilitar el icono de la app por si hay algún estilo que lo deshabilitó
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Crear ActionBarDrawerToggle para la apertura y cierre
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
			public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(itemTitle);

                /*Usa este método si vas a modificar la action bar
                con cada fragmento
                 */
                //invalidateOptionsMenu();
            }

            @Override
			public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(activityTitle);

                /*Usa este método si vas a modificar la action bar
                con cada fragmento
                 */
                //invalidateOptionsMenu();
            }
        };
        //Seteamos la escucha
        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null)
        {
			mostrarListadoLV();
			drawerList.setItemChecked(0, true);
        }
    }
    
    private ArrayList<DrawerItem> itemsInicio()
    {
    	ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        items.add(new DrawerItem(getResources().getString(R.string.nueva_lista), R.drawable.ic_nuevo));
        items.add(new DrawerItem(getResources().getString(R.string.tit_listas_vertificacion), R.drawable.ic_listado));
    	return items;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	if(modo == Modo.ListadoLV)     
    		inflater.inflate(R.menu.menu_main, menu);
    	else
    		inflater.inflate(R.menu.menu_pc, menu);
		
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item))
			// Toma los eventos de selección del toggle aquí
            return true;
        return super.onOptionsItemSelected(item);
    }

    /* La escucha del ListView en el Drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //selectItem(position);
        	switch (position) {
				case 0:
					mostrarIntroducirNombreArchivo();
					break;
				case 1:
					mostrarListadoLV();
					break;
				default:
					break;
			}        	
        	
        }
		
    }
    
 
    private void mostrarListadoLV() {
        // Reemplazar el contenido del layout principal por un fragmento
        //ArticleFragment fragment = new ArticleFragment();
    	ListadoArchivosFragment fragment = new ListadoArchivosFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        
        
        setTitle(R.string.tit_listas_vertificacion);
        drawerLayout.closeDrawer(drawerList);
    }
    
    public void editarLV(String nombreArchivo, boolean nuevo) {
    	PuntoControlFragment fragment = new PuntoControlFragment();
    	Bundle args = new Bundle();
        args.putString(PuntoControlFragment.ARG_NOMBRE_LISTA, nombreArchivo);
        args.putBoolean(PuntoControlFragment.ARG_NUEVA_LISTA, nuevo);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        drawerList.setItemChecked(POSICION_NUEVA_LISTA, true);
        setTitle(getResources().getString(R.string.nueva_lista));
        drawerLayout.closeDrawer(drawerList);
	}

    /**
     * Metodo para crear lista de verificacion
     */
	public void mostrarIntroducirNombreArchivo() {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();
		View v = inflater.inflate(R.layout.layout_nuevo_archivo, null); 
		final EditText et = (EditText)v.findViewById(R.id.et_nombre_archivo);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		ad
		.setTitle(R.string.tit_nueva_lista)
		.setView(v)
		// Add action buttons
		.setPositiveButton(R.string.aceptar, null)
		.setNegativeButton(R.string.cancelar,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// LoginDialogFragment.this.getDialog().cancel();
						}
					});
		
		final AlertDialog dialog = ad.create();
		dialog.show();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String nombre = et.getText().toString().trim();
						if(!FachadaExcel.nombreValido(nombre))
							Toast.makeText(getBaseContext(), getResources().getString(R.string.msg_error_nombre_archivo_no_valido), Toast.LENGTH_SHORT).show();
						else if(new FachadaExcel(getBaseContext()).existe(nombre))
							Toast.makeText(getBaseContext(), getResources().getString(R.string.msg_archivo_existe), Toast.LENGTH_SHORT).show();
						else
						{
							editarLV(nombre, true);
							modo = Modo.PuntoControl;
							dialog.dismiss();
						}
					}
				});
	}
		


    /* Método auxiliar para setear el titulo de la action bar */
    @Override
    public void setTitle(CharSequence title) {
        itemTitle = title;
        getSupportActionBar().setTitle(itemTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sincronizar el estado del drawer
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Cambiar las configuraciones del drawer si hubo modificaciones
        drawerToggle.onConfigurationChanged(newConfig);
    }
    
    
}