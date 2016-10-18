/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zumbi
 */
public class Cluster {
    
    private Entidade root;
    private List<Entidade> nos;
    
    public Cluster()
    { 
        nos = new ArrayList<>();
         }
   public void add(Entidade e){nos.add(e);}
   public void setRoot(Entidade e){root = e;}
   
   public List<Entidade> getListEntidades(){return nos;}
   public Entidade getRoot(){return root;}
   public boolean vazio(){return root == null || nos.isEmpty();}
}
