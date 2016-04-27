package agrocolor.listaverificacion.fachadas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import agrocolor.listaverificacion.modelos.GrupoPuntoControl;
import agrocolor.listaverificacion.modelos.ListaVerificacion;
import agrocolor.listaverificacion.modelos.PuntoControl;
import agrocolor.listaverificacion.presentacion.R;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;

public class FachadaExcel {


	private Context contexto;
	public static final String EXTENSION_EXCEL = ".xls";

	public String getRuta()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+contexto.getResources().getString(R.string.nombre_carpeta);
	}
	
	public FachadaExcel(Context contexto) {
		this.contexto = contexto;		
	}

	public void crearDirectorio() {
		File folder = new File(getRuta());
		if (!folder.exists())
			folder.mkdirs();
	}
	
	private void crearArchivo(String nombre) throws IOException
	{
		
		File f = new File(getRuta()+File.separator+nombre);
		f.createNewFile();		
	}


	public ListaVerificacion leer(String archivo) throws IOException {
		File inputWorkbook = new File(getRuta()+File.separator+archivo);
        FileInputStream input_document = new FileInputStream(inputWorkbook);
        HSSFWorkbook book = new HSSFWorkbook(input_document); 
        HSSFSheet sDatos = book.getSheetAt(0);
        HSSFSheet sConfig = book.getSheetAt(1); 
        GrupoPuntoControl ultimoGrupo = null;
        PuntoControl pcControl = null;

		//crea el objeto lista de verificacion
        ListaVerificacion lv = new ListaVerificacion(archivo);		
        if (inputWorkbook.exists())
        {

			for(int i=1; i<=sDatos.getLastRowNum(); i++)
        	{
        		Row rowDatos = sDatos.getRow(i);
        		Row rowConfig = sConfig.getRow(i);
        		Cell cel = rowDatos.getCell(contexto.getResources().getInteger(R.integer.COL_CODIGO));
        		String s;
        		cel.setCellType(Cell.CELL_TYPE_STRING);
        		s = cel.getStringCellValue();          		        		
				if(!s.contains(".")) //es un grupo
					ultimoGrupo = crearGrupo(rowDatos);
				else { //Es punto de control
					pcControl = crearPC(rowDatos, rowConfig, lv, ultimoGrupo);
					lv.getPuntosControl().add(pcControl);
				}
				
        	}
			lv.setWorkbook(book);
			return lv;
		
        }
		return null;
				
	}
	
	private String toString(Cell cell)
	{
		switch (cell.getCellType()) {
        	case Cell.CELL_TYPE_STRING:
        		return cell.getStringCellValue().trim();            
        	case Cell.CELL_TYPE_NUMERIC: 
        		return ""+cell.getNumericCellValue();
        	default:
        		return null;
		}
	}
	
	private static String nombreExcel(String nombre)
	{
		if(!nombre.toLowerCase().endsWith(EXTENSION_EXCEL))
			nombre+=EXTENSION_EXCEL;
		
		return nombre;
	}
	
	public void escribir(ListaVerificacion lv) throws  NotFoundException, IOException 
	{		
		String nombreArchivo = nombreExcel(lv.getNombreArchivo());
		File inputWorkbook = new File(getRuta()+File.separator+nombreExcel(nombreArchivo));
		FileOutputStream fos = new FileOutputStream(inputWorkbook);
		if (inputWorkbook.exists()) 
		{	
										
			HSSFSheet  sheet = lv.getWorkbook().getSheetAt(0);
			PuntoControl pc;
			for(int i=0; i<lv.getPuntosControl().size(); i++)
			{
				pc = lv.getPuntosControl().get(i);
				escribirPC(pc, sheet.getRow(pc.getFila()));
			}
			escribirCabecera(lv);
			lv.getWorkbook().write(fos);			
		}
		
		
	}
	




	private GrupoPuntoControl crearGrupo(Row r)
	{
		GrupoPuntoControl gr = new GrupoPuntoControl();
		Cell cel = r.getCell(contexto.getResources().getInteger(R.integer.COL_CODIGO));		
		gr.setCodigo(toString(cel));
		cel = r.getCell(contexto.getResources().getInteger(R.integer.COL_DESCRIPCION));
		gr.setDescripcion(cel.getStringCellValue().trim());
		return gr;
	}
	
	

	
	private void escribirPC(PuntoControl pcPuntoControl, Row r) {		
		r.getCell(contexto.getResources().getInteger(R.integer.COL_RESPUESTA)).setCellValue(pcPuntoControl.getValor());
		r.getCell(contexto.getResources().getInteger(R.integer.COL_OBSERVACIONES)).setCellValue(pcPuntoControl.getObservacion());         	
	}
	
	private void escribirCabecera(ListaVerificacion lv)
	{
	    Sheet sheet = lv.getWorkbook().getSheetAt(0);
	    Header header = sheet.getHeader();	   
	    header.setCenter(escribirCabecera(header.getCenter(), lv.numOperador, lv.numAuditoria));
	
	}
	
	private String escribirCabecera(String cabecera, int op, int aud)
	{
		int posIni = 0;
		int longitud = 0;
		int posBarra = cabecera.lastIndexOf("/");		
		cabecera = cabecera.substring(0, posBarra+1) + op + "-"+ aud;					
		return cabecera;
		
	}

	
	private PuntoControl crearPC(Row rDatos, Row rConfig, ListaVerificacion lv, GrupoPuntoControl grupo)
	{		
		
		PuntoControl pc = new PuntoControl();
		Cell cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_CODIGO));
		if(cel != null) pc.setCodigo(toString(cel));
		cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_CLASIFICACION));
		if(cel != null) pc.setClasificacion(cel.getStringCellValue().trim());
		cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_DESCRIPCION));
		if(cel != null) pc.setDescripcion(cel.getStringCellValue().trim());
		cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_OBSERVACIONES));
		if(cel != null) pc.setObservacion(cel.getStringCellValue().trim());
		cel = rDatos.getCell(contexto.getResources().getInteger(R.integer.COL_RESPUESTA));
		if(cel != null) pc.setValor(cel.getStringCellValue());
		cel = rConfig.getCell(contexto.getResources().getInteger(R.integer.COL_OPCIONES));		
		if(cel != null) pc.setPosiblesObservaciones(cel.getStringCellValue());
		pc.setGrupo(grupo);
		pc.setFila(rDatos.getRowNum());
		pc.setListaVerificacion(lv);
		
		return pc;
	}
	
	public ArrayList<String> leerListasDeRepositorio()
	{			
		File f = new File(getRuta());	
		ArrayList<String> lista = new ArrayList<String>();
		if (f.exists()){
			File[] ficheros = f.listFiles();
			for (int x=0; x < ficheros.length; x++)
				lista.add(ficheros[x].getName());
		}
		
		return lista;			
	}
	
	public ListaVerificacion nuevaLista(String nombreArchivo) throws NotFoundException, IOException
	{			
		nombreArchivo = nombreExcel(nombreArchivo);
		if(!existe(nombreArchivo))
		{
			//AssetManager assetManager = contexto.getResources().getAssets();
			InputStream is = contexto.getResources().openRawResource(R.raw.lv_eco);	
			byte[] buffer = new byte[1024];
			crearArchivo(nombreArchivo);
			File out = new File(getRuta(),nombreArchivo);
			FileOutputStream fos = new FileOutputStream(out);
			int read = 0;
			
			while ((read = is.read(buffer, 0, 1024)) >= 0)
				fos.write(buffer, 0, read);
			
			fos.flush();
			fos.close();
			is.close();			
			
			return leer(nombreArchivo);
		}
		return null;
	}
	
	public boolean existe(String nombreArchivo)
	{
		nombreArchivo = nombreExcel(nombreArchivo);
		File out = new File(getRuta(), nombreArchivo);
		return out.exists();
		
	}
	
	public static boolean nombreValido(String nombre) {
		if(nombre.trim().length()==0)
			return false;
		
		Pattern pattern = Pattern.compile("^[A-Z0-9 a-z]*$");
		Matcher matcher = pattern.matcher(nombre);
		return matcher.find();
	}
	
	public void copiar(String src, String dst) throws IOException {
	
	    InputStream in = new FileInputStream(getRuta()+File.separator+nombreExcel(src));
	    OutputStream out = new FileOutputStream(getRuta()+File.separator+nombreExcel(dst));

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0)
			out.write(buf, 0, len);
	    in.close();
	    out.close();
	}

}
