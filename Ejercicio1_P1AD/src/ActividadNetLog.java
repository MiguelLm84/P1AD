import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActividadNetLog {

	public final static String DIR_ACTIVIDAD="actividad";
	public final static String DIR_NET="net";
	public final static String ERROR_LOG="err.log";
	
	/*
	 * M�todo para mostar la fecha y la hora.
	 */	
	private static String fecha() {		
		DateFormat formatoFecha=new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]");
		Calendar cal=Calendar.getInstance();
		String fecha=formatoFecha.format(cal.getTime());
		return fecha;
	}
	
	/*
	 * M�todo parar registrar en el directorio"actividad" el usuario que ejecute la aplicaci�n.
	 */
	public static void registrarAccesoUsuario() throws IOException {
		String fecha=ActividadNetLog.fecha();
		String user=System.getProperty("user.name");

		FileWriter fichero=new FileWriter(DIR_ACTIVIDAD + "\\" + user + ".log", true);
		PrintWriter pW=new PrintWriter(fichero);
		pW.println(fecha);
		pW.close();
	}
	
	/*
	 * M�todo que lee todos los ficheros "netstat_x.log" del directorio ".\net" y devuelve 
	 * el n�mero mayor de todos ellos. En caso de no haber ning�n fichero devolver�a 0.
	 */
	public static int getNumeroUltimoNetStatLog() {
		int mayor=0;
		String cod_fichero="";
		File dir=new File(DIR_NET);
		String fichero[]=dir.list();
		for(String listaFichero:fichero) {
			cod_fichero=listaFichero.substring(8, listaFichero.length()-4);
			int num_fichero=Integer.parseInt(cod_fichero);
			if(num_fichero>=mayor) {
				mayor=num_fichero;
			}
		}	 
		return mayor;
	}
	
	/*
	 * M�todo para comprobar si la estructura de datos existe, en caso de no existir, las crear�.
	 */
	public static void crearEstructuraDirectorios() {
		File dirActividad=new File(DIR_ACTIVIDAD);
		if (!dirActividad.exists()) {
			dirActividad.mkdir();
		}
		File dirNet=new File(DIR_NET);
		if (!dirNet.exists()) {
			dirNet.mkdir();
		}
	}
	
	/*
	 * M�todo que recibe una lista con todas las l�neas de "netstat /a" y un fichero
	 * del directorio ".\net", en el que se le a�adir�n todas las lineas que contenga la lista.
	 */
	public static void escribirNetStatLog(String nombreFichero, List<String> netstat) throws IOException {
		FileWriter fW=new FileWriter(DIR_NET+"\\"+nombreFichero, true);
		PrintWriter pW=new PrintWriter(fW);
		pW.println(fecha());
		for(int i=0;i<netstat.size();i++) {
			pW.println(netstat.get(i));
		}
		pW.close();
	}
	
	/*
	 * M�todo que ejecuta el comando "netstat /a" y guarda todas las l�neas que devuelve en una lista.
	 */
	public static List<String> leerResultadoNetStat() throws IOException{
		List<String>lista=new ArrayList<String>();
		String result;
		Process proceso=Runtime.getRuntime().exec("netstat /a");
		InputStreamReader iSr=new InputStreamReader(proceso.getInputStream());
		BufferedReader br=new BufferedReader(iSr);
		while((result=br.readLine())!=null) {
			lista.add(result);
		}
		br.close();
		
		return lista;
	}
	
	/*
	 * M�todo que devuelve una lista con todas las l�neas del fichero "netstat_x.log"
	 * del directorio".\net" que recibe como par�metro.
	 */
	public static List<String> leerNetStatLog(String nombreFichero) throws IOException{
		List<String>listaLineas=new ArrayList<String>();
		FileReader leerFichero=new FileReader(DIR_NET+"\\"+nombreFichero);
		BufferedReader bufferLectura=new BufferedReader(leerFichero);
		String resultado=bufferLectura.readLine();
		while (resultado!=null) {
			listaLineas.add(resultado);
			resultado=bufferLectura.readLine();
		}
		bufferLectura.close();
		
		return listaLineas;
	}
	
	/*
	 * M�todo que escribe en el fichero err.log el mensaje que le entra como par�metro
	 * acompa�ado de la fecha y hora actual.
	 */
	public static void escribirEnErrLog(String mensaje) {
		String msj=mensaje;
		FileWriter fW;
		PrintWriter pW=null;
		try {
			fW=new FileWriter(ERROR_LOG,true);
			pW=new PrintWriter(fW);
			pW.println(fecha());
			pW.println(msj);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			pW.close();
		}
	}
	
	public static void main(String[] args) {
		
		crearEstructuraDirectorios();
		try {
			List<String>resultadoNetStat=leerResultadoNetStat();
			registrarAccesoUsuario();
			int ultimoNum=getNumeroUltimoNetStatLog();
			if(ultimoNum==0) {
				escribirNetStatLog("netstat_1.log",resultadoNetStat);
			}else {
				List<String> listaLineas=leerNetStatLog("netstat_"+ultimoNum+".log");
				int tamanhoTotal=listaLineas.size()+resultadoNetStat.size();
				if(tamanhoTotal<200) {
						escribirNetStatLog("netstat_"+ultimoNum+".log",resultadoNetStat);						
				}else {
					escribirNetStatLog("netstat_"+(ultimoNum+1)+".log",resultadoNetStat);
				}	
			}
		} catch (IOException e) {
			escribirEnErrLog(e.getMessage());
		}
	}
}
