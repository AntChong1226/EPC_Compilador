import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static LinkedHashMap<String, Instruccion> setInstrucciones = new LinkedHashMap<>();
    private static List<String> directivas = Arrays.asList("org", "equ", "fcb", "end");
    private static void cargarSetInstrucciones() {
        try {
            File archivoSetInstrucciones = new File("setInstrucciones.txt");
            Scanner lector = new Scanner(archivoSetInstrucciones);
            do {
                String linea = lector.nextLine().replaceAll(" |-", "");
                String[] datos = linea.split(",");
                Instruccion instruccion = new Instruccion(datos[0]);
                
                if(datos.length >= 2 && !datos[1].isEmpty()) {
                instruccion.codigos.put(Instruccion.TipoInstruccion.INM, datos[1]);
                }
                
                if(datos.length >= 3 && !datos[2].isEmpty()) {
                instruccion.codigos.put(Instruccion.TipoInstruccion.DIR, datos[2]);
                }
                
                if(datos.length >= 4 && !datos[3].isEmpty()) {
                instruccion.codigos.put(Instruccion.TipoInstruccion.INDX, datos[3]);
                }
                
                if(datos.length >= 5 && !datos[4].isEmpty()) {
                instruccion.codigos.put(Instruccion.TipoInstruccion.INDY, datos[4]);
                }
                
                if(datos.length >= 6 && !datos[5].isEmpty()) {
                instruccion.codigos.put(Instruccion.TipoInstruccion.EXT, datos[5]);
                }
                
                if(datos.length >= 7 && !datos[6].isEmpty()) {
                instruccion.codigos.put(Instruccion.TipoInstruccion.INH, datos[6]);
                }
                
                if(datos.length >= 8 && !datos[7].isEmpty()) {
                instruccion.codigos.put(Instruccion.TipoInstruccion.REL, datos[7]);
                }
                setInstrucciones.put(datos[0], instruccion);
            } while (lector.hasNextLine());
            lector.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
    }

    private static boolean esDirectiva(String cadena){
        cadena = cadena.toLowerCase();
        for (String directiva : directivas)
            if (cadena.equals(directiva))
                return true;
        return false;
    }

    public static void compilarArchivo(String rutaArchivo){
        try {
            File archivoSetInstrucciones = new File(rutaArchivo);
            Scanner lector = new Scanner(archivoSetInstrucciones);
            boolean estaPegadoAlMargen;
            String linea, lineaSinComentarios, mnemonico, operando, lineaCompilada, hexadecimal = "";
            int numLineaActual, contador = 0, bytesAOcupar, direccionActualH = 0, auxPosAsterisco;
            do {
                numLineaActual = ++contador;
                linea = lector.nextLine();
                System.out.println("START" + linea + "END");
                auxPosAsterisco = linea.indexOf("*");
                lineaSinComentarios = auxPosAsterisco != -1 ? linea.toLowerCase().substring(0, auxPosAsterisco) : linea;
                if (lineaSinComentarios.isBlank()) {
                    System.out.println((numLineaActual++) + "\t\t\t:" + linea);
                    continue;
                }
                estaPegadoAlMargen = false; // TODO: saber si está pegada al margen para saber si es una instrucción o es una etiqueta
                if (estaPegadoAlMargen){
                    // Es una etiqueta
                    // TODO: Reconocer etiquetas y guardar su dirección
                }
                else {
                    lineaSinComentarios = lineaSinComentarios.trim();
                    System.out.println("SINCOMENT_START" + lineaSinComentarios + "END");
                    int primerEspacio = lineaSinComentarios.indexOf(" ");
                    if (primerEspacio == -1) {
                        primerEspacio = lineaSinComentarios.length();
                    }
                    mnemonico = lineaSinComentarios.substring(0, primerEspacio);
                    if(primerEspacio == lineaSinComentarios.length())
                        operando = "";
                    else
                        operando = lineaSinComentarios.substring(primerEspacio + 1);
                    if (esDirectiva(mnemonico)) {
                        System.out.println("Directiva:" + mnemonico);
                        // TODO: Manejar cuando sea directiva
                        switch (mnemonico) {
                            case "org":
                                direccionActualH = Integer.parseInt(operando, 16);
                                break;
                        
                            default:
                                break;
                        }
                    }
                    else {
                        // Es una instrucción
                        // TODO: quitar el espacio entre la instrucción y el margen
                        System.out.println("Mnemonico:" + mnemonico);
                        Instruccion instruccion = setInstrucciones.get(mnemonico);
                        if (instruccion.codigos.containsKey(Instruccion.TipoInstruccion.INH)
                                || instruccion.codigos.containsKey(Instruccion.TipoInstruccion.REL)) {
                            // Estos tipos no comparte mnemónico con los demás
                            if (operando != "") {
                                // Es REL
                            }
                            else {
                                // Es INH
                            }
                        }
                        else {

                        }
                    }
                    System.out.println(numLineaActual++ + ": " + Integer.toHexString(direccionActualH) + "(" + hexadecimal + ")\t\t:" + linea);
                    direccionActualH++;
                }
            } while (lector.hasNextLine());
            lector.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
    }

    public static void main(String[] args) {
        cargarSetInstrucciones();
        compilarArchivo("prueba.asc");
        
    }
}