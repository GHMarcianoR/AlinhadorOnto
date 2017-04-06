/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 *
 * @author zumbi
 */
public class AncoragemMemoria {
    
    private HashMap <String, List<String> > articleCategories;
    private HashMap <String, List<String> > labels;
    private HashMap <String, List<String> > redirects;
    private HashMap <String, List<String> > skos_categories;
    private Levenshtein lv;
    private List<Cluster> listClusters;
    public AncoragemMemoria(List<Cluster>  s) throws IOException
    {
        articleCategories = new HashMap<>();
        labels = new HashMap<>();
        redirects = new HashMap<>();
        skos_categories = new HashMap<>();
        lv = new Levenshtein();
        listClusters = s;
       
    }
    public void iniciarProcessoAncoragem() throws IOException
    {
        String [] dbs = {"labels_en.ttl","redirects_en.ttl"};
        System.out.println("Carregando datasets DBPedia");
        for(String db : dbs)
             load(db,listClusters);
    }
    private void load(String db, List<Cluster> listclusters) throws FileNotFoundException, IOException
    {
        String path = null;
        HashMap<String, List<String> >  aux;
        switch(db)
        {
            case "labels_en.ttl":
                aux = labels;
                path  = Globais.path_labels;
              
                break;
            case "redirects_en.ttl":
                aux = redirects;
                 path  = Globais.path_redirects;
                
                break;
            case "article_categories_en.ttl":
                    aux = articleCategories;
                    path = Globais.path_article_categories;
                    break;
            default:
                aux = null;
                break;
        }
       
        if(aux!= null)
        {            
            BufferedReader br = new BufferedReader(new FileReader(path));
            br.readLine();
            String aux_;
            while(br.ready())
            {
                try
                {
                     String[] lSeparada = null;

                    switch (db) {
                        case "labels_en.ttl":
                            lSeparada = br.readLine().split("<http://www.w3.org/2000/01/rdf-schema#label>");
                            
                            lSeparada[0] = lSeparada[0].replace("<http://dbpedia.org/resource/", "");
                            lSeparada[0] = lSeparada[0].substring(0, lSeparada[0].length()-2);
                           
                            lSeparada[1] = lSeparada[1].replace("\"", "");
                            lSeparada[1] = lSeparada[1].replace("@en", "");
                            lSeparada[1] = lSeparada[1].replace(".", "");
                            lSeparada[1] = lSeparada[1].substring(1, lSeparada[1].length()-1);
                            lSeparada[1] = lSeparada[1].replace(" ", "_");

                            break;
                        case "redirects_en.ttl":
                             lSeparada = br.readLine().split("<http://dbpedia.org/ontology/wikiPageRedirects>");
                             lSeparada[0] = lSeparada[0].replace("<http://dbpedia.org/resource/", "");
                             lSeparada[0] = lSeparada[0].substring(0, lSeparada[0].length()-2);
                             lSeparada[1] = lSeparada[1].replace("<http://dbpedia.org/resource/", "");
                             lSeparada[1] = lSeparada[1].replace("> .", "");
                            break;
                        case "article_categories_en.ttl":
                            lSeparada = br.readLine().split("<http://purl.org/dc/terms/subject>");
                         
                            lSeparada[0] = lSeparada[0].replace("<http://dbpedia.org/resource/", "");
                            lSeparada[0] = lSeparada[0].substring(0, lSeparada[0].length()-2);
                            
                            lSeparada[1] = lSeparada[1].replace( "<http://dbpedia.org/resource/Category:", "");
                            lSeparada[1]= lSeparada[1].replace( "> .", "");
                            break;
                        default:
                            break;
                    }
                    if(!aux.containsKey(lSeparada[0]) && lSeparada[1]!= null)
                    {
                        List<String> l = new ArrayList<>();
                        l.add(lSeparada[1].toLowerCase());
                        aux.put(lSeparada[0].toLowerCase(),l);
                    }
                    else
                    {
                       if(lSeparada[1] != null)
                        aux.get(lSeparada[0].toLowerCase()).add(lSeparada[1].toLowerCase());
                    }
                }catch(Exception e){}
               
            }
        
        }
         switch(db)
        {
            case "labels_en.ttl":
                ancorar_labels(listclusters);
                labels = null;
                break;
            case "redirects_en.ttl":
               ancorar_redirects(listclusters);
               redirects = null;
                break;
            case "article_categories_en.ttl":
                 ancorar_categories(listclusters);
                articleCategories = null;
                break;
            default:
                break;
        }
       System.gc();
    }
    private String selecionarLabels(List<String> list, String ancora)
    {
        String escolhida = null;
        double max = 0.8;
            for(String s : list)
            {
               if(lv.getSimilarity(s, ancora) > max)
                { 
                    escolhida = s;
                    break;           
                }
            }

        return list.get(0);
    }
    
