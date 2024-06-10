package btree;

import java.util.ArrayList;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> { // Nombre de la clase
    // Usar Comparable para realizar comparación de cualquier tipo de dato
    protected ArrayList<E> keys; // ArrayList para guardar las claves
    protected ArrayList<BNode<E>> childs; // Un ArrayList de los nodos hijos
    protected int count; // Variable contador que indica cuántas claves se han creado
    private static int nextId = 0; // Variable estática para generar identificadores únicos
    protected int idNode; // Identificador único del nodo
    
    // Constructor, recibe como argumento el orden del árbol
    public BNode(int n) {
        this.keys = new ArrayList<>(n); // Crear el ArrayList de claves con tamaño n
        this.childs = new ArrayList<>(n); // Crear el ArrayList de hijos con tamaño n
        this.count = 0; // El contador inicia en cero
        this.idNode = nextId++; // Asigna el identificador único al nodo y lo incrementa
        for (int i = 0; i < n; i++) { // Inicializa las claves y los hijos con null
            this.keys.add(null); // Añadir las claves necesarias
            this.childs.add(null); // Añadir los hijos necesarios
        }
    }

    // Método para comprobar si un nodo está lleno
    public boolean nodeFull(int n) {
        return this.count == n; // Si el contador es igual al orden, el nodo está lleno
    }

    // Método para comprobar si un nodo está vacío
    public boolean nodeEmpty() {
        return this.count == 0; // Si el contador es igual a 0, el nodo está vacío
    }

    // Buscar un valor en el nodo
    // Retorna true si encuentra la clave, false en caso contrario
    public boolean searchNode(E key) {
        int i = 0;
        // Iterar mientras no se hayan revisado todas las claves y la clave a buscar sea mayor
        while (i < count && key.compareTo(keys.get(i)) > 0) {
            i++;
        }
        // Si encuentra la clave, retorna true, de lo contrario retorna false
        if (i < count && key.compareTo(keys.get(i)) == 0) {
            return true; // Clave encontrada en la posición i
        } else {
            return false; // Clave no encontrada, debería descender al hijo en la posición i
        }
    }

    // Imprimir los valores de las claves del nodo
    @Override
    public String toString() {
        String result = "Node ID: " + idNode + ", Keys: ";
        for (int i = 0; i < count; i++) {
            if (keys.get(i) != null) {
                result += keys.get(i).toString() + " "; // Concatenar las claves con un espacio
            }
        }
        return result.trim(); // Retornar la cadena 
    }

}


