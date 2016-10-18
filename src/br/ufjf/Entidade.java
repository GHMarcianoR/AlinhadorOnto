package br.ufjf;

import java.util.ArrayList;
import java.util.List;

public class Entidade {
    private String localName;
    private String uri;
    private String resourceLabel;
    private List<String> redirects;
    private List<String> artCategorias;
    private List<String> propriedades;
    private List<String> propriedadesURI;
    public Entidade()
    {
        localName = null;
        resourceLabel = null;
        redirects = null;
        propriedades = new ArrayList<>();
        propriedadesURI = new  ArrayList<>();
        artCategorias=  null;
    }
    public void setNome(String n){localName = n;}
    public void setrscLabel(String l){resourceLabel = l;}
    public void setRedirects(List<String> r){redirects = r;}
    public void setURI(String u){uri = u;}
    public void addPropriedades(String uri,String s ){ propriedades.add(s); propriedadesURI.add(uri);}
   // public void addCategorias(String c){artCategorias.add(c);}
    public void addCategorias(List<String> l){artCategorias = l;}
    
    public List<String> getPropriedadesURI(){return propriedadesURI;}
    public String getNome(){return localName;}
    public String getRscLabel(){return resourceLabel;}
    public List<String> getListRedirects(){return redirects;}
    public String getURI(){return uri;}
    public List<String> getPropriedades(){return propriedades;}
    public List<String> getCategorias(){return artCategorias;}
    //public DbpCategories getSubCategorias(){ return artCategorias;}
    
}
