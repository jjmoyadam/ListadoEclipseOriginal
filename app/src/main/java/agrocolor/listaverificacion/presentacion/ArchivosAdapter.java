/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agrocolor.listaverificacion.presentacion;

import java.util.List;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 *
 * @author Admin
 */
public class ArchivosAdapter extends ArrayAdapter<String>  {

	private List<String> values;
	private Context context;
	FachadaExcel fachadaExcel;
	
	

	public ArchivosAdapter(Context context) {
		super(context, R.layout.listado_archivos);
		this.context = context;
		fachadaExcel = new FachadaExcel(getContext());  
		actualizar();
		
	}
	
	
	public void actualizar()
	{
		values = fachadaExcel.leerListasDeRepositorio();
		notifyDataSetChanged();
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return values.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		
		
		if (v == null) {
			// Ensure sorted values
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.lv_item, parent, false);
			holder = new ViewHolder();
			holder.etqArchivo = (TextView) v.findViewById(R.id.tv_nombre_archivo);					
			v.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		/*btnEliminar = (ImageButton) v.findViewById(R.id.btnEliminar);
		btnEliminar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mostrarAdvertencia(position);
			}
		});*/

		holder.etqArchivo.setText(values.get(position));		
		
		return v;
		
	}

	/*

	private void mostrarAdvertencia(final int posicion) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setMessage(
				context.getResources().getString(
						R.string.pregunta_eliminar_comida))
				.setPositiveButton(R.string.aceptar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								FachadaComida fc = new FachadaComida(context);
								Comida c = getItem(posicion);
								fc.eliminarComida(c);
								values.remove(c);
								notifyDataSetChanged();
							}
						})
				.setNegativeButton(R.string.cancelar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		builder.show();
	}

	*/

	


	static class ViewHolder {

		TextView etqArchivo;

	}
}
