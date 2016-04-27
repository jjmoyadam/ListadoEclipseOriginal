package agrocolor.listaverificacion.modelos;

import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ListaVerificacion {
	
	public ListaVerificacion(String nombreArchivo)
	{
		puntosControl = new ArrayList<PuntoControl>();
		this.nombreArchivo = nombreArchivo;
	}
	
	private ArrayList<PuntoControl> puntosControl;
	private HSSFWorkbook workbook;
	private String nombreArchivo;
	public int numOperador;
	public int numAuditoria;

	public ArrayList<PuntoControl> getPuntosControl() {
		return puntosControl;
	}
	
	public ArrayList<String> getPuntosControlString()
	{
		ArrayList<String> pcs = new ArrayList<String>();
		for(int i=0; i<puntosControl.size(); i++)
			pcs.add(puntosControl.get(i).getCodigo()+" "+ puntosControl.get(i).getDescripcion());
		return pcs;
	}
	

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}


	public HSSFWorkbook getWorkbook() {
		return workbook;
	}


	public void setWorkbook(HSSFWorkbook book) {
		this.workbook = book;
	}
	
	public double getCompletado()
	{
		double completados = 0;
		for(PuntoControl pc: puntosControl)
			if(pc.completado())
				completados++;
		
		return  100*(completados /  puntosControl.size());
	}


	

	

}
