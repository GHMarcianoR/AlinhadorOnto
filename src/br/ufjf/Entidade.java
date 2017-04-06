package br.ufjf;

import java.util.ArrayList;
import java.util.List;

public class Entidade {
    private String localName;
    private String uri;
    private String resourceLabel;
    private String anchor_text;
    private String uri_anc_esp;
    private List<String> redirects;
    private List<String> artCategorias;
    private List<String> propriedades;
    private List<String> propriedadesURI;
    private List<String> labels_onto;
    public Entidade()
    {
        localName = null;
        resourceLabel = null;
        redirects = null;
        propriedades = new ArrayList<>();
        propriedadesURI = new  ArrayList<>();
        artCategorias=  null;
        labels_onto = null;
        uri_anc_esp = null;
    }
    public void setNome(String n){localName = n;}
    public void setrscLabel(String l){resourceLabel = l;}
    public void setRedirects(List<String> r){redirects = r;}
    public void setURI(String u){uri = u;}
    public void addPropriedades(String uri,String s ){ propriedades.add(s); propriedadesURI.add(uri);}
    public void addCategorias(List<String> l){artCategorias = l;}
    public void setLabelsOnto(List<String> l){labels_onto = l;}
    public void setAnchorText(String s ){anchor_text = s;}
    public void setUriAncEsp(String s){uri_anc_esp = s;}
    
    public List<String> getPropriedadesURI(){return propriedadesURI;}
    public String getNome(){return localName;}
    public String getRscLabel(){return resourceLabel;}
    public List<String> getListRedirects(){return redirects;}
    public String getURI(){return uri;}
    public List<String> getPropriedades(){return propriedades;}
    public List<String> getCategorias(){return artCategorias;}
    public List<String> getLabelsOnto(){return labels_onto;}
    public String getAnchorText(){return anchor_text;}
    public String getUriAncEsp(){return uri_anc_esp;}
    //public DbpCategories getSubCategorias(){ return artCategorias;}
    
}