    private List<String> selecionarRedirects(List<String> listStr, String ancora)
     {
       List<String> escolhidas = new ArrayList<>();
     
       int i = 0;
       for(String s : listStr)
       {
            if(lv.getSimilarity(ancora, s)> Globais.val_redirects)
                escolhidas.add(s);
       } 
      return escolhidas;
     }
    
    private void ancorar_labels(List<Cluster> listClusters)
    {        
        System.out.println("Iniciando Processo de Ancoragem labels");
        System.out.flush();
        for(Cluster c : listClusters)
        {
        
           if(labels.get(c.getRoot().getNome())!= null && !labels.get(c.getRoot().getNome()).isEmpty())  
                  c.getRoot().setrscLabel(labels.get(c.getRoot().getNome()).get(0));
           else
               for(String s : c.getRoot().getLabelsOnto())
                    if(labels.get(s) != null && !labels.get(s).isEmpty())
                        c.getRoot().setrscLabel(labels.get(s).get(0));
           
           for(Entidade e : c.getListEntidades())
                if(labels.get(e.getNome())!= null && !labels.get(e.getNome()).isEmpty())                    
                        e.setrscLabel(labels.get(e.getNome()).get(0));    
                else 
                   for(String st : e.getLabelsOnto())
                       if(labels.get(st) != null && !labels.get(st).isEmpty())
                            e.setrscLabel(labels.get(st).get(0));
                            
                    
                
        }
    }
    private void ancorar_redirects(List<Cluster> listClusters)            
    {
        System.out.println("Iniciando Processo de Ancoragem redirects");
        System.out.flush();
        for(Cluster c: listClusters)
        {
            List<String> red = null;
            String label =  c.getRoot().getRscLabel();
            if(label != null)
                red = redirects.get(label);
            if(red != null)
                    c.getRoot().setRedirects(selecionarRedirects(red,c.getRoot().getNome()) );  
            for(Entidade e : c.getListEntidades())
               if( e.getRscLabel() != null && redirects.get( e.getRscLabel()) != null ) 
                   e.setRedirects(selecionarRedirects(redirects.get( e.getRscLabel()),e.getNome()));
            
        }
    }
    private void ancorar_categories(List<Cluster> listClusters)
    {
        System.out.println("Iniciando Processo de Ancoragem article_categories");
        System.out.flush();
        List<String> ctg;
        for(Cluster c: listClusters)
        {
            ctg = articleCategories.get(c.getRoot().getNome());           
            if(ctg != null)
               c.getRoot().addCategorias(ctg);
            for(Entidade e : c.getListEntidades())
            {
                ctg = articleCategories.get(e.getNome());
                    if(ctg != null)
                      e.addCategorias(ctg);                    
            }
        }
    }

    public HashMap<Cluster, List<Entidade> > getAncorados() throws IOException
    {
        HashMap<Cluster, List<Entidade>> ancorados = new HashMap<> ();
       
        for(Cluster c : listClusters)
        {
             List<Entidade> list = new ArrayList<> ();
            if(c.getRoot().getListRedirects() != null)
                   list.add(c.getRoot());
            for(Entidade e : c.getListEntidades())
              if(e.getListRedirects() != null )
                    list.add(e);
            if(!list.isEmpty())
                 ancorados.put(c, list);
              
        }
        System.out.println("Ancorados Redirects: "+ancorados.size());
        return ancorados.isEmpty() ? null : ancorados ;
    }
    public HashMap <String, List<String> > getArticleCategories(){return articleCategories;}
    public HashMap <String, List<String> > getLabels(){return labels;}
    public HashMap <String, List<String> > getRedirects(){return redirects;}
    public HashMap <String, List<String> > getSkos_cateries(){return skos_categories;}
    
}
