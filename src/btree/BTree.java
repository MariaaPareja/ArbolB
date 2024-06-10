package btree;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import exceptions.ItemNoFound;

public class BTree<E extends Comparable<E>> {
    private BNode<E> root; // Nodo raíz del árbol B
    private int orden; // Orden del árbol B
    private boolean up; // Indica si se debe elevar una clave
    private BNode<E> nDes; // Nodo desbordado

    public BTree(int orden) {
        this.orden = orden;
        this.root = null;
    }

    // Método para verificar si el árbol está vacío
    public boolean isEmpty() {
        return this.root == null;
    }

    // Método para insertar una clave en el árbol
    public void insert(E cl) {
        up = false;
        E mediana;
        BNode<E> pnew;
        mediana = push(this.root, cl);
        if (up) {
            pnew = new BNode<E>(this.orden);
            pnew.count = 1;
            pnew.keys.set(0, mediana);
            pnew.childs.set(0, this.root);
            pnew.childs.set(1, nDes);
            this.root = pnew;
        }
    }

    // Método auxiliar para insertar una clave en un nodo
    private E push(BNode<E> current, E cl) {
        int pos[] = new int[1];
        E mediana;
        if (current == null) {
            up = true;
            nDes = null;
            return cl;
        } else {
            boolean fl;
            fl = current.searchNode(cl);
            if (fl) {
                System.out.println("Item duplicado\n");
                up = false;
                return null;
            }
            mediana = push(current.childs.get(pos[0]), cl);
            if (up) {
                if (current.nodeFull(this.orden - 1)) {
                    mediana = dividedNode(current, mediana, pos[0]);
                } else {
                    up = false;
                    putNode(current, mediana, nDes, pos[0]);
                }
            }
            return mediana;
        }
    }

    // Método para insertar una clave y su hijo derecho en el nodo actual
    private void putNode(BNode<E> current, E cl, BNode<E> rd, int k) {
        int i;
        for (i = current.count - 1; i >= k; i--) {
            current.keys.set(i + 1, current.keys.get(i));
            current.childs.set(i + 2, current.childs.get(i + 1));
        }
        current.keys.set(k, cl);
        current.childs.set(k + 1, rd);
        current.count++;
    }

    // Método para dividir un nodo que está lleno
    private E dividedNode(BNode<E> current, E cl, int k) {
        BNode<E> rd = nDes;
        int i, posMdna;
        posMdna = (k <= this.orden / 2) ? this.orden / 2 : this.orden / 2 + 1;
        nDes = new BNode<E>(this.orden);
        for (i = posMdna; i < this.orden - 1; i++) {
            nDes.keys.set(i - posMdna, current.keys.get(i));
            nDes.childs.set(i - posMdna + 1, current.childs.get(i + 1));
        }
        nDes.count = (this.orden - 1) - posMdna;
        current.count = posMdna;
        if (k <= this.orden / 2)
            putNode(current, cl, rd, k);
        else
            putNode(nDes, cl, rd, k - posMdna);
        E median = current.keys.get(current.count - 1);
        nDes.childs.set(0, current.childs.get(current.count));
        current.count--;
        return median;
    }
    
    // Método toString para devolver el contenido del árbol
    @Override
    public String toString() {
        String s = "";
        if (isEmpty()) {
            s += "Vacio...";
        } else {
            s += "Id.Nodo    Claves Nodo    Id.Padre    Id.Hijos\n"; // Títulos de la tabla
            s += writeTree(this.root, null); // Llamada al método recursivo
        }
        return s;
    }

    // Método recursivo para recorrer el árbol y agregar el contenido de cada nodo a la cadena
    private String writeTree(BNode<E> current, BNode<E> parent) {
        if (current == null) {
            return "";
        }

        StringBuilder s = new StringBuilder();

        // Añadir la información del nodo actual
        s.append(current.idNode).append("    (");
        for (int i = 0; i < current.count; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(current.keys.get(i));
        }
        s.append(")    [");
        s.append(parent != null ? parent.idNode : "--");
        s.append("]    [");
        for (int i = 0; i <= current.count; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(current.childs.get(i) != null ? current.childs.get(i).idNode : "--");
        }
        s.append("]\n");

        // Recursivamente recorrer los nodos hijos
        for (int i = 0; i <= current.count; i++) {
            s.append(writeTree(current.childs.get(i), current));
        }

        return s.toString();
    }
    
