import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("     ARBOL B+  -  Estructuras de Datos    ");
        System.out.println("==========================================");

        int orden = leerEntero("Ingrese el orden del arbol (minimo 3, recomendado 4): ", 3, 100);
        ArbolBMas arbol = new ArbolBMas(orden);
        System.out.println("Arbol B+ de orden " + orden + " creado.");

        menu(arbol);
        System.out.println("Hasta pronto!");
    }

    private static void menu(ArbolBMas arbol) {
        int opcion;
        do {
            System.out.println();
            System.out.println("------------- MENU -------------");
            System.out.println(" 1. Insertar un elemento");
            System.out.println(" 2. Buscar una clave");
            System.out.println(" 3. Eliminar una clave");
            System.out.println(" 4. Recorrer un rango (n elementos desde una clave)");
            System.out.println(" 5. Mostrar el arbol");
            System.out.println(" 6. Mostrar las hojas enlazadas");
            System.out.println(" 7. Cargar datos de ejemplo");
            System.out.println(" 0. Salir");
            System.out.println("--------------------------------");
            opcion = leerEntero("Seleccione una opcion: ", 0, 7);
            System.out.println();

            switch (opcion) {
                case 1: opcionInsertar(arbol); break;
                case 2: opcionBuscar(arbol); break;
                case 3: opcionEliminar(arbol); break;
                case 4: opcionRango(arbol); break;
                case 5: arbol.imprimirArbol(); break;
                case 6: arbol.imprimirHojas(); break;
                case 7: cargarEjemplo(arbol); break;
                default: break;
            }
        } while (opcion != 0);
    }

    private static void opcionInsertar(ArbolBMas arbol) {
        int clave = leerEntero("Clave (numero entero): ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.print("Dato asociado (texto): ");
        String dato = scanner.nextLine().trim();
        if (dato.isEmpty()) dato = "(sin dato)";
        boolean existia = arbol.buscar(clave);
        arbol.insertar(clave, dato);
        System.out.println(existia ? "La clave ya existia; se actualizo su dato."
                                   : "Elemento insertado correctamente.");
        arbol.imprimirArbol();
    }

    private static void opcionBuscar(ArbolBMas arbol) {
        int clave = leerEntero("Clave a buscar: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        String dato = arbol.obtenerDato(clave);
        System.out.println(dato != null ? "Encontrado: " + clave + " -> " + dato
                                        : "La clave " + clave + " no existe en el arbol.");
    }

    private static void opcionEliminar(ArbolBMas arbol) {
        int clave = leerEntero("Clave a eliminar: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (arbol.eliminar(clave)) {
            System.out.println("Clave " + clave + " eliminada. El arbol se rebalanceo si fue necesario.");
            arbol.imprimirArbol();
        } else {
            System.out.println("La clave " + clave + " no existe en el arbol.");
        }
    }

    private static void opcionRango(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El arbol esta vacio.");
            return;
        }
        int clave = leerEntero("Clave inicial: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        int n = leerEntero("Cantidad de elementos a mostrar (n): ", 1, Integer.MAX_VALUE);
        System.out.println("Resultado:");
        int impresos = arbol.recorrerRango(clave, n);
        if (impresos == 0) System.out.println("   No hay elementos con clave >= " + clave + ".");
        else if (impresos < n) System.out.println("   (solo hay " + impresos + " elementos disponibles)");
    }

    // datos para probar rapido
    private static void cargarEjemplo(ArbolBMas arbol) {
        int[] claves = {10, 20, 5, 6, 15, 30, 25, 40, 45, 50};
        for (int c : claves) arbol.insertar(c, "Dato-" + c);
        System.out.println("Se cargaron " + claves.length + " elementos de ejemplo.");
        arbol.imprimirArbol();
    }

    // repite hasta que mete un numero valido
    private static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= min && valor <= max) return valor;
                System.out.println("  Valor fuera de rango (" + min + " a " + max + ").");
            } catch (NumberFormatException e) {
                System.out.println("  Entrada invalida. Ingrese un numero entero.");
            }
        }
    }
}
