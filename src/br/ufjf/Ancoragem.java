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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 *
 * @author zumbi
 */
public class Ancoragem {

    public Ancoragem(List<Cluster>  s) throws IOException
    {

        ancorar(s);
    }
    /*     private void loadCategories() throws FileNotFoundException, IOException
    {
    String database = "/root/ontology_alignment/dbpedia/article_categories_en.ttl";
    BufferedReader br = new BufferedReader(new FileReader(database));
    List<String> listCategories = new ArrayList<String>();
    
    boolean n = false;
    String anterior = "";
    
    while(br.ready())
    {
    String [] campos  = br.readLine().split(" ");
    listCategories.add(campos[2]);
    
    if(!anterior.equals(campos[0]))
    {
    articleCategories.put(anterior, listCategories);
    anterior = campos[0];
    listCategories = new ArrayList<>();
    }
    
    
    }
    
    }*/
    private String selecionarLabels(List<String> list, String ancora)
    {
        String escolhida = null;
        Levenshtein lv =  new Levenshtein();
        double max = 0.8;
        for(String s : list)
        {
            String nova = s.replace("http://dbpedia.org/resource/", "");
            nova = nova.substring(1, nova.indexOf(">"));
           
            if(lv.getSimilarity(nova, ancora) > max)
            { 
                escolhida = nova;
                break;           
            }
        }            
        return escolhida;
    }
     private List<String> selecionarRedirects(List<String> listStr, String ancora)
     {
       List<String> escolhidas = new ArrayList<String>();
       Levenshtein lv =  new Levenshtein();
       double max = 0.8;
       int i = 0;
       for(String s : listStr)
       {
           String separadas [] = s.split(" ");
           
           separadas[2] = separadas[2].replace("http://dbpedia.org/resource/", "");
           separadas[2] = separadas[2].substring(1,separadas[2].indexOf(">"));
           
           separadas[0] = separadas[0].replace("http://dbpedia.org/resource/", "");
           separadas[0] = separadas[0].substring(1,separadas[0].indexOf(">"));
           if(lv.getSimilarity(ancora, separadas[2]) > max)
           escolhidas.add(separadas[0]);
       }
       return escolhidas;
     }
     private List<String> selecionarCategories(List<String> listStr,String ancora)
     {
       List<String> escolhidas = new ArrayList<String>();
       Levenshtein lv =  new Levenshtein();
       double max = 0.8;
       int i = 0;
       for(String s : listStr)
       {
           try
           {
               String separadas [] = s.split(" ");
               separadas[0] = separadas[0].replace("http://dbpedia.org/resource/", "");
               separadas[0] = separadas[0].substring(1,separadas[0].indexOf(">"));
               
               if(lv.getSimilarity(ancora, separadas[0]) > max)
               {               
                  separadas[2] = separadas[2].replace("http://dbpedia.org/resource/Category:", "");
                  separadas[2] = separadas[2].substring(1,separadas[2].indexOf(">"));
                  escolhidas.add(separadas[2]);
               }
           }catch(Exception e)
           {
               System.err.println(e.getMessage());
           }
       }
       return escolhidas;
     }
     private List<String> selecionarSkosCategories(List<String> listStr,String ancora)
     {
       List<String> escolhidas = new ArrayList<String>();
       Levenshtein lv =  new Levenshtein();
       double max = 0.8;
       int i = 0;
       for(String s : listStr)
       {
           try
           {
               String separadas [] = s.split(" ");
               separadas[0] = separadas[0].replace("http://dbpedia.org/resource/Category:", "");
               separadas[0] = separadas[0].substring(1,separadas[0].indexOf(">"));
               
               if(lv.getSimilarity(ancora, separadas[0]) > max)
               {               
                  separadas[2] = separadas[2].replace("http://dbpedia.org/resource/Category:", "");
                  separadas[2] = separadas[2].substring(1,separadas[2].indexOf(">"));
                  escolhidas.add(separadas[2]);
               }
           }catch(Exception e)
           {
               System.err.println(e.getMessage());
           }
       }
       return escolhidas;
     }
  
