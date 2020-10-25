
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.Scanner;

public class MyEchoCliente {

	public static void main(String[] args) {

		BufferedReader entrada=null;
		PrintWriter salida;
		String msj=null;
		String texto=null;
		
		String puerto;
		String host;
		Properties echo=new Properties();
		
		try {
			echo.setProperty("puerto","2222");
			echo.setProperty("direccion","127.0.0.1");
			
			echo.store(new FileOutputStream("echo.props"),"Fichero de configuración echo.props");
			echo.load(new FileInputStream("echo.props"));
			puerto=echo.getProperty("puerto");
			host=echo.getProperty("direccion");
			int port=Integer.parseInt(puerto);
			Socket sc=new Socket(host,port);
			
			salida=new PrintWriter(sc.getOutputStream());
			Scanner scanner=new Scanner(System.in);
						
			System.out.println("MENSAJES CLIENTE/SERVIDOR\n");
			do {					
				System.out.println("Introducir el mensaje o 'fin' para salir:\n");
				msj=scanner.nextLine();
				salida.println(msj);
				salida.flush();
					
				entrada=new BufferedReader(new InputStreamReader(sc.getInputStream()));
				texto=entrada.readLine();
				System.out.println("\nSERVIDOR: "+texto+"\n");
				msj=msj.toLowerCase();
				if(msj.equals("fin")) {
					entrada.close();
					salida.close();
					sc.close();
					scanner.close();
					System.out.println("Se ha terminado el chat.");
				}
					
			}while(!msj.equals("fin"));
			
		} catch (ConnectException e) {
			System.out.println("Error al intentar conectar un socket a una dirección y puerto remotos.");
			e.getMessage();
			
		} catch(FileNotFoundException g) {
			System.out.println("El fichero no se encuentra en la ruta especificada.");
			g.getMessage();
			
		}catch(SocketException h) {
			System.out.println("Error en el socket.");
			h.getMessage();
			
		} catch(IOException f) {
			f.getMessage();
			
		} 
	}
}
