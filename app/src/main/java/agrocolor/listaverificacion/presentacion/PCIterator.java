package agrocolor.listaverificacion.presentacion;

import agrocolor.listaverificacion.modelos.ListaVerificacion;
import agrocolor.listaverificacion.modelos.PuntoControl;

public class PCIterator {
	
	private ListaVerificacion lv;
	private int actual;
	
	public PCIterator(ListaVerificacion lv)
	{
		this.lv = lv;
		actual=-1;
	}
	
	public ListaVerificacion getListaVerificacion()
	{
		return lv;
	}
	
	public PuntoControl siguiente()
	{
		if(++actual >= lv.getPuntosControl().size())
		{
			actual = lv.getPuntosControl().size()-1;
			return null;
		}
		return lv.getPuntosControl().get(actual);
	}
	
	public PuntoControl anterior()
	{
		if(--actual < 0)
		{
			actual = 0;
			return null;
		}
		return lv.getPuntosControl().get(actual);
	}
	
	public PuntoControl get(int i)
	{		
		if(i >= 0 && i < lv.getPuntosControl().size())
		{
			actual = i;
			return lv.getPuntosControl().get(i);
		}
		return null;
	}
	
	public void set(PuntoControl pc)
	{
		if(actual > 0 && actual < lv.getPuntosControl().size())
			lv.getPuntosControl().set(actual, pc);		
	}
	
	public PuntoControl actual()
	{
		return lv.getPuntosControl().get(actual);
	}
	
	public int getPosicionActual()
	{
		return actual;
	}
	
	public void setActual(int posicion)
	{
		actual = posicion;
	}
	
	
}
