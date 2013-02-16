package Core.grammar;

import java.util.HashMap;
import java.util.LinkedList;

import Core.structs.Struct;

public class Arbol{ //El arbol de instrucciones de karel
    public HashMap<String, FunctionDef> funciones;
    public LinkedList<Struct> main;
    public Arbol(){
        this.funciones = new HashMap<String, FunctionDef>();
        this.main = new LinkedList<Struct>();
    }
}
