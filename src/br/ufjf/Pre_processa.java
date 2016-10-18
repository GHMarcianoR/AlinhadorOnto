/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Pre_processa {
    private final int tamanhoi, tamanhoj;
    private final double [][] matrix;   
    private IniClusters st;
    private TreeMap<Integer,List<Integer> > candidatos;
    private List<Cluster> listClusters; 
    private List<Entidade> ancLabels;
    private List<Entidade> ancRedirects;
    private BufferedWriter ancLabelTxt;
    public Pre_processa(String ont1, String ont2) throws IOException
    {
        System.out.println("pre processamento em andamento...");
        st  = new IniClusters(ont1,ont2);
        tamanhoi = st.getSchem().size();
        tamanhoj = st.getSchem2().size();
        matrix =  new double [tamanhoi][tamanhoj];
        listClusters= new ArrayList<> ();
        ancRedirects = new ArrayList<>();
        ancLabels =  new ArrayList<>();
       
    }    
    private double distancia(SchemaGraph sg, SchemaGraph sg1)
    {
        double dividendo, divisor;
        dividendo = 0;
        divisor = 0;
        boolean v = false;      
        for( Entidade s : sg.getListEntidade())   
                 for(Entidade s1 : sg1.getListEntidade())
                 {
                       if(s1.getNome().contains(s.getNome()) 
                               || s.getNome().contains(s1.getNome()))
                                   dividendo++;
                       for(String spro : s.getPropriedades())
                           for(String spro1 : s1.getPropriedades())
                           {
                               if(spro.contains(spro1) ||
                                       spro1.contains(spro))
                                       dividendo++;
                           }                     
                 }
        divisor =  Math.sqrt( sg.getListEntidade().size() * sg1.getListEntidade().size());               
      
        return  divisor ==  0 ?  0 : (dividendo/divisor);
    }
   public HashMap<Cluster, List<Entidade> > getAncorados()
    {
        HashMap<Cluster, List<Entidade>> ancorados = new HashMap<> ();
        List<Entidade> list = new ArrayList<> ();
        for(Cluster c : listClusters)
        {
            if(c.getRoot().getListRedirects() != null ||
                    c.getRoot().getCategorias() != null)
                   list.add(c.getRoot());
            for(Entidade e : c.getListEntidades())
            {
                if(e.getListRedirects() != null ||
                           e.getCategorias() != null)
                    list.add(e);
            }
            if(!list.isEmpty())
                ancorados.put(c, list);
        }
        return ancorados.isEmpty() ? null : ancorados ;
    }
   private void conta()
   {
       int labels = 0, redirects = 0, categories = 0;
       for(Cluster c: listClusters)
       {
           if(c.getRoot().getRscLabel() != null)
               labels++;
           if(c.getRoot().getListRedirects() != null && !c.getListEntidades().isEmpty())
               redirects++;
           if(c.getRoot().getCategorias() != null && !c.getRoot().getCategorias().isEmpty())
               categories++;
           for(Entidade e : c.getListEntidades())
           {
               if(e.getRscLabel() != null)
                   labels++;
               if(e.getListRedirects() != null && !e.getListRedirects().isEmpty())
                   redirects++;
               if(e.getCategorias() != null && !e.getCategorias().isEmpty())
                   categories++;
           }
               
       }
       System.out.println("Numero de labels ancorados: "+labels);
       System.out.println("Numero de redirects ancorados: "+redirects);
       System.out.println("Numero de article_categories ancorados: "+categories);
       System.out.flush();
   }
      public List<Cluster> getListCluster()
      {
           return listClusters;
      }
    public void iniciaPreProcessamento() throws IOException
    {     
        
        calculaDistancias(st.getSchem(), st.getSchem2());   
        selecionarCandidatos();
        unir();
        imprimiCluster();
        AncoragemMemoria anc = new AncoragemMemoria(listClusters);   
        conta();
       
    }
    public void imprimiCluster() throws IOException
    {
        BufferedWriter bw = new BufferedWriter(new FileWriter("Clusters"));
        for(Cluster c: listClusters)
        {
            bw.write(c.getRoot().getNome()+"\n");
            for(Entidade e : c.getListEntidades())
            {
               bw.write("\t"+e.getNome()+"\n");
            }
            bw.flush();
        }
    }

    private void calculaDistancias(Collection<SchemaGraph> c, Collection<SchemaGraph> c2)
    {
        int i = 0; 
        for(SchemaGraph s : c)
        {
            int j= 0;
            for(SchemaGraph p : c2)
            {
                matrix[i][j] = distancia(s, p);
                j++;
            }
            i++;
        }
    }
   private void selecionarCandidatos()
    {
        candidatos = new TreeMap< >();
        double threshold = 0.6;
        for(int i = 0; i<tamanhoi; i++)
        {
            List<Integer> listaCandidatos =  new ArrayList<>();
            for(int j = 0; j<tamanhoj; j++)
            {
                if(matrix[i][j] >= threshold)
                    listaCandidatos.add(j);
            }
            if(!listaCandidatos.isEmpty())
                candidatos.put(i, listaCandidatos);
        }
        System.out.println("Tamanho Canditados: "+candidatos.size());
    }
    private void unir()
    {
        
        Iterator<Entry<Integer,List<Integer>>> it = candidatos.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<Integer,List<Integer>> ob = (Map.Entry<Integer,List<Integer>>) it.next();
            int iClust1 = ob.getKey();
            int iAux = 0;
            Cluster clust = new Cluster();
            for(SchemaGraph s : st.getSchem())
            {
                if(iAux == iClust1 )
                {
                    clust.setRoot(s.getRoot());
                    for(Entidade e: s.getListEntidade())
                               clust.add(e);                 
                   
                } 
                iAux++;
            }
            
            for(Integer aux : ob.getValue())
            {
                int i = 0;
                for(SchemaGraph s : st.getSchem2())
                  {
                      if(i == aux)
                      {
                          clust.add(s.getRoot());
                          for(Entidade e : s.getListEntidade()) 
                                 clust.add(e);                                         
                      }
                      i++;
                  }
            }
               
            
            if(!clust.vazio())
                listClusters.add(clust);
        }
    }


   private void imprime() throws IOException
   {
       BufferedWriter bw = new BufferedWriter(new FileWriter("dbAncoragens.txt"));
       for(Cluster c : listClusters)
       {           
                try
                {
                    bw.write("Root Name\n");
                    bw.write("\t"+c.getRoot().getNome()+"\n");
                    bw.write("dbp:label\n");
                    bw.write("\t"+c.getRoot().getRscLabel()+"\n");
                    bw.write("dbp:redirects\n");
                    for(String s : c.getRoot().getListRedirects())
                          bw.write("\t"+s+"\n");
                    bw.write("dbp:Article_Categories\n");
                    for(String s : c.getRoot().getCategorias())
                           bw.write("\t"+s+"\n");

                   for(Entidade l : c.getListEntidades())
                   {
                        bw.write("Entidade Name\n");
                        bw.write("\t"+l.getNome()+"\n");
                        bw.write("dbp:label\n");
                        bw.write("\t"+l.getRscLabel()+"\n");
                        bw.write("dbp:redirects\n");
                        for(String s : l.getListRedirects())
                              bw.write("\t"+s+"\n");
                        bw.write("dbp:Article_Categories\n");
                        for(String s : l.getCategorias())
                               bw.write("\t"+s+"\n");               
                     }
                     bw.flush();
                }
                catch(Exception e){ System.err.println(e.getMessage()); }
                
       }
        bw.close();
   }


   
    
}