import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static LinkedHashMap<String, Instruccion> setInstrucciones = new LinkedHashMap<>();
    private static List<String> directivas = Arrays.asList("org", "equ", "fcb", "end");
    private static HashMap<String, Integer> etiquetas = new HashMap<>();
    
    private static void cargarSetInstrucciones() {
        try {
            File archivoSetInstrucciones = new File("setInstruccionesConBytes.txt");
            Scanner lector = new Scanner(archivoSetInstrucciones);
            do {
                String linea = lector.nextLine().replaceAll(" |-", "");
                String[] datos = linea.split(",");
                Instruccion instruccion = new Instruccion(datos[0]);
                String codigo, bytes;
                
                if(datos.length >= 2 && !datos[1].isEmpty()) {
                    codigo = datos[1];
                    bytes = datos[2];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.INM, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 4 && !datos[3].isEmpty()) {
                    codigo = datos[3];
                    bytes = datos[4];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.DIR, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 6 && !datos[5].isEmpty()) {
                    codigo = datos[5];
                    bytes = datos[6];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.INDX, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 8 && !datos[7].isEmpty()) {
                    codigo = datos[7];
                    bytes = datos[8];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.INDY, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 10 && !datos[9].isEmpty()) {
                    codigo = datos[9];
                    bytes = datos[10];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.EXT, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 12 && !datos[11].isEmpty()) {
                    codigo = datos[11];
                    bytes = datos[12];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.INH, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 14 && !datos[13].isEmpty()) {
                    codigo = datos[13];
                    bytes = datos[14];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.REL, new TipoInstruccionDetalle(codigo, bytes));
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
        etiquetas = new HashMap<>();
        LinkedList<Linea> lineas = new LinkedList<>();
        try {
            File archivoSetInstrucciones = new File(rutaArchivo);
            Scanner lector = new Scanner(archivoSetInstrucciones);
            boolean estaPegadoAlMargen, finDelPrograma = false, hacerSegundaVuelta = false;
            String lineaSinComentarios, mnemonico, hexadecimal = "";
            int contador = 0, bytesAOcupar, direccionActualH = 0, auxPosAsterisco;
            Linea lineaDetalle = new Linea();
            do {
                lineaDetalle = new Linea();
                lineaDetalle.numLinea = ++contador;
                lineaDetalle.lineaOriginal = lector.nextLine();
                System.out.println("START" + lineaDetalle.lineaOriginal + "END");
                auxPosAsterisco = lineaDetalle.lineaOriginal.indexOf("*");
                lineaSinComentarios = lineaDetalle.lineaOriginal.toLowerCase();
                lineaSinComentarios = auxPosAsterisco != -1 ? lineaSinComentarios.substring(0, auxPosAsterisco) : lineaSinComentarios;
                if (lineaSinComentarios.isBlank()) {
                    //System.out.println((lineaDetalle.numLinea) + "\t\t\t:" + lineaDetalle.lineaOriginal);
                    lineas.add(lineaDetalle);
                    continue;
                }
                estaPegadoAlMargen = !lineaSinComentarios.substring(0, 1).isBlank(); // TODO: saber si está pegada al margen para saber si es una instrucción o es una etiqueta
                if (estaPegadoAlMargen){
                    // Es una etiqueta
                    // TODO: Reconocer etiquetas y guardar su dirección
                    lineaSinComentarios = lineaSinComentarios.trim();
                    if(lineaSinComentarios.matches("\s")){
                        throw new Error("Una etiqueta no puede tener espacios.");
                    }
                    etiquetas.put(lineaSinComentarios, direccionActualH);
                    hacerSegundaVuelta = true;
                    System.out.println((lineaDetalle.numLinea) + "\t\t\t:" + lineaDetalle.lineaOriginal);
                    System.out.println("ETQIEUTAS:" + etiquetas.toString());
                    continue;
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
                        lineaDetalle.operando = "";
                    else
                        lineaDetalle.operando = lineaSinComentarios.substring(primerEspacio + 1);
                    if (esDirectiva(mnemonico)) {
                        System.out.println("Directiva:" + mnemonico);
                        // TODO: Manejar cuando sea directiva
                        switch (mnemonico) {
                            case "org":
                                direccionActualH = Integer.parseInt(lineaDetalle.operando, 16);
                                break;
                        
                            case "equ":
                                // TODO: PENDIENTE EQU
                            break;
                    
                            case "fcb":
                                // TODO: PENDIENTE FCB
                            break;
                    
                            case "end":
                                finDelPrograma = true;
                            break;
                        
                            default:
                                lector.close();
                                // TODO: Cerrar archivo de escritura
                                break;
                        }
                    }
                    else {
                        // Es una instrucción
                        // TODO: quitar el espacio entre la instrucción y el margen
                        System.out.println("Mnemonico:" + mnemonico);
                        Instruccion instruccion = setInstrucciones.get(mnemonico);
                        System.out.println("TIPOS:" + instruccion.codigos.toString());
                        if (instruccion.codigos.containsKey(Instruccion.TipoInstruccion.INH)
                                || instruccion.codigos.containsKey(Instruccion.TipoInstruccion.REL)) {
                            // Estos tipos no comparte mnemónico con los demás
                            if (lineaDetalle.operando != "") {
                                // Es REL
                                hacerSegundaVuelta = true;
                                lineaDetalle.pendiente = Linea.Tarea.CALC_SALTO;
                            }
                            else {
                                // Es INH
                            }
                        }
                        else {
                            String[] operandos = lineaDetalle.operando.split(",");
                            for (int i = 0; i < operandos.length; i++) {
                                operandos[i] = operandos[i].trim();
                            }
                            boolean espacioUltimaParte = operandos[operandos.length - 1].contains(" ");
                            LinkedList<String> operandosLista = new LinkedList<>();
                            for (String operando : operandos) {
                                operandosLista.add(operando);
                            }
                            if (espacioUltimaParte) {
                                String ultimo = operandosLista.removeLast();
                                String[] partesUltimo = ultimo.split(" ");
                                operandosLista.add(partesUltimo[0].trim());
                                operandosLista.add(partesUltimo[1].trim());
                            }
                            int numOperandos = operandosLista.size();
                            // TODO: Saber a qué modo se refiere con los operandos recibidos
                        }
                    }
                    lineaDetalle.direccion = direccionActualH++;
                    lineas.add(lineaDetalle);
                }
                // TODO: hacer segunda vuelta
            } while (lector.hasNextLine() && !finDelPrograma);
            lector.close();
            for (Linea linea : lineas) {
                System.out.println(linea);
            }
            System.out.println("ETQIEUTAS:" + etiquetas.toString());
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
    }

    public static void main(String[] args) {
        cargarSetInstrucciones();
        compilarArchivo("prueba.asc");
    }
}