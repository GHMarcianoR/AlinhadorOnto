/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.util.List;

/**
 *
 * @author zumbi
 */
public class DbpCategories {
    private int nivel;
    private List<String> categorias;
    private DbpCategories subCategorias;
    
    public DbpCategories()
    {
        categorias = null;
        subCategorias = null;
        nivel = -1;
    }
    public void setNivel(int n){nivel = n;}
    public void setCategorias (List<String> cat){categorias = cat;}
    public List<String> getCategorias(){ return categorias;}
    public void setSubCategorias(List<String> cat, int n)
    {
        if(nivel == -1)
        {
            this.setCategorias(cat);
            this.setNivel(n);            
        }
        else
        {
            subCategorias = new DbpCategories();
            this.subCategorias.setSubCategorias(cat, n);
        }
        
        
    }
    public  List<String> getSubCategorias(int n)    
    {
        if(n > nivel)
           this.subCategorias.getSubCategorias(n);
        else if(n == nivel)
            return categorias;
    
        return null;
        
    }
}
