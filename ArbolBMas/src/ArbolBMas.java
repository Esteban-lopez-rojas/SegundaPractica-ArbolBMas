import java.util.ArrayList;

public class ArbolBMas {

    private final int orden;
    private NodoArbolBMas raiz;

    public ArbolBMas(int orden) {
        this.orden = orden;
        raiz = new NodoArbolBMas(true);
    }

    // minimos segun el orden
    private int minClavesHoja() {
        return orden / 2;
    }

    private int minHijosInterno() {
        return (orden + 1) / 2;
    }

    private boolean estaEnDeficit(NodoArbolBMas nodo) {
        return nodo.esHoja() ? nodo.getClaves().size() < minClavesHoja()
                             : nodo.getHijos().size() < minHijosInterno();
    }

    // si le sobra para prestar
    private boolean puedePrestar(NodoArbolBMas nodo) {
        return nodo.esHoja() ? nodo.getClaves().size() > minClavesHoja()
                             : nodo.getHijos().size() > minHijosInterno();
    }

    public boolean estaVacio() {
        return raiz.esHoja() && raiz.getClaves().isEmpty();
    }

    // si la llave ya existe solo cambia el dato
    public void insertar(int clave, String dato) {
        insertarRecursivo(this.raiz, clave, dato);

        // la raiz se paso del limite
        if(this.raiz.getClaves().size() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
    }

    // deja que el nodo se desborde y lo divide al volver
    private void insertarRecursivo(NodoArbolBMas nodo, int llave, String dato) {
        int i = nodo.getClaves().size() - 1;

        if(nodo.esHoja()) {
            while(i >= 0 && llave < nodo.getClaves().get(i)) {
                i--;
            }
            // repetida
            if(i >= 0 && nodo.getClaves().get(i) == llave) {
                nodo.getDatos().set(i, dato);
                return;
            }
            nodo.getClaves().add(i + 1, llave);
            nodo.getDatos().add(i + 1, dato);
        } else {
            while(i >= 0 && llave < nodo.getClaves().get(i)) {
                i--;
            }
            i++;
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            insertarRecursivo(hijo, llave, dato);

            if(hijo.getClaves().size() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;

        if (nodoLleno.esHoja()) {
            // en la hoja la llave del medio se queda tambien
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, nodoLleno.getDatos().size())));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            int llaveMedia = nuevoNodo.getClaves().get(0);
            padre.getClaves().add(indice, llaveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
        else {
            // en el interno la del medio sube
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
    }

    public void imprimirArbol() {
        imprimirNodo(raiz, "", true);
    }

    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for(int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }

    // recorre las hojas con el enlace siguiente
    public void imprimirHojas() {
        NodoArbolBMas actual = raiz;
        while(!actual.esHoja()) {
            actual = actual.getHijos().get(0);
        }
        StringBuilder sb = new StringBuilder("   ");
        while(actual != null) {
            sb.append(actual.getClaves()).append(" -> ");
            actual = actual.getSiguiente();
        }
        sb.append("null");
        System.out.println(sb);
    }

    public boolean buscar(int llave) {
        return obtenerDato(llave) != null;
    }

    public String obtenerDato(int llave) {
        NodoArbolBMas hoja = buscarHoja(llave);
        int pos = hoja.getClaves().indexOf(llave);
        return pos >= 0 ? hoja.getDatos().get(pos) : null;
    }

    // las claves de los internos son copias, hay que bajar hasta la hoja
    private NodoArbolBMas buscarHoja(int llave) {
        NodoArbolBMas nodo = raiz;
        while(!nodo.esHoja()) {
            int i = 0;
            while(i < nodo.getClaves().size() && llave >= nodo.getClaves().get(i)) {
                i++;
            }
            nodo = nodo.getHijos().get(i);
        }
        return nodo;
    }

    // devuelve true si la clave existia
    public boolean eliminar(int clave) {
        boolean eliminada = eliminarRecursivo(raiz, clave);

        // si la raiz se queda con un hijo baja un nivel
        if(!raiz.esHoja() && raiz.getHijos().size() == 1) {
            raiz = raiz.getHijos().get(0);
        }
        return eliminada;
    }

    private boolean eliminarRecursivo(NodoArbolBMas nodo, int llave) {
        if(nodo.esHoja()) {
            int pos = nodo.getClaves().indexOf(llave);
            if(pos < 0) {
                return false;
            }
            nodo.getClaves().remove(pos);
            nodo.getDatos().remove(pos);
            return true;
        }

        int i = 0;
        while(i < nodo.getClaves().size() && llave >= nodo.getClaves().get(i)) {
            i++;
        }
        NodoArbolBMas hijo = nodo.getHijos().get(i);
        boolean eliminada = eliminarRecursivo(hijo, llave);

        if(eliminada && estaEnDeficit(hijo)) {
            rebalancearHijo(nodo, i);
        }
        return eliminada;
    }

    // primero intenta prestamo, si no fusiona
    private void rebalancearHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas izquierdo = indice > 0 ? padre.getHijos().get(indice - 1) : null;
        NodoArbolBMas derecho = indice < padre.getHijos().size() - 1 ? padre.getHijos().get(indice + 1) : null;

        if(izquierdo != null && puedePrestar(izquierdo)) {
            prestarDeIzquierdo(padre, indice);
        } else if(derecho != null && puedePrestar(derecho)) {
            prestarDeDerecho(padre, indice);
        } else if(izquierdo != null) {
            fusionarHijos(padre, indice - 1);
        } else {
            fusionarHijos(padre, indice);
        }
    }

    private void prestarDeIzquierdo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas izquierdo = padre.getHijos().get(indice - 1);
        int ultima = izquierdo.getClaves().size() - 1;

        if(hijo.esHoja()) {
            hijo.getClaves().add(0, izquierdo.getClaves().remove(ultima));
            hijo.getDatos().add(0, izquierdo.getDatos().remove(ultima));
            padre.getClaves().set(indice - 1, hijo.getClaves().get(0));
        } else {
            // rotacion: separador baja, la ultima del hermano sube
            hijo.getClaves().add(0, padre.getClaves().get(indice - 1));
            hijo.getHijos().add(0, izquierdo.getHijos().remove(izquierdo.getHijos().size() - 1));
            padre.getClaves().set(indice - 1, izquierdo.getClaves().remove(ultima));
        }
    }

    private void prestarDeDerecho(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if(hijo.esHoja()) {
            hijo.getClaves().add(derecho.getClaves().remove(0));
            hijo.getDatos().add(derecho.getDatos().remove(0));
            padre.getClaves().set(indice, derecho.getClaves().get(0));
        } else {
            hijo.getClaves().add(padre.getClaves().get(indice));
            hijo.getHijos().add(derecho.getHijos().remove(0));
            padre.getClaves().set(indice, derecho.getClaves().remove(0));
        }
    }

    // une el hijo indice con el indice + 1, queda en el izquierdo
    private void fusionarHijos(NodoArbolBMas padre, int indice) {
        NodoArbolBMas izquierdo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if(izquierdo.esHoja()) {
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            // el separador del padre baja
            izquierdo.getClaves().add(padre.getClaves().get(indice));
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }
        padre.getClaves().remove(indice);
        padre.getHijos().remove(indice + 1);
    }

    // imprime n elementos desde la primera clave >= claveInicial, devuelve cuantos imprimio
    public int recorrerRango(int claveInicial, int n) {
        NodoArbolBMas hoja = buscarHoja(claveInicial);

        int pos = 0;
        while(pos < hoja.getClaves().size() && hoja.getClaves().get(pos) < claveInicial) {
            pos++;
        }

        int impresos = 0;
        while(hoja != null && impresos < n) {
            if(pos >= hoja.getClaves().size()) {
                // pasa a la siguiente hoja
                hoja = hoja.getSiguiente();
                pos = 0;
                continue;
            }
            System.out.println("   Clave " + hoja.getClaves().get(pos) + " -> " + hoja.getDatos().get(pos));
            impresos++;
            pos++;
        }
        return impresos;
    }
}
