/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
    public AncoragemMemoria(List<Cluster>  s) throws IOException
    {
        articleCategories = new HashMap< > ();
        labels = new HashMap< > ();
        redirects = new HashMap< > ();
        skos_categories = new HashMap< > ();
        lv = new Levenshtein();
        String [] dbs = {"labels_en.ttl","redirects_en.ttl","article_categories_en.ttl"};
        System.out.println("Carregando datasets DBPedia");
        for(String db : dbs)
             load(db,s);
       
        
    }
    private void load(String db, List<Cluster> listclusters) throws FileNotFoundException, IOException
    {
        String path = null;
        HashMap<String, List<String> >  aux;
        switch(db)
        {
            case "labels_en.ttl":
                aux = labels;
                path  = "/root/ontology_alignment/dbpedia/labels_en.ttl";
                break;
            case "redirects_en.ttl":
                aux = redirects;
                 path  = "/root/ontology_alignment/dbpedia/redirects_en.ttl";
                break;
            case "article_categories_en.ttl":
                    aux = articleCategories;
                    path  = "/root/ontology_alignment/dbpedia/article_categories_en.ttl";
                    break;
            case "skos_categories_en.ttl":
                    aux = articleCategories;
                    path  = "/root/ontology_alignment/dbpedia/skos_categories_en.ttl";
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
                            lSeparada[1] = lSeparada[1].replace("\"", "");
                            lSeparada[1] = lSeparada[1].replace("@en", "");
                            lSeparada[1] = lSeparada[1].replace(".", "");
                            lSeparada[1] = lSeparada[1].substring(1, lSeparada[1].length()-1);
                            lSeparada[1] = lSeparada[1].replace(" ", "_");

                            break;
                        case "redirects_en.ttl":
                             lSeparada = br.readLine().split("<http://dbpedia.org/ontology/wikiPageRedirects>");
                            break;
                        case "article_categories_en.ttl":
                            lSeparada = br.readLine().split("<http://purl.org/dc/terms/subject>");
                            lSeparada[1] = lSeparada[1].replace( "<http://dbpedia.org/resource/Category:", "");
                            lSeparada[1]= lSeparada[1].replace( "> .", "");
                            break;
                        case "skos_categories_en.ttl":
                           String str = br.readLine();
                            if(!str.contains("core#broader"))
                            {
                               lSeparada[1] = null;
                            }   
                            else
                            {
                                lSeparada = str.split("<http://purl.org/dc/terms/subject>");
                                lSeparada[1] = lSeparada[1].replace("http://dbpedia.org/resource/Category:","");
                                lSeparada[1] = lSeparada[1].substring(1, lSeparada[1].indexOf(">"));
                            }   break;
                        default:
                            break;
                    }
                    if(!aux.containsKey(lSeparada[0]) && lSeparada[1]!= null)
                    {
                        List<String> l = new ArrayList<>();
                        l.add(lSeparada[1]);
                        aux.put(lSeparada[0],l);
                    }
                    else
                    {
                       if(lSeparada[1] != null)
                        aux.get(lSeparada[0]).add(lSeparada[1]);
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
            case "skos_categories_en.ttl":
                skos_categories = null;
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
       double max = 0.8;
       int i = 0;
       /*       for(String s : listStr)
       {
       if(lv.getSimilarity(ancora, s) > max)
       escolhidas.add(s);
       }*/
       return listStr;
     }
    
    private void ancorar_labels(List<Cluster> listClusters)
    {        
        System.out.println("Iniciando Processo de Ancoragem labels");
        for(Cluster c : listClusters)
        {
           List<String> l =labels.get(c.getRoot().getNome());
           if(l!= null)
           {
               if(l.size() == 1)  
               c.getRoot().setrscLabel(l.get(0));
               for(Entidade e : c.getListEntidades())
               {
                    if(labels.get(e.getNome())!= null && labels.get(e.getNome()).size() == 1)                    
                        e.setrscLabel(labels.get(e.getNome()).get(0));              

               }
           }
            
        }
    }
    private void ancorar_redirects(List<Cluster> listClusters)            
    {
        System.out.println("Iniciando Processo de Ancoragem redirects");
        for(Cluster c: listClusters)
        {
            List<String> red = null;
            String label =  c.getRoot().getRscLabel();
            if(label != null)
                red = redirects.get(label);
            if(red != null)
                    c.getRoot().setRedirects(selecionarRedirects(red,c.getRoot().getNome()) );  
            for(Entidade e : c.getListEntidades())
            {
                String labelE =  e.getRscLabel();
                if(labelE != null) 
                    red = redirects.get(labelE);
                if(red != null)
                  e.setRedirects(selecionarRedirects(red,e.getNome()));
            }
        }
    }
    private void ancorar_categories(List<Cluster> listClusters)
    {
        System.out.println("Iniciando Processo de Ancoragem article_categories");
        List<String> ctg = null;
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
    public HashMap <String, List<String> > getArticleCategories(){return articleCategories;}
    public HashMap <String, List<String> > getLabels(){return labels;}
    public HashMap <String, List<String> > getRedirects(){return redirects;}
    public HashMap <String, List<String> > getSkos_cateries(){return skos_categories;}
    
}
