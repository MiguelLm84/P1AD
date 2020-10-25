
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

public class MyEchoServer {

	public static void main(String[] args) {
		
		final String HOST="127.0.0.1";
		ServerSocket servidor=null;
		Socket sc=null;
		BufferedReader entrada=null;
		PrintWriter salida=null;
		String msj=null;
		String puerto;
		
		//Se crea un objeto Properties.
		Properties server=new Properties();
		
		try {
			server.setProperty("puerto","2222");
			server.store(new FileOutputStream("server.props"),"Fichero de configuración");
			server.load(new FileInputStream("server.props"));
			puerto= server.getProperty("puerto");
			int port=Integer.parseInt(puerto);
			
			//Se instancia un Socket y se le pasa el puerto almacenado en el fichero .props.
			servidor=new ServerSocket(port);
			ArrayList<String> listaMsj=new ArrayList<String>();
			
			System.out.println("Servidor iniciado");
			sc=servidor.accept();
			System.out.println("Cliente conectado");
			
			do {
				//El servidor lee el mensaje del cliente y lo guarda en el fichero .log y lo imprime por pantalla.
				entrada=new BufferedReader(new InputStreamReader(sc.getInputStream()));
				msj=entrada.readLine();
				if(msj!=null) {
					Registro.registrarAcceso(HOST,listaMsj);
				}
				listaMsj.add(msj);
				System.out.println("CLIENTE: "+msj);	
				
				salida=new PrintWriter(sc.getOutputStream());						
				salida.println(msj);
				salida.flush();
				
				msj=msj.toLowerCase();
				if(!msj.equals("fin")) {
					System.out.println("Se ha terminado el chat.");	
				}
				
			} while(!msj.equals("fin"));
				
		} catch (ConnectException e) {
			System.out.println("Error al intentar conectar un socket a una dirección y puerto remotos.");
			e.getMessage();
			
		} catch(FileNotFoundException g) {
			System.out.println("El fichero no se encuentra en la ruta especificada.");
			g.getMessage();
			
		} catch(SocketException h) {
			System.out.println("Error en el socket.");
			h.getMessage();
			
		} catch (IOException e) {
			System.out.println("Se ha producido un error.");
			e.getMessage();
			
		} finally { //Se cierran los flujos.
			try {
				entrada.close();
				salida.close();
				sc.close();
				System.out.println("Servidor desconectado.");
				
			} catch (IOException e) {
				System.out.println("Se ha producido un error.");
				e.getMessage();
			}
		}
	}
}

class Registro {
	
	public Registro() {
		
	}
	
	//Método para registrar el chat cliente-servidor.
	public static void registrarAcceso(final String HOST, ArrayList<String> listaMsj) throws IOException {
		
		final String IP=HOST; 
		String result=null;		
		String ruta=".\\logs";
		File logs=new File(ruta);
		
		//Se crea el fichero de registro .log
		if(!logs.exists()) {		
			logs.mkdir();			
		}else {			
			File fichero=new File(logs+"\\trafic.log");
			if (!fichero.exists()) {
				fichero.createNewFile();
			}			
			ArrayList<String> mensaje=listaMsj;
			for(String m : mensaje) {
				result=m;
			}
			
			//Se un objeto Calendar y se le da formato.
			DateFormat formato=new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]");
			Calendar cal=Calendar.getInstance();
			String fecha=formato.format(cal.getTime());

			//Se escribe en el fichero la fecha,el IP y el resultado del chat.
			FileWriter fW=new FileWriter(fichero,true);
			PrintWriter pW=new PrintWriter(fW);
			pW.println(fecha+" "+IP+" "+result);
			pW.close();
		}		
	}
}	