    private String auxAncorarLabels(String s) throws IOException
    {
       
      //  System.out.println("Ancorando: "+s+" ao labels");
        String database = "/root/ontology_alignment/dbpedia/labels_en.ttl";
        //String database = "/home/zumbi/dbpedia/labels_en.nt";
        List<String> listStr = new ArrayList<String>();
        String cmd = "grep "+s+ " "+database;
        String linha;       
        String str = null;
	Runtime rt = Runtime.getRuntime();
	Process proc = rt.exec(cmd);	      
		 
	BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	while( (linha = is.readLine() ) != null)
              listStr.add(linha.split(" ")[0]);
        if(!listStr.isEmpty())        
             str = selecionarLabels(listStr, s);
          
        return str;
    }
    private List<String>  auxAncorarRedirects(String s) throws IOException
    {
       // System.out.println("Ancorando: "+s+" ao redirects");
        String database = "/root/ontology_alignment/dbpedia/redirects_en.ttl";
        System.out.flush();
      //  String database = "/home/zumbi/dbpedia/redirects_en.nt";
        List<String> listStr = new ArrayList<String>();
        String cmd = "grep "+s+ " "+database;
        String linha;
        String str = null;
	Runtime rt = Runtime.getRuntime();
	Process proc = rt.exec(cmd);	      		 
	BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while( (linha = is.readLine() ) != null)
              listStr.add(linha);
      
        return selecionarRedirects(listStr,s);      
    }
    private List<String>  auxAncorarArtCategories(String s) throws IOException
    {
  //      System.out.println("Ancorando: "+s+" ao Article Categories");
        String database = "/root/ontology_alignment/dbpedia/article_categories_en.ttl";
        System.out.flush();
      //  String database = "/home/zumbi/dbpedia/article_templates_en.ttl";
        List<String> listStr = new ArrayList<String>();
        String cmd = "grep "+s+ " "+database;
        String linha;
        List<String> result = null;
        String str = null;
	Runtime rt = Runtime.getRuntime();
	Process proc = rt.exec(cmd);	      		 
	BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while( (linha = is.readLine() ) != null)
              listStr.add(linha);
     
        return selecionarCategories(listStr,s);
     }
        private List<String>  auxAncorarSkosCategories(String s) throws IOException
    {
       // System.out.println("Ancorando: "+s+" ao skos_Categories");
        String database = "/root/ontology_alignment/dbpedia/skos_categories_en.ttl";
        System.out.flush();
      //  String database = "/home/zumbi/dbpedia/article_templates_en.ttl";
        List<String> listStr = new ArrayList<String>();
        String cmd = "grep "+s+ " "+database;
        String linha;
        String str = null;
	Runtime rt = Runtime.getRuntime();
	Process proc = rt.exec(cmd);	      		 
	BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while( (linha = is.readLine() ) != null)
              listStr.add(linha);
     
        return selecionarSkosCategories(listStr,s);
     }
    private void ancoraSubCategorias(List<Cluster> listClusters) throws IOException
    {
      int aux = 0, nivel =3;
        
        for(Cluster c : listClusters)
        {
            if(c.getRoot().getCategorias() != null)
            {
                    List<String> it = c.getRoot().getCategorias();
                    while(aux < nivel)
                    {
                        List<String> subCategorias = new ArrayList<String>();
                        for(String l : it )
                            subCategorias.addAll(auxAncorarSkosCategories(l));
                        if(!subCategorias.isEmpty())
                        {
      //                      c.getRoot().getSubCategorias().setSubCategorias(subCategorias, aux);
       //                     it = c.getRoot().getSubCategorias().getSubCategorias(aux);
                        }
                        
                        
                        aux++;
                    }
            }
            aux= 0;
            for(Entidade e : c.getListEntidades())
            {
                if(e.getCategorias() != null)
                {
                    List<String> it = e.getCategorias();
                    while(aux < nivel)
                    {
                        List<String> subCategorias = new ArrayList<String>();
                        for(String l : it )
                            subCategorias.addAll(auxAncorarSkosCategories(l));
                        if(!subCategorias.isEmpty())
                        {
 //                           e.getSubCategorias().setSubCategorias(subCategorias, aux);
   //                         it = e.getSubCategorias().getSubCategorias(aux);
                        }
                        
                        
                        aux++;
                    }
                }
            }
        }
        
       
    }
    private void ancorar(List<Cluster> listClusters) throws IOException
    {
        
        System.out.println("Iniciando Processo de Ancoragem labels");
        System.out.println("Quantidade de Clusters "+listClusters.size());
        int nivel = 3;
      
        for(Cluster c : listClusters)
        {
            c.getRoot().setrscLabel(auxAncorarLabels(c.getRoot().getNome()));
            for(Entidade s : c.getListEntidades())
                s.setrscLabel(auxAncorarLabels(s.getNome()));           
        }
        for(Cluster c : listClusters)
        {
            if(c.getRoot().getRscLabel()!= null)
             {  
                 c.getRoot().setRedirects(auxAncorarRedirects(c.getRoot().getRscLabel()));
                 c.getRoot().addCategorias(auxAncorarArtCategories(c.getRoot().getRscLabel()));
             }
            for(Entidade e: c.getListEntidades())
            { 
              if(e.getRscLabel()!= null)
              {
                   e.setRedirects(auxAncorarRedirects(e.getRscLabel()));
                   e.addCategorias(auxAncorarArtCategories(e.getRscLabel()));
              }                   
            }     
            
        }
        ancoraSubCategorias(listClusters);
       
        
    }
    
}
