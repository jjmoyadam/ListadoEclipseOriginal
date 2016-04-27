/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agrocolor.listaverificacion.presentacion;

import java.util.List;

import agrocolor.listaverificacion.fachadas.FachadaExcel;
import agrocolor.listaverificacion.modelos.PuntoControl;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * @author Admin
 */
public class SelectorPCAdapter extends ArrayAdapter<PuntoControl>  {

	private List<PuntoControl> values;
	private Context context;
	FachadaExcel fachadaExcel;
	
	

	public SelectorPCAdapter(Context context, List<PuntoControl>values) {
		super(context, R.layout.listado_puntos_control);
		this.context = context;
		this.values = values;  
		actualizar();
		
	}
	
	
	public void actualizar()
	{
		
		notifyDataSetChanged();
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.size();
	}

	@Override
	public PuntoControl getItem(int position) {
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
			v = inflater.inflate(R.layout.selector_pc_item, parent, false);
			holder = new ViewHolder();
			holder.etqArchivo = (TextView) v.findViewById(R.id.tv_descripcion_pc_selector);		
			holder.imgEstado = (ImageView) v.findViewById(R.id.img_estado_pc);
			v.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		PuntoControl pc = values.get(position);
		holder.etqArchivo.setText(pc.toString());		
		
		if(pc.completado())
			holder.imgEstado.setBackgroundResource(R.drawable.ic_completado);
		else
			holder.imgEstado.setBackgroundResource(R.drawable.ic_no_completado);
		return v;
		
	}


	


	static class ViewHolder {
		ImageView imgEstado;
		TextView etqArchivo;

	}
}