    // Método para buscar una clave en el árbol
    public boolean search(E cl) {
        return searchInNode(this.root, cl);
    }

    // Método de soporte recursivo para buscar una clave en el árbol
    private boolean searchInNode(BNode<E> current, E cl) {
        if (current == null) {
            return false;
        }

        int[] pos = new int[1];
        boolean found = current.searchNode(cl);

        if (found) {
            System.out.println(cl + " se encuentra en el nodo " + current.idNode + " en la posición " + pos[0]);
            return true;
        } else {
            return searchInNode(current.childs.get(pos[0]), cl);
        }
    }
    
 // Método recursivo para eliminar una clave del árbol
    private void delete(BNode<E> current, E cl) {
        int[] pos = new int[1];
        boolean found = current.searchNode(cl);

        if (found) { // Clave encontrada en el nodo actual
            if (current.childs.get(pos[0]) == null) { // Nodo hoja
                removeFromLeaf(current, pos[0]);
            } else { // Nodo interno
                removeFromInternalNode(current, pos[0]);
            }
        } else { // Clave no encontrada en el nodo actual
            if (current.childs.get(pos[0]) == null) {
                System.out.println("La clave " + cl + " no se encuentra en el árbol.");
                return;
            }
            boolean atLeastMinKeys = current.childs.get(pos[0]).count > (orden / 2 - 1);
            delete(current.childs.get(pos[0]), cl);
            if (!atLeastMinKeys && current.childs.get(pos[0]).count < (orden / 2)) {
                fixChildSize(current, pos[0]);
            }
        }
    }

    // Eliminar una clave de un nodo hoja
    private void removeFromLeaf(BNode<E> node, int pos) {
        for (int i = pos; i < node.count - 1; i++) {
            node.keys.set(i, node.keys.get(i + 1));
        }
        node.count--;
    }

    // Eliminar una clave de un nodo interno
    private void removeFromInternalNode(BNode<E> node, int pos) {
        E key = node.keys.get(pos);
        BNode<E> predNode = node.childs.get(pos);
        if (predNode.count > (orden / 2 - 1)) { // Predesor
            E predKey = getPredecessor(predNode);
            node.keys.set(pos, predKey);
            delete(predNode, predKey);
        } else { // Sucesor
            BNode<E> succNode = node.childs.get(pos + 1);
            if (succNode.count > (orden / 2 - 1)) {
                E succKey = getSuccessor(succNode);
                node.keys.set(pos, succKey);
                delete(succNode, succKey);
            } else { // Fusionar
                mergeNodes(node, pos);
                delete(predNode, key);
            }
        }
    }

    // Obtener el predecesor de una clave
    private E getPredecessor(BNode<E> node) {
        while (node.childs.get(node.count) != null) {
            node = node.childs.get(node.count);
        }
        return node.keys.get(node.count - 1);
    }

    // Obtener el sucesor de una clave
    private E getSuccessor(BNode<E> node) {
        while (node.childs.get(0) != null) {
            node = node.childs.get(0);
        }
        return node.keys.get(0);
    }

    // Arreglar el tamaño del hijo después de una eliminación
    private void fixChildSize(BNode<E> node, int pos) {
        if (pos > 0 && node.childs.get(pos - 1).count > (orden / 2 - 1)) {
            borrowFromLeftSibling(node, pos);
        } else if (pos < node.count && node.childs.get(pos + 1).count > (orden / 2 - 1)) {
            borrowFromRightSibling(node, pos);
        } else {
            if (pos > 0) {
                mergeNodes(node, pos - 1);
            } else {
                mergeNodes(node, pos);
            }
        }
    }

    // Pedir prestado de un hermano izquierdo
    private void borrowFromLeftSibling(BNode<E> node, int pos) {
        BNode<E> child = node.childs.get(pos);
        BNode<E> leftSibling = node.childs.get(pos - 1);

        for (int i = child.count - 1; i >= 0; i--) {
            child.keys.set(i + 1, child.keys.get(i));
        }
        if (child.childs.get(0) != null) {
            for (int i = child.count; i >= 0; i--) {
                child.childs.set(i + 1, child.childs.get(i));
            }
        }

        child.keys.set(0, node.keys.get(pos - 1));
        if (child.childs.get(0) != null) {
            child.childs.set(0, leftSibling.childs.get(leftSibling.count));
        }
        node.keys.set(pos - 1, leftSibling.keys.get(leftSibling.count - 1));
        child.count++;
        leftSibling.count--;
    }

