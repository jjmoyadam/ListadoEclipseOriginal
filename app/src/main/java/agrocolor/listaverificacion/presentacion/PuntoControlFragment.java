package agrocolor.listaverificacion.presentacion;

import java.io.IOException;
import java.text.DecimalFormat;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import agrocolor.listaverificacion.modelos.ListaVerificacion;
import agrocolor.listaverificacion.modelos.PuntoControl;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class PuntoControlFragment extends Fragment {
    public static final String ARG_NOMBRE_LISTA = "nombre_lista";
    public static final String ARG_NUEVA_LISTA = "nueva_lista";
	private PCIterator pcIter;
	private Context contexto;
	private TextView tvDescripcionPC, tvDescripcionGrupo, tvClasificacion, tvCompletado;
	private EditText etObservacion;
	private boolean nuevaLista;
	private MenuItem miGuardar;
	private FachadaExcel fachadaExcel;
	private Spinner spValor;
	private boolean userSelect;
	private static final String ARG_PC_ACTUAL = "PC_ACTUAL";
	

    public PuntoControlFragment() {
    	setHasOptionsMenu(true);


    }
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {	
		menu.clear();
    	inflater.inflate(R.menu.menu_pc, menu);	
    	miGuardar = menu.getItem(2);
    	miGuardar.setIcon(R.drawable.ic_guardado); //Hay que hacerlo asi pq no funciona con el setchecked    	
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.btn_listar_pcs:
				mostrarSelectorPC();
				return true;
			case R.id.btn_anterior:			
				anterior();
				return true;
			case R.id.btn_siguiente:
				siguiente();
				return true;
			case R.id.btn_guardar:	
				if(validar())
					guardar();
				else
					Toast.makeText(contexto, getResources().getString(R.string.msg_error_cabecera_no_valida), Toast.LENGTH_LONG).show();
				return true;
			case R.id.btn_cabecera:
				mostrarCabecera();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
	
	private void guardar()
	{
		try {
					
			setPC();
			fachadaExcel.escribir(pcIter.getListaVerificacion());			
			miGuardar.setIcon(R.drawable.ic_guardado);
			
		} catch (IOException e) {
			//Mensaje.mostrar(contexto, getResources().getString(R.string.tit_error), e.getStackTrace().toString(), getResources().getString(R.string.aceptar), null);		
		}
	}
	
	private boolean validar()
	{
		return pcIter.getListaVerificacion().numAuditoria != 0 && pcIter.getListaVerificacion().numOperador != 0;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.layout_pc, container, false);
        String archivo = getArguments().getString(ARG_NOMBRE_LISTA);   
        nuevaLista = getArguments().getBoolean(ARG_NUEVA_LISTA);
        ListaVerificacion lv;
        
        contexto = getActivity();
    	fachadaExcel = new FachadaExcel(contexto);
        
        //Inicializamos los controles
        tvDescripcionPC = (TextView) rootView.findViewById(R.id.tv_descripcion_pc);        
        tvDescripcionGrupo = (TextView) rootView.findViewById(R.id.tv_descripcion_grupo);
        etObservacion = (EditText) rootView.findViewById(R.id.et_observacion);
        tvClasificacion = (TextView) rootView.findViewById(R.id.tv_clasificacion_pc);
        tvCompletado = (TextView) rootView.findViewById(R.id.tv_porcentaje_completado);
        etObservacion.setOnClickListener(onClick_etObservacion);
        etObservacion.setOnLongClickListener(onLongClickListener_etObservacionClickListener);
        etObservacion.addTextChangedListener(onTextChange_etObservacionTextWatcher);
        
        spValor = (Spinner) rootView.findViewById(R.id.sp_valor);        
        ArrayAdapter<CharSequence> adp = ArrayAdapter.createFromResource(contexto, R.array.valores_array, R.layout.layout_spinner);        
        adp.setDropDownViewResource(R.layout.layout_spinner);
        spValor.setAdapter(adp);
        spValor.setOnItemSelectedListener(onItemSelected_spValor);
        spValor.setOnTouchListener(onTouchListener_spValorListener);
        
        //Mostramos el contenido del primer punto de control o del actual si hay cambio de orientaion       
        try {        	        
        	if(nuevaLista)
        		lv = fachadaExcel.nuevaLista(archivo);
			else
				lv = fachadaExcel.leer(archivo);
        	if(lv != null)
        	{
        		pcIter = new PCIterator(lv);
        		if(savedInstanceState != null)
        		{
        			pcIter.setActual(savedInstanceState.getInt(ARG_PC_ACTUAL));
        			mostrarPC(pcIter.actual());
        		}
        		else
        			mostrarPC(pcIter.siguiente());
        	}
        	
		} catch (IOException e) {
			//Mensaje.mostrar(contexto, getResources().getString(R.string.tit_error), e.getStackTrace().toString(), getResources().getString(R.string.aceptar), null);
		}
       

        return rootView;
    }
    
    private void siguiente() 
    {
    	guardar();    	
    	PuntoControl pc = pcIter.siguiente();
    	mostrarPC(pc);
    }
    
    private void irA(int i) 
    {
    	guardar();    	
    	PuntoControl pc = pcIter.get(i);
    	mostrarPC(pc);
    }
    
    
    private void anterior()
    {
    	guardar();
    	PuntoControl pc = pcIter.anterior();
    	mostrarPC(pc);
    }
    
    private void mostrarPC(PuntoControl pc)
    {
    	if(pc != null)
    	{	
    		spValor.setOnItemSelectedListener(null); //Quitamos el evento para que no salte ya que no es una modificacion del usuario
    		if(pc.getClasificacion().trim().length() > 0){
				spValor.setEnabled(true);    			
    			spValor.setSelection(((ArrayAdapter)spValor.getAdapter()).getPosition(pc.getValor().trim()));
    		}
			else{
				spValor.setEnabled(false);				
    			spValor.setSelection(((ArrayAdapter)spValor.getAdapter()).getPosition(""));
			}
    		spValor.setOnItemSelectedListener(onItemSelected_spValor); //Restauramos el evento
    		
    		tvDescripcionGrupo.setText(pc.getGrupo().getCodigo() + ". " +pc.getGrupo().getDescripcion());
    		tvDescripcionPC.setText(pc.getCodigo() + " " + pc.getDescripcion());
    		etObservacion.removeTextChangedListener(onTextChange_etObservacionTextWatcher); //para que no lance el evento ahora
    		etObservacion.setText(pc.getObservacion());
    		etObservacion.addTextChangedListener(onTextChange_etObservacionTextWatcher); //restauramos el evento
    		tvClasificacion.setText(pc.getClasificacion());    		
    	    actualizarCompletado();
    	} else
			Toast.makeText(contexto, contexto.getResources().getString(R.string.msg_tope_pc), Toast.LENGTH_SHORT).show();    	
    }

    private void setPC()
    {
    	PuntoControl pc = pcIter.actual();
    	pc.setObservacion(etObservacion.getText().toString().trim());    
    	pc.setValor(spValor.getSelectedItem().toString());
    }
    
    private void mostrarSelectorObservacion() {
		AlertDialog.Builder ad = new AlertDialog.Builder(contexto);
		LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.layout_selector_observacion, null); 
		
		ad
		.setTitle(R.string.tit_selector_observacion)
		.setView(v)
		// Add action buttons
		.setNegativeButton(R.string.cancelar,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
						}
					});
		
		final AlertDialog dialog = ad.create();
		dialog.show();
		final ListView lv = (ListView)v.findViewById(R.id.lv_observaciones);
		lv.setOnItemClickListener(new OnItemClickListener() {           
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				String ob = ((TextView)v).getText().toString();
				etObservacion.setText(ob);	    	
				etObservacion.setSelection(ob.length());
				dialog.dismiss();
			}
        });
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, android.R.id.text1, pcIter.actual().getListaPosiblesObservaciones());		
        lv.setAdapter(adapter);	
	}
    
    private void mostrarCabecera()
    {
		AlertDialog.Builder ad = new AlertDialog.Builder(contexto);
		LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.layout_cabecera, null); 
		final EditText etNumVisita = (EditText)v.findViewById(R.id.et_numero_visita);
		final EditText etNumOp = (EditText)v.findViewById(R.id.et_numero_operador);
		
		if(pcIter.getListaVerificacion().numAuditoria != 0) etNumOp.setText(""+pcIter.getListaVerificacion().numAuditoria);
		if(pcIter.getListaVerificacion().numOperador != 0) etNumOp.setText(""+pcIter.getListaVerificacion().numOperador);

		ad
		.setTitle(R.string.tit_cabecera)
		.setView(v)
		// Add action buttons
		.setPositiveButton(R.string.aceptar, null)
		.setNegativeButton(R.string.cancelar,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
						}
					});
		
		final AlertDialog dialog = ad.create();
		dialog.show();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try
						{
							pcIter.getListaVerificacion().numOperador = Integer.parseInt(etNumOp.getText().toString());
							pcIter.getListaVerificacion().numAuditoria = Integer.parseInt(etNumVisita.getText().toString());
							dialog.dismiss();
						}
						catch(NumberFormatException exc)
						{
							Toast.makeText(contexto, getResources().getString(R.string.msg_error_cabecera_no_valida) , Toast.LENGTH_LONG).show();							
						}											
					}
				});
    	
    }
    
    private void mostrarSelectorPC() {
		AlertDialog.Builder ad = new AlertDialog.Builder(contexto);
		LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.layout_selector_pc, null); 
		
		ad
		.setTitle(R.string.tit_selector_pc)
		.setView(v)
		// Add action buttons
		.setNegativeButton(R.string.cancelar,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							
						}
					});
		
		final AlertDialog dialog = ad.create();
		dialog.show();
		final ListView lv = (ListView)v.findViewById(R.id.lv_pcs);
		lv.setOnItemClickListener(new OnItemClickListener() {           
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) {
				irA(pos);
				dialog.dismiss();
			}
        });
		
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_list_item_1, android.R.id.text1, pcIter.getListaVerificacion().getPuntosControlString());
		SelectorPCAdapter adapter = new SelectorPCAdapter(contexto, pcIter.getListaVerificacion().getPuntosControl());
        lv.setAdapter(adapter);	
	}
    
    
    
    private View.OnClickListener onClick_etObservacion = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			PuntoControl pc = pcIter.actual();
			if(etObservacion.getText().toString().trim().length() == 0 && pc.getPosiblesObservaciones().trim().length() > 0)
				mostrarSelectorObservacion();
		}
	};
	
	private void actualizarCompletado()
	{
		PuntoControl pc = pcIter.actual();
		DecimalFormat f = new DecimalFormat("#0.0");
		tvCompletado.setText(f.format(pc.getListaVerificacion().getCompletado()) + "%");
	}
	
	private View.OnLongClickListener onLongClickListener_etObservacionClickListener = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			PuntoControl pc = pcIter.actual();
			if(pc.getPosiblesObservaciones().trim().length() > 0)
				mostrarSelectorObservacion();
			return true;
		}
	};
    
    private TextWatcher onTextChange_etObservacionTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {			
			if(miGuardar != null)
				miGuardar.setIcon(R.drawable.ic_guardar);
	    	PuntoControl pc = pcIter.actual();
	    	pc.setObservacion(etObservacion.getText().toString().trim());   
	    	actualizarCompletado();
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			
		}
	};
    
    private AdapterView.OnItemSelectedListener onItemSelected_spValor = new AdapterView.OnItemSelectedListener(){
    	 @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    		 	if(userSelect)
    		 	{
    		 		PuntoControl pc = pcIter.actual();
    		 		pc.setValor(spValor.getSelectedItem().toString());
    		 		actualizarCompletado();
    		 		miGuardar.setIcon(R.drawable.ic_guardar);
    		 		userSelect = false;
    		 	}
    	    }

    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) 
    	    {
    	    }
    };
    
    private OnTouchListener onTouchListener_spValorListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			return false;
		}
	};
	
    @Override
    public void onSaveInstanceState(Bundle b) {
        
        super.onSaveInstanceState(b);
        b.putInt( ARG_PC_ACTUAL, pcIter.getPosicionActual());
    }
}