    // Pedir prestado de un hermano derecho
    private void borrowFromRightSibling(BNode<E> node, int pos) {
        BNode<E> child = node.childs.get(pos);
        BNode<E> rightSibling = node.childs.get(pos + 1);

        child.keys.set(child.count, node.keys.get(pos));
        if (child.childs.get(0) != null) {
            child.childs.set(child.count + 1, rightSibling.childs.get(0));
        }
        node.keys.set(pos, rightSibling.keys.get(0));

        for (int i = 0; i < rightSibling.count - 1; i++) {
            rightSibling.keys.set(i, rightSibling.keys.get(i + 1));
        }
        if (rightSibling.childs.get(0) != null) {
            for (int i = 0; i < rightSibling.count; i++) {
                rightSibling.childs.set(i, rightSibling.childs.get(i + 1));
            }
        }

        child.count++;
        rightSibling.count--;
    }

    // Fusionar nodos
    private void mergeNodes(BNode<E> node, int pos) {
        BNode<E> leftChild = node.childs.get(pos);
        BNode<E> rightChild = node.childs.get(pos + 1);

        leftChild.keys.set(leftChild.count, node.keys.get(pos));
        for (int i = 0; i < rightChild.count; i++) {
            leftChild.keys.set(leftChild.count + 1 + i, rightChild.keys.get(i));
        }
        if (leftChild.childs.get(0) != null) {
            for (int i = 0; i <= rightChild.count; i++) {
                leftChild.childs.set(leftChild.count + 1 + i, rightChild.childs.get(i));
            }
        }

        for (int i = pos; i < node.count - 1; i++) {
            node.keys.set(i, node.keys.get(i + 1));
            node.childs.set(i + 1, node.childs.get(i + 2));
        }
        node.count--;

        leftChild.count += rightChild.count + 1;
    }
    
    // Método estático para construir un BTree a partir de un archivo
    public static BTree<Integer> building_Btree(String filename) throws IOException, ItemNoFound {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int order = Integer.parseInt(reader.readLine().trim()); // Leer el orden del árbol
        BTree<Integer> tree = new BTree<>(order);

        String line;
        Map<Integer, BNode<Integer>> nodeMap = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",\\s*");
            int level = Integer.parseInt(parts[0]);
            int idNode = Integer.parseInt(parts[1]);
            String[] keyStrings = parts[2].split(",");
            ArrayList<Integer> keys = new ArrayList<>();
            for (String keyString : keyStrings) {
                keys.add(Integer.parseInt(keyString.trim()));
            }

            BNode<Integer> node = new BNode<>(order);
            node.idNode = idNode;
            node.count = keys.size();
            for (int i = 0; i < keys.size(); i++) {
                node.keys.set(i, keys.get(i));
            }

            nodeMap.put(idNode, node);
            if (tree.root == null) {
                tree.root = node;
            }
        }
        reader.close();

        // Verificar y construir el árbol
        for (BNode<Integer> node : nodeMap.values()) {
            for (int i = 0; i <= node.count; i++) {
                int childId = node.childs.get(i) != null ? node.childs.get(i).idNode : -1;
                if (childId != -1) {
                    node.childs.set(i, nodeMap.get(childId));
                }
            }
        }

        if (!verifyBTree(tree)) {
            throw new ItemNoFound("El archivo no cumple con las propiedades de un BTree.");
        }

        return tree;
    }

    // Método para verificar si el árbol cumple con las propiedades de un BTree
    private static boolean verifyBTree(BTree<Integer> tree) {
        return verifyNode(tree.root, tree.orden);
    }

    // Método recursivo para verificar las propiedades de un nodo
    private static boolean verifyNode(BNode<Integer> node, int order) {
        if (node == null) {
            return true;
        }

        if (node.count > order - 1 || node.count < (order / 2) - 1) {
            return false;
        }

        for (int i = 0; i <= node.count; i++) {
            if (!verifyNode(node.childs.get(i), order)) {
                return false;
            }
        }

        return true;
    }
}
